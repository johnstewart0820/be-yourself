package fr.be.your.self.backend;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
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
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.repository.FunctionalityRepository;
import fr.be.your.self.repository.PermissionRepository;
import fr.be.your.self.repository.UserRepository;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
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
	
	private Permission updatePermission(User adminUser, String path, String name, UserPermission userPermission) {
		final Optional<Functionality> optionalFunctionality = this.functionalityRepository.findByPath(path);
		Functionality functionality = optionalFunctionality.isPresent() ? optionalFunctionality.get() : null;
		if (functionality == null) {
			functionality = new Functionality();
			functionality.setPath(path);
			functionality.setName(name);
			
			functionality = this.functionalityRepository.save(functionality);
		}
		
		final Optional<Permission> optionalPermission = this.permissionRepository.findByUserIdAndFunctionalityId(adminUser.getId(), functionality.getId());
		Permission permission = optionalPermission.isPresent() ? optionalPermission.get() : null;
		if (permission == null) {
			permission = new Permission();
			permission.setUser(adminUser);
			permission.setFunctionality(functionality);
			permission.setUserPermission(userPermission.getValue());
			
			permission = this.permissionRepository.save(permission);
		} else if (permission.getUserPermission() != userPermission.getValue()) {
			permission.setUserPermission(userPermission.getValue());
			
			permission = this.permissionRepository.save(permission);
		}
		
		return permission;
	}
	
	@Override
	public void run(String... args) throws Exception {
		final String email = "admin@gmail.com";
		
		try {
			User adminUser = userRepository.findByEmail(email);
			
			if (adminUser == null) {
				final String password = this.passwordEncoder.encode("123456");
				
				adminUser = new User(email, password, UserType.ADMIN.getValue(), "Administrator", "");
				adminUser.setStatus(UserStatus.ACTIVE.getValue());
				
				adminUser = userRepository.save(adminUser);
			} else if (!UserType.ADMIN.getValue().equalsIgnoreCase(adminUser.getUserType())) {
				adminUser.setUserType(UserType.ADMIN.getValue());
				
				adminUser = userRepository.save(adminUser);
			}
			
			// User management
			{
				final String path = "/user";
				final String name = "User Management";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Subscription type management
			{
				final String path = "/subtype";
				final String name = "Subscription Type Management";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Subscription management
			{
				final String path = "/subscription";
				final String name = "Subscription Management";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Session group management
			{
				final String path = "/session-category";
				final String name = "Session Category";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Session management
			{
				final String path = "/session";
				final String name = "Session";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Edit Account Type
			{
				final String path = UserConstants.EDIT_ACCOUNT_TYPE_PATH;
				final String name = "Edit Account Type";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
			}
			
			// Edit Permissions
			{
				final String path = UserConstants.EDIT_PERMISSIONS_PATH;
				final String name = "Edit Permissions";
				
				this.updatePermission(adminUser, path, name, UserPermission.WRITE);
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
