package fr.be.your.self.backend.dto;

import java.util.HashMap;
import java.util.Map;

import fr.be.your.self.common.UserPermission;

public class PermissionDto {
	
	private Map<String, Integer> userPermissions = new HashMap<>();
	
	public boolean hasPermission(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		
		final String funcPath = (path.startsWith("/") ? "" : "/") + path.toLowerCase();
		final Integer permission = this.userPermissions.get(funcPath);
		
		if (permission == null) {
			return false;
		}
		
		return permission == UserPermission.READONLY.getValue() 
				|| permission == UserPermission.WRITE.getValue();
	}
	
	public boolean hasWritePermission(String path) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		
		final String funcPath = (path.startsWith("/") ? "" : "/") + path.toLowerCase();
		final Integer permission = this.userPermissions.get(funcPath);
		
		if (permission == null) {
			return false;
		}
		
		return permission == UserPermission.WRITE.getValue();
	}
	
	public int getPermission(String path) {
		if (path == null || path.isEmpty()) {
			return UserPermission.DENIED.getValue();
		}
		
		final String funcPath = (path.startsWith("/") ? "" : "/") + path.toLowerCase();
		final Integer permission = this.userPermissions.get(funcPath);
		
		if (permission == null) {
			return UserPermission.DENIED.getValue();
		}
		
		return permission.intValue();
	}
	
	public void addPermission(String path, int permission) {
		this.userPermissions.put(path.toLowerCase(), permission);
	}
}
