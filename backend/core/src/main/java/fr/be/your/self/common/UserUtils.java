package fr.be.your.self.common;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.util.StringUtils;

public final class UserUtils {
	
	public static boolean isAdmin(User user) {
		return UserType.ADMIN.getValue().equals(user.getUserType()); 
				//|| UserType.SUPER_ADMIN.getValue().equals(user.getUserType());
	}
	
	public static List<User> convertUsersCsv(List<UserCSV> usersCsv){
		List<User> users = new ArrayList<>();
		
		if (usersCsv == null || usersCsv.size() ==0) {
			return users;
		}
		
		for (UserCSV userCsv : usersCsv) {
			User user = new User();
			user.setTitle(userCsv.getTitle());
			user.setFirstName(userCsv.getFirstName());
			user.setLastName(userCsv.getLastName());
			user.setEmail(userCsv.getEmail());

			user.setStatus(userCsv.getStatus());
			user.setReferralCode(userCsv.getReferralCode());
			user.setUserType(userCsv.getUserType());
		
			users.add(user);
			
		}
		return users;
		
	}
	
	public static String generateRandomPassword(int pwdLength) {
		return StringUtils.randomAlphanumeric(pwdLength);
	}
	
	public static Subscription findSubscriptionUser(User user) {
		Subscription subscription = new Subscription();
		
		//Find the active subscription
		Optional<Subscription> subscriptionOptional = user.getSubscriptions().stream().filter(x -> x.isStatus()).findAny();
		
		if (subscriptionOptional.isPresent()) {
			subscription =  subscriptionOptional.get();
		} else { //Otherwise, find the last inactive subscription
			List<Subscription> subscriptions = user.getSubscriptions();
			if (subscriptions != null && subscriptions.size() > 0) {
				Subscription subsription = subscriptions.get(0);
				Date date = subsription.getSubscriptionEndDate();
				for (Subscription sub : subscriptions) {
					if (sub.getSubscriptionEndDate().after(date)) {
						subsription = sub;
						date = sub.getSubscriptionEndDate();
					}
					
				}
				subscription = subsription;
			}
		}
		
		return subscription;
	}
	
	public static String assignPassword(User user, PasswordEncoder passwordEncoder, int pwdLength) {
		String tempPwd = UserUtils.generateRandomPassword(pwdLength);
		String encodedPwd = passwordEncoder.encode(tempPwd);
		user.setPassword(encodedPwd);
		return tempPwd;
	}
}
