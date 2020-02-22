package fr.be.your.self.backend.setting;

public final class Constants {
	
	public static interface MEDIA_TYPE {
		public static final String IMAGE = "image";
		public static final String AUDIO = "audio";
		public static final String VIDEO = "video";
	}
	
	public static interface FOLDER {
		public static interface MEDIA {
			public static final String AVATAR = "/avatar";
			
			public static final String SESSION_CATEGORY = "/session-category";
			public static final String SESSION = "/session";
		}
	}
	
	public static interface PATH {
		public static final String API_PREFIX = "/api";
		public static final String WEB_ADMIN_PREFIX = "";
		
		/** Never change the value when using OAuth2 */
		public static final String AUTHENTICATION_PREFIX = "/oauth";
		
		public static final String ACCESS_DENIED = "/403";
		public static final String ERROR = "/error";
		
		public static interface AUTHENTICATION {
			/** Never change the value when using OAuth2 */
			public static final String AUTHORIZE = "/authorize";
			
			/** Never change the value when using OAuth2 */
			public static final String TOKEN = "/token";
			
			public static final String LOGIN = "/login";
			public static final String LOGOUT = "/logout";
			
			public static final String REGISTER = "/register";
			public static final String ACTIVATE = "/activate";
			public static final String SEND_ACTIVATE_CODE = "/send-activate-code";
			
			public static final String VERIFY_TOKEN_GOOGLE = "/google/verify-token";
		}
		
		public static interface WEB_ADMIN {
			public static final String MEDIA = "/media";
			//public static final String ADMIN_USER = "/admin-user";
			
			public static final String SESSION = "/session";
			public static final String SESSION_CATEGORY = "/session-category";
		}
	}
}
