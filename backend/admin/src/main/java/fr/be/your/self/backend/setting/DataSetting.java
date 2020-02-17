package fr.be.your.self.backend.setting;

import java.util.Collections;
import java.util.Set;

public class DataSetting {
	
	private int defaultPageSize;

	private String uploadFolder;
	
	private long imageMaxFileSize;
	private long audioMaxFileSize;
	private long videoMaxFileSize;
	private long uploadMaxFileSize;
	
	private Set<String> imageFileExtensions;
	private Set<String> audioFileExtensions;
	private Set<String> videoFileExtensions;
	
	private Set<String> imageMediaTypes;
	private Set<String> audioMediaTypes;
	private Set<String> videoMediaTypes;
	
	//'image/png', 'image/jpg', 'image/jpeg'
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
	}
	
	public void setUploadMediaTypes(Set<String> imageMediaTypes, Set<String> audioMediaTypes, Set<String> videoMediaTypes) {
		this.imageMediaTypes = imageMediaTypes;
		this.audioMediaTypes = audioMediaTypes;
		this.videoMediaTypes = videoMediaTypes;
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

	public Set<String> getImageMediaTypes() {
		return imageMediaTypes == null ? Collections.emptySet() : this.imageMediaTypes;
	}

	public Set<String> getAudioMediaTypes() {
		return audioMediaTypes == null ? Collections.emptySet() : this.audioMediaTypes;
	}

	public Set<String> getVideoMediaTypes() {
		return videoMediaTypes == null ? Collections.emptySet() : this.videoMediaTypes;
	}

	public String getAvatarFolder() {
		return this.getUploadFolder() + Constants.FOLDER.MEDIA.AVATAR;
	}
	
	public String getSessionGroupFolder() {
		return this.getUploadFolder() + Constants.FOLDER.MEDIA.SESSION_GROUP;
	}
}
