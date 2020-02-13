package fr.be.your.self.security.oauth2;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

public class InMemoryCsrfTokenRepository implements CsrfTokenRepository {
	
	public static final String CSRF_PARAMETER_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    
	private final ConcurrentHashMap<String, CsrfToken> csrfTokenStore = new ConcurrentHashMap<String, CsrfToken>();
	
	@Override
    public CsrfToken generateToken(HttpServletRequest request) {
		return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, createNewToken());
    }

	@Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
		final String key = getKey(request);
        if (key == null) {
            return;
        }
        
        if (token == null) {
        	this.csrfTokenStore.remove(key);
        } else {
        	this.csrfTokenStore.put(key, token);
        }
    }

	@Override
    public CsrfToken loadToken(HttpServletRequest request) {
		final String key = getKey(request);
        if (key != null && csrfTokenStore.containsKey(key)) {
        	return this.csrfTokenStore.get(key);
        }
        
        return null;
    }
	
	private String getKey(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}
