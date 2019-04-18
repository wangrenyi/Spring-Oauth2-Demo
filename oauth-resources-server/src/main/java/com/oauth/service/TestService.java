package com.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.dao.MstUserInfoDAO;
import com.oauth.table.MstUserInfo;

@Service
@Transactional(readOnly = true)
public class TestService {

    // @Autowired
    // private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MstUserInfoDAO mstUserInfoDAO;

    public MstUserInfo signUp(MstUserInfo mstUserInfo) {
        // mstUserInfo.setPassword(bCryptPasswordEncoder.encode(mstUserInfo.getPassword()));
        mstUserInfoDAO.insert(mstUserInfo);
        return mstUserInfo;
    }

    public String getLoginAccount() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
