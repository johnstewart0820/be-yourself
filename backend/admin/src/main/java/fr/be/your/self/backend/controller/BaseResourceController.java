package fr.be.your.self.backend.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.StatusCode;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.PO;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.util.StringUtils;

public abstract class BaseResourceController<T extends PO<Integer>, SimpleDto, DetailDto> extends BaseController {
	
	private static final String ACCESS_DENIED_URL = Constants.PATH.AUTHENTICATION_PREFIX + Constants.PATH.AUTHENTICATION.ACCESS_DENIED;
	
	protected Logger logger;
	
	public BaseResourceController() {
		super();
		
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	protected abstract BaseService<T> getService();
	
	protected abstract String getName();
	
	protected abstract String getDefaultPageTitle();
	
	protected abstract T newDomain();
	
	protected abstract DetailDto createDetailDto(T domain);
	
	protected abstract SimpleDto createSimpleDto(T domain);
	
	protected String getBaseMediaURL() {
		return null;
	}
	
	protected void loadDetailForm(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model, DetailDto dto) throws BusinessException {
		
	}
	
	@Override
	public void initAttributes(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model, PermissionDto permission) {
		
		final String pageName = this.getName();
		final boolean writePermission = permission.hasWritePermission(pageName);
		final boolean readPermission = writePermission || permission.hasPermission(pageName);
		
		if (!readPermission) {
			try {
				response.sendRedirect(ACCESS_DENIED_URL);
				
				return;
			} catch (IOException e) {}
		}
		
		final String baseMessageKey = pageName.replace('-', '.');
		
		model.addAttribute("writePermission", writePermission);
		model.addAttribute("readPermission", readPermission);
		
		model.addAttribute("pageName", pageName);
		model.addAttribute("pageTitle", this.getDefaultPageTitle());
		model.addAttribute("baseMessageKey", baseMessageKey);
		model.addAttribute("baseURL", this.getBaseURL());
		
		final String baseImageURL = this.getBaseMediaURL();
		if (!StringUtils.isNullOrSpace(baseImageURL)) {
			model.addAttribute("baseMediaURL", baseImageURL);
		}
	}
	
	@GetMapping(value = { "", "/search" })
    public String listPage(
    		@RequestParam(name = "q", required = false) String search,
    		@RequestParam(name = "sort", required = false) String sort,
    		@RequestParam(name = "page", required = false) Integer page,
    		@RequestParam(name = "size", required = false) Integer size,
    		HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final PageRequest pageable = this.getPageRequest(page, size);
		
		// Save session
		session.setAttribute(this.getSearchSessionKey(), search);
		session.setAttribute(this.getSortSessionKey(), sort);
		session.setAttribute(this.getPageIndexSessionKey(), pageable.getPageNumber() + 1);
		session.setAttribute(this.getPageSizeSessionKey(), pageable.getPageSize());
		
		return this.listPage(session, request, response, model, search, pageable);
    }
	
	@GetMapping(value = { "/page/{page}" })
    public String changePage(
    		@PathVariable(name = "page", required = true) Integer page,
    		HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final String search = (String) session.getAttribute(this.getSearchSessionKey());
		final String sort = (String) session.getAttribute(this.getSortSessionKey());
		final Integer size = (Integer) session.getAttribute(this.getPageSizeSessionKey());
		
		if (page == null || page <= 0) {
			page = (Integer) session.getAttribute(this.getPageIndexSessionKey());
		}
		
		final PageRequest pageable = this.getPageRequest(page, size);
		
		// Save session
		session.setAttribute(this.getPageIndexSessionKey(), pageable.getPageNumber() + 1);
		
		return this.listPage(session, request, response, model, search, pageable);
	}
	
	@GetMapping(value = { "/current-page" })
    public String currentPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final String search = (String) session.getAttribute(this.getSearchSessionKey());
		final String sort = (String) session.getAttribute(this.getSortSessionKey());
		final Integer size = (Integer) session.getAttribute(this.getPageSizeSessionKey());
		final Integer page = (Integer) session.getAttribute(this.getPageIndexSessionKey());
		
		final PageRequest pageable = this.getPageRequest(page, size);
		
		return this.listPage(session, request, response, model, search, pageable);
	}
	
	private String listPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model, 
    		String search, PageRequest pageable) {
		
		final PageableResponse<T> domainPage = this.getService().pageableSearch(search, pageable);
		if (domainPage == null) {
			throw new BusinessException(StatusCode.PROCESSING_ERROR);
		}
		
		final PageableResponse<SimpleDto> result = new PageableResponse<>();
		result.setPageIndex(domainPage.getPageIndex());
		result.setPageSize(domainPage.getPageSize());
		result.setTotalItems(domainPage.getTotalItems());
		result.setTotalPages(domainPage.getTotalPages());
		
		for (T domain : domainPage.getItems()) {
			result.addItem(this.createSimpleDto(domain));
		}
		
		// Store properties
		final String titleKey = this.getName().replace('-', '.') + ".page.title"; 
		model.addAttribute("formTitle", this.getMessage(titleKey));
		
		model.addAttribute("search", search == null ? "" : search);
		//model.addAttribute("sort", sort == null ? "" : sort);
		model.addAttribute("page", pageable.getPageNumber() + 1);
		model.addAttribute("size", pageable.getPageSize());
		
		// Store result
		model.addAttribute("result", result);
		
        return this.getListView();
	}
	
	@GetMapping(value = { "/create" })
    public String addNewPage(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final DetailDto dto = this.createDetailDto(null);
		
		if (dto == null) {
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		try {
			this.loadDetailForm(session, request, response, model, dto);
		} catch (BusinessException ex) {
			this.logger.error("Business error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		} catch (Exception ex) {
			this.logger.error("Process error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		// Store result
		final String titleKey = this.getName().replace('-', '.') + ".create.page.title"; 
		model.addAttribute("formTitle", this.getMessage(titleKey));
		
		model.addAttribute("action", "create");
		model.addAttribute("dto", dto);
		
		return this.getFormView();
	}
	
	@GetMapping(value = { "/edit/{id}" })
    public String editPage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final T domain = this.getService().getById(id);
		if (domain == null) {
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final DetailDto dto = this.createDetailDto(domain);
		if (dto == null) {
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		try {
			this.loadDetailForm(session, request, response, model, dto);
		} catch (BusinessException ex) {
			this.logger.error("Business error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		} catch (Exception ex) {
			this.logger.error("Process error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		// Store result
		final String titleKey = this.getName().replace('-', '.') + ".edit.page.title";
		final Object[] titleParams = new Object[] { domain.getDisplay() };
		model.addAttribute("formTitle", this.getMessage(titleKey, titleParams));
		
		model.addAttribute("action", "update/" + id);
		model.addAttribute("dto", dto);
		
		return this.getFormView();
	}
	
	@RequestMapping(value = { "/view/{id}" }, method = { RequestMethod.GET, RequestMethod.POST })
    public String viewPage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model) {
		
		final T domain = this.getService().getById(id);
		if (domain == null) {
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final DetailDto dto = this.createDetailDto(domain);
		if (dto == null) {
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		try {
			this.loadDetailForm(session, request, response, model, dto);
		} catch (BusinessException ex) {
			this.logger.error("Business error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		} catch (Exception ex) {
			this.logger.error("Process error", ex);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		// Store result
		final String titleKey = this.getName().replace('-', '.') + ".view.page.title";
		final Object[] titleParams = new Object[] { domain.getDisplay() };
		model.addAttribute("formTitle", this.getMessage(titleKey, titleParams));
		
		model.addAttribute("action", "view/" + id);
		model.addAttribute("dto", dto);
		
		model.addAttribute("writePermission", false);
		
		return this.getFormView();
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
	
	protected final ObjectError createFieldError(BindingResult result, String fieldName, String messageCode, String defaultMessage) {
		return new FieldError(result.getObjectName(), fieldName, null, false, new String[] { messageCode }, null, defaultMessage);
	}
	
	protected final ObjectError createFieldError(BindingResult result, String fieldName, String messageCode) {
		return new FieldError(result.getObjectName(), fieldName, null, false, new String[] { messageCode }, null, messageCode);
	}
	
	protected final ObjectError createIdNotFoundError(BindingResult result) {
		return new ObjectError(result.getObjectName(), new String[] { "error.id.not.found" }, null, "Not found");
	}
	
	protected final ObjectError createProcessingError(BindingResult result) {
		return new ObjectError(result.getObjectName(), new String[] { "error.processing" }, null, "Processing error");
	}
	
	protected final String getBaseURL() {
		return Constants.PATH.WEB_ADMIN_PREFIX + "/" + this.getName();
	}
	
	protected final String getListView() {
		return "pages/" + this.getName() + "-list";
	}
	
	protected final String getFormView() {
		return "pages/" + this.getName() + "-form";
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
