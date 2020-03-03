package fr.be.your.self.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.dto.UserDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.backend.utils.AdminUtils;
import fr.be.your.self.common.FormationType;
import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;
import fr.be.your.self.common.UserUtils;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Address;
import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserConstants;
import fr.be.your.self.service.AddressService;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.NumberUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + ProfessionalController.NAME)
public class ProfessionalController extends BaseResourceController<User, User, UserDto, Integer> {
	public static final String NAME = "professional";

	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressService addressService; 

	private static final Map<String, String[]> SORTABLE_COLUMNS = new HashMap<>();

	@Override
	protected BaseService<User, Integer> getService() {
		return userService;
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getFormView() {
		return "professional/professional-form";
	}

	@Override
	protected String getListView() {
		return "professional/professional-list";
	}

	@Override
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "Professional management");
	}

	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.PROFESSIONAL;

	}

	@Override
	protected User newDomain() {
		return new User();
	}

	@Override
	protected UserDto createDetailDto(User domain) {
		UserDto userDto = new UserDto(domain);
		if (domain == null) {
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

	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, User domain, UserDto dto) throws BusinessException {
		List<String> formations = FormationType.getPossibleStrValues();

		model.addAttribute("formations", formations);

	}
	
	@Override
	protected PageableResponse<User> pageableSearch(Map<String, String> searchParams, PageRequest pageable, Sort sort) {
		final String search = searchParams.get("q");
		return this.userService.pageableSearchPro(search, pageable, sort);
	}

	@PostMapping("/create")
	public String createDomain(@Validated @ModelAttribute("dto") UserDto dto, HttpSession session,
			HttpServletRequest request, HttpServletResponse response, BindingResult result,
			RedirectAttributes redirectAttributes, Model model) {

		if (result.hasErrors()) {
			return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
		}

		final User user = this.newDomain();
		dto.copyToDomain(user);
		user.setUserType(UserType.PROFESSIONAL.getValue());

		User savedUser = userService.create(user);
		//Address
		

		//Add default permission value = "Denied" to professionals
		addDefaultPermissions(savedUser);
		for (Permission permission : savedUser.getPermissions()) {
			permission.setUser(savedUser); // We need user id of saved user
			this.getPermissionService().saveOrUpdate(permission);
		}

		return "redirect:" + this.getBaseURL();
	}
	
	@PostMapping("/update/{id}")
	@Transactional
    public String updateDomain(
    		@PathVariable("id") Integer id, 
    		@ModelAttribute @Validated UserDto dto, 
    		@RequestParam(value = "addressId" , required = false) Integer addressId,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	dto.setId(id);
        	return this.getFormView();
        }
       
        User domain = this.userService.getById(id);
                
        if (domain == null) {
        	final ObjectError error = this.createIdNotFoundError(result, id);
        	result.addError(error);
        	
        	dto.setId(id);
        	return this.getFormView();
        }
        
        dto.copyToDomain(domain);
        
        final User updatedDomain = this.userService.update(domain);
        
        if (addressId != null) {
        	Address add = addressService.getById(addressId);
        	add.setAddress(dto.getAddress().getAddress());
        	addressService.update(add);
        }
        

        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "update");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL() + "/edit/" + id;
    }

}
