package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.model.BusinessCode;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.BusinessCodeRepository;
import fr.be.your.self.service.BusinessCodeService;
import fr.be.your.self.util.StringUtils;

@Service
public class BusinessCodeServiceImpl extends BaseServiceImpl<BusinessCode, String> implements BusinessCodeService {
	
	@Autowired
	private BusinessCodeRepository repository;

	@Override
	protected BaseRepository<BusinessCode, String> getRepository() {
		return this.repository;
	}
	
	@Override
	public String getDefaultSort() {
		return "name|asc";
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
	protected Iterable<BusinessCode> getList(String text, Sort sort) {
		return StringUtils.isNullOrSpace(text)
				? this.repository.findAll(sort) 
				: this.repository.findAllByNameContainsIgnoreCase(text, sort);
	}

	@Override
	protected Page<BusinessCode> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByNameContainsIgnoreCase(text, pageable);
	}
}
