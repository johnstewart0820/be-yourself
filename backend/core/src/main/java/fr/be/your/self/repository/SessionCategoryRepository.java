package fr.be.your.self.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.SessionCategory;

@Repository
public interface SessionCategoryRepository extends BaseRepository<SessionCategory> {
	
	long countByNameContainsIgnoreCase(String name);
    
	// findAllByNameContainsIgnoreCase("value")
    Iterable<SessionCategory> findAllByNameContainsIgnoreCase(String name);
    
    Page<SessionCategory> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    
    // findAllByNameNotContainsIgnoreCase("value")
    Iterable<SessionCategory> findAllByNameNotContainsIgnoreCase(String name);
    
    // findAllByNameLikeIgnoreCase("%value%"), with %
    Iterable<SessionCategory> findAllByNameLikeIgnoreCase(String name);
    
    // findAllByNameStartsWithIgnoreCase("value")
    Iterable<SessionCategory> findAllByNameStartsWithIgnoreCase(String name);
    
    // findAllByNameEndsWithIgnoreCase("value")
    Iterable<SessionCategory> findAllByNameEndsWithIgnoreCase(String name);
    
    @Query("SELECT m FROM SessionCategory m WHERE m.name LIKE ?1%")
    List<SessionCategory> searchByNameStartsWith(String name);
}
