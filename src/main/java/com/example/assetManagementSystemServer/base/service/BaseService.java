package com.example.assetManagementSystemServer.base.service;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.query.ListParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 基础服务类 - 提供通用分页查询能力
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public abstract class BaseService<T, ID> {

    /**
     * 执行分页查询（核心方法）
     * @param param 分页查询参数
     * @return 分页结果包装对象
     */
    public Items<T> list(ListParam param) {
        // 参数校验
        validateParam(param);

        // 构建分页参数
        Pageable pageable = buildPageRequest(param);

        // 执行分页查询
        Page<T> pageResult = getRepository().findAll(
                getRepository().parseFilter(param.getFilter()),
                pageable
        );

        // 构建返回结果
        return buildResult(pageResult, param.isCount());
    }

    /**
     * 获取关联的Repository（由子类实现）
     */
    protected abstract BaseRepository<T, ID> getRepository();

    /**
     * 参数有效性检查
     */
    private void validateParam(ListParam param) {
        if (param.getLimit() <= 0) {
            throw new IllegalArgumentException("limit必须大于0");
        }
        if (param.getOffset() < 0) {
            throw new IllegalArgumentException("offset不能为负数");
        }
    }

    /**
     * 构建分页请求对象
     */
    protected Pageable buildPageRequest(ListParam param) {
        // 计算页码（示例：offset=15, limit=10 → page=1）
        int pageNumber = (param.getOffset() + param.getLimit() - 1) / param.getLimit();
        return PageRequest.of(pageNumber, param.getLimit());
    }

    /**
     * 构建返回结果
     */
    protected Items<T> buildResult(Page<T> pageResult, boolean needTotal) {
        return new Items<>(
                pageResult.getContent(),
                needTotal ? pageResult.getTotalElements() : null
        );
    }


}