package fr.be.your.self.security.oauth2;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class DefaultLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	public DefaultLoginSuccessHandler() {
	    super();
	    
	    //setUseReferer(true);
    }

	public DefaultLoginSuccessHandler(String defaultTargetUrl) {
	    super(defaultTargetUrl);
	    
	    //setUseReferer(true);
    }

}
