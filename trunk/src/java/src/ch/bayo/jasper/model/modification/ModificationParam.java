package ch.bayo.jasper.model.modification;

public class ModificationParam {
	
	private String name;
	private String value;
	
	ModificationParam(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}

}
