package de.oswoboda.spotripel;

public class SPOTripel {
	
	private String subject = "";
	private String predicate = "";
	private String object = "";
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	public boolean hasSubject() {
		return !subject.equals("");
	}
	
	public boolean hasPredicate() {
		return !predicate.equals("");
	}
	
	public boolean hasObject() {
		return !object.equals("");
	}
}
