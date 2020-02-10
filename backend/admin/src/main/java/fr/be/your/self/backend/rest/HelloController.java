package fr.be.your.self.backend.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.be.your.self.security.jwt.JwtUserDetails;

@RestController(value = "DemoController")
@RequestMapping(value = "/test")
public class HelloController {
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> demo() {
		Map<String, Object> result = new HashMap<>();
		result.put("status", true);
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			result.put("username", authentication.getName());
			
			final Object principal = authentication.getPrincipal();
			if (principal instanceof JwtUserDetails) {
				final JwtUserDetails userDetails = (JwtUserDetails) principal;
				
				result.put("userId", userDetails.getUserId());
				result.put("fullName", userDetails.getFullname());
				result.put("avatar", userDetails.getAvatar());
			}
			
			result.put("principal", principal);
		}
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/demo1", method = RequestMethod.GET)
	public ResponseEntity<?> demo1() {
		Map<String, Object> result = new HashMap<>();
		result.put("status", true);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
