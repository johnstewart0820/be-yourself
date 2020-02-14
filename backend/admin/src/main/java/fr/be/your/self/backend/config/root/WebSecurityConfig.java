package fr.be.your.self.backend.config.root;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;

import fr.be.your.self.security.jwt.JwtAuthenticationEntryPoint;
import fr.be.your.self.security.jwt.JwtRequestFilter;
import fr.be.your.self.security.jwt.JwtToken;
import fr.be.your.self.security.jwt.JwtUserDetailsService;
import fr.be.your.self.util.StringUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${cors.allowed.origins}")
	private String allowedOrigins;
    
	@Value("${jwt.secret}")
	private String secret;
	
	@Bean
    public AllowFromStrategy allowFromStrategy() {
    	List<String> listAllowedOrgins = Arrays.asList(allowedOrigins.split(","));
		return new AllowFromStrategyImpl(listAllowedOrgins);
	}

	@Bean
    public PasswordEncoder passwordEncoder() {
    	return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	/*@Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
    	return new LoginSuccessHandler();
    }*/
	
	/********************* JWT Configuration ********************/
	@Bean
	public JwtToken jwtToken() {
		return new JwtToken(this.secret);
	}
	
	@Bean
	public UserDetailsService jwtUserDetailsService() {
		return new JwtUserDetailsService();
	}
	
	@Bean
	public JwtAuthenticationEntryPoint jwtAuthenticationEntryPointBean() throws Exception {
		return new JwtAuthenticationEntryPoint();
	}
	
	@Bean
	public JwtRequestFilter jwtRequestFilter() {
		return new JwtRequestFilter(this.jwtUserDetailsService(), this.jwtToken());
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.jwtUserDetailsService()).passwordEncoder(passwordEncoder());
	}
	
	/********************* Security Configuration **************/
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
        		"/images/**", 
        		"/css/**",
        		"/js/**",
        		"/fonts/**",
        		"/assets/**",
        		"/api/image/**",
        		"/api/notoken/**",
        		"/api/service/**",
        		"/websocket/**",
        		"/ws/**"
    		);
    }
    
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	// Note: Role should not start with ROLE_ because it
    	// will be inserted automatically
    	
        // @formatter:off
    	http
        	.authorizeRequests().antMatchers("/account/login", "/account/ping").permitAll()
	        .and()
	        	.authorizeRequests().anyRequest().hasRole("USER")
	        .and()
	        	.exceptionHandling()
	            	.accessDeniedPage("/account/login?authorization_error=true")
	            	.authenticationEntryPoint(new LoginAuthenticationEntryPoint("/account/login"))
	        // XXX: put CSRF protection back into this endpoint
	        .and()
	        	.csrf()
	            	.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
	        .logout()
	            .logoutSuccessUrl("/account/login")
	            .logoutUrl("/account/logout")
	            .deleteCookies("JSESSIONID", CookieLocaleResolver.DEFAULT_COOKIE_NAME)
	            .logoutSuccessHandler(new LogoutSuccessHandler())
	            .permitAll()
	        .and()
	        	.formLogin()
	        		.usernameParameter("j_username")
	        		.passwordParameter("j_password")
	        		.failureUrl("/account/login?authentication_error=true")
	        		.loginPage("/account/login")
	        		.loginProcessingUrl("/account/login")
	        		.defaultSuccessUrl(defaultRedirectUrl)
	        		//.successHandler(authenticationSuccessHandler())
	        ;
        
        // If no allowed origin, mean only allow from same domain
        if (!StringUtils.isNullOrSpace(allowedOrigins)) {
        	http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(allowFromStrategy()));
        } else {
        	http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
        }
        // @formatter:on
    }
    */
    
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// roles admin allow to access /admin/**
	    // roles user allow to access /user/**
	    // custom 403 access denied handler
		httpSecurity
			.csrf().disable()
				.authorizeRequests()
					.antMatchers("/", "/authenticate", "/home", "/about", "/greeting", "/test", "/user","/userform").permitAll()
					.antMatchers("/admin/**").hasAnyRole("ADMIN")
					//TODO TVA: temporarily disable login//.antMatchers("/user/**").hasAnyRole("USER")
					.antMatchers("/api/**").authenticated()
					//.anyRequest().authenticated()
		        .and()
			        .formLogin()
						.loginPage("/login")
						.permitAll()
				.and()
			        .logout()
						.permitAll()
				.and()
		        	.exceptionHandling()
		        		.authenticationEntryPoint(this.jwtAuthenticationEntryPointBean())
		        .and()
		    		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			;
			
			//.authorizeRequests()
			//	.antMatchers("/authenticate", "/user").permitAll()
			//	.antMatchers("/api/**").authenticated()
			//	.antMatchers("/**").permitAll()
			//.and()
			//	.exceptionHandling().authenticationEntryPoint(this.jwtAuthenticationEntryPointBean())
			//.and()
			//	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(this.jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
	}
    
    private class AllowFromStrategyImpl implements AllowFromStrategy {
    	
    	private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private Collection<String> allowedOrigins = Collections.emptyList();

		public AllowFromStrategyImpl(Collection<String> alloweds) {
			super();
			
			if (alloweds != null && !alloweds.isEmpty()) {
				this.allowedOrigins = new ArrayList<String>(alloweds.size());
				for (String origin : alloweds) {
					this.allowedOrigins.add(getOrigin(origin));
				}
			}
		}

		protected boolean isAllowed(String origin) {
			if (StringUtils.isNullOrSpace(origin)) {
				return false;
			}
			
			return allowedOrigins.contains(origin);
		}

		@Override
		public String getAllowFromValue(HttpServletRequest request) {
			String origin = request.getHeader("Origin");
			if (isAllowed(origin)) {
				return origin;
			}
			return null;
		}

		private String getOrigin(String url) {
			if (!url.startsWith("http:") && !url.startsWith("https:")) {
				return url;
			}

			String origin = null;

			try {
				URI uri = new URI(url);
				origin = uri.getScheme() + "://" + uri.getAuthority();
			} catch (URISyntaxException e) {
				logger.error("Cannot parse URI from " + url, e);
			}

			return origin;
		}
	}
}