package com.example.assetManagementSystemServer.base.service;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


/**
 * 条件构建工具 - 简化动态查询条件创建
 */
public class ConditionBuilder {

    /**
     * 等于条件
     * @param fieldPath 字段路径（如 "owner.userId"）
     * @param value 匹配值
     */
    public static <T> Specification<T> equals(String fieldPath, Object value) {
        return (root, query, cb) -> {
            Path<Object> path = buildNestedPath(root, fieldPath);
            return cb.equal(path, value);
        };
    }

    /**
     * IN 查询条件
     * @param fieldPath 字段路径
     * @param values 值集合
     */
    public static <T> Specification<T> in(String fieldPath, Iterable<?> values) {
        return (root, query, cb) -> {
            Path<Object> path = buildNestedPath(root, fieldPath);
            return path.in(values);
        };
    }

    /**
     * 构建嵌套字段路径（支持 owner.userId 格式）
     */
    private static Path<Object> buildNestedPath(Root<?> root, String fieldPath) {
        String[] fields = fieldPath.split("\\.");
        Path<Object> path = root.get(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            path = path.get(fields[i]);
        }
        return path;
    }
}