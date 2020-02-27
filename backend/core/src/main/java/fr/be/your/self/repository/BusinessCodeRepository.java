package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.BusinessCode;

@Repository
public interface BusinessCodeRepository extends BaseRepository<BusinessCode, String> {
	
	long countByNameContainsIgnoreCase(String name);
	
	Iterable<BusinessCode> findAllByNameContainsIgnoreCase(String name, Sort sort);
    
    Page<BusinessCode> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    
	long countByType(int type);
    
    Iterable<BusinessCode> findAllByType(int type, Sort sort);
    
    Page<BusinessCode> findAllByType(int type, Pageable pageable);
    
    @Modifying
    @Query("UPDATE BusinessCode SET status = ?2 WHERE name = ?1")
    int updateStatus(String name, Integer status);
}
