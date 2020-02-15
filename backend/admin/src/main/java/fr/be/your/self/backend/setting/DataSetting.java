package fr.be.your.self.backend.setting;

public class DataSetting {
	
	private int defaultPageSize;

	private String uploadFolder;
	
	public int getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize > 0 ? defaultPageSize : 10;
	}

	public String getUploadFolder() {
		return uploadFolder;
	}

	public void setUploadFolder(String uploadFolder) {
		this.uploadFolder = uploadFolder == null ? "" : uploadFolder;
	}
	
	public String getAvatarFolder() {
		return uploadFolder + Constants.FOLDER.MEDIA.AVATAR;
	}
}
