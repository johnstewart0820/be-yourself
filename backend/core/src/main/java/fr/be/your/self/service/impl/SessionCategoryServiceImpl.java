package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.model.SessionCategory;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.SessionCategoryRepository;
import fr.be.your.self.service.SessionCategoryService;
import fr.be.your.self.util.StringUtils;

@Service
public class SessionCategoryServiceImpl extends BaseServiceImpl<SessionCategory> implements SessionCategoryService {
	
	@Autowired
	private SessionCategoryRepository repository;

	@Override
	protected BaseRepository<SessionCategory> getRepository() {
		return this.repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.repository.count();
		}
		
		return this.repository.countByNameContainsIgnoreCase(text);
	}

	@Override
	protected Iterable<SessionCategory> getList(String text) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll()
				: this.repository.findAllByNameContainsIgnoreCase(text);
	}

	@Override
	protected Page<SessionCategory> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByNameContainsIgnoreCase(text, pageable);
	}
}
