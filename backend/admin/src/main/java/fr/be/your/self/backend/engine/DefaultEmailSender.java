package fr.be.your.self.backend.engine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.util.StringUtils;

public class DefaultEmailSender implements EmailSender {
	
	@Autowired
    private MailSender mailSender;
    
    @Autowired
    private SimpleMailMessage defaultMessage;
    
    private String activateUserSubject;
    private String activateUserBody;
    
    private String forgetPasswordSubject;
    private String forgetPasswordBody;
    
    public DefaultEmailSender(String activateUserSubject, String activateUserBody, 
    		String forgetPasswordSubject, String forgetPasswordBody) {
		this.activateUserSubject = activateUserSubject;
		this.activateUserBody = activateUserBody;
		this.forgetPasswordSubject = forgetPasswordSubject;
		this.forgetPasswordBody = forgetPasswordBody;
	}
    
    @Override
    public void send(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		defaultMessage.copyTo(message);
		
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }
    
    @Override
    public boolean sendActivateUser(String email, String activateUrl, String validToken) {
    	final String realUrl = activateUrl 
    			+ (activateUrl.indexOf("?") > 0 ? "&" : "?") 
    			+ "activeCode=" + validToken;
    	
    	String mailBody = this.activateUserBody
    			.replace("[ValidCode]", validToken)
    			.replace("[ActivateUrl]", realUrl);
    	
    	if (JavaMailSender.class.isAssignableFrom(mailSender.getClass()))
    	{
    		try {
	    		JavaMailSender javaMailSender = (JavaMailSender) mailSender;
	    		
	    		MimeMessage message = javaMailSender.createMimeMessage();
	    		message.setSubject(this.activateUserSubject, "UTF-8");
				
	    		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            helper.setFrom(defaultMessage.getFrom());
	            
	            String[] bcc = defaultMessage.getBcc();
	            if (bcc != null && bcc.length > 0 && !StringUtils.isNullOrEmpty(bcc[0])) {
	            	helper.setBcc(bcc);
	            }
	            
	            String[] cc = defaultMessage.getCc();
	            if (cc != null && cc.length > 0 && !StringUtils.isNullOrEmpty(cc[0])) {
	            	helper.setCc(cc);
	            }
				
	            helper.setTo(email);
	            helper.setText(mailBody, true);
	            
	            javaMailSender.send(message);
	            return true;
    		} catch (MessagingException e) {
    			
			} catch (Exception e) {
				
			}
    	}
    	
    	try {
			SimpleMailMessage message = new SimpleMailMessage();
			defaultMessage.copyTo(message);
			
	        message.setTo(email);
	        message.setSubject(this.activateUserSubject);
	        message.setText(mailBody);
	        
	        mailSender.send(message);
	        
	        return true;
    	} catch (Exception e) {
			
		}
    	
    	return false;
    }
    
    @Override
    public boolean sendForgotPassword(String forgotPasswordUrl, String email, String validToken) {
    	final String realUrl = forgotPasswordUrl
    			+ (forgotPasswordUrl.indexOf("?") > 0 ? "&" : "?")
    			+ "validCode=" + validToken;
    	
    	final String mailBody = this.forgetPasswordBody
    			.replace("[ValidCode]", validToken)
    			.replace("[ForgotPasswordUrl]", realUrl);
    	
    	if (JavaMailSender.class.isAssignableFrom(mailSender.getClass()))
    	{
    		try {
	    		JavaMailSender javaMailSender = (JavaMailSender) mailSender;
	    		MimeMessage message = javaMailSender.createMimeMessage();
	    		message.setSubject(this.forgetPasswordSubject, "UTF-8");
				
	    		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            helper.setFrom(defaultMessage.getFrom());
	            
	            String[] bcc = defaultMessage.getBcc();
				if (bcc != null && bcc.length > 0 && !StringUtils.isNullOrEmpty(bcc[0])) {
	            	helper.setBcc(bcc);
	            }
	            
	            String[] cc = defaultMessage.getCc();
				if (cc != null && cc.length > 0 && !StringUtils.isNullOrEmpty(cc[0])) {
	            	helper.setCc(cc);
	            }
	            
	            helper.setTo(email);
	            helper.setText(mailBody, true);
	            
	            javaMailSender.send(message);
	            return true;
    		} catch (MessagingException e) {
				// e.printStackTrace();
			} catch (Exception e) {
				// e.printStackTrace();
			}
    	}
    	
    	try {
			SimpleMailMessage message = new SimpleMailMessage();
			defaultMessage.copyTo(message);
			
	        message.setTo(email);
	        message.setSubject(this.forgetPasswordSubject);
	        message.setText(mailBody);
	        
	        mailSender.send(message);
	        
	        return true;
    	} catch (Exception e) {
			// e.printStackTrace();
		}
    	
    	return false;
    }
}
