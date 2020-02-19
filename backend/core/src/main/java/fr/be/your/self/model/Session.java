package fr.be.your.self.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Session extends PO<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GroupID")
	private SessionGroup sessionGroup;

	@Column(name = "Title", length = 255)
	@NotEmpty(message = "{session.error.title.not.empty}")
	@NotNull(message = "{session.error.title.not.empty}")
	private String title;

	@Column(name = "Subtitle", length = 255)
	@NotEmpty(message = "{session.error.subtitle.not.empty}")
	@NotNull(message = "{session.error.subtitle.not.empty}")
	private String subtitle;

	@Column(name = "Image", length = 255)
	private String image;

	@Column(name = "ContentFile", length = 255)
	private String contentFile;

	@Column(name = "Duration")
	private int duration;

	@Lob
	@Column(name = "Description")
	private String description;

	@Column(name = "Free")
	private boolean free;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SessionGroup getSessionGroup() {
		return sessionGroup;
	}

	public void setSessionGroup(SessionGroup sessionGroup) {
		this.sessionGroup = sessionGroup;
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

	public String getContentFile() {
		return contentFile;
	}

	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
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

	@Override
	public String getDisplay() {
		return this.title;
	}
}
