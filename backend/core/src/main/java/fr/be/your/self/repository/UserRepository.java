package fr.be.your.self.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User,Integer> {
    
	Boolean existsByEmail(String email);
    
    User findByEmail(String email);
    
    User findBySocialId(String socialId);
    
    long countByEmailOrFullname(String email, String fullname);
    
    Iterable<User> findAllByEmailOrFullname(String email, String fullname);
    
    Page<User> findAllByEmailOrFullname(String email, String fullname, Pageable pageable);
}
