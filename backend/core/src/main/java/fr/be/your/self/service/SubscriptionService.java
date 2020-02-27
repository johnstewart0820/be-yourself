package fr.be.your.self.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.model.Session;
import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionType;

public interface SubscriptionService extends BaseService<Subscription, Integer>  {

	Page<Subscription> getListByPage(Pageable pageable);

	Iterable<Subscription> getList();

}
