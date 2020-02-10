package fr.be.your.self.security.jwt;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.be.your.self.model.User;
import fr.be.your.self.repository.UserRepository;

public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = this.userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		
		final JwtUserDetails jwtUser = new JwtUserDetails(
				user.getId(), user.getEmail(), 
				user.getPassword(), new ArrayList<>());
		
		jwtUser.setFullname(user.getFullname());
		
		// TODO
		jwtUser.setAvatar(null);
		
		return jwtUser;
	}
}
