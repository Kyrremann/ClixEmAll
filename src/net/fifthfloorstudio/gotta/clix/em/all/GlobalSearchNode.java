package net.fifthfloorstudio.gotta.clix.em.all;

public class GlobalSearchNode {
	
	private String name, set, id, keywords;
	
	public GlobalSearchNode(String id, String name, String set) {
		this(id, name, set, "No keywords");
	}
	
	public GlobalSearchNode(String id, String name, String set, String keywords) {
		this.name = name;
		this.set = set;
		this.id = id;
		this.keywords = keywords;
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
}
