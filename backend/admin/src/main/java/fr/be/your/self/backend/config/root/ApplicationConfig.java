package fr.be.your.self.backend.config.root;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import fr.be.your.self.backend.setting.DataSetting;

@Configuration
@ComponentScan(basePackages = {
		"fr.be.your.self.backend.startup",
        "fr.be.your.self.service",
        "fr.be.your.self.util" })
public class ApplicationConfig {
	
	@Value("${data.default.page.size:10}")
	private int defaultPageSize;
	
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
	
	@Value("#{'${data.upload.image.media.type}'.split(',')}")
	private Set<String> imageFileMediaTypes;
	
	@Value("#{'${data.upload.audio.media.type}'.split(',')}")
	private Set<String> audioFileMediaTypes;
	
	@Value("#{'${data.upload.video.media.type}'.split(',')}")
	private Set<String> videoFileMediaTypes;
	
	@Autowired
	private MessageSource messageSource;
	
	@Bean
	public DataSetting dataSetting() {
		final DataSetting setting = new DataSetting();
		setting.setDefaultPageSize(this.defaultPageSize);
		
		setting.setUploadFolder(this.uploadFolder);
		setting.setUploadFileSizes(this.imageMaxFileSize, this.audioMaxFileSize, this.videoMaxFileSize);
		setting.setUploadFileExtensions(this.imageFileExtensions, this.audioFileExtensions, this.videoFileExtensions);
		setting.setUploadMediaTypes(this.imageFileMediaTypes, this.audioFileMediaTypes, this.videoFileMediaTypes);
		
		return setting;
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
	    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
	    validatorFactory.setValidationMessageSource(this.messageSource);
	    
	    return validatorFactory;
	}
}
