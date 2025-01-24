package com.example.assetmanagementsystemserver.service;

import com.example.assetmanagementsystemserver.pojo.User;
import com.example.assetmanagementsystemserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRole().getDescription());
        // 使用完全限定名解决命名冲突
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getUserPassword(), authorities);
    }
}