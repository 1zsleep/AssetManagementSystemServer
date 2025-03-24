package com.example.assetManagementSystemServer.base.query;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自动解析ListParam参数的解析器
 * 处理以下参数：
 * - limit
 * - offset
 * - count
 * - filter
 */
public class ListParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ListParam.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        System.out.println("Received parameters: " + webRequest.getParameterMap());
        ListParam param = new ListParam();

        // 解析limit参数（带默认值）
        String limit = webRequest.getParameter("limit");
        if (limit != null && !limit.isEmpty()) {
            param.setLimit(parseIntSafely(limit, 10));
        }

        // 解析offset参数（带默认值）
        String offset = webRequest.getParameter("offset");
        if (offset != null && !offset.isEmpty()) {
            param.setOffset(parseIntSafely(offset, 0));
        }

        // 解析count参数
        String count = webRequest.getParameter("count");
        if (count != null && !count.isEmpty()) {
            param.setCount(parseBooleanSafely(count));
        }

        // 直接传递filter字符串（保留原始值）
        param.setFilter(webRequest.getParameter("filter"));

        return param;
    }

    /**
     * 安全解析整数值
     * @param value 字符串值
     * @param defaultValue 解析失败时的默认值
     */
    private int parseIntSafely(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue; // 确保正数
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 安全解析布尔值
     * @param value 字符串值（支持true/false/yes/no/1/0）
     */
    private boolean parseBooleanSafely(String value) {
        if (value == null) return false;
        String lowerValue = value.trim().toLowerCase();
        return lowerValue.equals("true") ||
                lowerValue.equals("yes") ||
                lowerValue.equals("1");
    }
}