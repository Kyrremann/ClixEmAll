package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.*;
import android.util.Log;
import android.widget.TextView;
import net.fifthfloorstudio.gotta.clix.em.all.honeycomb.ClixEmAllHoneyComb;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Loading extends Activity {

	private static final String SEARCH = "search";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		new Thread(new Runnable() {
			@Override
			public void run() {
				lagSok(getApplicationContext());
			}
		}).start();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startClixEmAll();
			}
		}, 1200);
	}

	private void startClixEmAll() {
		Intent intent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			intent = new Intent(Loading.this, ClixEmAllHoneyComb.class);
		} else {
			intent = new Intent(Loading.this, ClixEmAll.class);
		}
		startActivity(intent);
		finish();
	}

	public void lagSok(Context context) {
		MyApplication application = (MyApplication) getApplicationContext();
		Object syncToken = new Object();
		application.setSyncToken(syncToken);

		ArrayList<GlobalSearchNode> all = new ArrayList<GlobalSearchNode>();

		long start = System.currentTimeMillis();
		for (String s : getResources().getStringArray(
				R.array.json_modern)) {
			JSONObject set = JsonParser.getJsonSet(context, s);
			s = s.replace(".json", "");
			Iterator<String> keys = set.keys();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					all.add(createSearchNode(key, s, figure));
				} catch (JSONException e) {
					Log.d(SEARCH, "Couldn't get " + key);
				}
			}
		}

		for (String s : getResources().getStringArray(
				R.array.json_golden)) {
			JSONObject set = JsonParser.getJsonSet(context, s);
			s = s.replace(".json", "");
			Iterator<String> keys = set.keys();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					all.add(createSearchNode(key, s, figure));
				} catch (JSONException e) {
					Log.d(SEARCH, "Couldn't get " + key);
				}
			}
		}

		for (String s : getResources().getStringArray(
				R.array.json_other)) {
			JSONObject set = JsonParser.getJsonSet(context, s);
			s = s.replace(".json", "");
			Iterator<String> keys = set.keys();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					all.add(createSearchNode(key, s, figure));
				} catch (JSONException e) {
					Log.d(SEARCH, "Couldn't get " + key);
				}
			}
		}
		Log.d(SEARCH, "Search took " + (System.currentTimeMillis() - start) + " ms to generate");
		application.setSearchNodes(all);
		application.setGlobalSearchReady(true);
		synchronized (syncToken) {
			application.getSyncToken().notify();
		}
	}

	private GlobalSearchNode createSearchNode(String id, String set, JSONObject figure) throws JSONException {
		GlobalSearchNode searchNode = new GlobalSearchNode(id, set);
		searchNode.setName(figure.getString("name"));
		if (figure.has("keywords")) {
			searchNode.setKeywords(figure.getString("keywords"));
		}
		// TODO: Missing team ability
		// TODO: Missing points

		return searchNode;
	}

	private boolean notPassableJSON(String key) {
		return key.equals("version")
				|| key.equals("set_title");
	}
}
