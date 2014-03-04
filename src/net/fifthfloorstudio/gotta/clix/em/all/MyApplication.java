package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;

import android.app.Application;

public class MyApplication extends Application {

	private Object syncToken;
	private ArrayList<GlobalSearchNode> searchNodes;
	private boolean globalSearchReady;

	public ArrayList<GlobalSearchNode> getSearchNodes() {
		return searchNodes;
	}

	public void setSearchNodes(ArrayList<GlobalSearchNode> searchNodes) {
		this.searchNodes = searchNodes;
	}

	public boolean addToSearchNodes(GlobalSearchNode node) {
		return this.searchNodes.add(node);
	}

	public Object getSyncToken() {
		return this.syncToken;
	}

	public void setSyncToken(Object syncToken) {
		this.syncToken = syncToken;
	}

	public boolean isGlobalSearchReady() {
		return globalSearchReady;
	}

	public void setGlobalSearchReady(boolean globalSearchReady) {
		this.globalSearchReady = globalSearchReady;
	}

}
