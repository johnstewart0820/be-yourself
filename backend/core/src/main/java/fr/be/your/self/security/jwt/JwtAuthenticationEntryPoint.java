package fr.be.your.self.security.jwt;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.StatusResponse;

public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	@Override
	public void commence(
			HttpServletRequest request, 
			HttpServletResponse response, 
			AuthenticationException authException) throws IOException {
		
		String requestURL = request.getRequestURI().toLowerCase();
		if (requestURL.startsWith("/api/")) {
			final StatusResponse result = new StatusResponse(false);
			result.setCode(StatusCode.UNAUTHORIZED.getValue());
			result.setMessage("Unauthorized");
			
			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writeValueAsString(result);
			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write(json);
			response.flushBuffer();
			
			return;
		}
		
		response.sendRedirect(request.getContextPath() + "/login");
	}
}