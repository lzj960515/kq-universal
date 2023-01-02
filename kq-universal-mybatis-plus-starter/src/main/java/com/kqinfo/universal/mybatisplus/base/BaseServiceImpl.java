package com.kqinfo.universal.mybatisplus.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 通用业务实现
 *
 * @author Zijian Liao
 * @since v1.0.0
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseDomain> extends ServiceImpl<M, T>
        implements BaseService<T> {

    /**
     * 检查字段：ID
     */
    protected static final String ID = "id";

    @Override
    public boolean create(T domain) {
        return super.save(domain);
    }

    @Override
    public boolean remove(Long id) {
        if (checkId(id)) {
            return super.removeById(id);
        }
        return false;
    }

    @Override
    public boolean update(T domain) {
        if (checkId(domain.getId())) {
            return super.updateById(domain);
        }
        return false;
    }

    @Override
    public T get(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<T> page(int current, int size, T domain) {
        return super.page(new Page<>(current, size), Wrappers.lambdaQuery(domain));
    }

    @Override
    public boolean updateState(Long id) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.setSql("state = 1 - state")
            .eq(ID, id);
        return super.update(wrapper);
    }

    /**
     * 检查 ID 是否存在
     *
     * @param id {@code Long} ID
     * @return {@code boolean}
     */
    protected boolean checkId(Long id) {
        return checkUniqueness(ID, id);
    }

    protected boolean checkUniqueness(String column, Object value) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq(column, value);
        return super.count(wrapper) > 0;
    }

}
