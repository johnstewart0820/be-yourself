package fr.be.your.self.backend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.model.User;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.UserService;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.ADMIN_USER)
public class AdminUserController extends BaseController<User> {
	
	private static final String BASE_URL = Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.ADMIN_USER;
	
	@Autowired
	private UserService userService;
	
	@Override
	protected BaseService<User> getService() {
		return this.userService;
	}

	@Override
	protected String getBaseURL() {
		return BASE_URL;
	}

	@Override
	protected String getDefaultPageTitle() {
		return this.getMessage("title.admin-user", "User management");
	}
	
	@GetMapping(value = { "/edit/{id}" })
    public String editPage(HttpSession session, HttpServletRequest request, Model model,
    		@RequestParam(name = "q", required = false) String search,
    		@RequestParam(name = "sort", required = false) String sort,
    		@RequestParam(name = "page", required = false) Integer page,
    		@RequestParam(name = "size", required = false) Integer size) {
		return "admin-user/form-page";
	}
}
