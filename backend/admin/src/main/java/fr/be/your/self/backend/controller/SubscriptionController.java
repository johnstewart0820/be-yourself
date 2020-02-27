package fr.be.your.self.backend.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import fr.be.your.self.backend.dto.SubscriptionDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.dto.PageableResponse;
import fr.be.your.self.exception.BusinessException;
import fr.be.your.self.model.Session;
import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SubscriptionService;
import fr.be.your.self.service.SubscriptionTypeService;
import fr.be.your.self.service.UserService;
import fr.be.your.self.util.NumberUtils;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SubscriptionController.NAME)
public class SubscriptionController extends BaseResourceController<Subscription, Subscription, SubscriptionDto, Integer> {
	public static final String NAME = "subscription";

	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SubscriptionTypeService subtypeService;
	
	public static String CSV_SUBSCRIPTION_EXPORT_FILE = "subscription.csv";

	
	private static final Set<String> SORTABLE_COLUMNS = new HashSet<String>();

	static {
		SORTABLE_COLUMNS.add("name");
		//TODO TVA add sortable columns
	}
	
	@Override
	protected String getFormView() {
		return "subscription/subscription-form";
	}
	
	@Override
	protected String getListView() {
		return "subscription/subscription-list";
	}
	
	@Override
	protected BaseService<Subscription, Integer> getService() {
		return subscriptionService;
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getDefaultPageTitle(String baseMessageKey) {
		return this.getMessage(baseMessageKey + ".page.title", "Subscription management");
	}

	@Override
	protected String getUploadDirectoryName() {
		return this.dataSetting.getUploadFolder() + Constants.FOLDER.MEDIA.SESSION;
	}

	@Override
	protected Subscription newDomain() {
		return new Subscription();

	}

	@Override
	protected SubscriptionDto createDetailDto(Subscription domain) {
		return new SubscriptionDto(domain);
	}

	@Override
	protected Subscription createSimpleDto(Subscription domain) {
		return domain;
	}

	@Override
	protected Set<String> getSortableColumns() {
		return SORTABLE_COLUMNS;
	}


	@Override
	protected void loadDetailFormOptions(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			Model model, Subscription domain, SubscriptionDto dto) throws BusinessException {
		
		final List<String> canals = Arrays.asList("WEB", "APP");
		final List<Integer> durations = Arrays.asList(1, 3, 6, 12, 24);
		
		final String userDefaultSort = this.userService.getDefaultSort();
		final Sort userSort = this.getSortRequest(userDefaultSort);
		final List<User> users = userService.getAll(userSort);
		
		final String subtypeDefaultSort = this.subtypeService.getDefaultSort();
		final Sort subtypeSort = this.getSortRequest(subtypeDefaultSort);
		final List<SubscriptionType> subtypes = subtypeService.getAll(subtypeSort);

		model.addAttribute("canals", canals); //TODO TVA check if we keep this
		model.addAttribute("users", users);
		model.addAttribute("subtypes", subtypes);
		model.addAttribute("durations", durations);
	}
	
	@PostMapping("/create")
	@Transactional
    public String createDomain(
    		@Validated @ModelAttribute("dto") SubscriptionDto dto, 
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
        if (result.hasErrors()) {
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
                
        final Subscription domain = this.newDomain();
        dto.copyToDomain(domain);

        User user = userService.getById(dto.getUserId());
        SubscriptionType subtype = subtypeService.getById(dto.getSubtypeId());
        domain.setUser(user);
        domain.setSubtype(subtype);;
        
        final Subscription savedDomain = this.subscriptionService.create(domain);
        
        //Error
        if (savedDomain == null || result.hasErrors()) {
        	
        	return this.redirectAddNewPage(session, request, response, redirectAttributes, model, dto);
        }
        
        redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "create");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
        
        return "redirect:" + this.getBaseURL();
    }
	
	@PostMapping(value = { "/delete/{id}" })
	@Transactional
    public String deletePage(
    		@PathVariable(name = "id", required = true) Integer id,
    		HttpSession session, HttpServletRequest request, HttpServletResponse response, 
    		RedirectAttributes redirectAttributes, Model model) {
		
		final Subscription domain = this.subscriptionService.getById(id);
		if (domain == null) {
			final String message = this.getIdNotFoundMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
	        
			return "redirect:" + this.getBaseURL() + "/current-page";
		}
		
		
		final boolean result = this.subscriptionService.delete(id);
		if (result) {
	        
			final String message = this.getDeleteSuccessMessage(id);
			
			redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
	        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "success");
	        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
			
			return "redirect:" + this.getBaseURL();
		}
		
		final String message = this.getDeleteByIdErrorMessage(id);
		
		redirectAttributes.addFlashAttribute(TOAST_ACTION_KEY, "delete");
        redirectAttributes.addFlashAttribute(TOAST_STATUS_KEY, "warning");
        redirectAttributes.addFlashAttribute(TOAST_MESSAGE_KEY, message);
        
		return "redirect:" + this.getBaseURL() + "/current-page";
	}
	
	@RequestMapping("/user/exportcsv")
	public void exportCSV(@RequestParam(value="selected_id", required=false) List<Integer> subscriptionIds,
			HttpServletResponse response) throws Exception {

		// set file name and content type

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + CSV_SUBSCRIPTION_EXPORT_FILE + "\"");

		// create a csv writer
		StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
				.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withOrderedResults(false).build();

		// write all users to csv file
		/*List<UserCSV> usersList = StreamSupport.stream(userService.extractUserCsv(userId).spliterator(), false)
				.collect(Collectors.toList());
	
		writer.write(usersList);	*/
	}
	
}
