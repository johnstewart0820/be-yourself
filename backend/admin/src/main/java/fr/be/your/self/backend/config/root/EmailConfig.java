package fr.be.your.self.backend.config.root;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

	@Value("${mail.smtp.host}")
	private String smtpHost;
	
	@Value("${mail.smtp.port}")
	private int smtpPort;
	
	@Value("${mail.smtp.username}")
	private String smtpUsername;
	
	@Value("${mail.smtp.password}")
	private String smtpPassword;
	
	@Value("${mail.smtp.address.from}")
	private String fromAddress;
	
	@Value("#{'${mail.smtp.address.cc}'.split(',')}")
	private Set<String> ccAddresses;
	
	@Value("#{'${mail.smtp.address.bcc}'.split(',')}")
	private Set<String> bccAddresses;
	
	@Value("#{'${mail.smtp.properties}'.split(';')}")
	private Set<String> smtpProperties;
	
	@Bean 
	public MailSender javaMailSender() {
		Properties properties = new Properties();
		
		for (String smtpProperty : smtpProperties) {
			String[] itemProperties = smtpProperty.split(":");
			
			if (itemProperties.length == 2) {
				String[] itemValues = itemProperties[1].split(",");
				if (itemValues.length == 1) {
					properties.put(itemProperties[0], itemProperties[1]);
				} else {
					List<String> propertyValues = Arrays.asList(itemValues);
					properties.put(itemProperties[0], propertyValues);
				}
			}
		}
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setProtocol(JavaMailSenderImpl.DEFAULT_PROTOCOL);
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		mailSender.setUsername(smtpUsername);
		mailSender.setPassword(smtpPassword);
		mailSender.setJavaMailProperties(properties);
		
		return mailSender;
	}
	
	@Bean
	public SimpleMailMessage defaultMailMessage() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		
		if (ccAddresses != null && !ccAddresses.isEmpty()) {
			message.setCc(ccAddresses.toArray(new String[ccAddresses.size()]));
		}
		
		if (bccAddresses != null && !bccAddresses.isEmpty()) {
			message.setBcc(bccAddresses.toArray(new String[bccAddresses.size()]));
		}
		
		return message;
	}
}
