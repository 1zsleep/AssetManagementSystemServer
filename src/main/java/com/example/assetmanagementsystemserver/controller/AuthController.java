package com.example.assetmanagementsystemserver.controller;

import com.example.assetmanagementsystemserver.pojo.User;
import com.example.assetmanagementsystemserver.service.CustomUserDetailsService;
import com.example.assetmanagementsystemserver.service.UserService;
import com.example.assetmanagementsystemserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册成功消息
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        User user1 = userService.insertUser(user);
        System.out.println(user1);
        return "User registered successfully";
    }

    /**
     * 用户登录
     * @param user 用户对象
     * @return JWT
     * @throws AuthenticationException 认证异常
     */
    @PostMapping("/login")
    public String login(@RequestBody User user) throws AuthenticationException {
        System.out.println(user);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getUserPassword()));
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUserName());
        return jwtUtil.generateToken(userDetails.getUsername());
    }
}