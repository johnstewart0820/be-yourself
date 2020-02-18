package fr.be.your.self.service;

import java.util.List;

import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;

public interface UserService extends BaseService<User> {
	
	public boolean existsEmail(String email);
			
	public User saveOrUpdate(User user);
	
	public List<UserCSV> extractUserCsv();

}
