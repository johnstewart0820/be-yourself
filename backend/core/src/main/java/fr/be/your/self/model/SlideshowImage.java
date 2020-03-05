package fr.be.your.self.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class SlideshowImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "Image", length = 255)
	private String image;

	@Column(name = "WebLink", length = 255)
	private String webLink;
	
	@Column(name = "MobileLink", length = 255)
	private String mobileLink;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SlideshowId")
	private Slideshow slideshow;

	@Column(name = "Index", nullable = false)
	private int index;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getWebLink() {
		return webLink;
	}

	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}

	public String getMobileLink() {
		return mobileLink;
	}

	public void setMobileLink(String mobileLink) {
		this.mobileLink = mobileLink;
	}

	public Slideshow getSlideshow() {
		return slideshow;
	}

	public void setSlideshow(Slideshow slideshow) {
		this.slideshow = slideshow;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
