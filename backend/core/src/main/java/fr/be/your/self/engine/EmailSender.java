package fr.be.your.self.engine;

public interface EmailSender {
	
	public boolean send(String to, String subject, String body);
	
	public boolean sendActivateUser(String email, String activeUrl, String validToken);
	
	public boolean sendForgotPassword(String email, String forgetPasswordUrl, String validToken);
	
	public boolean sendTemporaryPassword(String email, String tempPassword);

}
