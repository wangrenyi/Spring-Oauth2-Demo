package com.oauth.dao;

import org.springframework.stereotype.Repository;

import com.base.persistence.mybatis.BaseDAO;
import com.oauth.mapper.AuthClientDetailsMapper;
import com.oauth.table.AuthClientDetails;
import com.oauth.table.AuthClientDetailsExample;

@Repository
public class AuthClientDetailsDAO
    extends BaseDAO<AuthClientDetails, AuthClientDetailsExample, AuthClientDetailsMapper> {

    public AuthClientDetails getAuthClientDetailsByClientId(String clientId) {
        String sql = "select *from auth_client_details where clientId = #{p0}";
        return this.uniqueBySql(sql, AuthClientDetails.class, clientId);
    }
}
