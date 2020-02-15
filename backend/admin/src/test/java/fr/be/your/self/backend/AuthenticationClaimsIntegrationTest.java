package fr.be.your.self.backend;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(
  classes = AdminApplication.class, 
  webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationClaimsIntegrationTest {
	
	@Autowired
    private JwtTokenStore tokenStore;
 
    @Test
    public void whenTokenDoesNotContainIssuer_thenSuccess() {
        String tokenValue = obtainAccessToken("be-your-self.fr", "admin@gmail.com", "123456");
        System.out.println(tokenValue);
        
        OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
  
        assertTrue(details.containsKey("organization"));
    }
 
    private String obtainAccessToken(String clientId, String username, String password) {
  
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("client_secret", "secret");
        params.put("username", username);
        params.put("password", password);
        
        Response response = RestAssured.given()
        		//.auth()//.preemptive().basic(clientId, "secret")
        		.and().with().params(params).when()
          .post("http://localhost:2020/oauth/token");
        
        return response.jsonPath().getString("access_token");
    }
    
}
