package com.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.dao.AuthClientDetailsDAO;
import com.oauth.table.AuthClientDetails;

@Component
@Transactional(readOnly = true)
public class DaoBaseClientDetailsProvider implements BaseClientDetailsProvider {

    @Autowired
    private AuthClientDetailsDAO authClientDetailsDAO;

    @Override
    public AuthClientDetails selectClientDetailsByClientId(String clientId) {
        return authClientDetailsDAO.getAuthClientDetailsByClientId(clientId);
    }

}
