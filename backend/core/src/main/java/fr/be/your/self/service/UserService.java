package fr.be.your.self.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;

public interface UserService extends BaseService<User, Integer> {
	
	public boolean existsEmail(String email);
	
	public User getByEmail(String email);
	
	public User saveOrUpdate(User user);
	
	public User getByActivateCode(String activateCode);
	
	public PageableResponse<User> searchByName(String firstNameOrLastName, Pageable pageable, Sort sort);
	
	public PageableResponse<User> searchProfessionalByName(String firstNameOrLastName, Pageable pageable, Sort sort);
	
	public List<User> getActivateProfessionals(Collection<Integer> ids, Sort sort);
	
	Page<User> findAllByUserType(String userType, Pageable pageable);
    Page<User> findAllByStatus(int status, Pageable pageable);
    Page<User> findAllByEmailOrFirstNameOrLastName(String email, String firstName, String lastName, Pageable pageable);

	Iterable<User> findAllById(List<Integer> ids);

	public List<UserCSV> extractUserCsv(List<Integer> ids);

	public boolean activateUser(Integer userId);
}
