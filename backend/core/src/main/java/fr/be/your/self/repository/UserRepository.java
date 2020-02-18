package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.User;

@Repository
public interface UserRepository extends BaseRepository<User> {
    
	Boolean existsByEmail(String email);
    
    User findByEmail(String email);
    
    User findBySocialLogin(String socialLogin);
    
    long countByEmailOrFirstNameOrLastName(String email, String firstName, String lastName);
    
    Iterable<User> findAllByEmailOrFirstNameOrLastName(String email, String firstName, String lastName);
    
    Page<User> findAllByEmailOrFirstNameOrLastName(String email, String firstName, String lastName, Pageable pageable);
    
    @Modifying
    @Query("UPDATE User SET status = ?2 WHERE id = ?1")
    int updateStatus(Integer id, Integer status);
}
