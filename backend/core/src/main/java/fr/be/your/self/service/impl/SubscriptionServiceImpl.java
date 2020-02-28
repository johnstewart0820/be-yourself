package fr.be.your.self.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionCsv;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.repository.BaseRepository;
import fr.be.your.self.repository.SubscriptionRepository;
import fr.be.your.self.service.SubscriptionService;
import fr.be.your.self.util.StringUtils;

@Service
public class SubscriptionServiceImpl extends BaseServiceImpl<Subscription, Integer> implements SubscriptionService {

	@Autowired
	SubscriptionRepository subscriptionRepo;

	@Override
	public String getDefaultSort() {
		return null;
	}
	
	@Override
	public long count(String text) {
		if (StringUtils.isNullOrSpace(text)) {
			return this.subscriptionRepo.count();
		} else {
			throw new UnsupportedOperationException("Count() method of subscription service does not support text parameter.");
		}
	}

	@Override
	protected BaseRepository<Subscription, Integer> getRepository() {
		return subscriptionRepo;
	}

	@Override
	protected Iterable<Subscription> getList(String text, Sort sort) {
		if (!StringUtils.isNullOrEmpty(text)) {
			throw new UnsupportedOperationException("Get list by text not supported!");
		}

		return this.subscriptionRepo.findAll(sort);
	}

	@Override
	public Iterable<Subscription> getList() {
		return this.subscriptionRepo.findAll();
	}

	@Override
	protected Page<Subscription> getListByPage(String text, Pageable pageable) {
		if (!StringUtils.isNullOrEmpty(text)) {
			return this.subscriptionRepo.searchByFirstNameOrLastNameOrEmail(text, pageable);
		}
		return this.subscriptionRepo.findAll(pageable);
	}

	@Override
	public Page<Subscription> getListByPage(Pageable pageable) {
		return this.subscriptionRepo.findAll(pageable);
	}

	@Override
	public List<SubscriptionCsv> extractSubscriptionCsv(List<Integer> subscriptionIds) {
		Iterable<Subscription> users= this.subscriptionRepo.findAllById(subscriptionIds);
		List<SubscriptionCsv> returnList = new ArrayList<SubscriptionCsv>();
		for (Subscription subscription : users) {
			SubscriptionCsv subscriptionCsv = new SubscriptionCsv(subscription);
			returnList.add(subscriptionCsv);
		}
		
		return returnList;
	}

}
