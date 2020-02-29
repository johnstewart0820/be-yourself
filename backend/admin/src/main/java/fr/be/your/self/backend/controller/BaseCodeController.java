package fr.be.your.self.backend.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.BusinessCodeDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.BusinessCode;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.BusinessCodeService;
import fr.be.your.self.util.StringUtils;

public abstract class BaseCodeController extends BaseResourceController<BusinessCode, BusinessCodeDto, BusinessCodeDto, String> {
	
	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.FOLDER.MEDIA.BUSINESS_CODE;
	
	@Autowired
	protected BusinessCodeService mainService;
	
	protected abstract void validateCreateDomain(BindingResult result, Model model, BusinessCode domain);
	
	protected abstract void validateUpdateDomain(BindingResult result, Model model, BusinessCode domain);
	
	/**
	 * @return error message
	 **/
	protected abstract String validateDeleteDomain(Model model, BusinessCode domain);
	
	public BaseCodeController() {
		super();
	}

	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.BUSINESS_CODE;
	}
	
	@Override
	protected BaseService<BusinessCode, String> getService() {
		return this.mainService;
	}
	
	@Override
	protected BusinessCode newDomain() {
		return new BusinessCode();
	}

	@Override
	protected BusinessCodeDto createDetailDto(BusinessCode domain) {
		return new BusinessCodeDto(domain);
	}

	@Override
	protected BusinessCodeDto createSimpleDto(BusinessCode domain) {
		return new BusinessCodeDto(domain);
	}

	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}
	
	@Override
	protected void loadListPageOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Map<String, String> searchParams, PageableResponse<BusinessCodeDto> pageableDto)
			throws BusinessException {
		super.loadListPageOptions(session, request, response, model, searchParams, pageableDto);
		
		model.addAttribute("priceUnitSymbol", this.dataSetting.getPriceUnitSymbol());
		model.addAttribute("priceUnitName", this.dataSetting.getPriceUnitName());
	}

	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, BusinessCode domain, BusinessCodeDto dto) throws BusinessException {
		super.loadDetailFormOptions(session, request, response, model, domain, dto);
		
		model.addAttribute("priceUnitSymbol", this.dataSetting.getPriceUnitSymbol());
		model.addAttribute("priceUnitName", this.dataSetting.getPriceUnitName());
	}

	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@ModelAttribute("dto") @Validated BusinessCodeDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        if (dto.getStartDate() != null && dto.getStartDate().getTime() > dto.getEndDate().getTime()) {
        	final ObjectError error = this.createFieldError(result, "startDate", "before.end.date", null, "The start date must before end date");
        	result.addError(error);
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        final BusinessCode domain = this.newDomain();
        dto.copyToDomain(domain, true);
        
        this.validateCreateDomain(result, model, domain);
        if (result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        final BusinessCode savedDomain = this.mainService.create(domain);
        
        // ====> Error
        if (savedDomain == null || result.hasErrors()) {
        	if (!result.hasErrors()) {
	        	final ObjectError error = this.createProcessingError(result);
	        	result.addError(error);
        	}
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "create");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL();
    }
	
	@PostMapping("/update/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") String id, 
    		@ModelAttribute("dto") @Validated BusinessCodeDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        if (dto.getStartDate() != null && dto.getStartDate().getTime() > dto.getEndDate().getTime()) {
        	final ObjectError error = this.createFieldError(result, "startDate", "before.end.date", null, "The start date must before end date");
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        BusinessCode domain = this.mainService.getById(id);
        if (domain == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        dto.copyToDomain(domain, false);
        
        this.validateUpdateDomain(result, model, domain);
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        final BusinessCode savedDomain = this.mainService.update(domain);
        
        // ====> Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {
        	if (!result.hasErrors()) {
	        	final ObjectError error = this.createProcessingError(result);
	        	result.addError(error);
        	}
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        // ====> Success
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL() + "/current-page";
    }
	
	@PostMapping(value = { "/delete/{id}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "id", required = true) String id,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final BusinessCode domain = this.mainService.getById(id);
		if (domain == null) {
			final String message = this.getIdNotFoundMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final String errorMessage = this.validateDeleteDomain(model, domain);
        if (!StringUtils.isNullOrSpace(errorMessage)) {
        	redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, errorMessage);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
        }
        
		final boolean result = this.mainService.delete(id);
		if (result) {
			// ====> Success
			final String message = this.getDeleteSuccessMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
			
			return "redirect:" + this.getBaseURL();
		}
		
		final String message = this.getDeleteByIdErrorMessage(id);
		
		redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
        
		return "redirect:" + this.getBaseURL() + "/current-page";
	}
}
