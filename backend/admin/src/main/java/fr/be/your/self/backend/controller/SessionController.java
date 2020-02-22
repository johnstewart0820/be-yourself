package fr.be.your.self.backend.controller;

import java.nio.file.Path;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.SessionDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Session;
import fr.be.your.self.model.SessionCategory;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SessionCategoryService;
import fr.be.your.self.service.SessionService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SessionController.NAME)
public class SessionController extends BaseResourceController<Session, Session, SessionDto> {
	
	public static final String NAME = "session";
	
	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.FOLDER.MEDIA.SESSION;
	
	@Autowired
	private SessionService mainService;
	
	@Autowired
	private SessionCategoryService sessionCategoryService;
	
	@Override
	protected String getName() {
		return NAME;
	}
	
	@Override
	protected String getDefaultPageTitle() {
		return this.getMessage(this.getName() + ".page.title", "Session management");
	}
	
	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.SESSION;
	}
	
	@Override
	protected BaseService<Session> getService() {
		return this.mainService;
	}
	
	@Override
	protected Session newDomain() {
		return new Session();
	}

	@Override
	protected SessionDto createDetailDto(Session domain) {
		final SessionDto dto = new SessionDto(domain);
		
		if (this.isValidAudioContentType(dto.getContentMimeType())) {
			dto.setContentFileType(Constants.MEDIA_TYPE.AUDIO);
		} else if (this.isValidVideoContentType(dto.getContentMimeType())) {
			dto.setContentFileType(Constants.MEDIA_TYPE.VIDEO);
		} else {
			dto.setContentFileType(Constants.MEDIA_TYPE.IMAGE);
		}
		
		return dto;
	}

	@Override
	protected Session createSimpleDto(Session domain) {
		return domain;
	}

	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}
	
	@Override
	protected void loadDetailForm(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Session domain, SessionDto dto) throws BusinessException {
		super.loadDetailForm(session, request, response, model, domain, dto);
		
		final String supportImageTypes = String.join(",", this.dataSetting.getImageMimeTypes());
		final String supportImageExtensions = String.join(",", this.dataSetting.getImageFileExtensions());
		final long supportImageSize = this.dataSetting.getImageMaxFileSize();
		
		final String supportAudioTypes = String.join(",", this.dataSetting.getAudioMimeTypes());
		final String supportAudioExtensions = String.join(",", this.dataSetting.getAudioFileExtensions());
		final long supportAudioSize = this.dataSetting.getAudioMaxFileSize();
		
		final String supportVideoTypes = String.join(",", this.dataSetting.getVideoMimeTypes());
		final String supportVideoExtensions = String.join(",", this.dataSetting.getVideoFileExtensions());
		final long supportVideoSize = this.dataSetting.getVideoMaxFileSize();
		
		final String supportMediaTypes = String.join(",", this.dataSetting.getMediaMimeTypes());
		final String supportMediaExtensions = String.join(",", this.dataSetting.getMediaFileExtensions());
		final long supportMediaSize = supportVideoSize > supportAudioSize ? supportVideoSize : supportAudioSize;
		
		model.addAttribute("supportImageTypes", supportImageTypes);
		model.addAttribute("supportImageExtensions", supportImageExtensions);
		model.addAttribute("supportImageSize", supportImageSize);
		model.addAttribute("supportImageSizeLabel", StringUtils.formatFileSize(supportImageSize));
		
		model.addAttribute("supportAudioTypes", supportAudioTypes);
		model.addAttribute("supportAudioExtensions", supportAudioExtensions);
		model.addAttribute("supportAudioSize", supportAudioSize);
		model.addAttribute("supportAudioSizeLabel", StringUtils.formatFileSize(supportAudioSize));
		
		model.addAttribute("supportVideoTypes", supportVideoTypes);
		model.addAttribute("supportVideoExtensions", supportVideoExtensions);
		model.addAttribute("supportVideoSize", supportVideoSize);
		model.addAttribute("supportVideoSizeLabel", StringUtils.formatFileSize(supportVideoSize));
		
		model.addAttribute("supportMediaTypes", supportMediaTypes);
		model.addAttribute("supportMediaExtensions", supportMediaExtensions);
		model.addAttribute("supportMediaSize", supportMediaSize);
		model.addAttribute("supportMediaSizeLabel", StringUtils.formatFileSize(supportMediaSize));
		
		final List<SessionCategory> sessionCategories = this.sessionCategoryService.getAll();
		model.addAttribute("sessionCategories", sessionCategories);
	}

	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@ModelAttribute @Validated SessionDto dto, 
    		@RequestParam(value = "categoryIds" , required = false) int[] categoryIds,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	return this.getFormView();
        }
        
        // ====> Session category
        //final Set<Integer> categoryIds = dto.getCategoryIds();
        final List<SessionCategory> categories = this.sessionCategoryService.getByIds(categoryIds);
        
        // ====> Validate image and content file
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile == null || uploadImageFile.isEmpty()) {
        	final ObjectError error = this.createFieldError(result, "image", "required", "Image is required");
        	result.addError(error);
        	
        	return this.getFormView();
        }
        
        final MultipartFile uploadContentFile = dto.getUploadContentFile();
        if (uploadContentFile == null || uploadContentFile.isEmpty()) {
        	final ObjectError error = this.createFieldError(result, "contentFile", "required", "Content file is required");
        	result.addError(error);
        	
        	return this.getFormView();
        }
        
        // ====> Process upload image and content file
        final Path uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
        if (uploadImageFilePath == null) {
        	return this.getFormView();
        }
        
        final Path uploadContentFilePath = this.processUploadContentFile(uploadContentFile, result);
        if (uploadContentFilePath == null) {
        	this.deleteUploadFile(uploadImageFilePath);
        	
        	return this.getFormView();
        }
        
        // ====> Update domain
        final String uploadFileName = uploadImageFilePath.getFileName().toString();
        final String uploadContentFileName = uploadContentFilePath.getFileName().toString();
        final String contentFileContentType = this.getFileContentType(uploadContentFilePath);
        
        final Session domain = this.newDomain();
        dto.copyToDomain(domain);
        
        domain.setCategories(categories);
        domain.setImage(uploadFileName);
        domain.setContentFile(uploadContentFileName);
        domain.setContentMimeType(contentFileContentType);
        
        final Session savedDomain = this.mainService.create(domain);
        
        // ====> Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {
        	this.deleteUploadFile(uploadImageFilePath);
        	this.deleteUploadFile(uploadContentFilePath);
        	
        	if (!result.hasErrors()) {
	        	final ObjectError error = this.createProcessingError(result);
	        	result.addError(error);
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
    		@ModelAttribute @Validated SessionDto dto, 
    		@RequestParam(value = "categoryIds" , required = false) int[] categoryIds,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.getFormView();
        }
        
        Session domain = this.mainService.getById(id);
        if (domain == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        dto.copyToDomain(domain);
        
        // ====> Session category
        //final Set<Integer> categoryIds = dto.getCategoryIds();
        final List<SessionCategory> categories = this.sessionCategoryService.getByIds(categoryIds);
        domain.setCategories(categories);
        
        // ====> Process upload image and content file
        String deleteImageFileName = null;
        Path uploadImageFilePath = null;
        
        String deleteContentFileName = null;
        Path uploadContentFilePath = null;
        
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile != null && !uploadImageFile.isEmpty()) {
        	deleteImageFileName = domain.getImage();
        	
        	// ====> Process upload image file
        	uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
            if (uploadImageFilePath == null) {
            	dto.setId(id);
            	return this.getFormView();
            }
            
            final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
        	domain.setImage(uploadImageFileName);
        }
        
        final MultipartFile uploadContentFile = dto.getUploadContentFile();
        if (uploadContentFile != null && !uploadContentFile.isEmpty()) {
        	deleteContentFileName = domain.getContentFile();
        	
        	// ====> Process upload content file
        	uploadContentFilePath = this.processUploadContentFile(uploadContentFile, result);
        	if (uploadContentFilePath == null) {
    			this.deleteUploadFile(uploadImageFilePath);
    			
            	dto.setId(id);
            	return this.getFormView();
        	}
        	
        	final String uploadContentFileName = uploadContentFilePath.getFileName().toString();
        	domain.setContentFile(uploadContentFileName);
        }
        
        final Session savedDomain = this.mainService.update(domain);
        
        // ====> Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {
        	this.deleteUploadFile(uploadImageFilePath);
        	this.deleteUploadFile(uploadContentFilePath);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        // ====> Success, delete old image file
        this.deleteUploadFile(deleteImageFileName);
        this.deleteUploadFile(deleteContentFileName);
        
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
		
		final Session domain = this.mainService.getById(id);
		if (domain == null) {
			final String message = this.getIdNotFoundMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final String deleteImageFileName = domain.getImage();
		final String deleteContentFileName = domain.getContentFile();
		
		final boolean result = this.mainService.delete(id);
		if (result) {
			// Success, delete old media files
			this.deleteUploadFile(deleteImageFileName);
			this.deleteUploadFile(deleteContentFileName);
	        
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
