package com.oauth.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.oauth.security.DaoClientDetailsService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    // @Value("spring.security.oauth2.resource.resource-id")
    // private static String RESOURCE_ID;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(new DaoClientDetailsService());
     // @formatter:off
//        clients.inMemory()
//            .withClient("client_1").resourceIds(RESOURCE_ID)
//            .authorizedGrantTypes("client_credentials", "refresh_token")
//            .scopes("select").authorities("client")
//            .secret(bCryptPasswordEncoder.encode("123456"))
//            .and()
//            .withClient("client_2").resourceIds(RESOURCE_ID)
//            .authorizedGrantTypes("password", "refresh_token")
//            .scopes("select").authorities("client")
//            .secret(bCryptPasswordEncoder.encode("123456"));
     // @formatter:on
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(new RedisTokenStore(redisConnectionFactory)).authenticationManager(authenticationManager);
        // Configuration TokenService parameters
        DefaultTokenServices tokenService = new DefaultTokenServices();
        tokenService.setTokenStore(endpoints.getTokenStore());
        tokenService.setSupportRefreshToken(true);
        tokenService.setClientDetailsService(endpoints.getClientDetailsService());
        tokenService.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenService.setAccessTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(30));
        tokenService.setRefreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(50));
        tokenService.setReuseRefreshToken(false);
        endpoints.tokenServices(tokenService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients().tokenKeyAccess("isAuthenticated()")
            .checkTokenAccess("permitAll()");
    }

}
