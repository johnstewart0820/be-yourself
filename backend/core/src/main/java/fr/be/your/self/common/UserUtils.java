package fr.be.your.self.common;

import java.util.ArrayList;
import java.util.List;

import fr.be.your.self.model.User;
import fr.be.your.self.model.UserCSV;
import fr.be.your.self.util.StringUtils;

public final class UserUtils {
	public static boolean isAdmin(User user) {
		return UserType.ADMIN.getValue().equals(user.getUserType() ) || UserType.SUPER_ADMIN.getValue().equals(user.getUserType());
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
}
