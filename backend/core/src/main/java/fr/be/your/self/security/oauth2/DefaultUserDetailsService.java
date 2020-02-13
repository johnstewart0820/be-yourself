package fr.be.your.self.security.oauth2;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.be.your.self.model.User;
import fr.be.your.self.repository.UserRepository;

public class DefaultUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = this.userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		final List<GrantedAuthority> roles = Arrays.asList(
				new SimpleGrantedAuthority("ROLE_ADMIN"), 
				new SimpleGrantedAuthority("ROLE_USER"));
		
		final AuthenticationUserDetails authenticationUser = new AuthenticationUserDetails(
				user.getId(), user.getEmail(), 
				user.getPassword(), roles);
		
		authenticationUser.setFullname(user.getFullname());
		
		// TODO
		authenticationUser.setAvatar(null);
		
		return authenticationUser;
	}
}
