package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.be.your.self.model.Session;
import fr.be.your.self.model.SubscriptionType;

public interface SubscriptionTypeRepository extends BaseRepository<SubscriptionType>  {

	Page<SubscriptionType> findAllByNameContainsIgnoreCase(String text, Pageable pageable);

	Iterable<SubscriptionType> findAllByNameContainsIgnoreCase(String text);

	long countByNameContainsIgnoreCase(String text);

}
