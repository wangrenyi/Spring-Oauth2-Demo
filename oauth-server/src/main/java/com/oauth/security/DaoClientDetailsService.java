package com.oauth.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.oauth.dao.MstUserInfoDAO;

public class DaoClientDetailsService extends BaseClientDetailsService {

    @Autowired
    private MstUserInfoDAO mstUserInfoDAO;

    @Override
    protected Map<String, ?> getAdditionalInformation() {
        return new HashMap<>();
    }

    @Override
    protected Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("client"));
    }

}
