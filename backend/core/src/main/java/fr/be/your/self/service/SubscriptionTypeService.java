package fr.be.your.self.service;

import fr.be.your.self.model.SubscriptionType;

public interface SubscriptionTypeService extends BaseService<SubscriptionType>  {

	SubscriptionType saveOrUpdate(SubscriptionType subtype);

}
