package fr.be.your.self.service;

import fr.be.your.self.model.User;

public interface UserService extends BaseService<User> {
	
	public boolean existsEmail(String email);
			
	public User saveOrUpdate(User user);

}
