package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.BusinessCode;

@Repository
public interface BusinessCodeRepository extends BaseRepository<BusinessCode, Integer> {
	
	Boolean existsByNameIgnoreCase(String name);
	
	long countByNameContainsIgnoreCase(String name);
	Iterable<BusinessCode> findAllByNameContainsIgnoreCase(String name, Sort sort);
    Page<BusinessCode> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
    
	long countByTypeIn(Iterable<Integer> types);
    Iterable<BusinessCode> findAllByTypeIn(Iterable<Integer> types, Sort sort);
    Page<BusinessCode> findAllByTypeIn(Iterable<Integer> types, Pageable pageable);
    
    long countByNameContainsIgnoreCaseAndTypeIn(String name, Iterable<Integer> types);
	Iterable<BusinessCode> findAllByNameContainsIgnoreCaseAndTypeIn(String name, Iterable<Integer> types, Sort sort);
    Page<BusinessCode> findAllByNameContainsIgnoreCaseAndTypeIn(String name, Iterable<Integer> types, Pageable pageable);
    
    @Modifying
    @Query("UPDATE BusinessCode SET status = ?2 WHERE name = ?1")
    int updateStatus(String name, Integer status);

    Iterable<BusinessCode> findAllByNameContainsIgnoreCase(String name);
}
