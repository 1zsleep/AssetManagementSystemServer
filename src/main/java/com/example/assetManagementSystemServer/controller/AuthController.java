package com.example.assetManagementSystemServer.controller;

import com.example.assetManagementSystemServer.dto.BaseResponse;
import com.example.assetManagementSystemServer.enums.ResponseStatusEnum;
import com.example.assetManagementSystemServer.exception.BusinessException;
import com.example.assetManagementSystemServer.entity.user.User;
import com.example.assetManagementSystemServer.service.user.UserService;
import com.example.assetManagementSystemServer.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 认证控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     * @param user 用户对象
     * @return JWT
     * @throws AuthenticationException 认证异常
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody User user) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(),
                            user.getUserPassword()
                    )
            );

            // 获取用户权限信息
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            User User1 = userService.getUserByUserName(user.getUserName());
            // 生成包含角色的 Token

            final String token = jwtUtil.generateToken(user.getUserName(),User1.getId(),User1.isStatus(), authorities);
            return ResponseEntity.ok(BaseResponse.success(token));
        } catch (AuthenticationException e) {
            throw new BusinessException(ResponseStatusEnum.INVALID_CREDENTIALS);
        }
    }
}