package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.model.Session;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.SessionRepository;
import fr.be.your.self.service.SessionService;
import fr.be.your.self.util.StringUtils;

@Service
public class SessionServiceImpl extends BaseServiceImpl<Session> implements SessionService {
	
	@Autowired
	private SessionRepository repository;

	@Override
	protected BaseRepository<Session> getRepository() {
		return this.repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.repository.count();
		}
		
		return this.repository.countByTitleContainsIgnoreCase(text);
	}

	@Override
	protected Iterable<Session> getList(String text) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll()
				: this.repository.findAllByTitleContainsIgnoreCase(text);
	}

	@Override
	protected Page<Session> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByTitleContainsIgnoreCase(text, pageable);
	}
}
