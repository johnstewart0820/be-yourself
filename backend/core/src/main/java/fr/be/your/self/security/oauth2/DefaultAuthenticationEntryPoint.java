package fr.be.your.self.security.oauth2;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.be.your.self.common.ErrorStatusCode;
import fr.be.your.self.dto.StatusResponse;
import fr.be.your.self.util.StringUtils;

public class DefaultAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
	
	private final String apiPathPrefix;
	
	public DefaultAuthenticationEntryPoint(String loginFormUrl, String apiPathPrefix) {
	    super(loginFormUrl);
	    
	    this.apiPathPrefix = apiPathPrefix == null ? null : apiPathPrefix.toLowerCase();
    }

	@Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
		final String loginUrl = super.determineUrlToUseForThisRequest(request, response, exception);
	    
	    final Map<String, String[]> params = request.getParameterMap();
	    if (params.containsKey("client_id"))
	    {
	    	final String clientId = request.getParameter("client_id");
		    request.getSession().setAttribute("client_id", clientId);
	    }
	    
	    return loginUrl;
    }
	
	@Override
	public void commence(
			HttpServletRequest request, 
			HttpServletResponse response, 
			AuthenticationException authException) throws IOException, ServletException {
		
		if (!StringUtils.isNullOrSpace(this.apiPathPrefix)) {
			final String contextPath = request.getContextPath().toLowerCase();
			final String apiPath = (contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath) + this.apiPathPrefix;
			final String requestURL = request.getRequestURI().toLowerCase();
			
			if (requestURL.startsWith(apiPath)) {
				final StatusResponse result = new StatusResponse(false);
				result.setCode(ErrorStatusCode.UNAUTHORIZED.getValue());
				result.setMessage("Unauthorized");
				
				final ObjectMapper mapper = new ObjectMapper();
				final String json = mapper.writeValueAsString(result);
				
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write(json);
				response.flushBuffer();
				
				return;
			}			
		}
		
		super.commence(request, response, authException);
	}
}
