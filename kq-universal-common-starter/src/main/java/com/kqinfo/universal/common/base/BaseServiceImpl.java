package com.kqinfo.universal.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kqinfo.universal.common.exception.BusinessException;
import com.kqinfo.universal.common.response.BaseResultCode;

/**
 * 通用业务实现
 *
 * @author Zijian Liao
 * @since v1.0.0
 * @see kq-universal-mybatis-plus
 */
@Deprecated
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
		try {
			if (checkId(domain.getId())) {
				return super.updateById(domain);
			}
			return false;
		}
		catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	@Override
	public T get(Long id) {
		T domain = super.getById(id);
		if (null == domain) {
			throw new BusinessException(BaseResultCode.RESULT_DATA_NONE);
		}
		return domain;
	}

	@Override
	public IPage<T> page(int current, int size, T domain) {
		return super.page(new Page<>(current, size), Wrappers.lambdaQuery(domain));
	}

	/**
	 * 检查 ID 是否存在
	 * @param id {@code Long} ID
	 * @return {@code boolean} ID 不存在则抛出异常
	 */
	protected boolean checkId(Long id) {
		if (!checkUniqueness(ID, id)) {
			throw new BusinessException(BaseResultCode.RESULT_DATA_NONE);
		}
		return true;
	}

	protected boolean checkUniqueness(String column, Object value) {
		QueryWrapper<T> wrapper = new QueryWrapper<>();
		wrapper.eq(column, value);
		return super.count(wrapper) > 0;
	}

}
