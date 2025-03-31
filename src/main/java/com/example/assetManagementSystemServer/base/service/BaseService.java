package com.example.assetManagementSystemServer.base.service;

import com.example.assetManagementSystemServer.base.query.Items;
import com.example.assetManagementSystemServer.base.repository.BaseRepository;
import com.example.assetManagementSystemServer.base.query.ListParam;
import com.example.assetManagementSystemServer.entity.asset.AssetFile;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


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
     * 分页查询（增强版）- 支持附加动态条件
     * @param param    分页参数
     * @param extraSpec 额外查询条件（可与其他条件组合）
     */
    public Items<T> list(ListParam param, @Nullable Specification<T> extraSpec) {
        // 参数校验
        validateParam(param);

        // 构建分页请求
        Pageable pageable = buildPageRequest(param);

        // 组合查询条件
        Specification<T> baseSpec = getRepository().parseFilter(param.getFilter());
        Specification<T> finalSpec = extraSpec != null ?
                baseSpec.and(extraSpec) : baseSpec;

        // 执行查询
        Page<T> pageResult = getRepository().findAll((root, query, cb) -> {
            // 动态加载关联实体（适用于所有实体）
            if (query != null && query.getResultType() == AssetFile.class) {
                root.fetch("asset", JoinType.INNER);
            }
            return finalSpec.toPredicate(root, query, cb);
        }, pageable);

        // 返回标准化结果
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