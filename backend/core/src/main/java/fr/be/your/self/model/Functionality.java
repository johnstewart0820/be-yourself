package fr.be.your.self.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Functionality extends PO<Integer> {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FunctionalityID")
    private int functionalityId;
	
	@Column(name = "Path", length = 120)
	private String path;
	
	@Column(name = "Name", length = 120)
	private String name;
	
	@Column(name = "Description", length = 255)
	private String description;
	
	public Functionality() {
		super();
	}

	@Override
	public String getDisplay() {
		return this.name;
	}

	@Override
	public Integer getId() {
		return this.functionalityId;
	}

	public int getFunctionalityId() {
		return functionalityId;
	}

	public void setFunctionalityId(int functionalityId) {
		this.functionalityId = functionalityId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
