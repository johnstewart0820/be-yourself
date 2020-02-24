package fr.be.your.self.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.be.your.self.backend.dto.SubscriptionDto;
import fr.be.your.self.backend.setting.Constants;
import fr.be.your.self.model.Subscription;
import fr.be.your.self.service.BaseService;
import fr.be.your.self.service.SubscriptionService;

@Controller
@RequestMapping(Constants.PATH.WEB_ADMIN_PREFIX + "/" + SubscriptionController.NAME)
public class SubscriptionController extends BaseResourceController<Subscription, Subscription, SubscriptionDto> {
	public static final String NAME = "subscription";

	@Autowired
	SubscriptionService subscriptionService;
	
	@Override
	protected String getFormView() {
		return "subscription/subscription-form";
	}
	
	@Override
	protected String getListView() {
		return "subscription/subscription-list";
	}
	
	@Override
	protected BaseService<Subscription> getService() {
		return subscriptionService;
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getDefaultPageTitle() {
		return this.getMessage(this.getName() + ".page.title", "Session management");
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

}
