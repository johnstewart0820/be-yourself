package fr.be.your.self.backend.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import fr.be.your.self.backend.dto.IdNameDto;
import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.dto.ResultStatus;
import fr.be.your.self.backend.dto.SessionSimpleDto;
import fr.be.your.self.backend.dto.SimpleResult;
import fr.be.your.self.backend.dto.SubscriptionDto;
import fr.be.your.self.backend.dto.UserDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.Session;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.FunctionalityService;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.NumberUtils;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + UserController.NAME)
public class UserController extends BaseResourceController<User, User, UserDto, Integer>  {
	public static final String NAME = "users";

	public static String CSV_USERS_EXPORT_FILE = "users.csv";
	
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
	private static final Map<String, String[]> SORTABLE_COLUMNS = new HashMap<>();

	static {
		SORTABLE_COLUMNS.put("name", new String[] { "name" });
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
	protected Map<String, String[]> getSortableColumns() {
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
		
		model.addAttribute("editAccType", editAccType);
		model.addAttribute("editPermissions", editPermissions);

	}
	
	private void addDefaultPermissions(UserDto userdto) {
		List<Permission> permissions = createDefaultPermissions(new User());
		userdto.setPermissions(permissions);
	}
	
	private void addDefaultPermissions(User user) {
		List<Permission> permissions = createDefaultPermissions(user);
		user.setPermissions(permissions);
	}

	private List<Permission> createDefaultPermissions(User user) {
		Iterable<Functionality> functionalities = functionalityService.findAll();
		List<Permission> permissions = new ArrayList<Permission>();

		for (Functionality func : functionalities) {
			Permission permission = new Permission();
			permission.setUser(user);
			permission.setFunctionality(func);
			permission.setUserPermission(UserPermission.DENIED.getValue());
			permissions.add(permission);
		}
		return permissions;
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
	
	@PostMapping(value = { "/delete/{id}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final User domain = this.userService.getById(id);
		if (domain == null) {
			final String message = this.getIdNotFoundMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		
		final boolean result = this.userService.delete(id);
		if (result) {
	        
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
	
	@PostMapping("/exportcsv")
	public void exportCSV(@RequestParam(value="selected_id", required=false) List<Integer> userIds,
			HttpServletResponse response) throws Exception {

		// set file name and content type

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + CSV_USERS_EXPORT_FILE + "\"");

		// create a csv writer
		StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();

		// write all users to csv file
		List<UserCSV> usersList = StreamSupport.stream(userService.extractUserCsv(userIds).spliterator(), false)
				.collect(Collectors.toList());
		
		writer.write(usersList);
	}
	@PostMapping(value = "/importcsv")
	public String fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request, Model model)
			throws IOException {

		SimpleResult result = new SimpleResult(ResultStatus.UNKNOWN.getValue(), "Unknown status");
		result.setFunctionalityName("Upload users CSV file!");
		
		if (file.isEmpty()) {
			result.setResStatus(ResultStatus.ERROR.getValue());
			result.setMessage("File is empty");
			return this.getBaseURL() + "/simple_status";
		}

		List<UserCSV> usersCsv;
		try {
			usersCsv = readCsvFile(file);
		} catch (Exception e) {
			result.setResStatus(ResultStatus.ERROR.getValue());
			result.setMessage("Exception occured while reading CSV file: " + e.getMessage());
			return this.getBaseURL() + "/simple_status";
		}
		List<User> users = UserUtils.convertUsersCsv(usersCsv);
				
		model.addAttribute("result", result);
		
		for (User user : users) {
			if (StringUtils.isNullOrEmpty(user.getEmail()) || !EmailValidator.getInstance().isValid(user.getEmail())) {
				result.setResStatus(ResultStatus.ERROR.getValue());
				result.setMessage("ERROR: Email field of " + user.getFullName() + " is empty or not valid!");
				return this.getBaseURL() + "/simple_status";
			} else {
				if (userService.existsEmail(user.getEmail())) {
					result.setResStatus(ResultStatus.ERROR.getValue());
					result.setMessage("ERROR: Email of user " + user.getFullName() + " already exists!");
					return this.getBaseURL() +  "/simple_status";
				}
			}
		}
		
		
		for (User user : users) {
			String tempPwd = UserUtils.generateRandomPassword(this.dataSetting.getTempPwdLength());
			String encodedPwd = passwordEncoder.encode(tempPwd);
			user.setPassword(encodedPwd);

			if (user.getStatus() == UserStatus.DRAFT.getValue()) {
				setActivateCodeAndTimeout(user);
			}

			User savedUser = userService.saveOrUpdate(user);
			addDefaultPermissions(savedUser);
			permissionService.saveAll(savedUser.getPermissions());

			this.emailSender.sendTemporaryPassword(user.getEmail(), tempPwd);
			if (user.getStatus() == UserStatus.DRAFT.getValue()) {
				String activateAccountUrl = buildActivateAccountUrl(request);
				sendVerificationEmailToUser(activateAccountUrl, user);
			}
		}
		
		result.setResStatus(ResultStatus.SUCCESS.getValue());
		result.setMessage("File imported successfully!");
		return this.getBaseURL() + "/simple_status";
	}

	
	private List<UserCSV> readCsvFile(MultipartFile file) throws Exception {
		Reader reader = new InputStreamReader(file.getInputStream());
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(0).build();

		MappingStrategy<UserCSV> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(UserCSV.class);

		CsvToBean<UserCSV> csvToBean = new CsvToBeanBuilder<UserCSV>(csvReader).withType(UserCSV.class)
				.withMappingStrategy(strategy).build();
		List<UserCSV> usersCsv = csvToBean.parse();
		return usersCsv;
	}

	// reset password user by email
	@RequestMapping(value = "/password/resetbyemail")
	public String resetPasswordByEmail(@RequestParam("user_email") String userEmail, Model model) {
		SimpleResult result = new SimpleResult(ResultStatus.UNKNOWN.getValue(), "Unknown status");
		result.setFunctionalityName("Reset password by email");
		User user = userService.getByEmail(userEmail);
		if (user == null) {
			result.setResStatus(ResultStatus.ERROR.getValue());
			result.setMessage("Email address does not exist!");
			return this.getBaseURL() +  "/simple_status";
		}
		String tempPwd = UserUtils.generateRandomPassword(this.dataSetting.getTempPwdLength());
		String encodedPwd = passwordEncoder.encode(tempPwd);
		user.setPassword(encodedPwd);
		userService.saveOrUpdate(user);
		this.emailSender.sendTemporaryPassword(user.getEmail(), tempPwd);
		result.setResStatus(ResultStatus.SUCCESS.getValue());
		result.setMessage("Password reset successfully for user: '" + user.getFullName() + "' with email: " + user.getEmail());
		model.addAttribute("result", result);
		return this.getBaseURL() + "/simple_status";
	}
	
	// save account settings
	@PostMapping(value = "/settings/save")
	public String saveAccountSettings(@ModelAttribute @Validated User user,
			@RequestParam(name = "current_pwd") String current_pwd,
			@RequestParam(name = "new_pwd") String new_pwd,
			HttpServletRequest request, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes)  {
			
			int userId = user.getId();
			User currentUser = userService.getById(userId);
			
			//The fields we want to change
			currentUser.setFirstName(user.getFirstName());
			currentUser.setLastName(user.getLastName());
			currentUser.setEmail(user.getEmail());
			currentUser.setTitle(user.getTitle());
			
			SimpleResult simpleResult = new SimpleResult();
			if (!StringUtils.isNullOrEmpty(new_pwd)) {
				if (!passwordEncoder.matches(current_pwd, currentUser.getPassword())) {
					String message = this.getMessage("settings.error.incorrectpwd");
					simpleResult.setResStatus(ResultStatus.ERROR.getValue());
					simpleResult.setMessage(message);
					redirectAttributes.addFlashAttribute("result", simpleResult);
					return "redirect:" + this.getBaseURL() + "/settings"; 
				}
				String encoded_new_pwd = this.passwordEncoder.encode(new_pwd);
				currentUser.setPassword(encoded_new_pwd);
			}

			userService.saveOrUpdate(currentUser);
			String message = this.getMessage("settings.update.success");
			simpleResult.setResStatus(ResultStatus.SUCCESS.getValue());
			simpleResult.setMessage(message);
			redirectAttributes.addFlashAttribute("result", simpleResult);
			return "redirect:" + this.getBaseURL() + "/settings"; 
	}

	// show account settings
	@GetMapping(value = "/settings")
	public String showAccountSettings(Model model) {
		User loggedUser = userService.getById((int) model.getAttribute("userId"));
		model.addAttribute("user", loggedUser);
		return this.getBaseURL() + "/account_settings";
	}
		
	// show reset password form
	@RequestMapping(value = "/resetpwdbyemail")
	public String showResetPassword(Model model) {
		return this.getBaseURL() + "/reset_password_form";
	}
	
	// show upload csv form
	@GetMapping(value = "/upload_csv_form")
	public String showUploadUserForm(Model model) {
		return this.getBaseURL() + "/upload_csv_form";
	}
	
	//generate a new activation code and timeout for a user
	private void setActivateCodeAndTimeout(User user) {
		String activateCode = StringUtils.randomAlphanumeric(this.dataSetting.getActivateCodeLength());
		long activateCodeTimeout = (new Date().getTime() / (60 * 1000))
				+ this.dataSetting.getActivateCodeTimeout();

		user.setActivateCode(activateCode);
		user.setActivateTimeout(activateCodeTimeout);
	}
	
	@Override
	protected void loadListPageOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Map<String, String> searchParams, PageableResponse<User> pageableDto) throws BusinessException {
		
		final String filterRole = searchParams.get("filterRole");
		final String filterStatus = searchParams.get("filterStatus");
		List<String> userTypes = UserType.getPossibleStrValues();
		List<Integer> userStatuses = UserStatus.getPossibleIntValues();
		
		model.addAttribute("filterRole", filterRole);
		model.addAttribute("filterStatus", filterStatus);
		model.addAttribute("userTypes", userTypes);
		model.addAttribute("userStatuses", userStatuses);


	}
	
	@Override
	protected PageableResponse<User> pageableSearch(Map<String, String> searchParams, PageRequest pageable, Sort sort) {
		final String search = searchParams.get("q");
		final String filterRole = searchParams.get("filterRole");
		final Integer filterStatus = NumberUtils.parseInt(searchParams.get("filterStatus"), UserStatus.FIND_ALL);
		final List<Integer> filterSubscriptionTypesIds = NumberUtils.parseIntegers(searchParams.get("filterSubtypeIds"), ",");
		
		return this.userService.pageableSearch(search, filterRole, filterStatus, filterSubscriptionTypesIds, pageable, sort);
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