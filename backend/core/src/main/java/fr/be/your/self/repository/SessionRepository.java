package fr.be.your.self.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Session;

@Repository
public interface SessionRepository extends BaseRepository<Session> {
	
	long countByTitleContainsIgnoreCase(String title);
    
	// findAllByNameContainsIgnoreCase("value")
    Iterable<Session> findAllByTitleContainsIgnoreCase(String title);
    
    Iterable<Session> findAllByTitleContainsIgnoreCase(String title, Sort sort);
    
    Page<Session> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Session a WHERE (:title IS NULL OR :title = '' OR lower(a.title) LIKE lower(concat('%', :title,'%'))) AND EXISTS (SELECT c FROM a.categories c WHERE c.id IN :categoryIds)")
    long count(@Param("title") String title, @Param("categoryIds") Collection<Integer> categoryIds);
    
    @Query("SELECT a FROM Session a WHERE (:title IS NULL OR :title = '' OR lower(a.title) LIKE lower(concat('%', :title,'%'))) AND EXISTS (SELECT c FROM a.categories c WHERE c.id IN :categoryIds)")
    Iterable<Session> findAll(@Param("title") String title, @Param("categoryIds") Collection<Integer> categoryIds, Sort sort);
    
    @Query(
    	value = "SELECT a FROM Session a WHERE (:title IS NULL OR :title = '' OR lower(a.title) LIKE lower(concat('%', :title,'%'))) AND EXISTS (SELECT c FROM a.categories c WHERE c.id IN :categoryIds)",
    	countQuery = "SELECT COUNT(a) FROM Session a WHERE (:title IS NULL OR :title = '' OR lower(a.title) LIKE lower(concat('%', :title,'%'))) AND EXISTS (SELECT c FROM a.categories c WHERE c.id IN :categoryIds)" 
    )
    Page<Session> findAll(@Param("title") String title, @Param("categoryIds") Collection<Integer> categoryIds, Pageable pageable);
}
