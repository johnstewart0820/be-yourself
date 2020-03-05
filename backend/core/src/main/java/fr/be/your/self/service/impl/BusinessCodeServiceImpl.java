package fr.be.your.self.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.be.your.self.common.BusinessCodeStatus;
import fr.be.your.self.common.ErrorStatusCode;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.BusinessCode;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.BusinessCodeRepository;
import fr.be.your.self.repository.SubscriptionRepository;
import fr.be.your.self.service.BusinessCodeService;
import fr.be.your.self.util.StringUtils;

@Service
public class BusinessCodeServiceImpl extends BaseServiceImpl<BusinessCode, Integer> implements BusinessCodeService {
	
	@Autowired
	private BusinessCodeRepository repository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Override
	protected BaseRepository<BusinessCode, Integer> getRepository() {
		return this.repository;
	}
	
	@Override
	public String getDefaultSort() {
		return "name|asc";
	}
	
	@Override
	protected void handleAfterUpdate(BusinessCode domain) throws RuntimeException {
		this.subscriptionRepository.updateFromBusinessCode(domain.getId(), 
				domain.getStartDate(), domain.getEndDate(), 
				domain.getStartDate(), domain.getEndDate(), 
				domain.getPricePerUser(), domain.getStatus() == BusinessCodeStatus.ACTIVE.getValue());
		
		super.handleAfterUpdate(domain);
	}

	@Override
	protected void handleBeforeDelete(Integer id) throws RuntimeException {
		final Boolean existsSubscription = this.subscriptionRepository.existsByBusinessCodeId(id);
		if (existsSubscription != null && existsSubscription.booleanValue()) {
			throw new BusinessException(ErrorStatusCode.SUBSCRIPTION_EXISTED);
		}
		
		super.handleBeforeDelete(id);
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
	@Transactional(readOnly = true)
	protected Iterable<BusinessCode> getList(String text, Sort sort) {
		return StringUtils.isNullOrSpace(text)
				? this.repository.findAll(sort) 
				: this.repository.findAllByNameContainsIgnoreCase(text, sort);
	}

	@Override
	@Transactional(readOnly = true)
	protected Page<BusinessCode> getListByPage(String text, Pageable pageable) {
		return StringUtils.isNullOrSpace(text) 
				? this.repository.findAll(pageable)
				: this.repository.findAllByNameContainsIgnoreCase(text, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsName(String name) {
		final Boolean existed = this.repository.existsByNameIgnoreCase(name);
		return existed == null ? false : existed.booleanValue();
	}

	@Override
	@Transactional(readOnly = true)
	public long count(String text, Collection<Integer> types) {
		if (types == null || types.isEmpty()) {
			return this.count(text);
		}
		
		if (StringUtils.isNullOrSpace(text)) {
			return this.repository.countByTypeIn(types);
		}
		
		return this.repository.countByNameContainsIgnoreCaseAndTypeIn(text, types);
	}

	@Override
	public PageableResponse<BusinessCode> pageableSearch(String text, Collection<Integer> types, 
			Pageable pageable, Sort sort) {
		if (types == null || types.isEmpty()) {
			return this.pageableSearch(text, pageable, sort);
		}
		
		if (pageable == null) {
			final List<BusinessCode> items = this.search(text, types, sort);
			return new PageableResponse<>(items);
		}
		
		if (StringUtils.isNullOrSpace(text)) {
			final Page<BusinessCode> page = this.repository.findAllByTypeIn(types, pageable);
			return new PageableResponse<>(page);
		}
		
		final Page<BusinessCode> page = this.repository.findAllByNameContainsIgnoreCaseAndTypeIn(text, types, pageable);
		return new PageableResponse<>(page);
	}

	@Override
	public List<BusinessCode> search(String text, Collection<Integer> types, Sort sort) {
		if (types == null || types.isEmpty()) {
			return this.search(text, sort);
		}
		
		final Sort domainSort = sort == null ? Sort.unsorted() : sort;
		if (StringUtils.isNullOrSpace(text)) {
			final Iterable<BusinessCode> domains = this.repository.findAllByTypeIn(types, domainSort);
			return this.toList(domains);
		}
		
		final Iterable<BusinessCode> domains = this.repository.findAllByNameContainsIgnoreCaseAndTypeIn(text, types, domainSort);
		return this.toList(domains);
	}

	@Override
	public Map<Integer, Integer> getUsedAmountByIds(Collection<Integer> ids) {
		final Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		
		final List<ImmutablePair<Integer, Long>> usedAmounts = this.subscriptionRepository.countByBusinessCodes(ids);
		if (usedAmounts == null) {
			return result;
		}
		
		for (ImmutablePair<Integer, Long> usedAmount : usedAmounts) {
			final Long value = usedAmount.getValue();
			
			if (value != null) {
				result.put(usedAmount.getKey(), value.intValue());
			}
		}
		
		return result;
	}
}
