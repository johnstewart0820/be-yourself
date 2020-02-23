package fr.be.your.self.backend.setting;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DataSetting {
	
	private int defaultPageSize;
	
	private boolean autoActivateAccount;
	private boolean autoActivateAdminAccount;
	private int activateCodeLength;
	private long activateCodeTimeout;
	private int tempPwdLength;
	
	private String uploadFolder;
	
	private long imageMaxFileSize;
	private long audioMaxFileSize;
	private long videoMaxFileSize;
	private long uploadMaxFileSize;
	
	private Set<String> imageFileExtensions;
	private Set<String> audioFileExtensions;
	private Set<String> videoFileExtensions;
	private Set<String> mediaFileExtensions;		// include audioFileExtensions and videoFileExtensions
	
	private Set<String> imageMimeTypes;
	private Set<String> audioMimeTypes;
	private Set<String> videoMimeTypes;
	private Set<String> mediaMimeTypes;				// include audioMimeTypes and videoMimeTypes
	
	private boolean displayHeaderOnAuthPage;
	private boolean allowRegisterOnAuthPage;
	private boolean allowSocialOnAuthPage;
	
	public int getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize > 0 ? defaultPageSize : 10;
	}

	public String getUploadFolder() {
		return uploadFolder == null ? "" : uploadFolder;
	}

	public void setUploadFolder(String uploadFolder) {
		this.uploadFolder = uploadFolder;
	}

	public long getImageMaxFileSize() {
		return imageMaxFileSize;
	}

	public long getAudioMaxFileSize() {
		return audioMaxFileSize;
	}

	public long getVideoMaxFileSize() {
		return videoMaxFileSize;
	}

	public long getUploadMaxFileSize() {
		return uploadMaxFileSize;
	}

	public boolean isAutoActivateAccount() {
		return autoActivateAccount;
	}

	public boolean isAutoActivateAdminAccount() {
		return autoActivateAdminAccount;
	}

	public int getActivateCodeLength() {
		return activateCodeLength;
	}

	public long getActivateCodeTimeout() {
		return activateCodeTimeout;
	}

	public Set<String> getImageFileExtensions() {
		return imageFileExtensions == null ? Collections.emptySet() : this.imageFileExtensions;
	}

	public Set<String> getAudioFileExtensions() {
		return audioFileExtensions == null ? Collections.emptySet() : this.audioFileExtensions;
	}

	public Set<String> getVideoFileExtensions() {
		return videoFileExtensions == null ? Collections.emptySet() : this.videoFileExtensions;
	}

	public Set<String> getMediaFileExtensions() {
		return mediaFileExtensions == null ? Collections.emptySet() : this.mediaFileExtensions;
	}

	public Set<String> getImageMimeTypes() {
		return imageMimeTypes == null ? Collections.emptySet() : this.imageMimeTypes;
	}

	public Set<String> getAudioMimeTypes() {
		return audioMimeTypes == null ? Collections.emptySet() : this.audioMimeTypes;
	}

	public Set<String> getVideoMimeTypes() {
		return videoMimeTypes == null ? Collections.emptySet() : this.videoMimeTypes;
	}

	public Set<String> getMediaMimeTypes() {
		return mediaMimeTypes == null ? Collections.emptySet() : this.mediaMimeTypes;
	}

	public boolean isDisplayHeaderOnAuthPage() {
		return displayHeaderOnAuthPage;
	}

	public boolean isAllowRegisterOnAuthPage() {
		return allowRegisterOnAuthPage;
	}

	public boolean isAllowSocialOnAuthPage() {
		return allowSocialOnAuthPage;
	}

	public void setUploadFileSizes(long imageMaxFileSize, long audioMaxFileSize, long videoMaxFileSize) {
		this.imageMaxFileSize = imageMaxFileSize * 1024;
		this.audioMaxFileSize = audioMaxFileSize * 1024;
		this.videoMaxFileSize = videoMaxFileSize * 1024;
		
		this.uploadMaxFileSize = this.imageMaxFileSize;
		if (this.uploadMaxFileSize < this.audioMaxFileSize) {
			this.uploadMaxFileSize = this.audioMaxFileSize;
		}
		
		if (this.uploadMaxFileSize < this.videoMaxFileSize) {
			this.uploadMaxFileSize = this.videoMaxFileSize;
		}
	}

	public void setUploadFileExtensions(Set<String> imageFileExtensions, Set<String> audioFileExtensions, Set<String> videoFileExtensions) {
		this.imageFileExtensions = imageFileExtensions;
		this.audioFileExtensions = audioFileExtensions;
		this.videoFileExtensions = videoFileExtensions;
		
		this.mediaFileExtensions = new HashSet<String>();
		if (this.audioFileExtensions != null) {
			this.mediaFileExtensions.addAll(this.audioFileExtensions);
		}
		if (this.videoFileExtensions != null) {
			this.mediaFileExtensions.addAll(this.videoFileExtensions);
		}
	}
	
	public void setUploadMimeTypes(Set<String> imageMimeTypes, Set<String> audioMimeTypes, Set<String> videoMimeTypes) {
		this.imageMimeTypes = imageMimeTypes;
		this.audioMimeTypes = audioMimeTypes;
		this.videoMimeTypes = videoMimeTypes;
		
		this.mediaMimeTypes = new HashSet<String>();
		if (this.audioMimeTypes != null) {
			this.mediaMimeTypes.addAll(this.audioMimeTypes);
		}
		if (this.videoMimeTypes != null) {
			this.mediaMimeTypes.addAll(this.videoMimeTypes);
		}
	}
	
	public void setAutoActivateAccount(boolean autoActivateNormalAccount, boolean autoActivateAdminAccount, 
			int activateAccountCodeLength, long activateCodeTimeout) {
		this.autoActivateAccount = autoActivateNormalAccount;
		this.autoActivateAdminAccount = autoActivateAdminAccount;
		this.activateCodeLength = activateAccountCodeLength;
		this.activateCodeTimeout = activateCodeTimeout;
	}
	
	public void setAuthenticationConfiguration(boolean displayHeaderOnAuthPage, boolean allowRegisterOnAuthPage, 
			boolean allowSocialOnAuthPage) {
		this.displayHeaderOnAuthPage = displayHeaderOnAuthPage;
		this.allowRegisterOnAuthPage = allowRegisterOnAuthPage;
		this.allowSocialOnAuthPage = allowSocialOnAuthPage;
	}

	public int getTempPwdLength() {
		return tempPwdLength;
	}

	public void setTempPwdLength(int tempPwdLength) {
		this.tempPwdLength = tempPwdLength;
	}
}
