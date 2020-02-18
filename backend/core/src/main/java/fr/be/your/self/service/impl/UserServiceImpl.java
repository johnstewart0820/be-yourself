package fr.be.your.self.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.UserRepository;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	protected BaseRepository<User> getRepository() {
		return this.repository;
	}

	@Override
	public boolean existsEmail(String email) {
		return this.repository.existsByEmail(email);
	}

	@Override
	protected Iterable<User> getList(String text) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll()
				: this.repository.findAllByEmailOrFirstNameOrLastName(text, text, text);
	}
	
	@Override
	public Iterable<User> findAll() {
		return this.repository.findAll();
	}

	@Override
	protected Page<User> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByEmailOrFirstNameOrLastName(text, text, text, pageable);
	}
	
	 public Page<User> getPaginatedUsers(Pageable pageable) {
	        return this.repository.findAll(pageable);
	 }
	

	@Override
	@Transactional(readOnly = true)
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.repository.count();
		}
		
		return this.repository.countByEmailOrFirstNameOrLastName(text, text, text);
	}
	
	@Override
	public User saveOrUpdate(User user) {
		return this.repository.save(user);
	}

	@Override
	public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
		return this.repository.saveAll(entities);
	}


	@Override
	public List<UserCSV> extractUserCsv() {
		Iterable<User> users= this.repository.findAll();
		List<UserCSV> returnList = new ArrayList<UserCSV>();
		for (User user: users) {
			UserCSV userCSV = new UserCSV(user);
			returnList.add(userCSV);
		}
		
		return returnList;
	}
}
