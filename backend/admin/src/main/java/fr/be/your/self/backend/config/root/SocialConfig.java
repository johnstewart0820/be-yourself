package fr.be.your.self.backend.config.root;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Configuration
public class SocialConfig {
	
	@Value("${google.client.id}")
	private String googleClientId;
	
	@Bean
	public GoogleIdTokenVerifier googleIdTokenVerifier() {
		final HttpTransport transport = new NetHttpTransport();
		final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Collections.singletonList(this.googleClientId))
				.build();
	}
}
