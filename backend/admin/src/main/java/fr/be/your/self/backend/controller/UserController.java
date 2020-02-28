package fr.be.your.self.backend.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.dto.SubscriptionDto;
import fr.be.your.self.backend.dto.UserDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.FunctionalityService;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + UserController.NAME)
public class UserController extends BaseResourceController<User, User, UserDto, Integer>  {
	public static final String NAME = "users";

	public static String CSV_SUBSCRIPTION_EXPORT_FILE = "subscription.csv";
	
	private static final String ACTIVATE_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.AUTHENTICATION_PREFIX 
			+ Constants.PATH.AUTHENTICATION.ACTIVATE;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	PermissionService permissionService;

	@Autowired
	FunctionalityService functionalityService;
	
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private DataSetting dataSetting;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	private static final Set<String> SORTABLE_COLUMNS = new HashSet<String>();

	static {
		SORTABLE_COLUMNS.add("name");
		//TODO TVA add sortable columns
	}
	
	@Override
	protected String getFormView() {
		return "users/user-form";
	}
	
	@Override
	protected String getListView() {
		return "users/user-list";
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
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "User management");
	}

	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.SESSION;
	}

	@Override
	protected User newDomain() {
		return new User();
	}

	@Override
	protected UserDto createDetailDto(User domain) {
		UserDto userDto =  new UserDto(domain);
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
	protected Set<String> getSortableColumns() {
		return SORTABLE_COLUMNS;
	}

	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, User domain, UserDto dto) throws BusinessException {
		int editAccType = UserPermission.DENIED.getValue();
		int editPermissions = UserPermission.DENIED.getValue();
				
		PermissionDto permission = (PermissionDto) model.getAttribute("permission");
		
		editAccType = permission.getPermission(UserConstants.EDIT_ACCOUNT_TYPE_PATH);
		editPermissions = permission.getPermission(UserConstants.EDIT_PERMISSIONS_PATH);
		//addDefaultPermissions(dto);
		
		model.addAttribute("editAccType", editAccType);
		model.addAttribute("editPermissions", editPermissions);
	}
	
	private void addDefaultPermissions(UserDto userdto) {
		Iterable<Functionality> functionalities = functionalityService.findAll();
		List<Permission> permissions = new ArrayList<Permission>();

		for (Functionality func : functionalities) {
			Permission permission = new Permission();
			permission.setFunctionality(func);
			permission.setUserPermission(UserPermission.DENIED.getValue());
			permissions.add(permission);
		}
		
		userdto.setPermissions(permissions);
	}
	
	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@Validated @ModelAttribute("dto") UserDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
                
        final User user = this.newDomain();
        dto.copyToDomain(user);
        
		boolean isAdminUser = UserUtils.isAdmin(user);
		boolean isAutoActivateAccount = isAdminUser ? this.dataSetting.isAutoActivateAdminAccount()
				: this.dataSetting.isAutoActivateAccount();
		
		String tempPwd = null;
		User savedUser;
		if (user.getLoginType() == LoginType.PASSWORD.getValue()) {
			tempPwd = UserUtils.generateRandomPassword(this.dataSetting.getTempPwdLength());
					
			String encodedPwd = passwordEncoder.encode(tempPwd);
			user.setPassword(encodedPwd);
		}

		if (userService.existsEmail(user.getEmail())) {
			final ObjectError error = this.createRequiredFieldError(result, "Email", "E-mail already exists!");
        	result.addError(error);
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
		}
			
		if (isAutoActivateAccount) {
			user.setStatus(UserStatus.ACTIVE.getValue());
		} else {
			setActivateCodeAndTimeout(user);
			user.setStatus(UserStatus.DRAFT.getValue());
		}
		savedUser = userService.create(user);

		//Error
        if (savedUser == null || result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
		//For normal user, default value = "Denied"
		for (Permission permission : dto.getPermissions()) {
			permission.setUser(savedUser); // We need user id of saved user
			permissionService.saveOrUpdate(permission);
		}
		
		if (!isAutoActivateAccount) {
			if (savedUser.getStatus() == UserStatus.DRAFT.getValue()) {
				String activateAccountUrl = buildActivateAccountUrl(request);
				boolean success = sendVerificationEmailToUser(activateAccountUrl, savedUser);
				// TODO: Use success variable?
			}
			
			if (tempPwd != null) {
				this.emailSender.sendTemporaryPassword(savedUser.getEmail(), tempPwd);
			}
		}
        
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "create");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL();
    }
	
	
	@PostMapping("/update/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") Integer id, 
    		@ModelAttribute @Validated UserDto dto, 
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
        
        dto.copyToDomain(domain);
        
        final User updatedDomain = this.userService.update(domain);
        for (Permission permission : dto.getPermissions()) {
			permission.setUser(domain);
			permissionService.saveOrUpdate(permission);
		}

        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL() + "/current-page";
    }
	
	//generate a new activation code and timeout for a user
	private void setActivateCodeAndTimeout(User user) {
		String activateCode = StringUtils.randomAlphanumeric(this.dataSetting.getActivateCodeLength());
		long activateCodeTimeout = (new Date().getTime() / (60 * 1000))
				+ this.dataSetting.getActivateCodeTimeout();

		user.setActivateCode(activateCode);
		user.setActivateTimeout(activateCodeTimeout);
	}
	
	private String buildActivateAccountUrl(HttpServletRequest request) {
		String activateAccountUrl = request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath();

		if (activateAccountUrl.endsWith("/")) {
			activateAccountUrl = activateAccountUrl.substring(0, activateAccountUrl.length() - 1);
		}
		activateAccountUrl += ACTIVATE_URL;

		return activateAccountUrl;
	}
	
	private boolean sendVerificationEmailToUser(String activateAccountUrl, User savedUser) {

		boolean success = this.emailSender.sendActivateUser(savedUser.getEmail(), activateAccountUrl,
				savedUser.getActivateCode());
		return success;
	}
	
	

}