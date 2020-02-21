package fr.be.your.self.backend.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import fr.be.your.self.model.Session;
import fr.be.your.self.model.SessionGroup;

public class SessionDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3830533931201988167L;

	private int id;

	private int groupId;

	@NotEmpty(message = "{session.error.title.not.empty}")
	@NotNull(message = "{session.error.title.not.empty}")
	private String title;

	@NotEmpty(message = "{session.error.subtitle.not.empty}")
	@NotNull(message = "{session.error.subtitle.not.empty}")
	private String subtitle;

	private String image;

	private MultipartFile uploadImageFile;

	private String contentFile;
	private String contentFileType;
	private String contentMimeType;
	
	private MultipartFile uploadContentFile;

	private int duration;

	private String description;

	private boolean free;

	public SessionDto() {
		super();
	}

	public SessionDto(Session domain) {
		this();
		
		this.free = false;
		
		if (domain != null) {
			this.id = domain.getId().intValue();
			this.title = domain.getTitle();
			this.subtitle = domain.getSubtitle();
			this.image = domain.getImage();
			this.contentFile = domain.getContentFile();
			this.contentMimeType = domain.getContentMimeType();
			this.duration = domain.getDuration();
			this.description = domain.getDescription();
			this.free = domain.isFree();

			final SessionGroup group = domain.getSessionGroup();
			if (group != null) {
				this.groupId = group.getId();
			}
		}
	}
	
	public void copyToDomain(Session domain) {
		domain.setTitle(this.title);
        domain.setSubtitle(this.subtitle);
        domain.setDuration(this.duration);
        domain.setDescription(this.description);
        domain.setFree(this.free);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public MultipartFile getUploadImageFile() {
		return uploadImageFile;
	}

	public void setUploadImageFile(MultipartFile uploadImageFile) {
		this.uploadImageFile = uploadImageFile;
	}

	public String getContentFile() {
		return contentFile;
	}

	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
	}

	public String getContentMimeType() {
		return contentMimeType;
	}

	public void setContentMimeType(String contentMimeType) {
		this.contentMimeType = contentMimeType;
	}

	public String getContentFileType() {
		return contentFileType;
	}

	public void setContentFileType(String contentFileType) {
		this.contentFileType = contentFileType;
	}

	public MultipartFile getUploadContentFile() {
		return uploadContentFile;
	}

	public void setUploadContentFile(MultipartFile uploadContentFile) {
		this.uploadContentFile = uploadContentFile;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}
}
