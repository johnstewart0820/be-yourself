package fr.be.your.self.backend.dto;

import java.util.HashMap;
import java.util.Map;

import fr.be.your.self.common.UserPermission;

public class PermissionDto {
	
	private Map<String, Integer> userPermissions = new HashMap<>();
	
	public boolean hasPermission(String path) {
		return this.userPermissions.containsKey(path.toLowerCase());
	}
	
	public boolean hasWritePermission(String path) {
		final Integer permission = this.userPermissions.get(path.toLowerCase());
		
		if (permission == null) {
			return false;
		}
		
		return permission == UserPermission.WRITE.getValue();
	}
	
	public void addPermission(String path, int permission) {
		this.userPermissions.put(path.toLowerCase(), permission);
	}
}
