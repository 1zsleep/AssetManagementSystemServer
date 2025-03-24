// BaseRepository.java
package com.example.assetManagementSystemServer.base.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 将过滤字符串转换为JPA Specification
     * @param filter 前端传入的过滤条件字符串
     * @return 组合后的查询条件
     */
    default Specification<T> parseFilter(String filter) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (filter == null || filter.trim().isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // 分割AND条件
            String[] andConditions = filter.split("\\s+and\\s+");
            for (String condition : andConditions) {
                // 使用正则解析条件表达式
                Matcher matcher = Pattern.compile("(\\w+)\\s+(=|!=|>|<|>=|<=|like|ilike)\\s+('[^']*'|%?\\S+%?)")
                        .matcher(condition);
                if (matcher.find()) {
                    String field = matcher.group(1);
                    String operator = matcher.group(2).toLowerCase();
                    String value = matcher.group(3).replaceAll("^%|'|%$", "");

                    // 构建查询条件
                    Path<Object> fieldPath = getFieldPath(root, field);
                    predicates.add(buildPredicate(cb, fieldPath, operator, value));
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 根据操作符构建条件断言
     * @param cb CriteriaBuilder
     * @param fieldPath 字段路径
     * @param operator 操作符
     * @param value 值
     * @return 条件断言
     */
    @SuppressWarnings("unchecked")
    private Predicate buildPredicate(CriteriaBuilder cb, Path<?> fieldPath, String operator, String value) {
        // 处理不同类型字段的转换
        if (fieldPath.getJavaType() == Integer.class) {
            int numValue = Integer.parseInt(value);
            return buildNumericPredicate(cb, (Path<Integer>) fieldPath, operator, numValue);
        }
        if (fieldPath.getJavaType() == Long.class) {
            long numValue = Long.parseLong(value);
            return buildNumericPredicate(cb, (Path<Long>) fieldPath, operator, numValue);
        }
        // 默认按字符串处理
        return buildStringPredicate(cb, (Path<String>) fieldPath, operator, value);
    }

    /**
     * 构建数值类型条件
     */
    private <N extends Number & Comparable<? super N>> Predicate buildNumericPredicate(
            CriteriaBuilder cb, Path<N> fieldPath, String operator, N value) {
        return switch (operator) {
            case "=" -> cb.equal(fieldPath, value);
            case "!=" -> cb.notEqual(fieldPath, value);
            case ">" -> cb.greaterThan(fieldPath, value);
            case ">=" -> cb.greaterThanOrEqualTo(fieldPath, value);
            case "<" -> cb.lessThan(fieldPath, value);
            case "<=" -> cb.lessThanOrEqualTo(fieldPath, value);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    /**
     * 构建字符串类型条件
     */
    private Predicate buildStringPredicate(CriteriaBuilder cb, Path<String> fieldPath,
                                           String operator, String value) {
        return switch (operator) {
            case "=" -> cb.equal(fieldPath, value);
            case "!=" -> cb.notEqual(fieldPath, value);
            case "like" -> cb.like(fieldPath, "%" + value + "%");
            case "ilike" -> cb.like(cb.lower(fieldPath), "%" + value.toLowerCase() + "%");
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    /**
     * 将带下划线的字段名转换为实体属性路径
     * @param root 查询根对象
     * @param field 前端字段名（可能带下划线）
     * @return 对应的路径对象
     */
    private Path<Object> getFieldPath(Root<?> root, String field) {
        String[] parts = field.split("_");
        Path<Object> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }
}