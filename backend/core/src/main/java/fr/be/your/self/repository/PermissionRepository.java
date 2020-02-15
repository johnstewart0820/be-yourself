package fr.be.your.self.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Permission;

@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission,Integer> {
	
	public Iterable<Permission> findAllByUserId(Integer userId);
}
