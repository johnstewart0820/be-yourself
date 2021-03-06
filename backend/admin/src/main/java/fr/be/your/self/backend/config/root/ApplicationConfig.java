package fr.be.your.self.backend.config.root;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.util.StringUtils;

@Configuration
@ComponentScan(basePackages = {
		"fr.be.your.self.backend.startup",
        "fr.be.your.self.service",
        "fr.be.your.self.util" })
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApplicationConfig {
	
	@Value("${data.default.page.size:10}")
	private int defaultPageSize;
	
	@Value("#{'${data.option.support.page.size.list}'.split(',')}")
	private Set<Integer> supportPageSizes;
	
	@Value("${data.upload.folder}")
	private String uploadFolder;
	
	@Value("${data.upload.image.max-file-size:1024}")
	private int imageMaxFileSize;
	
	@Value("${data.upload.audio.max-file-size:1024}")
	private int audioMaxFileSize;
	
	@Value("${data.upload.video.max-file-size:1024}")
	private int videoMaxFileSize;
	
	@Value("#{'${data.upload.image.extension}'.split(',')}")
	private Set<String> imageFileExtensions;
	
	@Value("#{'${data.upload.audio.extension}'.split(',')}")
	private Set<String> audioFileExtensions;
	
	@Value("#{'${data.upload.video.extension}'.split(',')}")
	private Set<String> videoFileExtensions;
	
	@Value("#{'${data.upload.image.mime.type}'.split(',')}")
	private Set<String> imageFileMimeTypes;
	
	@Value("#{'${data.upload.audio.mime.type}'.split(',')}")
	private Set<String> audioFileMimeTypes;
	
	@Value("#{'${data.upload.video.mime.type}'.split(',')}")
	private Set<String> videoFileMimeTypes;
	
	@Value("${account.auto.activate:false}")
	private boolean autoActivateNormalAccount;
	
	@Value("${account.admin.auto.activate:false}")
	private boolean autoActivateAdminAccount;
	
	@Value("${account.activate.code.length:6}")
	private int activateCodeLength;
	
	@Value("${account.temporary.password.length:6}")
	private int tempPasswordLength;
	
	@Value("${account.activate.code.timeout}")
	private long activateCodeTimeout;
	
	@Value("${auth.page.display.header:false}")
	private boolean displayHeaderOnAuthPage;
	
	@Value("${auth.page.allow.register:false}")
	private boolean allowRegisterOnAuthPage;
	
	@Value("${auth.page.allow.social:false}")
	private boolean allowSocialOnAuthPage;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
        	final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        	
        	if (authentication != null) {
        		return Optional.ofNullable(authentication.getName());
        	}
        	
        	return Optional.ofNullable("system");
        };
    }
	
	@Bean
	public DataSetting dataSetting() {
		final DataSetting setting = new DataSetting();
		setting.setDataPageSize(this.supportPageSizes, this.defaultPageSize);
		
		setting.setUploadFolder(this.uploadFolder);
		setting.setUploadFileSizes(this.imageMaxFileSize, this.audioMaxFileSize, this.videoMaxFileSize);
		setting.setUploadFileExtensions(this.imageFileExtensions, this.audioFileExtensions, this.videoFileExtensions);
		setting.setUploadMimeTypes(this.imageFileMimeTypes, this.audioFileMimeTypes, this.videoFileMimeTypes);
		setting.setAutoActivateAccount(this.autoActivateNormalAccount, this.autoActivateAdminAccount, 
				this.activateCodeLength, this.activateCodeTimeout);
		setting.setAuthenticationConfiguration(this.displayHeaderOnAuthPage, this.allowRegisterOnAuthPage, this.allowSocialOnAuthPage);
		setting.setTempPwdLength(this.tempPasswordLength);
		
		for (final String fileExt : this.imageFileExtensions) {
			final String mimeType = this.env.getProperty("setting.default.mime.type.mapping." + fileExt.toLowerCase());
			
			if (!StringUtils.isNullOrSpace(mimeType)) {
				setting.addDefaultMimeTypeMapping(fileExt, mimeType);
			}
		}
		
		for (final String fileExt : this.audioFileExtensions) {
			final String mimeType = this.env.getProperty("setting.default.mime.type.mapping." + fileExt.toLowerCase());
			
			if (!StringUtils.isNullOrSpace(mimeType)) {
				setting.addDefaultMimeTypeMapping(fileExt, mimeType);
			}
		}
		
		for (final String fileExt : this.videoFileExtensions) {
			final String mimeType = this.env.getProperty("setting.default.mime.type.mapping." + fileExt.toLowerCase());
			
			if (!StringUtils.isNullOrSpace(mimeType)) {
				setting.addDefaultMimeTypeMapping(fileExt, mimeType);
			}
		}
		
		return setting;
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
	    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
	    validatorFactory.setValidationMessageSource(this.messageSource);
	    
	    return validatorFactory;
	}
}
