package fr.be.your.self.model;

import java.io.Serializable;

public abstract class PO<K extends Serializable> {
	
	public abstract K getId();
}
