package com.oauth.dao;

import org.springframework.stereotype.Repository;

import com.base.persistence.mybatis.BaseDAO;
import com.oauth.mapper.MstUserInfoMapper;
import com.oauth.table.MstUserInfo;
import com.oauth.table.MstUserInfoExample;

@Repository
public class MstUserInfoDAO extends BaseDAO<MstUserInfo, MstUserInfoExample, MstUserInfoMapper> {

    public MstUserInfo getMstUserInfoByUsername(String username) {
        String sql = "select * from mst_user_info where username= #{p0}";
        return this.uniqueBySql(sql, MstUserInfo.class, username);
    }

}
