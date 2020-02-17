package fr.be.your.self.service;

import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;

public interface PermissionService extends BaseService<Permission> {
	
	public Iterable<Permission> getPermissionByUserId(Integer userId);
	public void saveOrUpdate(Permission permission);

}
