package com.example.assetmanagementsystemserver.controller;

import com.example.assetmanagementsystemserver.dto.BaseResponse;
import com.example.assetmanagementsystemserver.enums.ResponseStatusEnum;
import com.example.assetmanagementsystemserver.exception.BusinessException;
import com.example.assetmanagementsystemserver.pojo.User;
import com.example.assetmanagementsystemserver.repository.UserRepository;
import com.example.assetmanagementsystemserver.service.CustomUserDetailsService;
import com.example.assetmanagementsystemserver.service.UserService;
import com.example.assetmanagementsystemserver.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final UserRepository userRepository;

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册成功消息
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> register(@Valid @RequestBody User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new BusinessException(ResponseStatusEnum.USER_EXISTS);
        }
        User registeredUser = userService.insertUser(user);
        return ResponseEntity.ok(BaseResponse.success(registeredUser));
    }

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

            // 生成包含角色的 Token
            final String token = jwtUtil.generateToken(user.getUserName(), authorities);
            return ResponseEntity.ok(BaseResponse.success(token));
        } catch (AuthenticationException e) {
            throw new BusinessException(ResponseStatusEnum.INVALID_CREDENTIALS);
        }
    }
}