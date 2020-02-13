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

import fr.be.your.self.backend.engine.DefaultEmailSender;
import fr.be.your.self.engine.EmailSender;

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
	
	@Value("${mail.activate.user.subject}")
	private String activateUserSubject;
	
	@Value("${mail.activate.user.body}")
	private String activateUserBody;
	
	@Value("${mail.forget.password.subject}")
	private String forgetPasswordSubject;
	
	@Value("${mail.forget.password.body}")
	private String forgetPasswordBody;
	
	@Bean 
	public MailSender javaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setProtocol(JavaMailSenderImpl.DEFAULT_PROTOCOL);
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		mailSender.setUsername(smtpUsername);
		mailSender.setPassword(smtpPassword);
		
		Properties properties = mailSender.getJavaMailProperties();
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
	
	@Bean
	public EmailSender emailSender() {
		return new DefaultEmailSender(
				this.activateUserSubject, this.activateUserBody, 
				this.forgetPasswordSubject, this.forgetPasswordBody);
	}
}
