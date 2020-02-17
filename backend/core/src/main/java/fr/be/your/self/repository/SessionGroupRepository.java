package fr.be.your.self.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.SessionGroup;

@Repository
public interface SessionGroupRepository extends BaseRepository<SessionGroup> {
	
	long countByNameContainsIgnoreCase(String name);
    
	// findAllByNameContainsIgnoreCase("value")
    Iterable<SessionGroup> findAllByNameContainsIgnoreCase(String name);
    
    Page<SessionGroup> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    
    // findAllByNameNotContainsIgnoreCase("value")
    Iterable<SessionGroup> findAllByNameNotContainsIgnoreCase(String name);
    
    // findAllByNameLikeIgnoreCase("%value%"), with %
    Iterable<SessionGroup> findAllByNameLikeIgnoreCase(String name);
    
    // findAllByNameStartsWithIgnoreCase("value")
    Iterable<SessionGroup> findAllByNameStartsWithIgnoreCase(String name);
    
    // findAllByNameEndsWithIgnoreCase("value")
    Iterable<SessionGroup> findAllByNameEndsWithIgnoreCase(String name);
    
    @Query("SELECT m FROM SessionGroup m WHERE m.name LIKE ?1%")
    List<SessionGroup> searchByNameStartsWith(String name);
}
