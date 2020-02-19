package fr.be.your.self.backend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;
import fr.be.your.self.security.oauth2.AuthenticationUserDetails;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

public enum AuthUtils {
	SINGLETON;
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	UserService userService;
	
	public User getLoggedUser() {
		PermissionDto permission = new PermissionDto();

		Integer userId = null;
		String displayName = null;
	
		final Authentication oauth = SecurityContextHolder.getContext().getAuthentication();
		if (oauth != null && oauth.isAuthenticated()) {
			final Object principal = oauth.getPrincipal();

			if (principal instanceof AuthenticationUserDetails) {
				final AuthenticationUserDetails userDetails = (AuthenticationUserDetails) principal;

				userId = userDetails.getUserId();
				
				User user = userService.getById(userId);
				return user;
				/*final Iterable<Permission> userPermissions = this.permissionService.getPermissionByUserId(userId);
				if (userPermissions != null) {
					for (Permission userPermission : userPermissions) {
						final Functionality functionality = userPermission.getFunctionality();
						permission.addPermission(functionality.getPath(), userPermission.getUserPermission());
					}
				}*/
			}
		} 
		return null;

	}
}
