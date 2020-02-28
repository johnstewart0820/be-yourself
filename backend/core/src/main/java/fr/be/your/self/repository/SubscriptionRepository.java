package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.be.your.self.model.Subscription;

public interface SubscriptionRepository extends BaseRepository<Subscription, Integer>   {
	@Query(
	    	"SELECT s FROM Subscription s WHERE lower(s.user.firstName) = lower(:text) "
	    	+ " or lower(s.user.lastName) = lower(:text) or lower(s.user.email) = lower(:text)"
	    )
	Page<Subscription> searchByFirstNameOrLastNameOrEmail(@Param("text") String lasttextName, Pageable pageable);
}
