package fr.be.your.self.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import fr.be.your.self.backend.config.rest.RestConfig;
import fr.be.your.self.backend.config.web.WebMvcConfig;
import fr.be.your.self.model.User;
import fr.be.your.self.repository.UserRepository;

@SpringBootApplication
@ComponentScan(basePackages = { "fr.be.your.self.backend.config.root" })
@EntityScan(basePackages = { "fr.be.your.self.model" })
@EnableJpaRepositories(basePackages = { "fr.be.your.self.repository" })
public class AdminApplication implements CommandLineRunner {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		final String email = "admin@gmail.com";
		
		try {
			if (!userRepository.existsByEmail(email)) {
				String password = this.passwordEncoder.encode("123456");
				
				User adminUser = new User(email, password, "Administrator");
				userRepository.save(adminUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}
	
	@Bean
	public ServletRegistrationBean<DispatcherServlet> apiServletRegistration() {
		AnnotationConfigWebApplicationContext apiServletContext = new AnnotationConfigWebApplicationContext();
        apiServletContext.register(RestConfig.class);
        apiServletContext.scan("fr.be.your.self.backend.config.rest");
        
        DispatcherServlet apiDispatcherServlet = new DispatcherServlet(apiServletContext);
        
		ServletRegistrationBean<DispatcherServlet> apiServletRegistration = new ServletRegistrationBean<>();
		apiServletRegistration.setName("apiDispatcherServlet");
		apiServletRegistration.setServlet(apiDispatcherServlet);
		apiServletRegistration.addUrlMappings("/api/*");
        apiServletRegistration.setLoadOnStartup(1);
        apiServletRegistration.setAsyncSupported(true);
        
        return apiServletRegistration;
	}
	
	@Bean
	public ServletRegistrationBean<DispatcherServlet> webServletRegistration() {
		AnnotationConfigWebApplicationContext webServletContext = new AnnotationConfigWebApplicationContext();
		webServletContext.register(WebMvcConfig.class);
		webServletContext.scan("fr.be.your.self.backend.config.web");
        
        DispatcherServlet webDispatcherServlet = new DispatcherServlet(webServletContext);
        
		ServletRegistrationBean<DispatcherServlet> webServletRegistration = new ServletRegistrationBean<>();
		webServletRegistration.setName("webDispatcherServlet");
		webServletRegistration.setServlet(webDispatcherServlet);
		webServletRegistration.addUrlMappings("/");
        webServletRegistration.setLoadOnStartup(2);
        webServletRegistration.setAsyncSupported(true);
        
        return webServletRegistration;
	}
}
