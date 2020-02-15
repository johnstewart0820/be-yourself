package fr.be.your.self.backend.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.service.BaseService;

public abstract class BaseResourceController<T> extends BaseController {
	
	private static final String BASE_AVATAR_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.PATH.WEB_ADMIN.MEDIA_TYPE.AVATAR;
	
	@Autowired
	protected DataSetting dataSetting;
	
	protected abstract BaseService<T> getService();
	
	protected abstract String getName();
	
	protected abstract String getDefaultPageTitle();
	
	protected abstract T newDomain();
	
	protected void loadDetailForm(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model, Integer id) {
	}
	
	@ModelAttribute
	public void initAttribute(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model) {
		
		model.addAttribute("baseAvatarURL", BASE_AVATAR_URL);
		model.addAttribute("baseURL", this.getBaseURL());
		model.addAttribute("pageTitle", this.getDefaultPageTitle());
	}
	
	@GetMapping(value = { "", "/search" })
    public String listPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@RequestParam(name = "q", required = false) String search,
    		@RequestParam(name = "sort", required = false) String sort,
    		@RequestParam(name = "page", required = false) Integer page,
    		@RequestParam(name = "size", required = false) Integer size) {
		
		final PageRequest pageable = this.getPageRequest(page, size);
		
		final PageableResponse<T> result = this.getService().pageableSearch(search, pageable);
		if (result == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		// Save session
		session.setAttribute(this.getSearchSessionKey(), search);
		session.setAttribute(this.getSortSessionKey(), sort);
		session.setAttribute(this.getPageIndexSessionKey(), pageable.getPageNumber() + 1);
		session.setAttribute(this.getPageSizeSessionKey(), pageable.getPageSize());
		
		// Store properties
		model.addAttribute("search", search == null ? "" : search);
		model.addAttribute("sort", search == null ? "" : search);
		model.addAttribute("page", pageable.getPageNumber() + 1);
		model.addAttribute("size", pageable.getPageSize());
		
		// Store result
		model.addAttribute("result", result);
		
        return "pages/" + this.getName() + "-list";
    }
	
	@GetMapping(value = { "/page" })
    public String changePage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@RequestParam(name = "page", required = false) Integer page) {
		
		final String search = (String) session.getAttribute(this.getSearchSessionKey());
		final String sort = (String) session.getAttribute(this.getSortSessionKey());
		final Integer size = (Integer) session.getAttribute(this.getPageSizeSessionKey());
		
		if (page == null) {
			page = (Integer) session.getAttribute(this.getPageIndexSessionKey());
		}
		
		final PageRequest pageable = this.getPageRequest(page, size);
		final PageableResponse<T> result = this.getService().pageableSearch(search, pageable);
		if (result == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		// Save session
		session.setAttribute(this.getPageIndexSessionKey(), pageable.getPageNumber() + 1);
		
		// Store properties
		model.addAttribute("page", pageable.getPageNumber() + 1);
		
		// Store result
		model.addAttribute("result", result);
		
        return "pages/" + this.getName() + "-list";
	}
	
	@GetMapping(value = { "/addnew" })
    public String addNewPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		T result = this.newDomain();
		
		if (result == null) {
			try {
				response.sendRedirect(this.getBaseURL() + "/page");
			} catch (IOException e) {}
			
			return null;
		}
		
		this.loadDetailForm(session, request, response, model, null);
		
		// Store result
		model.addAttribute("action", "create");
		model.addAttribute("domain", result);
		
		return "pages/" + this.getName() + "-form";
	}
	
	@GetMapping(value = { "/edit/{id}" })
    public String editPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model,
    		@PathVariable(name = "id", required = true) Integer id) {
		
		T result = this.getService().getById(id);
		
		if (result == null) {
			try {
				response.sendRedirect(this.getBaseURL() + "/page");
			} catch (IOException e) {}
			
			return null;
		}
		
		this.loadDetailForm(session, request, response, model, id);
		
		// Store result
		model.addAttribute("action", "update/" + id);
		model.addAttribute("domain", result);
		
		return "pages/" + this.getName() + "-form";
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
	
	protected final String getBaseURL() {
		return Constants.PATH.WEB_ADMIN_PREFIX + "/" + this.getName();
	}
	
	protected final String getSearchSessionKey() {
		return this.getName() + "_SEARCH";
	}
	
	protected final String getSortSessionKey() {
		return this.getName() + "_SORT";
	}
	
	protected final String getPageIndexSessionKey() {
		return this.getName() + "_PAGE_INDEX";
	}
	
	protected final String getPageSizeSessionKey() {
		return this.getName() + "_PASE_SIZE";
	}
	
	protected final PageRequest getPageRequest(Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }

        if (size == null || size < 1) {
            size = this.dataSetting.getDefaultPageSize();
        }
        
        return PageRequest.of(page - 1, size);
    }
}
