package fr.be.your.self.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.be.your.self.model.SessionGroup;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.SubscriptionTypeRepository;
import fr.be.your.self.service.SessionGroupService;
import fr.be.your.self.service.SubscriptionTypeService;
import fr.be.your.self.util.StringUtils;

@Service
public class SubscriptionTypeServiceImpl extends BaseServiceImpl<SubscriptionType> implements SubscriptionTypeService {

	@Autowired
	SubscriptionTypeRepository subtypeRepo;
	
	
	@Override
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.subtypeRepo.count();
		}
		
		return this.subtypeRepo.countByNameContainsIgnoreCase(text);
	}

	@Override
	protected BaseRepository<SubscriptionType> getRepository() {
		return subtypeRepo;
	}

	@Override
	protected Iterable<SubscriptionType> getList(String text) {
		return StringUtils.isNullOrSpace(text) 
				? this.subtypeRepo.findAll()
				: this.subtypeRepo.findAllByNameContainsIgnoreCase(text);
	}

	@Override
	protected Page<SubscriptionType> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.subtypeRepo.findAll(pageable)
				: this.subtypeRepo.findAllByNameContainsIgnoreCase(text, pageable);
	}

	@Override
	public SubscriptionType saveOrUpdate(SubscriptionType subtype) {
		return this.subtypeRepo.save(subtype);
	}

}
