package fr.be.your.self.backend.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.security.oauth2.AuthenticationUserDetails;
import fr.be.your.self.service.FunctionalityService;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;
import net.bytebuddy.matcher.ModifierMatcher.Mode;

@Controller
public class UserController {
	private static final String ACTIVATE_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.AUTHENTICATION_PREFIX 
			+ Constants.PATH.AUTHENTICATION.ACTIVATE;
	
	@Autowired
	UserService userService;

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
	
	public static final String DEFAULT_URL = "/user/list/page/1"; // FIXME: move this to config file
	public static String USERS_PER_PAGE = "USERS_PER_PAGE";
	public static final int DEFAULT_NB_PER_PAGE = 2;
	public static final String DEFAULT_NB_PER_PAGE_STR = String.valueOf(DEFAULT_NB_PER_PAGE);

	public static String CSV_USERS_EXPORT_FILE = "users.csv";

	
	// save or update user
	// 1. @ModelAttribute bind form value
	// 2. @Validated form validator
	// 3. RedirectAttributes for flash value
	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	public String saveOrUpdateUser(@ModelAttribute @Validated User user, HttpServletRequest request,
			BindingResult result, Model model, final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "user/userform";
		}
		boolean isNewUser = user.getId() == 0;
		boolean isAdminUser = UserUtils.isAdmin(user);

		boolean isAutoActivateAccount = isAdminUser ? this.dataSetting.isAutoActivateAdminAccount()
				: this.dataSetting.isAutoActivateAccount();
		
		String tempPwd = null;
		if (isNewUser) { // TODO TVA check this
			if (user.getLoginType() == LoginType.PASSWORD.getValue()) {
				tempPwd = UserUtils.generateRandomPassword(this.dataSetting.getTempPwdLength());
						
				String encodedPwd = passwordEncoder.encode(tempPwd);
				user.setPassword(encodedPwd);
			}

			if (userService.existsEmail(user.getEmail())) {
				model.addAttribute("msg", "E-mail already exists!");
				return "user/userform_result";
			}
				
			if (isAutoActivateAccount) {
				user.setStatus(UserStatus.ACTIVE.getValue());
			} else {
				String activateCode = StringUtils.randomAlphanumeric(this.dataSetting.getActivateCodeLength());
				long activateCodeTimeout = (new Date().getTime() / (60 * 1000))
						+ this.dataSetting.getActivateCodeTimeout();

				user.setActivateCode(activateCode);
				user.setActivateTimeout(activateCodeTimeout);
				user.setStatus(UserStatus.DRAFT.getValue());
			}
		}
		
		User savedUser = userService.saveOrUpdate(user);
		if (isAdminUser) {
			for (Permission permission : user.getPermissions()) {
				permission.setUser(savedUser); // We need user id of saved user
				permissionService.saveOrUpdate(permission);
			}
		}

		if (isNewUser && !isAutoActivateAccount) {
			boolean success = sendVerificationEmailToUser(request, savedUser);

			// TODO: Use success variable?
			
			if (tempPwd != null) {
				this.emailSender.sendTemporaryPassword(savedUser.getEmail(), tempPwd);
			}
		}

		return "redirect:" + DEFAULT_URL; // back to list of users

	}

	private boolean sendVerificationEmailToUser(HttpServletRequest request, User savedUser) {
		String activateAccountUrl = buildActivateAccountUrl(request);

		activateAccountUrl += ACTIVATE_URL;

		boolean success = this.emailSender.sendActivateUser(savedUser.getEmail(), activateAccountUrl,
				savedUser.getActivateCode());
		return success;
	}

	private String buildActivateAccountUrl(HttpServletRequest request) {
		String activateAccountUrl = request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath();

		if (activateAccountUrl.endsWith("/")) {
			activateAccountUrl = activateAccountUrl.substring(0, activateAccountUrl.length() - 1);
		}
		return activateAccountUrl;
	}

	// reset password user
	@RequestMapping(value = "/user/{id}/resetpassword")
	public String resetPasswordUser(@PathVariable("id") int id, Model model) {
		String tempPwd = UserUtils.generateRandomPassword(this.dataSetting.getTempPwdLength());
		String encodedPwd = passwordEncoder.encode(tempPwd);
		User user = userService.getById(id);
		user.setPassword(encodedPwd);
		userService.saveOrUpdate(user);
		this.emailSender.sendTemporaryPassword(user.getEmail(), tempPwd);
		model.addAttribute("msg", "Password reset successfully");
		return "user/userform_result";
	}

	// resend verification email user
	@RequestMapping(value = "/user/{id}/resendverifemail")
	public String resendVerificationEmail(@PathVariable("id") int id, HttpServletRequest request, Model model) {
		User user = userService.getById(id);
		sendVerificationEmailToUser(request, user);
		model.addAttribute("msg", "Resend verification email successfully");
		return "user/userform_result";
	}
	
	// show add user form
	@RequestMapping(value = "/user/add")
	public String showAddUserForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("isUpdating", false);
		Iterable<Functionality> functionalities = functionalityService.findAll();
		List<Permission> permissions = new ArrayList<Permission>();

		for (Functionality func : functionalities) {
			Permission permission = new Permission();
			permission.setFunctionality(func);
			permission.setUser(user);
			permission.setUserPermission(UserPermission.DENIED.getValue());
			permissions.add(permission);
		}
		user.setPermissions(permissions);
		
		findLoggedUserInfo(model);

		model.addAttribute("editAccType", UserPermission.WRITE.getValue()); //If we are creating a new user, we need to be able to change the account type

		return "user/userform";
	}

	// show update form
	@RequestMapping(value = "/user/{id}/update", method = RequestMethod.GET)
	public String showUpdateUserForm(@PathVariable("id") int id, Model model) {
		User user = userService.getById(id);
		model.addAttribute("user", user);
		model.addAttribute("isUpdating", true);
		findLoggedUserInfo(model);

		return "user/userform";
	}

	// save account settings
	@RequestMapping(value = "/user/settings/save", method = RequestMethod.POST)
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
			if (!StringUtils.isNullOrEmpty(new_pwd)) {
				if (!passwordEncoder.matches(current_pwd, currentUser.getPassword())) {
					model.addAttribute("msg", "Wrong current password!");
					return "user/account_settings_result"; 
				}
				String encoded_new_pwd = this.passwordEncoder.encode(new_pwd);
				currentUser.setPassword(encoded_new_pwd);
			}

			userService.saveOrUpdate(currentUser);
			
			model.addAttribute("msg", "Account settings changed successfully!");
			return "user/account_settings_result"; 
	}


	
	// show account settings
	@RequestMapping(value = "/user/settings", method = RequestMethod.GET)
	public String showAccountSettings(Model model) {
		findLoggedUserInfo(model);		
		User loggedUser = userService.getById((int) model.getAttribute("loggedUserId"));
		
		model.addAttribute("user", loggedUser);
		return "user/account_settings";
	}
	
	//TODO TVA: find a better way
	private void findLoggedUserInfo(Model model) {
		int editAccType = 0;
		int editPermissions = 0;
		Integer loggedUserId = null;
				
		//get current logged user information
		final Authentication oauth = SecurityContextHolder.getContext().getAuthentication();
		if (oauth != null && oauth.isAuthenticated()) {
			final Object principal = oauth.getPrincipal();
			
			if (principal instanceof AuthenticationUserDetails) {
				final AuthenticationUserDetails userDetails = (AuthenticationUserDetails) principal;			
				
				loggedUserId = userDetails.getUserId();
				
				final Iterable<Permission> userPermissions = this.permissionService.getPermissionByUserId(loggedUserId);
				if (userPermissions != null) {
					for (Permission userPermission : userPermissions) {
						final Functionality functionality = userPermission.getFunctionality();
						if (UserConstants.EDIT_ACCOUNT_TYPE_PATH.equals(functionality.getPath())) {
							editAccType = userPermission.getUserPermission();
						}
						if ( UserConstants.EDIT_PERMISSIONS_PATH.equals(functionality.getPath())) {
							editPermissions = userPermission.getUserPermission();
						}					
					}
				}
			}
		}
		
		model.addAttribute("loggedUserId", loggedUserId);
		model.addAttribute("editAccType", editAccType);
		model.addAttribute("editPermissions", editPermissions);
	}

	// delete user
	@RequestMapping(value = "/user/{id}/delete", method = RequestMethod.GET)
	public String deleteUser(@PathVariable("id") int id, Model model) {
		userService.delete(id);

		return "redirect:" + DEFAULT_URL; // back to list of users
	}

	@RequestMapping(value = "/user/display/settings", method = RequestMethod.POST)
	public String updatePageSettings(
			@RequestParam(value ="nb_per_page", required=false) Integer nb,			
			Model model) {
		
		
		return "redirect:" + DEFAULT_URL; 
	}
	
	
	@RequestMapping(value = "/user/list/page/{page}")
	public String listUserPageByPage(@PathVariable("page") int page, 
			@RequestParam(value="nb_per_page", required=false, defaultValue = "10") Integer nb,
			@RequestParam(value="filter_role", required=false) String role,
			@RequestParam(value="filter_status", required=false, defaultValue = "-2") Integer status,

			Model model) {

		PageRequest pageable = PageRequest.of(page - 1, nb);
		Page<User> userPage;
		if ("null".equals(role)) {
			role ="";
		}
		if (!StringUtils.isNullOrEmpty(role)) {
			userPage = userService.findAllByUserType(role, pageable);
		} else if (status != null && status != -2) {
			userPage = userService.findAllByStatus(status, pageable);
		} 	else {
			userPage = userService.getPaginatedUsers(pageable);
		}
		int totalPages = userPage.getTotalPages();
		model.addAttribute("totalPages", totalPages);

		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("activeUserList", true);
		model.addAttribute("userPage", userPage);
		model.addAttribute("nb_per_page", nb);
		model.addAttribute("filter_role", role);
		model.addAttribute("filter_status", status);

		return "user/userlist";
	}

	@RequestMapping("/user/exportcsv")
	public void exportCSV(@RequestParam(value="selected_id", required=false) List<Integer> userId,
			HttpServletResponse response) throws Exception {

		// set file name and content type

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + CSV_USERS_EXPORT_FILE + "\"");

		// create a csv writer
		StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();

		// write all users to csv file
		List<UserCSV> usersList = StreamSupport.stream(userService.extractUserCsv(userId).spliterator(), false)
				.collect(Collectors.toList());
		
		writer.write(usersList);
	}

	@RequestMapping(value = "/user/importcsv", method = RequestMethod.POST)
	public String fileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IOException {
		// LOGGER.info("File is {}", file.getName());
		// LOGGER.info("Company Guid is {}", companyGuid);

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:/uploadStatus";
		}

		Reader reader = new InputStreamReader(file.getInputStream());
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(0).build();

		MappingStrategy<UserCSV> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(UserCSV.class);

		CsvToBean<UserCSV> csvToBean = new CsvToBeanBuilder<UserCSV>(csvReader).withType(UserCSV.class)
				.withMappingStrategy(strategy).build();
		List<UserCSV> usersCsv = csvToBean.parse();

		List<User> users = UserUtils.convertUsersCsv(usersCsv);
		
		userService.saveAll(users);

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + " and added " + users.size() + " users");

		return "/user/upload_status";
	}

	// show upload user form
	@RequestMapping(value = "/user/uploadform")
	public String showUploadUserForm(Model model) {
		model.addAttribute("user", new User());
		return "user/upload_form";
	}
	

}
