package fr.be.your.self.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.be.your.self.backend.setting.Constants;

@CrossOrigin
@Controller
@RequestMapping(Constants.PATH.AUTHENTICATION_PREFIX)
public class AuthController {
	
	@GetMapping(Constants.PATH.AUTHENTICATION.LOGIN)
    public String loginPage(Model model) {
        return "login";
    }
}