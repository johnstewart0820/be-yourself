package fr.be.your.self.backend.config.root;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.InMemoryApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import fr.be.your.self.security.oauth2.AccessTokenEnhancer;
import fr.be.your.self.security.oauth2.DefaultUserApprovalHandler;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${oauth2.realm}")
	private String realm;

	@Value("#{'${oauth2.grant.type}'.split(',')}")
	private Set<String> defaultGrantTypes;

	@Value("#{'${oauth2.scope}'.split(',')}")
	private Set<String> defaultScopes;
	
	// Default 30d (2592000s)
	@Value("${oauth2.refresh.token.validity:2592000}")
	private int refreshTokenValidity;
	
	// Default 1h (3600s)
	@Value("${oauth2.access.token.validity:3600}")
	private int accessTokenValidity;

	@Value("${oauth2.client.id}")
	private String clientId;

	@Value("${oauth2.client.secret}")
	private String clientSecret;

	@Value("#{'${oauth2.redirect.uri}'.split(',')}")
	private List<String> redirectUri;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	/*
	@Bean
	public ClientDetailsService clientDetailsService() {
		final BaseClientDetails defaultClientDetails = new BaseClientDetails();
		defaultClientDetails.setClientId(this.clientId);
		defaultClientDetails.setAuthorizedGrantTypes(this.defaultGrantTypes);
		defaultClientDetails.setAccessTokenValiditySeconds(this.accessTokenValidity);
		defaultClientDetails.setRefreshTokenValiditySeconds(this.refreshTokenValidity);
		defaultClientDetails.setRegisteredRedirectUri(new HashSet<>(this.redirectUri));
		defaultClientDetails.setClientSecret(this.passwordEncoder.encode(this.clientSecret));
		defaultClientDetails.setScope(this.defaultScopes);
		defaultClientDetails.setAutoApproveScopes(this.defaultScopes);
		
		final InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();
		clientDetailsService.setClientDetailsStore(Collections.singletonMap(this.clientId, defaultClientDetails));
		return clientDetailsService;
	}
	*/
	
	/*
	@Bean
    public DefaultOAuth2RequestFactory defaultOAuth2RequestFactory() {
		return new DefaultOAuth2RequestFactory(this.clientDetailsService());
	}
	*/
	
	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new InMemoryAuthorizationCodeServices();
	}
	
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new AccessTokenEnhancer();
    }
    
    @Bean
	public ApprovalStore approvalStore() throws Exception {
		return new InMemoryApprovalStore();
	}
    
    @Bean
	@Lazy
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public DefaultUserApprovalHandler userApprovalHandler() throws Exception {
		final DefaultUserApprovalHandler handler = new DefaultUserApprovalHandler();
		handler.setApprovalStore(this.approvalStore());
		handler.setUseApprovalStore(true);
		
		return handler;
	}
    
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		String[] grantTypes = new String[this.defaultGrantTypes.size()];
		this.defaultGrantTypes.toArray(grantTypes);
		
		String[] scopes = new String[this.defaultScopes.size()];
		this.defaultScopes.toArray(scopes);
		
		String[] redirectUris = new String[this.redirectUri.size()];
		this.redirectUri.toArray(redirectUris);
		
		// @formatter:off
		clients
			.inMemory()
				.withClient(this.clientId)
				.secret(this.passwordEncoder.encode(this.clientSecret))
				.authorizedGrantTypes(grantTypes)
				.scopes(scopes)
				.accessTokenValiditySeconds(this.accessTokenValidity)
				.refreshTokenValiditySeconds(this.refreshTokenValidity)
				.redirectUris(redirectUris)
		;
		// @formatter:on
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		// @formatter:off
		oauthServer
			.realm(this.realm)
			.allowFormAuthenticationForClients()
		;
		// @formatter:on
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(this.tokenEnhancer(), this.accessTokenConverter));
		
		// @formatter:off
		endpoints
			.tokenStore(this.tokenStore)
			.tokenEnhancer(tokenEnhancerChain)
			.authenticationManager(this.authenticationManager)
			.userApprovalHandler(this.userApprovalHandler())
			.authorizationCodeServices(this.authorizationCodeServices());
		// @formatter:on
	}
}
