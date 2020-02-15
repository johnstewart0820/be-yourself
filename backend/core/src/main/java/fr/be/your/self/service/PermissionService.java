package fr.be.your.self.service;

import fr.be.your.self.model.Permission;

public interface PermissionService extends BaseService<Permission> {
	
	public Iterable<Permission> getPermissionByUserId(Integer userId);
	
}
