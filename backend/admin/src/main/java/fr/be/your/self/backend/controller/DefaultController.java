package fr.be.your.self.backend.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.UserType;

@Controller
public class DefaultController extends BaseController {
	
	@GetMapping(path = { "/", "/home" })
    public String defaultHome() {
		final Authentication oauth = SecurityContextHolder.getContext().getAuthentication();
		if (oauth == null || !oauth.isAuthenticated()) {
			return "redirect:" + Constants.PATH.AUTHENTICATION_PREFIX + Constants.PATH.AUTHENTICATION.LOGIN;	
		}
		
		final Collection<? extends GrantedAuthority> authorities = oauth.getAuthorities();
		if (authorities == null || authorities.isEmpty()) {
			return "redirect:" + Constants.PATH.AUTHENTICATION_PREFIX + Constants.PATH.AUTHENTICATION.LOGIN;
		}
		
		final Iterator<? extends GrantedAuthority> iteratorAuthorities = authorities.iterator();
		while (iteratorAuthorities.hasNext()) {
			final String role = iteratorAuthorities.next().getAuthority();
			
			if (UserType.ADMIN.getValue().equalsIgnoreCase(role)) {
				return "redirect:" + Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.SESSION;
			}
			
			if (("ROLE_" + UserType.ADMIN.getValue()).equalsIgnoreCase(role)) {
				return "redirect:" + Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.SESSION;
			}
		}
		
		return "redirect:" + Constants.PATH.AUTHENTICATION_PREFIX + Constants.PATH.AUTHENTICATION.LOGIN;
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping(Constants.PATH.ACCESS_DENIED)
    public String accessDenied(Model model) {
        return this.error(model);
    }
    
    @GetMapping(Constants.PATH.ERROR)
    public String error(Model model) {
    	model.addAttribute("displayHeader", this.dataSetting.isDisplayHeaderOnAuthPage());
		model.addAttribute("allowRegister", this.dataSetting.isAllowRegisterOnAuthPage());
		model.addAttribute("allowSocial", this.dataSetting.isAllowSocialOnAuthPage());
		
        return "error/403";
    }
}