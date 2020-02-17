package fr.be.your.self.repository;

import org.springframework.stereotype.Repository;

import fr.be.your.self.model.Permission;

@Repository
public interface PermissionRepository extends BaseRepository<Permission> {
	
	public Iterable<Permission> findAllByUserId(Integer userId);
	
}
