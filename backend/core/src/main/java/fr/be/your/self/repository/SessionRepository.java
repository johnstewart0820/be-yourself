package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Session;

@Repository
public interface SessionRepository extends BaseRepository<Session> {
	
	long countByTitleContainsIgnoreCase(String title);
    
	// findAllByNameContainsIgnoreCase("value")
    Iterable<Session> findAllByTitleContainsIgnoreCase(String title);
    
    Page<Session> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);
    
}
