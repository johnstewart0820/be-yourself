package fr.be.your.self.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.be.your.self.backend.setting.Constants;

@Controller
public class DefaultController extends BaseController {
	
	@GetMapping("/")
    public String defaultHome() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
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