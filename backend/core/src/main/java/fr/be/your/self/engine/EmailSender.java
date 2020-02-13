package fr.be.your.self.engine;

public interface EmailSender {
	
	public void send(String to, String subject, String body);
	
	public boolean sendActivateUser(String email, String activeUrl, String validToken);
	
	public boolean sendForgotPassword(String email, String forgetPasswordUrl, String validToken);
}
