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
	
	long countByCodeContainsIgnoreCase(String code);
	
	Iterable<BusinessCode> findAllByCodeContainsIgnoreCase(String code, Sort sort);
    
    Page<BusinessCode> findAllByCodeContainsIgnoreCase(String code, Pageable pageable);
    
	long countByCodeType(int codeType);
    
    Iterable<BusinessCode> findAllByCodeType(int codeType, Sort sort);
    
    Page<BusinessCode> findAllByCodeType(int codeType, Pageable pageable);
    
    @Modifying
    @Query("UPDATE BusinessCode SET status = ?2 WHERE code = ?1")
    int updateStatus(String code, Integer status);
}
