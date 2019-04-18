package com.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oauth.dao.MstUserInfoDAO;
import com.oauth.table.MstUserInfo;

public class BaseUserDetailService implements UserDetailsService {

    @Autowired
    private MstUserInfoDAO mstUserInfoDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MstUserInfo mstUserInfo = this.mstUserInfoDAO.getMstUserInfoByUsername(username);
        return new User(mstUserInfo.getUsername(), passwordEncoder.encode(mstUserInfo.getPassword()),
            Arrays.asList(new SimpleGrantedAuthority("admin")));
    }

}
