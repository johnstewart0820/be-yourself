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
import fr.be.your.self.repository.FunctionalityRepository;
import fr.be.your.self.repository.PermissionRepository;
import fr.be.your.self.repository.UserRepository;
import fr.be.your.self.model.UserConstants;

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
	
	@Override
	public void run(String... args) throws Exception {
		final String email = "admin@gmail.com";
		
		try {
			final User currentAdminUser = userRepository.findByEmail(email);
			
			if (currentAdminUser == null) {
				String password = this.passwordEncoder.encode("123456");
				
				User adminUser = new User(email, password, UserType.SUPER_ADMIN.getValue(), "Administrator", "");
				adminUser.setStatus(UserStatus.ACTIVE.getValue());
				User savedUser = userRepository.save(adminUser);
				
				Functionality userManagementFunc = new Functionality();
				userManagementFunc.setPath("/user");
				userManagementFunc.setName("User Management");
				Functionality savedAdminUserFunc = this.functionalityRepository.save(userManagementFunc);
				
				Functionality sessionGroupFunc = new Functionality();
				sessionGroupFunc.setPath("/session-group");
				sessionGroupFunc.setName("Session Group");
				Functionality savedSessionGroupFunc = this.functionalityRepository.save(sessionGroupFunc);
				
				Functionality sessionFunc = new Functionality();
				sessionFunc.setPath("/session");
				sessionFunc.setName("Session");
				Functionality savedSessionFunc = this.functionalityRepository.save(sessionFunc);
				
				
				Functionality editAccountTypeFunc = new Functionality();
				editAccountTypeFunc.setPath(UserConstants.EDIT_ACCOUNT_TYPE_PATH);
				editAccountTypeFunc.setName("Edit Account Type");
				Functionality savedEditAccountTypeFunc = this.functionalityRepository.save(editAccountTypeFunc);
				
				
				Functionality editPermissionsFunc = new Functionality();
				editPermissionsFunc.setPath(UserConstants.EDIT_PERMISSIONS_PATH);
				editPermissionsFunc.setName("Edit Permissions");
				Functionality savedEditPermissionsFunc = this.functionalityRepository.save(editPermissionsFunc);
				
				
				Permission adminUserPermission = new Permission();
				adminUserPermission.setUser(savedUser);
				adminUserPermission.setFunctionality(savedAdminUserFunc);
				adminUserPermission.setUserPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(adminUserPermission);
				
				Permission editAccountTypePermission = new Permission();
				editAccountTypePermission.setUser(savedUser);
				editAccountTypePermission.setFunctionality(savedEditAccountTypeFunc);
				editAccountTypePermission.setUserPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(editAccountTypePermission);
				
				Permission editPermissions = new Permission();
				editPermissions.setUser(savedUser);
				editPermissions.setFunctionality(savedEditPermissionsFunc);
				editPermissions.setUserPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(editPermissions);
				Permission sessionGroupPermission = new Permission();
				sessionGroupPermission.setUser(savedUser);
				sessionGroupPermission.setFunctionality(savedSessionGroupFunc);
				sessionGroupPermission.setUserPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(sessionGroupPermission);
				
				Permission sessionPermission = new Permission();
				sessionPermission.setUser(savedUser);
				sessionPermission.setFunctionality(savedSessionFunc);
				sessionPermission.setUserPermission(UserPermission.WRITE.getValue());
				this.permissionRepository.save(sessionPermission);
				
			} else {
				
				// session-group permission
				final Optional<Functionality> sessionGroupFunc = this.functionalityRepository.findByPath("/session-group");
				if (sessionGroupFunc.isPresent()) {
					final Optional<Permission> sessionGroupPermission = this.permissionRepository.findByUserIdAndFunctionalityId(currentAdminUser.getId(), sessionGroupFunc.get().getId());
					
					if (sessionGroupPermission.isPresent()) {
						final Permission permission = sessionGroupPermission.get();
						permission.setUserPermission(UserPermission.WRITE.getValue());
						
						this.permissionRepository.save(permission);
					}
				}
				
				
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
