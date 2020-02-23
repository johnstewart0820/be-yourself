package fr.be.your.self.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.SessionDto;
import fr.be.your.self.backend.dto.SubscriptionTypeDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Functionality;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.Session;
import fr.be.your.self.model.SessionGroup;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.model.User;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SubscriptionTypeService;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SubscriptionTypeController.NAME)
public class SubscriptionTypeController
		extends BaseResourceController<SubscriptionType, SubscriptionType, SubscriptionTypeDto> {

	public static final String NAME = "subtype";

	private static final String DEFAULT_URL = "/subtype/list/page/1";

	private static final String BASE_MEDIA_URL = Constants.PATH.WEB_ADMIN_PREFIX + Constants.PATH.WEB_ADMIN.MEDIA
			+ Constants.PATH.WEB_ADMIN.MEDIA_TYPE.SESSION;

	@Autowired
	private SubscriptionTypeService subTypeService;
	
	@Override
	protected String getFormView() {
		return "subtype/subtype_form";
	}
	
	@Override
	protected String getListView() {
		return "subtype/subtype-list";
	}
	
	@Override
	protected void loadDetailForm(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, SubscriptionTypeDto dto) throws BusinessException {
		
		final List<String> canals = Arrays.asList("WEB", "APP");
		final List<Integer> durations = Arrays.asList(1, 3, 6, 12, 24);

		model.addAttribute("canals", canals);
		model.addAttribute("durations", durations);

	}

	@RequestMapping(value = "/list/page/{page}")
	public String listUserPageByPage(@PathVariable("page") int page,
			@RequestParam(value = "nb_per_page", required = false, defaultValue = "10") Integer nb, Model model) {

		PageRequest pageable = PageRequest.of(page - 1, nb);
		Page<SubscriptionType> subtypePage;

		return "subtype/subtype_list";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveOrUpdateSubType(@ModelAttribute @Validated SubscriptionType subtype, HttpServletRequest request,
			BindingResult result, Model model, final RedirectAttributes redirectAttributes) {
		SubscriptionType savedSubtype = subTypeService.saveOrUpdate(subtype);
		return "redirect:" + DEFAULT_URL; // back to list of users

	}

	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@ModelAttribute @Validated SubscriptionTypeDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	return this.getFormView();
        }
        
       
        final SubscriptionType domain = this.newDomain();
        dto.copyToDomain(domain);
        
        final SubscriptionType savedDomain = this.subTypeService.create(domain);
        
        // Error, delete upload file
        if (savedDomain == null || result.hasErrors()) {

        	return this.getFormView();
        }   
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "create");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL();
    }

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getDefaultPageTitle() {
		return this.getMessage(this.getName() + ".page.title", "Subscription type management");
	}

	@Override
	protected String getUploadDirectoryName() {
		return null;
	}

	@Override
	protected BaseService<SubscriptionType> getService() {
		return this.subTypeService;
	}

	@Override
	protected SubscriptionType newDomain() {
		return new SubscriptionType();
	}

	@Override
	protected SubscriptionTypeDto createDetailDto(SubscriptionType domain) {
		return new SubscriptionTypeDto(domain);
	}

	@Override
	protected SubscriptionType createSimpleDto(SubscriptionType domain) {
		return domain;
	}

	@Override
	protected String getBaseMediaURL() {
		return BASE_MEDIA_URL;
	}

	// default page
	@RequestMapping(value = "/management")
	public String showDefaultSubscriptionTypePage(Model model) {
		return "redirect:" + DEFAULT_URL;
	}

}
