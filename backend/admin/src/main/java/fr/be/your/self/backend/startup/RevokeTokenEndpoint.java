package fr.be.your.self.backend.startup;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.be.your.self.backend.setting.Constants;

@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Resource(name = "tokenServices")
    private ConsumerTokenServices tokenServices;

    @RequestMapping(method = RequestMethod.DELETE, value = Constants.PATH.AUTHENTICATION_PREFIX + Constants.PATH.AUTHENTICATION.TOKEN)
    @ResponseBody
    public void revokeToken(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        
        if (authorization != null && authorization.contains("Bearer")) {
            final String tokenId = authorization.substring("Bearer".length() + 1);
            this.tokenServices.revokeToken(tokenId);
        }
    }

}
