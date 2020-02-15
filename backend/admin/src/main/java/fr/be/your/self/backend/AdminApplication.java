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
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;
import fr.be.your.self.repository.FunctionalityRepository;
import fr.be.your.self.repository.PermissionRepository;
import fr.be.your.self.repository.UserRepository;

@SpringBootApplication
@ComponentScan(basePackages = { "fr.be.your.self.backend.config.root" })
@EntityScan(basePackages = { "fr.be.your.self.model" })
@EnableJpaRepositories(basePackages = { "fr.be.your.self.repository" })
public class AdminApplication implements CommandLineRunner {
	
	private static final String WEB_URL_PATTERN = "/";
	private static final String API_URL_PATTERN = Constants.PATH.API_PREFIX + "/*";
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Override
	public void run(String... args) throws Exception {
		final String email = "admin@gmail.com";
		
		try {
			if (!userRepository.existsByEmail(email)) {
				String password = this.passwordEncoder.encode("123456");
				
				User adminUser = new User(email, password, UserType.SUPER_ADMIN.getValue(), "Administrator", "");
				adminUser.setStatus(UserStatus.ACTIVE.getValue());
				User savedUser = userRepository.save(adminUser);
				
				Functionality adminUserFunc = new Functionality();
				adminUserFunc.setPath("/admin-user");
				adminUserFunc.setName("Admin User");
				Functionality savedAdminUserFunc = this.functionalityRepository.save(adminUserFunc);
				
				Functionality tempFunc = new Functionality();
				tempFunc.setPath("/temp");
				tempFunc.setName("Temp function");
				Functionality savedTempFunc = this.functionalityRepository.save(tempFunc);
				
				Permission adminUserPermission = new Permission();
				adminUserPermission.setUserId(savedUser.getId());
				adminUserPermission.setFunctionality(savedAdminUserFunc);
				adminUserPermission.setPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(adminUserPermission);
			}
		} catch (Exception e) {
			//e.printStackTrace();
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
		apiServletRegistration.addUrlMappings(API_URL_PATTERN);
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
		webServletRegistration.addUrlMappings(WEB_URL_PATTERN);
        webServletRegistration.setLoadOnStartup(2);
        webServletRegistration.setAsyncSupported(true);
        
        return webServletRegistration;
	}
}
