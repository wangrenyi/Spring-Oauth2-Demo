package com.oauth.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.oauth.table.AuthClientDetails;
import com.oauth.util.OptionalUtil;

public abstract class BaseClientDetailsService implements ClientDetailsService {

    @Value("spring.security.oauth2.resource.resource-id")
    private static String RESOURCE_ID;

    @Autowired(required = true)
    private List<BaseClientDetailsProvider> clientDetailsProviders;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (clientId == null) {
            throw new ClientRegistrationException("client'id can not be empty");
        }

        AuthClientDetails authClientDetails = null;
        for (BaseClientDetailsProvider clientDetailsProvider : clientDetailsProviders) {
            authClientDetails = clientDetailsProvider.selectClientDetailsByClientId(clientId);
            if (authClientDetails != null) {
                break;
            }
        }
        if (authClientDetails == null) {
            throw new ClientRegistrationException("client details not found");
        }
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(clientId);
        clientDetails.setClientSecret(passwordEncoder.encode(authClientDetails.getClientsecret()));
        clientDetails.setScope(OptionalUtil.spiltToArray(authClientDetails.getScope(), ","));
        clientDetails.setResourceIds(Arrays.asList(RESOURCE_ID));
        clientDetails.setAuthorizedGrantTypes(OptionalUtil.spiltToArray(authClientDetails.getGranttype(), ","));
        clientDetails.setRegisteredRedirectUri(OptionalUtil.spiltToSet(authClientDetails.getRedirecturi(), ","));
        // clientDetails.setAutoApproveScopes(autoApproveScopes);
        clientDetails.setAuthorities(getAuthorities());
        clientDetails.setAccessTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(1));
        clientDetails.setRefreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(1));
        clientDetails.setAdditionalInformation(getAdditionalInformation());
        return clientDetails;
    }

    protected abstract Map<String, ?> getAdditionalInformation();

    protected abstract Collection<? extends GrantedAuthority> getAuthorities();
}
