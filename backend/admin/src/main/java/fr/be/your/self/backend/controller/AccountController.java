package fr.be.your.self.backend.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.UserDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.User;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + AccountController.NAME)
public class AccountController extends BaseResourceController<User, User, UserDto, Integer> {
	public static final String NAME = "account";

	@Autowired
	private UserService userService;

	private static final Map<String, String[]> SORTABLE_COLUMNS = new HashMap<>();

	static {
		SORTABLE_COLUMNS.put("email", new String[] { "email" });
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
		return this.getMessage(baseMessageKey + ".page.title", "Account Settings");
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
		UserDto userDto = new UserDto(domain);
		if (domain == null) { // This is the case when we create new User, if we update user then domain !=
								// null
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

	// save account settings
	@PostMapping(value = "/settings/save")
	public String saveAccountSettings(@ModelAttribute @Validated User user,
			@RequestParam(name = "current_pwd") String current_pwd, @RequestParam(name = "new_pwd") String new_pwd,
			HttpServletRequest request, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		int userId = user.getId();
		User currentUser = userService.getById(userId);

		// The fields we want to change
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setEmail(user.getEmail());
		currentUser.setTitle(user.getTitle());

		if (!StringUtils.isNullOrEmpty(new_pwd)) {
			if (!getPasswordEncoder().matches(current_pwd, currentUser.getPassword())) {
				String message = this.getMessage("account.error.incorrectpwd");
				redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
				redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
				redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);

				return "redirect:" + this.getBaseURL() + "/settings";
			}
			String encoded_new_pwd = this.getPasswordEncoder().encode(new_pwd);
			currentUser.setPassword(encoded_new_pwd);
		}

		userService.saveOrUpdate(currentUser);
		redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
		redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");

		return "redirect:" + this.getBaseURL() + "/settings";
	}

	// show account settings
	@GetMapping(value = "/settings")
	public String showAccountSettings(Model model, final RedirectAttributes redirectAttributes) {
		User loggedUser = userService.getById((int) model.getAttribute("userId"));
		model.addAttribute("user", loggedUser);
		super.loadPersonTitles(model);
		return this.getName() + "/account_settings";
	}

}