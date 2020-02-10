package fr.be.your.self.backend.auth;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import fr.be.your.self.backend.dto.AuthenticatedUserResponse;
import fr.be.your.self.backend.dto.JwtRequest;
import fr.be.your.self.backend.dto.TokenRequest;
import fr.be.your.self.backend.dto.TokenResponse;
import fr.be.your.self.backend.dto.UserCreateRequest;
import fr.be.your.self.common.AuthenticationStrategy;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.StatusResponse;
import fr.be.your.self.exception.InvalidException;
import fr.be.your.self.exception.ValidationException;
import fr.be.your.self.model.User;
import fr.be.your.self.security.jwt.JwtToken;
import fr.be.your.self.security.jwt.JwtUserDetails;
import fr.be.your.self.security.jwt.JwtUserDetailsService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@RestController
@CrossOrigin
public class AuthController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
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
}