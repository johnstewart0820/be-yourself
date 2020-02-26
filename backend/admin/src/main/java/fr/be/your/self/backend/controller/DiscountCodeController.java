package fr.be.your.self.backend.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.be.your.self.backend.dto.BusinessCodeDto;
import fr.be.your.self.backend.dto.PermissionDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.common.BusinessCodeType;
import fr.be.your.self.common.ErrorStatusCode;
import fr.be.your.self.common.UserPermission;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.BusinessCode;
import fr.be.your.self.util.NumberUtils;
import fr.be.your.self.util.StringUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + DiscountCodeController.NAME)
public class DiscountCodeController extends BaseCodeController {
	public static final String NAME = "discount-code";
	
	private static final Set<String> SORTABLE_COLUMNS = new HashSet<String>();
	
	private static final int[] AVAILABLE_CODE_TYPES = new int[] { 
			BusinessCodeType.B2B_MULTIPLE.getValue(),
			BusinessCodeType.B2B_UNIQUE.getValue(),
			BusinessCodeType.B2C_DISCOUNT_100.getValue(),
			BusinessCodeType.B2C_DISCOUNT.getValue()
	};
	
	static {
		SORTABLE_COLUMNS.add("name");
	}
	
	@Override
	protected String getName() {
		return NAME;
	}
	
	@Override
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "Discount code management");
	}
	
	@Override
	protected Set<String> getSortableColumns() {
		return SORTABLE_COLUMNS;
	}

	@Override
	protected UserPermission getGlobalPermission(Model model, PermissionDto permission) {
		final String pageName = this.getName();
		
		UserPermission userPermission = UserPermission.DENIED;
		for (int i = 0; i < AVAILABLE_CODE_TYPES.length; i++) {
			final String permissionPath = pageName + "-" + AVAILABLE_CODE_TYPES[i];
			
			if (permission.hasWritePermission(permissionPath)) {
				return UserPermission.WRITE;
			}
			
			if (permission.hasPermission(permissionPath)) {
				userPermission = UserPermission.READONLY;
			}
		}
		
		return userPermission;
	}
	
	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, BusinessCode domain, BusinessCodeDto dto) throws BusinessException {
		super.loadDetailFormOptions(session, request, response, model, domain, dto);
		
		final PermissionDto permission = (PermissionDto) model.getAttribute("permission");
		if (permission == null) {
			throw new BusinessException(ErrorStatusCode.INVALID_PERMISSION);
		}
		
		final int codeType = domain == null ? BusinessCodeType.UNKNOWN.getValue() : domain.getType();
		final String pageName = this.getName();
		
		final List<Integer> availableCodeTypes = new ArrayList<Integer>();
		for (int i = 0; i < AVAILABLE_CODE_TYPES.length; i++) {
			final String permissionPath = pageName + "-" + AVAILABLE_CODE_TYPES[i];
			
			if (permission.hasWritePermission(permissionPath)) {
				availableCodeTypes.add(AVAILABLE_CODE_TYPES[i]);
			} else if (AVAILABLE_CODE_TYPES[i] == codeType && !permission.hasPermission(permissionPath)) {
				throw new BusinessException(ErrorStatusCode.INVALID_PERMISSION);
			}
		}
		
		model.addAttribute("availableCodeTypes", availableCodeTypes);
	}
	
	@Override
	protected void loadListPageOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Map<String, String> searchParams, PageableResponse<BusinessCodeDto> pageableDto)
			throws BusinessException {
		super.loadListPageOptions(session, request, response, model, searchParams, pageableDto);
		
		final PermissionDto permission = (PermissionDto) model.getAttribute("permission");
		if (permission == null) {
			throw new BusinessException(ErrorStatusCode.INVALID_PERMISSION);
		}
		
		final String pageName = this.getName();
		
		final List<Integer> availableCodeTypes = new ArrayList<Integer>();
		for (int i = 0; i < AVAILABLE_CODE_TYPES.length; i++) {
			final String permissionPath = pageName + "-" + AVAILABLE_CODE_TYPES[i];
			
			if (permission.hasPermission(permissionPath)) {
				availableCodeTypes.add(AVAILABLE_CODE_TYPES[i]);
			}
		}
		
		model.addAttribute("availableCodeTypes", availableCodeTypes);
		
		final Set<Integer> filterCodeTypes = new HashSet<Integer>();
		final String queryCodeTypes = searchParams.get("codeTypes");
		if (!StringUtils.isNullOrSpace(queryCodeTypes)) {
			final String[] codeTypeValues = queryCodeTypes.split(",");
			for (String codeTypeValue : codeTypeValues) {
				final Integer codeType = NumberUtils.parseInteger(codeTypeValue);
				if (codeType != null) {
					filterCodeTypes.add(codeType);
				}
			}
		}
		
		model.addAttribute("filterCodeTypes", filterCodeTypes);
	}

	@Override
	protected PageableResponse<BusinessCode> pageableSearch(Map<String, String> searchParams, PageRequest pageable, Sort sort) {
		final String search = searchParams.get("q");
		return this.getService().pageableSearch(search, pageable, sort);
	}
	
	@Override
	protected void validateCreateDomain(BindingResult result, Model model, BusinessCode domain) {
		final int codeType = domain.getType();
		for (int i = 0; i < AVAILABLE_CODE_TYPES.length; i++) {
			if (codeType == AVAILABLE_CODE_TYPES[i]) {
				return;
			}
		}
		
		final ObjectError fieldError = this.createFieldError(result, "type", "invalid", new Object[] { codeType }, "Invalid code type");
		result.addError(fieldError);
	}

	@Override
	protected void validateUpdateDomain(BindingResult result, Model model, BusinessCode domain) {
		this.validateCreateDomain(result, model, domain);
	}

	@Override
	protected String validateDeleteDomain(Model model, BusinessCode domain) {
		// TODO Auto-generated method stub
		return null;
	}
}
