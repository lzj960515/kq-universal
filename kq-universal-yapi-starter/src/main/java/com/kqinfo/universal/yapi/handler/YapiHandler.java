package com.kqinfo.universal.yapi.handler;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.kqinfo.universal.yapi.domain.ApiBaseInfo;
import com.kqinfo.universal.yapi.domain.ApiInfo;
import com.kqinfo.universal.yapi.domain.CatInfo;
import com.kqinfo.universal.yapi.domain.ProjectInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * yapi交互
 * @author Zijian Liao
 * @since 2.13.0
 */
public class YapiHandler extends YapiTemplate {

    private final String token;

    private YapiHandler(String yapiAddress, String token) {
        super(yapiAddress);
        this.token = token;
    }

    public static YapiHandler getInstance(String yapiAddress, String token){
        return new YapiHandler(yapiAddress, token);
    }

    /**
     * 获取项目信息
     * @return 项目信息
     */
    public ProjectInfo getProjectInfo(){
        String result = super.get("/api/project/get", MapUtil.of("token", token));
        return getData(result, ProjectInfo.class);
    }

    /**
     * 添加分类
     * @param name 分类名称
     * @param desc 分类描述
     * @return 分类信息
     */
    public CatInfo addCat(String name, String desc){
        ProjectInfo projectInfo = getProjectInfo();
        Map<String, Object> param = new HashMap<>(4, 1);
        param.put("token", token);
        param.put("project_id", projectInfo.get_id());
        param.put("name", name);
        param.put("desc", desc);
        String result = super.postForm("/api/interface/add_cat", param);
        return getData(result, CatInfo.class);
    }
    
    public List<CatInfo> listCat(){
        Long projectId = getProjectInfo().get_id();
        Map<String, Object> param = new HashMap<>(2, 1);
        param.put("project_id", projectId);
        this.putToken(param);
        String result = super.get("/api/interface/getCatMenu", param);
        return getListData(result, CatInfo.class);
    }

    /**
     * 添加接口
     * @param apiInfo 接口信息
     */
    public void addApi(ApiInfo apiInfo){
        apiInfo.setToken(token);
        super.post("/api/interface/add", apiInfo);
    }

    /**
     * 添加或更新接口
     * @param apiInfo 接口信息
     */
    public void saveApi(ApiInfo apiInfo){
        apiInfo.setToken(token);
        super.post("/api/interface/save", apiInfo);
    }

    /**
     * 获取分类下的接口列表
     * @param catId 分类id
     */
    public List<ApiBaseInfo> listCatApi(Long catId){
        Map<String, Object> param = new HashMap<>(4,1);
        this.putToken(param);
        param.put("catid", catId);
        param.put("page", 1);
        param.put("limit", 1000);
        String result = super.get("/api/interface/list_cat", param);
        JSONObject resultJson = getData(result, JSONObject.class);
        Integer count = resultJson.getInteger("count");
        if(count != null && count.equals(0)){
            return null;
        }
        return resultJson.getObject("list", new TypeReference<List<ApiBaseInfo>>(){
        });
    }

    public CatInfo saveCat(String name){
        List<CatInfo> catInfos = listCat();
        for (CatInfo catInfo : catInfos) {
            if(catInfo.getName().equals(name)){
                return catInfo;
            }
        }
        return addCat(name, name);
    }

    public void saveOrUpdateApi(ApiInfo apiInfo, boolean override){
        if(!override){
            // 不覆盖则判断是否已存在接口，存在直接返回
            List<ApiBaseInfo> apiBaseInfoList = listCatApi(apiInfo.getCatid());
            if(apiBaseInfoList != null){
                for (ApiBaseInfo apiBaseInfo : apiBaseInfoList) {
                    if (apiBaseInfo.getPath().equals(apiInfo.getPath())
                            && apiBaseInfo.getMethod().equalsIgnoreCase(apiInfo.getMethod())){
                        return;
                    }
                }
            }
        }
        this.saveApi(apiInfo);
    }

    private static <T> T getData(String result, Class<T> cls){
        JSONObject resultJson = JSON.parseObject(result);
        return resultJson.getObject("data", cls);
    }

    private static <T> List<T> getListData(String result, Class<T> cls){
        JSONObject resultJson = JSON.parseObject(result);
        return resultJson.getObject("data", new TypeReference<List<T>>(cls){
        });
    }

    private void putToken(Map<String, Object> param){
        param.put("token", token);
    }
}
