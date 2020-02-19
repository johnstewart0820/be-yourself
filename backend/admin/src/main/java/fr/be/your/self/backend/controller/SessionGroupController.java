package fr.be.your.self.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
		
		final String supportImageTypes = String.join(",", this.dataSetting.getImageMediaTypes());
		final String supportImageExtensions = String.join(",", this.dataSetting.getImageFileExtensions());
		
		model.addAttribute("supportImageTypes", supportImageTypes);
		model.addAttribute("supportImageExtensions", supportImageExtensions);
		model.addAttribute("supportImageSize", this.dataSetting.getImageMaxFileSize());
		model.addAttribute("supportImageSizeLabel", StringUtils.formatFileSize(this.dataSetting.getImageMaxFileSize()));
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
        
        final MultipartFile mediaFile = dto.getImageFile();
        if (mediaFile == null || mediaFile.isEmpty()) {
        	final ObjectError error = this.createFieldError(result, "image", this.getName() + ".image.required", "Image required");
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
        	final ObjectError error = this.createIdNotFoundError(result);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        String deleteMediaFileName = null;
        Path uploadFilePath = null;
        
        final MultipartFile mediaFile = dto.getImageFile();
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
        if (!StringUtils.isNullOrSpace(deleteMediaFileName)) {
        	final Path mediaFilePath = Paths.get(this.dataSetting.getSessionGroupFolder() + "/" + deleteMediaFileName);
        	
        	try {
				Files.delete(mediaFilePath);
			} catch (IOException ex) {
				this.logger.error("Cannot delete media file", ex);
			}
        }
        
        return "redirect:" + this.getBaseURL() + "/current-page";
    }
	
	@PostMapping(value = { "/delete/{id}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final String baseMessageKey = this.getName().replace('-', '.');
		final String dataName = this.getMessage(baseMessageKey + ".name");
		
		final SessionGroup domain = this.mainService.getById(id);
		if (domain == null) {
			String message = this.getMessage("error.message.id.not.found", new String[] { dataName, id.toString() });
			if (message.startsWith(dataName)) {
				message = StringUtils.upperCaseFirst(message);
			}
			
			redirectAttributes.addAttribute("status", false);
			redirectAttributes.addAttribute("message", message);
			
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final String deleteMediaFileName = domain.getImage();
		
		final boolean result = this.mainService.delete(id);
		if (result) {
			// Success, delete old image file
	        if (!StringUtils.isNullOrSpace(deleteMediaFileName)) {
	        	final Path mediaFilePath = Paths.get(this.dataSetting.getSessionGroupFolder() + "/" + deleteMediaFileName);
	        	
	        	try {
					Files.delete(mediaFilePath);
				} catch (IOException ex) {
					this.logger.error("Cannot delete media file", ex);
				}
	        }
	        
			String message = this.getMessage("success.message.delete", new String[] { dataName, id.toString() });
			if (message.startsWith(dataName)) {
				message = StringUtils.upperCaseFirst(message);
			}

			redirectAttributes.addAttribute("status", result);
			redirectAttributes.addAttribute("message", message);
			
			return "redirect:" + this.getBaseURL();
		}
		
		String message = this.getMessage("error.message.cannot.delete.id", new String[] { dataName, id.toString() });
		if (message.startsWith(dataName)) {
			message = StringUtils.upperCaseFirst(message);
		}
		
		redirectAttributes.addAttribute("status", result);
		redirectAttributes.addAttribute("message", message);
		
		return "redirect:" + this.getBaseURL() + "/current-page";
	}
	
	private Path uploadFile(final MultipartFile mediaFile) {
		final String fileName = mediaFile.getOriginalFilename();
        final int dotIndex = fileName.lastIndexOf(".");
        final String fileExtension = dotIndex > 0 ? fileName.substring(dotIndex) : ".png";
        
        final String uploadDirectoryName = this.dataSetting.getSessionGroupFolder();
        
        try {
        	final File directory = new File(uploadDirectoryName);
	        if (!directory.exists()){
	            directory.mkdirs();
	        }
        } catch (Exception ex) {
        	this.logger.error("Cannot create media directory", ex);
        	
			return null;
		}
        
        final String uploadFileName = UUID.randomUUID().toString() + fileExtension;
        final Path uploadFilePath = Paths.get(uploadDirectoryName + "/" + uploadFileName);
        
        try {
	        final byte[] mediaBytes = mediaFile.getBytes();
	        Files.write(uploadFilePath, mediaBytes);
        } catch(Exception ex) {
        	this.logger.error("Cannot write media data", ex);
        	
        	return null;
        }
        
        return uploadFilePath;
	}
}
