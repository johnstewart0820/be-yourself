package fr.be.your.self.security.oauth2;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.be.your.self.common.UserType;
import fr.be.your.self.model.User;
import fr.be.your.self.repository.UserRepository;
import fr.be.your.self.util.StringUtils;

public class DefaultUserDetailsService implements UserDetailsService {
	
	private static final String ROLE_GRANTED_AUTHORITY_PREFIX = "ROLE_";
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = this.userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		final String userType = user.getUserType();
		if (StringUtils.isNullOrSpace(userType)) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		if (!userType.equalsIgnoreCase(UserType.SUPER_ADMIN.getValue())
				&& !userType.equalsIgnoreCase(UserType.ADMIN.getValue())) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		final List<GrantedAuthority> roles = Arrays.asList(
				new SimpleGrantedAuthority(ROLE_GRANTED_AUTHORITY_PREFIX + userType), 
				new SimpleGrantedAuthority(ROLE_GRANTED_AUTHORITY_PREFIX + UserType.USER.getValue()));
		
		final AuthenticationUserDetails authenticationUser = new AuthenticationUserDetails(
				user.getId(), user.getEmail(), 
				user.getPassword(), roles);
		
		authenticationUser.setFullName(user.getFullName());
		
		// TODO: PhatPQ => Initialize authentication user properties
		authenticationUser.setAvatar(null);
		
		return authenticationUser;
	}
}
