package fr.be.your.self.backend.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import fr.be.your.self.backend.dto.UserCreateRequest;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.StatusResponse;
import fr.be.your.self.exception.InvalidException;
import fr.be.your.self.exception.ValidationException;
import fr.be.your.self.model.User;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@CrossOrigin
@Controller
@RequestMapping(Constants.PATH.AUTHENTICATION_PREFIX)
public class AuthController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private DefaultTokenServices tokenServices;
    
    //@Autowired
    //private JwtToken jwtToken;

    //@Autowired
    //private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
    @GetMapping(Constants.PATH.AUTHENTICATION.LOGIN)
    public String loginPage(Model model) {
        return "login";
    }
    
    /*
    @GetMapping("/test")
    public ResponseEntity<?> loginTest() throws Exception {
    	HashMap<String, String> authorizationParameters = new HashMap<String, String>();
        authorizationParameters.put("scope", "read");
        authorizationParameters.put("username", "mobile_client");
        authorizationParameters.put("client_id", "mobile-client");
        authorizationParameters.put("grant", "password");

        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(authorizationParameters);
        authorizationRequest.setApproved(true);

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_UNTRUSTED_CLIENT"));
        authorizationRequest.setAuthorities(authorities);

        HashSet<String> resourceIds = new HashSet<String>();
        resourceIds.add("mobile-public");
        authorizationRequest.setResourceIds(resourceIds);

        // Create principal and auth token
        User userPrincipal = new User(user.getUserID(), "", true, true, true, true, authorities);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities) ;

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(authorizationRequest, authenticationToken);
        authenticationRequest.setAuthenticated(true);

        OAuth2AccessToken accessToken = this.tokenServices.createAccessTokenForUser(authenticationRequest, user);
        
		final StatusResponse result = new StatusResponse(true);
    	
    	try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin@gmail.com", "123456"));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    	
		return ResponseEntity.ok(result);
    }
    */
    
    @PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserCreateRequest body) throws Exception {
		final String email = body.getEmail();
		
		if (StringUtils.isNullOrSpace(email)) {
			throw new InvalidException(StatusCode.INVALID_PARAMETER, "email");
		}
		
		if (this.userService.existsEmail(email)) {
			throw new ValidationException(StatusCode.USERNAME_EXISTED, "Username already existed", email);
		}

		final String password = body.getPassword();
		final String encodedPassword = this.passwordEncoder.encode(password);
		final String fullname = body.getFullname();
		
		final User domain = new User(email, encodedPassword, fullname);
		final User savedDomain = this.userService.create(domain);
		
		final StatusResponse result = new StatusResponse(true);
		result.addData("data", savedDomain.getId());
		
		// TODO PhatPQ => Send email to notify
		return ResponseEntity.ok(result);
	}
    
    /*
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        this.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        
        final UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = this.jwtToken.generateToken(userDetails);
        final AuthenticatedUserResponse authenticatedUser = this.generateAuthenticatedUser(userDetails);
        
        return ResponseEntity.ok(new TokenResponse(authenticatedUser, AuthenticationStrategy.JWT.getValue(), token));
    }
    
    @RequestMapping(value = "/google/verify-token", method = RequestMethod.POST)
    public ResponseEntity<?> verifyGoogleToken(@RequestBody TokenRequest tokenRequest) throws Exception {
    	final GoogleIdToken idToken = this.googleIdTokenVerifier.verify(tokenRequest.getToken());
		final Payload payload = idToken.getPayload();
		final String userId = payload.getSubject();
		
        final UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(userId);
        final String token = this.jwtToken.generateToken(userDetails);
        final AuthenticatedUserResponse authenticatedUser = this.generateAuthenticatedUser(userDetails);
        
        return ResponseEntity.ok(new TokenResponse(authenticatedUser, AuthenticationStrategy.JWT.getValue(), token));
    }
    
    private void authenticate(String username, String password) throws Exception {
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    
    private AuthenticatedUserResponse generateAuthenticatedUser(UserDetails user) {
    	final Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
    	
    	final AuthenticatedUserResponse jwtUser = new AuthenticatedUserResponse();
    	jwtUser.setEmail(user.getUsername());
    	
    	if (!authorities.isEmpty()) {
    		jwtUser.setRole(authorities.iterator().next().getAuthority());
    	}
    	
    	if (user instanceof JwtUserDetails) {
    		final JwtUserDetails jwtUserDetails = (JwtUserDetails) user;
    		
    		jwtUser.setFullname(jwtUserDetails.getFullname());
        	jwtUser.setAvatar(jwtUserDetails.getAvatar());
        	
        	// TODO
    	}
    	
    	return jwtUser;
    }
    */
}