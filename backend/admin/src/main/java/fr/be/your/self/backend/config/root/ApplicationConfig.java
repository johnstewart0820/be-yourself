package fr.be.your.self.backend.config.root;

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
	
	@Autowired
	private MessageSource messageSource;
	
	@Bean
	public DataSetting dataSetting() {
		final DataSetting setting = new DataSetting();
		setting.setDefaultPageSize(this.defaultPageSize);
		setting.setUploadFolder(this.uploadFolder);
		
		return setting;
	}
	
	@Bean
	public LocalValidatorFactoryBean getValidator() {
	    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
	    validatorFactory.setValidationMessageSource(this.messageSource);
	    
	    return validatorFactory;
	}
}
