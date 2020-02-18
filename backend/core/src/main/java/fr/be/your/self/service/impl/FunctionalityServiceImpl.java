package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.be.your.self.model.Functionality;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.FunctionalityRepository;
import fr.be.your.self.service.FunctionalityService;

@Service
public class FunctionalityServiceImpl extends BaseServiceImpl<Functionality> implements FunctionalityService {
	@Autowired
	private FunctionalityRepository repository;

	@Override
	public long count(String text) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<Functionality> findAll() {
		return this.repository.findAll();
	}

	@Override
	public Page<Functionality> getPaginatedUsers(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Functionality> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BaseRepository<Functionality> getRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Iterable<Functionality> getList(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Page<Functionality> getListByPage(String text, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
}
