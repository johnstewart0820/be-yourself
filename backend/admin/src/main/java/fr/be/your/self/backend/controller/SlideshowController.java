package fr.be.your.self.backend.controller;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import fr.be.your.self.backend.dto.SlideshowDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Slideshow;
import fr.be.your.self.model.SlideshowImage;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SlideshowService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SlideshowController.NAME)
public class SlideshowController extends BaseResourceController<Slideshow, SlideshowDto, SlideshowDto, Integer> {
	
	public static final String NAME = "slideshow";
	
	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.FOLDER.MEDIA.SLIDE_SHOW;
	
	private static final Map<String, String[]> SORTABLE_COLUMNS = new HashMap<>();
	
	static {
		SORTABLE_COLUMNS.put("date", new String[] { "startDate", "endDate" });
	}
	
	@Autowired
	private SlideshowService mainService;
	
	@Override
	protected String getName() {
		return NAME;
	}
	
	@Override
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "Slideshow management");
	}
	
	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.SLIDE_SHOW;
	}
	
	@Override
	protected BaseService<Slideshow, Integer> getService() {
		return this.mainService;
	}
	
	@Override
	protected Map<String, String[]> getSortableColumns() {
		return SORTABLE_COLUMNS;
	}

	@Override
	protected Slideshow newDomain() {
		return new Slideshow();
	}

	@Override
	protected SlideshowDto createDetailDto(Slideshow domain) {
		return new SlideshowDto(domain);
	}

	@Override
	protected SlideshowDto createSimpleDto(Slideshow domain) {
		return new SlideshowDto(domain);
	}

	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}
	
	@Override
	protected String getListView() {
		return "pages/slideshow-form";
	}

	@Override
	protected String getFormView() {
		return "pages/slideshow-form";
	}

	@Override
	protected PageableResponse<Slideshow> pageableSearch(Map<String, String> searchParams, PageRequest pageable, Sort sort) {
		final Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		
		return this.mainService.searchAvailaible(today, null, sort);
	}

	@Override
	protected void loadListPageOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Map<String, String> searchParams, PageableResponse<SlideshowDto> pageableDto)
			throws BusinessException {
		super.loadListPageOptions(session, request, response, model, searchParams, pageableDto);
		
		final String supportImageTypes = String.join(",", this.dataSetting.getImageMimeTypes());
		final String supportImageExtensions = String.join(",", this.dataSetting.getImageFileExtensions());
		final long supportImageSize = this.dataSetting.getImageMaxFileSize();
		
		model.addAttribute("supportImageTypes", supportImageTypes);
		model.addAttribute("supportImageExtensions", supportImageExtensions);
		model.addAttribute("supportImageSize", supportImageSize);
		model.addAttribute("supportImageSizeLabel", StringUtils.formatFileSize(supportImageSize));
		
		final Slideshow mainSlideshow = this.mainService.getMainSlideshow();
		final SlideshowDto mainDto = new SlideshowDto(mainSlideshow);
		
		model.addAttribute("mainDto", mainDto);
		
		final SlideshowDto newScheduleDto = new SlideshowDto();
		newScheduleDto.setStartDate(new Date());
		model.addAttribute("newScheduleDto", newScheduleDto);
	}

	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Slideshow domain, SlideshowDto dto) throws BusinessException {
		super.loadDetailFormOptions(session, request, response, model, domain, dto);
		
		final String supportImageTypes = String.join(",", this.dataSetting.getImageMimeTypes());
		final String supportImageExtensions = String.join(",", this.dataSetting.getImageFileExtensions());
		final long supportImageSize = this.dataSetting.getImageMaxFileSize();
		
		model.addAttribute("supportImageTypes", supportImageTypes);
		model.addAttribute("supportImageExtensions", supportImageExtensions);
		model.addAttribute("supportImageSize", supportImageSize);
		model.addAttribute("supportImageSizeLabel", StringUtils.formatFileSize(supportImageSize));
		
		final Slideshow mainSlideshow = this.mainService.getMainSlideshow();
		final SlideshowDto mainDto = new SlideshowDto(mainSlideshow);
		
		model.addAttribute("mainDto", mainDto);
	}
	
	@PostMapping("/create-schedule")
	@Transactional
    public String createDomain(
    		@ModelAttribute("newScheduleDto") @Validated SlideshowDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
		if (result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        // ====> Validate image file
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile == null || uploadImageFile.isEmpty()) {
        	final ObjectError error = this.createRequiredFieldError(result, "image", "Image required");
        	result.addError(error);
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        // ====> Process upload image file
        final Path uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
        if (uploadImageFilePath == null) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        // ====> Update domain
        final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
        
        final Slideshow domain = this.newDomain();
        dto.copyToDomain(domain);
        
        try {
        	final Slideshow savedDomain = this.mainService.create(domain);
        	
        	// ====> Error, delete upload file
            if (savedDomain == null || result.hasErrors()) {
            	this.deleteUploadFile(uploadImageFilePath);
            	
            	if (!result.hasErrors()) {
    	        	final ObjectError error = this.createProcessingError(result);
    	        	result.addError(error);
            	}
            	
            	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
            }
	        
            // ====> Create new image
	        final SlideshowImage newImage = new SlideshowImage();
	        newImage.setSlideshow(domain);
	        newImage.setImage(uploadImageFileName);
	        newImage.setIndex(1);
	        
	        final SlideshowImage savedImage = this.mainService.createImage(newImage);
	        
	        // ====> Error, delete upload file
	        if (savedImage == null || result.hasErrors()) {
	        	this.deleteUploadFile(uploadImageFilePath);
	        	
	        	if (!result.hasErrors()) {
		        	final ObjectError error = this.createProcessingError(result);
		        	result.addError(error);
	        	}
	        	
	        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
	        }
	        
	        // ====> Success
	        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update.main");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
	        
	        return "redirect:" + this.getBaseURL() + "/current-page";
        } catch (Exception ex) {
        	this.deleteUploadFile(uploadImageFilePath);
        	
        	throw ex;
		}
    }
	
	@PostMapping("/update-main/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") Integer id, 
    		@ModelAttribute("mainDto") @Validated SlideshowDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        Slideshow domain = this.mainService.getById(id);
        if (domain != null && (domain.getStartDate() != null || domain.getEndDate() != null)) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        if (domain == null) {
        	domain = this.mainService.getMainSlideshow();
        	
        	if (domain == null) {
        		Slideshow defaultSlideshow = new Slideshow();
        		defaultSlideshow.setId(1);
        		domain = this.mainService.create(defaultSlideshow);
        		

                if (domain == null) {
                	final ObjectError error = this.createIdNotFoundError(result, id);
                	result.addError(error);
                	
                	dto.setId(id);
                	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
                }
        	}
        }
        
        // ====> Validate image file
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile == null || uploadImageFile.isEmpty()) {
        	final ObjectError error = this.createRequiredFieldError(result, "image", "Image required");
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        // ====> Process upload image file
        final Path uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
        if (uploadImageFilePath == null) {
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
        // ====> Create new image
        try {
	        final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
	        
	        final SlideshowImage newImage = new SlideshowImage();
	        newImage.setSlideshow(domain);
	        newImage.setImage(uploadImageFileName);
	        newImage.setIndex(this.mainService.getMaxImageIndex(id) + 1);
	        
	        final SlideshowImage savedImage = this.mainService.createImage(newImage);
	        
	        // ====> Error
	        if (savedImage == null || result.hasErrors()) {
	        	this.deleteUploadFile(uploadImageFilePath);
	        	
	        	if (!result.hasErrors()) {
		        	final ObjectError error = this.createProcessingError(result);
		        	result.addError(error);
	        	}
	        	
	        	dto.setId(id);
	        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
	        }
	        
	        // ====> Success
	        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update.main");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
	        
	        return "redirect:" + this.getBaseURL() + "/current-page";
        } catch (Exception ex) {
        	this.deleteUploadFile(uploadImageFilePath);
        	
        	throw ex;
		}
    }
	
	@PostMapping("/update-schedule/{id}")
	@Transactional
    public String updateScheduleDomain(
    		@PathVariable("id") Integer id, 
    		MultipartFile uploadImageFile, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, null);
        }
        
        Slideshow domain = this.mainService.getById(id);
        if (domain == null || domain.getStartDate() == null || domain.getEndDate() == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, null);
        }
        
        // ====> Validate image file
        if (uploadImageFile == null || uploadImageFile.isEmpty()) {
        	final ObjectError error = this.createRequiredFieldError(result, "image", "Image required");
        	result.addError(error);
        	
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, null);
        }
        
        // ====> Process upload image file
        final Path uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
        if (uploadImageFilePath == null) {
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, null);
        }
        
        // ====> Create new image
        try {
	        final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
	        
	        final SlideshowImage newImage = new SlideshowImage();
	        newImage.setSlideshow(domain);
	        newImage.setImage(uploadImageFileName);
	        newImage.setIndex(this.mainService.getMaxImageIndex(id) + 1);
	        
	        final SlideshowImage savedImage = this.mainService.createImage(newImage);
	        
	        // ====> Error
	        if (savedImage == null || result.hasErrors()) {
	        	this.deleteUploadFile(uploadImageFilePath);
	        	
	        	if (!result.hasErrors()) {
		        	final ObjectError error = this.createProcessingError(result);
		        	result.addError(error);
	        	}
	        	
	        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, null);
	        }
	        
	        // ====> Success
	        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update.main");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
	        
	        return "redirect:" + this.getBaseURL() + "/current-page";
        } catch (Exception ex) {
        	this.deleteUploadFile(uploadImageFilePath);
        	
        	throw ex;
		}
    }
	
	@PostMapping(value = { "/delete-image/{type}/{imageId}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "type", required = true) String slideshowType,
    		@PathVariable(name = "imageId", required = true) Integer imageId,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final SlideshowImage domain = this.mainService.getImage(imageId);
		if (domain == null) {
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete." + slideshowType + ".image");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		final String deleteImageFileName = domain.getImage();
		
		final boolean result = this.mainService.deleteImage(imageId);
		if (result) {
			// ====> Success, delete old image file
			this.deleteUploadFile(deleteImageFileName);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete." + slideshowType + ".image");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
			
			return "redirect:" + this.getBaseURL();
		}
		
		redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete." + slideshowType + ".image");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
        
		return "redirect:" + this.getBaseURL() + "/current-page";
	}
}
