package fr.be.your.self.backend.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.engine.EmailSender;
import fr.be.your.self.model.User;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.StringUtils;

@CrossOrigin
@Controller
@RequestMapping(Constants.PATH.AUTHENTICATION_PREFIX)
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	private static final String ACTIVATE_URL = Constants.PATH.WEB_ADMIN_PREFIX 
			+ Constants.PATH.AUTHENTICATION_PREFIX 
			+ Constants.PATH.AUTHENTICATION.ACTIVATE;
	
	@Autowired
	private DataSetting dataSetting;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailSender emailSender;
	
	@ModelAttribute
	protected void initModelAttribute(HttpSession session, HttpServletRequest request, 
			HttpServletResponse response, Model model) {
		
		model.addAttribute("displayHeader", dataSetting.isDisplayHeaderOnAuthPage());
		model.addAttribute("allowRegister", dataSetting.isAllowRegisterOnAuthPage());
		model.addAttribute("allowSocial", dataSetting.isAllowSocialOnAuthPage());
	}
	
	@GetMapping(Constants.PATH.AUTHENTICATION.LOGIN)
    public String loginPage(Model model) {
        return "login";
    }
	
	@GetMapping(Constants.PATH.AUTHENTICATION.ACTIVATE)
    public String activatePage(
    		@RequestParam(name = "code", required = false) String activateCode,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
		model.addAttribute("activateCode", activateCode);
		
		if (StringUtils.isNullOrSpace(activateCode)) {
			model.addAttribute("errorMessageKey", "invalid.activate.code");
			
			return "pages/auth-activate-error";
		}
		
		final User user = this.userService.getByActivateCode(activateCode);
		if (user == null) {
			model.addAttribute("errorMessageKey", "invalid.activate.code");
			
			return "pages/auth-activate-error";
		}
		
		if (user.getActivateTimeout() < new Date().getTime() / (60 * 1000)) {
			model.addAttribute("errorMessageKey", "expired.activate.code");
			
			return "pages/auth-activate-error";
		}
		
		try {
			if (!this.userService.activateUser(user.getId())) {
				model.addAttribute("errorMessageKey", "unknown");
				
				return "pages/auth-activate-error";
			}
		} catch (Exception ex) {
			logger.error("Activate user error", ex);
			
			model.addAttribute("errorMessageKey", "unknown");
			return "pages/auth-activate-error";
		}
		
		return "pages/auth-activate-success";
    }
	
	@PostMapping(Constants.PATH.AUTHENTICATION.SEND_ACTIVATE_CODE)
    public String sendActivatePage(
    		@RequestParam(name = "email", required = false) String email,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
		if (result.hasErrors()) {
			return "pages/auth-activate-error";
		}
		
		if (StringUtils.isNullOrSpace(email)) {
			model.addAttribute("formError", "invalid.email");
			
			return "pages/auth-activate-error";
		}
		
		final User user = this.userService.getByEmail(email);
		if (user == null || user.getStatus() != UserStatus.ACTIVE.getValue()) {
			model.addAttribute("formError", "invalid.email");
			
			return "pages/auth-activate-error";
		}
		
		final String activateCode = StringUtils.randomAlphanumeric(this.dataSetting.getActivateCodeLength());
		final long activateCodeTimeout = (new Date().getTime() / (60 * 1000)) + this.dataSetting.getActivateCodeTimeout();
		
		user.setActivateCode(activateCode);
		user.setActivateTimeout(activateCodeTimeout);
		
		User savedUser;
		try {
			savedUser = this.userService.saveOrUpdate(user);
			if (savedUser == null) {
				model.addAttribute("formError", "unknown");
				
				return "pages/auth-activate-error";
			}
		} catch (Exception ex) {
			logger.error("Send activate code error", ex);
			
			model.addAttribute("formError", "unknown");
			return "pages/auth-activate-error";
		}
		
		String activateAccountUrl = request.getScheme() + "://" 
				+ request.getServerName() + ":"
				+ request.getServerPort() 
				+ request.getContextPath();
		
		if (activateAccountUrl.endsWith("/")) {
			activateAccountUrl = activateAccountUrl.substring(0, activateAccountUrl.length() - 1);
		}
		
		activateAccountUrl += ACTIVATE_URL;
		
		this.emailSender.sendActivateUser(savedUser.getEmail(), 
				activateAccountUrl, savedUser.getActivateCode());
		
		return "redirect:/";
    }
	
	@GetMapping(Constants.PATH.AUTHENTICATION.ACCESS_DENIED)
    public String accessDeninedPage(Model model) {
        return "error/403";
    }
}