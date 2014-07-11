package net.fifthfloorstudio.gotta.clix.em.all;

public class GlobalSearchNode {
	
	private String name, set, id, keywords;

	public GlobalSearchNode(String id, String set) {
		this.id = id;
		this.set = set;
		this.keywords = "No keywords";
	}

	public String getName() {
		return name;
	}
	
	public String getSet() {
		return set;
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
