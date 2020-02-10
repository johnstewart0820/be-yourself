package fr.be.your.self.backend.config.root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import fr.be.your.self.backend.setting.DataSetting;

@Configuration
@ComponentScan(basePackages = {
		"fr.be.your.self.backend.exception",
        "fr.be.your.self.service",
        "fr.be.your.self.util" })
public class ApplicationConfig {
	
	@Value("${data.default.page.size:10}")
	private int defaultPageSize;
	
	@Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource ret = new ReloadableResourceBundleMessageSource();
        ret.setBasename("classpath:language");
        ret.setDefaultEncoding("UTF-8");
        return ret;
    }
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public DataSetting dataSetting() {
		final DataSetting setting = new DataSetting();
		setting.setDefaultPageSize(this.defaultPageSize);
		
		return setting;
	}
}
