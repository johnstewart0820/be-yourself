package fr.be.your.self.backend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.security.oauth2.AuthenticationUserDetails;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.util.StringUtils;

public abstract class BaseController<T> {
	
	private static final String BASE_AVATAR_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.PATH.WEB_ADMIN.MEDIA_TYPE.AVATAR;
	
	@Autowired
	protected DataSetting dataSetting;
	
	@Autowired
	private MessageSource messageSource;
	
	protected abstract BaseService<T> getService();
	
	protected abstract String getBaseURL();
	
	protected abstract String getDefaultPageTitle();
	
	@ModelAttribute
	public void initAttribute(HttpSession session, HttpServletRequest request, Model model) {
		model.addAttribute("baseAvatarURL", BASE_AVATAR_URL);
		model.addAttribute("baseURL", this.getBaseURL());
		model.addAttribute("pageTitle", this.getDefaultPageTitle());
		
		Integer userId = null;
		String displayName = null;
		
		final Authentication oauth = SecurityContextHolder.getContext().getAuthentication();
		if (oauth != null && oauth.isAuthenticated()) {
			final Object principal = oauth.getPrincipal();
			
			if (principal instanceof AuthenticationUserDetails) {
				final AuthenticationUserDetails userDetails = (AuthenticationUserDetails) principal;
				final String fullname = userDetails.getFullname();
				
				displayName = oauth.getName();
				if (!StringUtils.isNullOrSpace(fullname)) {
					displayName = fullname;
				}
				
				userId = userDetails.getUserId();
			}
		}
		
		if (StringUtils.isNullOrSpace(displayName)) {
			displayName = this.getMessage("user.anonymous", "Anonymous");
		}
		
		model.addAttribute("userId", userId);
		model.addAttribute("displayName", displayName);
	}
	
	@GetMapping(value = { "", "/search" })
    public String searchPage(HttpSession session, HttpServletRequest request, Model model,
    		@RequestParam(name = "q", required = false) String search,
    		@RequestParam(name = "sort", required = false) String sort,
    		@RequestParam(name = "page", required = false) Integer page,
    		@RequestParam(name = "size", required = false) Integer size) {
		
		final PageRequest pageable = this.getPageRequest(page, size);
		
		final PageableResponse<T> result = this.getService().pageableSearch(search, pageable);
		if (result == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		model.addAttribute("result", result);
        return "admin-user/list-page";
    }
	
	/*
	public final User getCurrentUser(HttpSession session, Model model) {
		Object accessToken = session.getAttribute(SESSION_KEY_ACCESS_TOKEN);

		if (accessToken != null) {
			final Object currentUser = model.getAttribute("currentUser");
			
			if (currentUser != null && currentUser instanceof User) {
				return (User) currentUser;
			}
			
			User authenticationUser = userRepository.findByAccessToken(accessToken.toString());
			if (authenticationUser != null) {
				model.addAttribute("currentUser", authenticationUser);
			}

			return authenticationUser;
		}

		return null;
	}
	*/
	
	protected final PageRequest getPageRequest(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }

        if (size == null || size < 1) {
            size = this.dataSetting.getDefaultPageSize();
        }
        
        return PageRequest.of(page - 1, size);
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
