package fr.be.your.self.backend.setting;

public final class Constants {
	
	public static interface PATH {
		public static final String API_PREFIX = "/api";
		
		/** Never change the value when using OAuth2 */
		public static final String AUTHENTICATION_PREFIX = "/oauth";
		
		public static interface AUTHENTICATION {
			/** Never change the value when using OAuth2 */
			public static final String AUTHORIZE = "/authorize";
			
			/** Never change the value when using OAuth2 */
			public static final String TOKEN = "/token";
			
			public static final String LOGIN = "/login";
			public static final String LOGOUT = "/logout";
			public static final String ACCESS_DENIED = "/403";
			
			public static final String REGISTER = "/register";
			public static final String VERIFY_TOKEN_GOOGLE = "/google/verify-token";
		}
	}
}
