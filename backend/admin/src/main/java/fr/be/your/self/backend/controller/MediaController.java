package fr.be.your.self.backend.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.setting.DataSetting;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.MEDIA)
public class MediaController {
	
	@Autowired
	private DataSetting dataSetting;
	
	@GetMapping(Constants.PATH.WEB_ADMIN.MEDIA_TYPE.AVATAR + "/{fileName}")
    public String avatar(HttpSession session, HttpServletRequest request, 
    		HttpServletResponse response, Model model, 
    		@PathVariable(name="fileName") String fileName) {
		// Read image file and response
        return "user";
    }
}
