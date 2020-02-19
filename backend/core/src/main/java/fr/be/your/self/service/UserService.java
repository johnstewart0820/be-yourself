package fr.be.your.self.service;

import fr.be.your.self.model.User;

public interface UserService extends BaseService<User> {
	
	public boolean existsEmail(String email);
	
	public User getByEmail(String email);
	
	public User saveOrUpdate(User user);
	
	public User getByActivateCode(String activateCode);

	public boolean activateUser(Integer userId);
}
