package com.oauth.security;

import com.oauth.table.AuthClientDetails;

public interface BaseClientDetailsProvider {

    AuthClientDetails selectClientDetailsByClientId(String clientId);
}
