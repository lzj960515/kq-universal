package com.kqinfo.universal.mybatisplus.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kqinfo.universal.common.response.BaseResult;
import com.kqinfo.universal.common.response.BaseResultCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 通用请求处理
 *
 * @author Zijian Liao
 * @since v1.0.0
 */
@SuppressWarnings("all")
public abstract class BaseController<T extends BaseDomain, S extends BaseService<T>> {

    @Autowired
    protected S service;

    /**
     * 新增
     *
     * @param domain 领域模型
     * @return {@link BaseResult}
     */
    @ApiOperation(value = "通用接口-新增")
    @PostMapping("/create")
    public BaseResult<Void> create(@Valid @RequestBody T domain) {
        // 业务逻辑
        boolean created = service.create(domain);
        if (created) {
            return BaseResult.success("创建成功");
        }

        return BaseResult.failure(BaseResultCode.FAILURE);
    }

    /**
     * 删除
     *
     * @param id {@code Long}
     * @return {@link BaseResult}
     */
    @ApiOperation(value = "通用接口-删除")
    @DeleteMapping("/remove/{id}")
    public BaseResult<Void> remove(@PathVariable Long id) {
        // 业务逻辑
        boolean deleted = service.remove(id);
        if (deleted) {
            return BaseResult.success("删除成功");
        }

        return BaseResult.failure(BaseResultCode.FAILURE);
    }

    /**
     * 修改
     *
     * @param domain 领域模型
     * @return {@link BaseResult}
     */
    @ApiOperation(value = "通用接口-修改")
    @PutMapping("/update")
    public BaseResult<Void> update(@Valid @RequestBody T domain) {
        // 业务逻辑
        boolean updated = service.update(domain);
        if (updated) {
            return BaseResult.success("编辑成功");
        }

        return BaseResult.failure(BaseResultCode.FAILURE);
    }

    /**
     * 获取
     *
     * @param id {@code Long}
     * @return {@link BaseResult}
     */
    @ApiOperation(value = "通用接口-获取")
    @GetMapping("/get/{id}")
    public BaseResult<T> get(@PathVariable Long id) {
        T domain = service.get(id);
        return BaseResult.success(domain);
    }

    /**
     * 分页
     *
     * @param current {@code int} 页码
     * @param size    {@code int} 笔数
     * @return {@link BaseResult}
     */
    @ApiOperation(value = "通用接口-分页")
    @GetMapping("/page")
    public BaseResult<IPage<T>> page(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size, T domain) {
        IPage<T> page = service.page(current, size, domain);
        return BaseResult.success(page);
    }

}
