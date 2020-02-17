package fr.be.your.self.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.be.your.self.backend.setting.Constants;

@CrossOrigin
@Controller
@RequestMapping(Constants.PATH.AUTHENTICATION_PREFIX)
public class AuthController {
	
	@GetMapping(Constants.PATH.AUTHENTICATION.LOGIN)
    public String loginPage(Model model) {
        return "login";
    }
	
	@GetMapping(Constants.PATH.AUTHENTICATION.ACTIVATE)
    public String activatePage(Model model, @RequestParam(name = "code", required = false) String activateCode) {
		// TODO
		System.out.println("ActivateCode: " + activateCode);
		
        return "redirect:/";
    }
	
	@GetMapping(Constants.PATH.AUTHENTICATION.ACCESS_DENIED)
    public String accessDeninedPage(Model model) {
        return "error/403";
    }
}