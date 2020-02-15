package fr.be.your.self.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/403")
    public String error403() {
        return "error/403";
    }
    
    @GetMapping("/error")
    public String error() {
        return "error/403";
    }
}