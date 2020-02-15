package fr.be.your.self.backend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.security.oauth2.AuthenticationUserDetails;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.util.StringUtils;

public abstract class BaseController {
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private PermissionService permissionService;
	
	@ModelAttribute
	protected void initPermission(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model) {
		
		PermissionDto permission = new PermissionDto();
		
		Integer userId = null;
		String displayName = null;
		
		final Authentication oauth = SecurityContextHolder.getContext().getAuthentication();
		if (oauth != null && oauth.isAuthenticated()) {
			final Object principal = oauth.getPrincipal();
			
			if (principal instanceof AuthenticationUserDetails) {
				final AuthenticationUserDetails userDetails = (AuthenticationUserDetails) principal;
				final String fullName = userDetails.getFullName();
				
				displayName = oauth.getName();
				if (!StringUtils.isNullOrSpace(fullName)) {
					displayName = fullName;
				}
				
				userId = userDetails.getUserId();
				
				final Iterable<Permission> userPermissions = this.permissionService.getPermissionByUserId(userId);
				if (userPermissions != null) {
					for (Permission userPermission : userPermissions) {
						final Functionality functionality = userPermission.getFunctionality();
						permission.addPermission(functionality.getPath(), userPermission.getPermission());
					}
				}
			}
		}
		
		if (StringUtils.isNullOrSpace(displayName)) {
			displayName = this.getMessage("user.anonymous", "Anonymous");
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("displayName", displayName);
		model.addAttribute("permission", permission);
	}
	
	protected String getMessage(String key, Object[] args, String defaultValue) {
		return this.messageSource.getMessage(key, args, defaultValue, LocaleContextHolder.getLocale());
	}
	
	protected String getMessage(String key, Object[] args) {
		return this.getMessage(key, args, null);
	}
	
	protected String getMessage(String key, String defaultValue) {
		return this.getMessage(key, null, defaultValue);
	}
	
	protected String getMessage(String key) {
		return this.getMessage(key, null, null);
	}
}
