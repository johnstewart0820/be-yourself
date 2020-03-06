package fr.be.your.self.backend.controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.dto.UserDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.utils.AdminUtils;
import fr.be.your.self.common.FileType;
import fr.be.your.self.common.FormationType;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Address;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.Price;
import fr.be.your.self.model.ProfessionalEvent;
import fr.be.your.self.model.SessionCategory;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.model.UserFile;
import fr.be.your.self.model.DegreeFile;
import fr.be.your.self.model.MediaFile;
import fr.be.your.self.service.AddressService;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.DegreeFileService;
import fr.be.your.self.service.MediaFileService;
import fr.be.your.self.service.PriceService;
import fr.be.your.self.service.ProfessionalEventService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.NumberUtils;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + ProfessionalController.NAME)
public class ProfessionalController extends BaseResourceController<User, User, UserDto, Integer> {
	public static final String NAME = "professional";

	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.WEB_ADMIN.MEDIA 
			+ Constants.FOLDER.MEDIA.PROFESSIONAL;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PriceService priceService; 
	
	@Autowired
	private DegreeFileService degreeFileService; 
	
	@Autowired
	private MediaFileService mediaFileService;
	
	@Autowired
	private ProfessionalEventService professionalEventService;

	private static final Map<String, String[]> SORTABLE_COLUMNS = new HashMap<>();
	
	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}

	@Override
	protected BaseService<User, Integer> getService() {
		return userService;
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getFormView() {
		return "professional/professional-form";
	}

	@Override
	protected String getListView() {
		return "professional/professional-list";
	}

	@Override
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "Professional management");
	}

	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.PROFESSIONAL;

	}

	@Override
	protected User newDomain() {
		return new User();
	}

	@Override
	protected UserDto createDetailDto(User domain) {
		UserDto userDto = new UserDto(domain);
		if (domain == null) {
			addDefaultPermissions(userDto);
		}
		return userDto;
	}

	@Override
	protected User createSimpleDto(User domain) {
		return domain;
	}

	@Override
	protected Map<String, String[]> getSortableColumns() {
		return SORTABLE_COLUMNS;
	}

	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, User domain, UserDto dto) throws BusinessException {
		List<String> formations = FormationType.getPossibleStrValues();

		model.addAttribute("formations", formations);

	}
	
	@Override
	protected PageableResponse<User> pageableSearch(Map<String, String> searchParams, PageRequest pageable, Sort sort) {
		final String search = searchParams.get("q");
		return this.userService.pageableSearchPro(search, pageable, sort);
	}

	@PostMapping("/create")
	public String createDomain(@Validated @ModelAttribute("dto") UserDto dto, HttpSession session,
			HttpServletRequest request, HttpServletResponse response, BindingResult result,
			RedirectAttributes redirectAttributes, Model model) {

		if (result.hasErrors()) {
			return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
		}

		final User user = this.newDomain();
		dto.copyToDomainOfProfessional(user);
		user.setUserType(UserType.PROFESSIONAL.getValue());
		user.setLoginType(LoginType.PASSWORD.getValue());

		//Set temporary password and activation code
		String tempPwd = UserUtils.assignPassword(user, getPasswordEncoder(), this.dataSetting.getTempPwdLength());
		setActivateCodeAndTimeout(user);
		user.setStatus(UserStatus.DRAFT.getValue());

		//Create Address
		user.setAddress(dto.getAddress());

		//Add prices
		if (dto.getPrices() != null) {
			for (Price price : dto.getPrices()) {
				price.setUser(user);
			}
		}
		user.setPrices(dto.getPrices());
		
		
		//Add events
		if (dto.getEvents() != null) {
			for (ProfessionalEvent event : dto.getEvents()) {
				event.setUser(user);
			}
			user.setEvents(dto.getEvents());
		}
		
		 //Validate image file
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile == null || uploadImageFile.isEmpty()) {
        	final ObjectError error = this.createRequiredFieldError(result, "image", "Image required");
        	result.addError(error);
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        //Process upload image file
        final Path uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
        if (uploadImageFilePath == null) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        //Set profile picture
        final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
        user.setProfilePicture(uploadImageFileName);
        
        
        //Degrees
        List<MultipartFile> degrees = dto.getDegrees();
        List<DegreeFile> degreesList = new ArrayList<>();
        for (MultipartFile file : degrees) {
        	if (StringUtils.isNullOrEmpty(file.getOriginalFilename())){
        		continue;
        	}
        	Path uploadFilePath = uploadFile(file, true);
        	DegreeFile userFile = new DegreeFile(uploadFilePath.getFileName().toString());
        	userFile.setUser(user);
        	degreesList.add(userFile);
        }
        user.setDegreeFiles(degreesList);
        
        //Medias
        List<MultipartFile> medias = dto.getMedias();
        List<MediaFile> mediasList = new ArrayList<>();
        for (MultipartFile file : medias) {
        	if (StringUtils.isNullOrEmpty(file.getOriginalFilename())){
        		continue;
        	}
        	Path uploadFilePath = uploadFile(file, true);
        	MediaFile userFile = new MediaFile(uploadFilePath.getFileName().toString());
        	userFile.setUser(user);
        	mediasList.add(userFile);
        }
        user.setMediaFiles(mediasList);
        
        
		User savedUser = userService.create(user);
		

		//Add default permission value = "Denied" to professionals
		addDefaultPermissions(savedUser);
		for (Permission permission : savedUser.getPermissions()) {
			permission.setUser(savedUser); // We need user id of saved user
			this.getPermissionService().saveOrUpdate(permission);
		}
		
		
		String activateAccountUrl = AdminUtils.buildActivateAccountUrl(request);
		sendVerificationEmailToUser(activateAccountUrl, savedUser);
		this.getEmailSender().sendTemporaryPassword(savedUser.getEmail(), tempPwd);
		

		return "redirect:" + this.getBaseURL();
	}
	
	@PostMapping("/update/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") Integer id, 
    		@ModelAttribute @Validated UserDto dto, 
    		@RequestParam(value="priceIdsToRemove", required=false) String priceIdsToRemove,
    		@RequestParam(value="degreeIdsToRemove", required=false) String degreeIdsToRemove, 
    		@RequestParam(value="mediaIdsToRemove", required=false) String mediaIdsToRemove, 
    		@RequestParam(value="eventIdsToRemove", required=false) String eventIdsToRemove, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.getFormView();
        }
       
        User domain = this.userService.getById(id);
                
        if (domain == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        dto.copyToDomainOfProfessional(domain);
             
        
        // Process upload image and content file
        String oldPictureToDelete = null;
        Path uploadImageFilePath = null;
        
        final MultipartFile uploadImageFile = dto.getUploadImageFile();
        if (uploadImageFile != null && !uploadImageFile.isEmpty()) {
        	oldPictureToDelete = domain.getProfilePicture();
        	
        	//Process upload image file
        	uploadImageFilePath = this.processUploadImageFile(uploadImageFile, result);
            if (uploadImageFilePath == null) {
            	dto.setId(id);
            	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
            }
            
            final String uploadImageFileName = uploadImageFilePath.getFileName().toString();
        	domain.setProfilePicture(uploadImageFileName);
        }

  		
        final User updatedDomain = this.userService.update(domain);
       //Error, delete upload file
        if (updatedDomain == null || result.hasErrors()) {
        	this.deleteUploadFile(uploadImageFilePath);
        	
        	if (!result.hasErrors()) {
	        	final ObjectError error = this.createProcessingError(result);
	        	result.addError(error);
        	}
        	
        	dto.setId(id);
        	return this.redirectEditPage(session, request, response, redirectAttributes, model, id, dto);
        }
        
       
		// Update prices
        if (dto.getPrices() !=  null) {
			for (Price price : dto.getPrices()) {
				if (price.getLabel() != null && price.getPrice() != null) {
					price.setUser(domain);
					priceService.update(price);
				}
			}
        }
        List<Integer> priceIdList = parseFromString(priceIdsToRemove);
        removeIds(priceService, priceIdList);  
        
		// Update degrees
        List<DegreeFile> uploadDegreeFileList = getUploadDegreeList(dto, domain);
		degreeFileService.saveAll(uploadDegreeFileList);
        List<Integer> degreeIdList = parseFromString(degreeIdsToRemove);
		deleteOldDegreeFile(degreeIdList);
        removeIds(degreeFileService, degreeIdList);
        
        // Update media files
        List<MediaFile> uploadMediaFileList = getUploadMediaList(dto, domain);
		mediaFileService.saveAll(uploadMediaFileList);
        List<Integer> mediaIdList = parseFromString(mediaIdsToRemove);
		deleteOldMediaFile(mediaIdList);
        removeIds(mediaFileService, mediaIdList);
        
        // Update events
        if (dto.getEvents() !=  null) {
			for (ProfessionalEvent event : dto.getEvents()) {
				if (event.getDate() != null && event.getAddress() != null &&
						event.getDescription() != null	&& event.getPrice() != null) {
					event.setUser(domain);
					professionalEventService.update(event);
				}
			}
        }
        
        List<Integer> eventIdList = parseFromString(eventIdsToRemove);
        removeIds(professionalEventService, eventIdList); 
        
        //Success, delete old profile picture
        this.deleteUploadFile(oldPictureToDelete);

        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL() + "/edit/" + id;
    }

	private List<DegreeFile> getUploadDegreeList(UserDto dto, User domain) {
		List<MultipartFile> degrees = dto.getDegrees();
		List<DegreeFile> uploadDegreeFileList = new ArrayList<>();
		for (MultipartFile file : degrees) {
			if (StringUtils.isNullOrEmpty(file.getOriginalFilename())){
        		continue;
        	}
			Path uploadFilePath = uploadFile(file, true);
			DegreeFile degreeFile = new DegreeFile(uploadFilePath.getFileName().toString());
			degreeFile.setUser(domain);
			uploadDegreeFileList.add(degreeFile);
		}
		return uploadDegreeFileList;
	}

	private List<MediaFile> getUploadMediaList(UserDto dto, User domain) {
		List<MultipartFile> medias = dto.getMedias();
		List<MediaFile> uploadMediaFileList = new ArrayList<>();
		for (MultipartFile file : medias) {
			if (StringUtils.isNullOrEmpty(file.getOriginalFilename())){
        		continue;
        	}
			Path uploadFilePath = uploadFile(file, true);
			MediaFile mediaFile = new MediaFile(uploadFilePath.getFileName().toString());
			mediaFile.setUser(domain);
			uploadMediaFileList.add(mediaFile);
		}
		return uploadMediaFileList;
	}
	
	private List<Integer> parseFromString(String idStr) {
		List<Integer> res = new ArrayList<>();
		if (!StringUtils.isNullOrEmpty(idStr)) {
			String[] removedIDs = idStr.split(",");
			for (int i = 0; i<removedIDs.length; i++) {
				res.add(Integer.valueOf(removedIDs[i]));
			}
		}
		return res;
	}

	private void deleteOldDegreeFile(List<Integer> degreeIdList) {
		for (Integer id : degreeIdList) {
			DegreeFile degreeFile = degreeFileService.getById(id);
			this.deleteUploadFile(degreeFile.getFilePath());
		}
	}
	
	private void deleteOldMediaFile(List<Integer> degreeIdList) {
		for (Integer id : degreeIdList) {
			MediaFile degreeFile = mediaFileService.getById(id);
			this.deleteUploadFile(degreeFile.getFilePath());
		}
	}

	private void removeIds(BaseService baseService, List<Integer> idList) {
		for (Integer id : idList) {
			baseService.delete(Integer.valueOf(id));
		}

	}

}
