package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.model.User;
import fr.be.your.self.repository.UserRepository;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	protected PagingAndSortingRepository<User, Integer> getRepository() {
		return this.repository;
	}

	@Override
	public boolean existsEmail(String email) {
		return this.repository.existsByEmail(email);
	}

	@Override
	protected Iterable<User> findAll(String text) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll()
				: this.repository.findAllByEmailOrFullname(text, text);
	}
	
	@Override
	public Iterable<User> findAll() {
		return this.repository.findAll();
	}

	@Override
	protected Page<User> findPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByEmailOrFullname(text, text, pageable);
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
		
		return this.repository.countByEmailOrFullname(text, text);
	}
	
	
	@Override
	public void saveOrUpdate(User user) {
		this.repository.save(user); //TODO TVA check this do both save and update
	}

	@Override
	public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
		return this.repository.saveAll(entities);
	}


}
