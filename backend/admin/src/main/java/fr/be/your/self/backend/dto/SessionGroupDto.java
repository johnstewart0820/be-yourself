package fr.be.your.self.backend.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import fr.be.your.self.model.SessionGroup;

public class SessionGroupDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7421853941733384505L;
	
	private int id;
	
	@NotEmpty(message = "{session.group.name.not.empty}")
	@NotNull(message = "{session.group.name.not.empty}")
	private String name;
	
	private String image;
	
	private MultipartFile imageFile;
	
	public SessionGroupDto() {
		super();
	}

	public SessionGroupDto(SessionGroup domain) {
		this();
		
		if (domain != null) {
			this.id = domain.getId().intValue();
			this.name = domain.getName();
			this.image = domain.getImage();
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
}
