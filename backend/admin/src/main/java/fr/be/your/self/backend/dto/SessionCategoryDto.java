package fr.be.your.self.backend.dto;

import org.springframework.web.multipart.MultipartFile;

import fr.be.your.self.model.SessionCategory;

public class SessionCategoryDto extends SessionCategorySimpleDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7421853941733384505L;
	
	private MultipartFile uploadImageFile;
	
	public SessionCategoryDto() {
		super();
	}

	public SessionCategoryDto(SessionCategory domain) {
		super(domain);
	}
	
	public MultipartFile getUploadImageFile() {
		return uploadImageFile;
	}

	public void setUploadImageFile(MultipartFile uploadImageFile) {
		this.uploadImageFile = uploadImageFile;
	}
}
