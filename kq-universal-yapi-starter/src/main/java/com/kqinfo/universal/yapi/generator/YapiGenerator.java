package com.kqinfo.universal.yapi.generator;

import cn.hutool.core.util.ClassLoaderUtil;
import com.kqinfo.universal.yapi.domain.ApiInfo;
import com.kqinfo.universal.yapi.domain.CatInfo;
import com.kqinfo.universal.yapi.domain.ControllerInfo;
import com.kqinfo.universal.yapi.handler.YapiHandler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * yapi生成器
 *
 * @author Zijian Liao
 * @since 2.13.0
 */
public final class YapiGenerator {

    private static final List<Class<?>> CLASS_REPOSITORY = new ArrayList<>(32);
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final Map<String, AbstractParser> PARSER_MAP = new HashMap<>(2);

    static {
        PARSER_MAP.put("yapi", new YapiParser());
        PARSER_MAP.put("swagger2", new Swagger2Parser());
    }

    private YapiGenerator() {
    }

    /**
     * 生成yapi
     *
     * @param basePath     项目基础路径
     * @param basePackages 扫描包路径
     * @param yapiAddress  yapi地址
     * @param token        项目token
     * @param type         类型 swagger2 yapi
     * @param override     是否覆盖之前接口
     */
    public static void generateYapi(String basePath, String basePackages,
                                    String yapiAddress, String token, String type, boolean override) {
        // 1.扫描出包下的所有controller，每个controller视为yapi中一个分类
        // 将包格式替换为文件路径格式
        String packageSearchPath = basePackages.replace('.', '/');
        String scanPath = basePath + File.separator + packageSearchPath;
        File dir = new File(scanPath);
        scanFile(dir, basePackages);
        generateYapi(yapiAddress, token, type, override);
    }


    /**
     * 生成yapi
     *
     * @param basePackages 扫描包路径
     * @param yapiAddress  yapi地址
     * @param token        项目token
     * @param type         类型 swagger2 yapi
     * @param override     是否覆盖之前接口
     */
    public static void generateYapi(String basePackages,
                                    String yapiAddress, String token, String type, boolean override){
        URI resource = getResource(basePackages);
        File dir = new File(resource.getPath());
        scan(basePackages, dir);
        generateYapi(yapiAddress, token, type, override);
    }

    private static void scan(String basePackages, File dir){
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                scan(basePackages + "." + file.getName(), file);
            }
        }else {
            addClass(basePackages);
        }
    }

    private static void addClass(String basePackages){
        String className = basePackages.replace(".class", "");
        try {
            Class<?> clazz = CLASS_LOADER.loadClass(className);
            CLASS_REPOSITORY.add(clazz);
        } catch (ClassNotFoundException ignore) {
        }
    }

    private static URI getResource(String basePackages){
        // 将包格式替换为文件路径格式
        String packageSearchPath = basePackages.replace('.', '/');
        try {
            // 根据路径获取资源
            return CLASS_LOADER.getResource(packageSearchPath).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateYapi(String yapiAddress, String token, String type, boolean override){
        // 2.遍历每个controller的接口
        List<ControllerInfo> controllerInfoList = PARSER_MAP.get(type).parse(CLASS_REPOSITORY);
        // 3.调用yapi接口
        YapiHandler yapiHandler = YapiHandler.getInstance(yapiAddress, token);
        for (ControllerInfo controllerInfo : controllerInfoList) {
            // 3.1 添加菜单
            CatInfo catInfo = yapiHandler.saveCat(controllerInfo.getName());
            // 3.2 添加接口 是否覆盖
            for (ApiInfo apiInfo : controllerInfo.getApiInfoList()) {
                apiInfo.setCatid(catInfo.get_id());
                yapiHandler.saveOrUpdateApi(apiInfo, override);
            }
        }
        // 4.清理数据
        CLASS_REPOSITORY.clear();
    }

    private static void scanFile(File dir, String basePackages) {
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                // 递归扫描
                scanFile(file, basePackages + "." + file.getName());
            }
        }else {
            String className = basePackages.replace(".class", "");
            Class<?> clazz = ClassLoaderUtil.loadClass(dir, className);
            CLASS_REPOSITORY.add(clazz);
        }
    }


}
