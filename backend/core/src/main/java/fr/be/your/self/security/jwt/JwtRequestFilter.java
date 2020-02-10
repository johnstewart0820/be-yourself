package fr.be.your.self.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.be.your.self.common.AuthenticationStrategy;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtRequestFilter extends OncePerRequestFilter {

	private final UserDetailsService jwtUserDetailsService;

	private final JwtToken jwtTokenUtil;

	public JwtRequestFilter(UserDetailsService jwtUserDetailsService, JwtToken jwtTokenUtil) {
		this.jwtUserDetailsService = jwtUserDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader == null) {
			this.logger.warn("JWT Token does not begin with JWT");
			
			chain.doFilter(request, response);
			return;
		}
		
		if (requestTokenHeader.startsWith(AuthenticationStrategy.JWT.getValue() + " ")) {
			final String jwtToken = requestTokenHeader.substring(AuthenticationStrategy.JWT.getValue().length() + 1);
			try {
				final String username = this.jwtTokenUtil.getUsernameFromToken(jwtToken);
				
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					final UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
	
					if (this.jwtTokenUtil.validateToken(jwtToken, userDetails)) {
						final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			} catch (IllegalArgumentException e) {
				this.logger.error("Unable to get JWT Token", e);
			} catch (ExpiredJwtException e) {
				this.logger.error("JWT Token has expired", e);
			} catch (Exception e) {
				this.logger.error("Unable to parse JWT Token", e);
			}
			
			chain.doFilter(request, response);
			return;
		}
		
		/*
		if (requestTokenHeader.startsWith(AuthenticationStrategy.BEARER.getValue() + " ")) {
			String oauthToken = requestTokenHeader.substring(AuthenticationStrategy.BEARER.getValue().length() + 1);
		}
		*/
		
		this.logger.warn("Invalid JWT Token");
		chain.doFilter(request, response);
	}
}
