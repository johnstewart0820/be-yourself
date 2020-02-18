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
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.service.FunctionalityService;
import fr.be.your.self.service.PermissionService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

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
	
	public static int NB_USERS_PER_PAGE = 2; // FIXME: move this to config file
	public static String CSV_USERS_EXPORT_FILE = "users.csv";

	
	// save or update user
	// 1. @ModelAttribute bind form value
	// 2. @Validated form validator
	// 3. RedirectAttributes for flash value
	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	public String saveOrUpdateUser(@ModelAttribute @Validated User user, 
			HttpServletRequest request, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return "user/userform";
		} else {
			boolean isNewUser = user.getId() == 0; 
			boolean isAdminUser = UserUtils.isAdmin(user);
			
			boolean isAutoActivateAccount = isAdminUser 
					? this.dataSetting.isAutoActivateAdminAccount() 
					: this.dataSetting.isAutoActivateAccount();
			
			// Add message to flash scope. TODO TVA: check if we need to keep this
			redirectAttributes.addFlashAttribute("css", "success");
			if (isNewUser) { // TODO TVA check this
				redirectAttributes.addFlashAttribute("msg", "User added successfully!");
				
				if (isAutoActivateAccount) {
					user.setStatus(UserStatus.ACTIVE.getValue());
				} else {
					String activateCode = StringUtils.randomAlphanumeric(this.dataSetting.getActivateCodeLength());
					long activateCodeTimeout = (new Date().getTime() / (60 * 1000)) + this.dataSetting.getActivateCodeTimeout();
					
					user.setActivateCode(activateCode);
					user.setActivateTimeout(activateCodeTimeout);
					user.setStatus(UserStatus.DRAFT.getValue());
				}
			} else {
				redirectAttributes.addFlashAttribute("msg", "User updated successfully!");
			}

			User savedUser = userService.saveOrUpdate(user);
			if (isAdminUser) {
				for (Permission permission : user.getPermissions()) {
					permission.setUser(savedUser); //We need user id of saved user
					permissionService.saveOrUpdate(permission);
				}
			}
			
			if (isNewUser && !isAutoActivateAccount) {
				String activateAccountUrl = request.getScheme() + "://" 
						+ request.getServerName() + ":"
						+ request.getServerPort() 
						+ request.getContextPath();
				
				if (activateAccountUrl.endsWith("/")) {
					activateAccountUrl = activateAccountUrl.substring(0, activateAccountUrl.length() - 1);
				}
				
				activateAccountUrl += ACTIVATE_URL;
				
				boolean success = this.emailSender.sendActivateUser(savedUser.getEmail(), 
						activateAccountUrl, savedUser.getActivateCode());
				
				// TODO: Use success variable?
			}
			
			return "redirect:/user/list/page/1"; // back to list of users
		}
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
		return "user/userform";
	}

	// show update form
	@RequestMapping(value = "/user/{id}/update", method = RequestMethod.GET)
	public String showUpdateUserForm(@PathVariable("id") int id, Model model) {
		User user = userService.getById(id);
		model.addAttribute("user", user);
		model.addAttribute("isUpdating", true);
		List<Permission> perms = user.getPermissions();
		int editAccType = 0;
		int editPermissions = 0;
		for (Permission perm : perms) {
			Functionality func = perm.getFunctionality();
			if (UserConstants.EDIT_ACCOUNT_TYPE_PATH.equals(func.getPath())) {
				editAccType = perm.getUserPermission();
			}
			if ( UserConstants.EDIT_PERMISSIONS_PATH.equals(func.getPath())) {
				editPermissions = perm.getUserPermission();
			}
		}
		
		model.addAttribute("editAccType", editAccType);
		model.addAttribute("editPermissions", editPermissions);

		return "user/userform";
	}

	// delete user
	@RequestMapping(value = "/user/{id}/delete", method = RequestMethod.GET)
	public String deleteUser(@PathVariable("id") int id, Model model) {
		userService.delete(id);

		return "redirect:/user/list/page/1"; // back to list of users
	}

	@RequestMapping(value = "/user/list/page/{page}")
	public String listUserPageByPage(@PathVariable("page") int page, Model model) {
		PageRequest pageable = PageRequest.of(page - 1, NB_USERS_PER_PAGE); // TODO TVA check this
		Page<User> userPage = userService.getPaginatedUsers(pageable);
		int totalPages = userPage.getTotalPages();
		model.addAttribute("totalPages", totalPages);

		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("activeUserList", true);
		model.addAttribute("userPage", userPage);
		return "user/userlist";
	}

	@GetMapping("/user/exportcsv")
	public void exportCSV(HttpServletResponse response) throws Exception {

		// set file name and content type

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + CSV_USERS_EXPORT_FILE + "\"");

		// create a csv writer
		StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();

		// write all users to csv file
		List<UserCSV> usersList = StreamSupport.stream(userService.extractUserCsv().spliterator(), false)
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

		MappingStrategy<User> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(User.class);

		CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(csvReader).withType(User.class)
				.withMappingStrategy(strategy).build();
		List<User> users = csvToBean.parse();

		System.out.println(users); // TODO TVA delete this line

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
