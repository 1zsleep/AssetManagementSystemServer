package com.example.assetmanagementsystemserver.config;

import com.example.assetmanagementsystemserver.service.CustomUserDetailsService;
import com.example.assetmanagementsystemserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * JWT 请求过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 过滤请求以验证 JWT
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param chain    过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        //字段是否存在且以"Bearer "开头，这是JWT的标准格式
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            //提取JWT令牌（去掉"Bearer "前缀）
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        //如果用户名不为空且当前安全上下文中没有认证信息，则继续处理。
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //加载用户详情
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
            //验证JWT令牌的有效性。
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                //创建一个认证令牌，包含用户详情和权限信息。
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 设置认证令牌的详细信息
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //将认证令牌设置到安全上下文中，表示用户已认证。
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        //继续处理请求链，将请求传递给下一个过滤器或控制器。
        chain.doFilter(request, response);
    }
}