package fr.be.your.self.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.SessionGroupDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.SessionGroup;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SessionGroupService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SessionGroupController.NAME)
public class SessionGroupController extends BaseResourceController<SessionGroup, SessionGroup, SessionGroupDto> {
	
	public static final String NAME = "session-group";
	
	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.PATH.WEB_ADMIN.MEDIA_TYPE.SESSION_GROUP;
	
	@Autowired
	private SessionGroupService mainService;
	
	@Override
	protected String getName() {
		return NAME;
	}
	
	@Override
	protected String getDefaultPageTitle() {
		return this.getMessage(this.getName() + ".page.title", "Session Group management");
	}
	
	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.SESSION_GROUP;
	}
	
	@Override
	protected BaseService<SessionGroup> getService() {
		return this.mainService;
	}
	
	@Override
	protected SessionGroup newDomain() {
		return new SessionGroup();
	}

	@Override
	protected SessionGroupDto createDetailDto(SessionGroup domain) {
		return new SessionGroupDto(domain);
	}

	@Override
	protected SessionGroup createSimpleDto(SessionGroup domain) {
		return domain;
	}

	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}
	
	@Override
	protected void loadDetailForm(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, SessionGroupDto dto) throws BusinessException {
		super.loadDetailForm(session, request, response, model, dto);
		
		final String supportImageTypes = String.join(",", this.dataSetting.getImageMimeTypes());
		final String supportImageExtensions = String.join(",", this.dataSetting.getImageFileExtensions());
		final long supportImageSize = this.dataSetting.getImageMaxFileSize();
		
		model.addAttribute("supportImageTypes", supportImageTypes);
		model.addAttribute("supportImageExtensions", supportImageExtensions);
		model.addAttribute("supportImageSize", supportImageSize);
		model.addAttribute("supportImageSizeLabel", StringUtils.formatFileSize(supportImageSize));
	}

	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@ModelAttribute @Validated SessionGroupDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
        	return this.getFormView();
        }
        
        final MultipartFile mediaFile = dto.getUploadImageFile();
        if (mediaFile == null || mediaFile.isEmpty()) {
        	final ObjectError error = this.createFieldError(result, "image", "required", "Image required");
        	result.addError(error);
        	
        	return this.getFormView();
        }
        
        final Path uploadFilePath = this.uploadFile(mediaFile);
        if (uploadFilePath == null) {
        	final ObjectError error = this.createProcessingError(result);
        	result.addError(error);
        	
        	return this.getFormView();
        }
        
        final String uploadFileName = uploadFilePath.getFileName().toString();
        
        final SessionGroup domain = this.newDomain();
        domain.setName(dto.getName());
        domain.setImage(uploadFileName);
        
        final SessionGroup savedDomain = this.mainService.create(domain);
        
        // Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {
        	try {
				Files.delete(uploadFilePath);
			} catch (IOException ex) {
				this.logger.error("Cannot delete media file", ex);
			}
        	
        	return this.getFormView();
        }
        
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "create");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL();
    }
	
	@PostMapping("/update/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") Integer id, 
    		@ModelAttribute @Validated SessionGroupDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.getFormView();
        }
        
        SessionGroup domain = this.mainService.getById(id);
        if (domain == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        String deleteMediaFileName = null;
        Path uploadFilePath = null;
        
        final MultipartFile mediaFile = dto.getUploadImageFile();
        if (mediaFile != null && !mediaFile.isEmpty()) {
        	deleteMediaFileName = domain.getImage();
        	uploadFilePath = this.uploadFile(mediaFile);
        	
        	if (uploadFilePath == null) {
        		final ObjectError error = this.createProcessingError(result);
            	result.addError(error);
            	
            	dto.setId(id);
            	return this.getFormView();
        	}
        	
        	final String uploadFileName = uploadFilePath.getFileName().toString();
        	domain.setImage(uploadFileName);
        }
        
        domain.setName(dto.getName());
        
        final SessionGroup savedDomain = this.mainService.update(domain);
        
        // Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {
        	if (uploadFilePath != null) {
	        	try {
					Files.delete(uploadFilePath);
				} catch (IOException ex) {
					this.logger.error("Cannot delete media file", ex);
				}
        	}
        	
        	return this.getFormView();
        }
        
        // Success, delete old image file
        this.deleteUploadFile(deleteMediaFileName);
        
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL() + "/current-page";
    }
	
	@PostMapping(value = { "/delete/{id}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final SessionGroup domain = this.mainService.getById(id);
		if (domain == null) {
			final String message = this.getIdNotFoundMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final String deleteMediaFileName = domain.getImage();
		
		final boolean result = this.mainService.delete(id);
		if (result) {
			// Success, delete old image file
			this.deleteUploadFile(deleteMediaFileName);
	        
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