package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.User;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
    
	Boolean existsByEmail(String email);
    
    User findByEmail(String email);
    
    User findBySocialLogin(String socialLogin);
    
    User findByActivateCode(String activateCode);
    
    long countByEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String email, String firstName, String lastName);
    
    Iterable<User> findAllByEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String email, String firstName, String lastName);
    
    Iterable<User> findAllByEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String email, String firstName, String lastName, Sort sort);
    
    Page<User> findAllByEmailContainsIgnoreCaseOrFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String email, String firstName, String lastName, Pageable pageable);
    
    Page<User> findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String firstName, String lastName, Pageable pageable);
    Iterable<User> findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String firstName, String lastName, Sort sort);
    
    Page<User> findAllByUserTypeAndStatusAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String userType, int status, 
    		String firstName, String lastName, Pageable pageable);
    Iterable<User> findAllByUserTypeAndStatusAndFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCase(String userType, int status, 
    		String firstName, String lastName, Sort sort);
    
    Page<User> findAllByUserTypeAndStatus(String userType, int status, Pageable pageable);
    Iterable<User> findAllByUserTypeAndStatus(String userType, int status, Sort sort);
    
    Page<User> findAllByUserType(String userType, Pageable pageable);
    Iterable<User> findAllByUserType(String userType, Sort sort);
    
    Page<User> findAllByStatus(int status, Pageable pageable);
    Iterable<User> findAllByStatus(int status, Sort sort);
    
    Iterable<User> findAllByIdInAndUserTypeAndStatus(Iterable<Integer> ids, String userType, int status, Sort sort);
    
    @Modifying
    @Query("UPDATE User SET status = ?2 WHERE id = ?1")
    int updateStatus(Integer id, Integer status);
}
