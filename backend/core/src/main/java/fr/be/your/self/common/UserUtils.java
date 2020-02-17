package fr.be.your.self.common;

import fr.be.your.self.model.User;

public final class UserUtils {
	public static boolean isAdmin(User user) {
		return UserType.ADMIN.getValue().equals(user.getUserType() ) || UserType.SUPER_ADMIN.getValue().equals(user.getUserType());
	}
}
