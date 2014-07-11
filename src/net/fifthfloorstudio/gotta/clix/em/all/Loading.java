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

	private static final int DONE = 1;
	private static final int UPDATING = 2;
	private static final int INFO = 3;

	private static final String SET = "set";
	private static final String SEARCH = "search";

	private String[] modernArray, otherArray, goldenArray;

	private TextView updateView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		updateView = (TextView) findViewById(R.id.updateView);

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case DONE:
						Log.d("HANDLER", "DONE");
						startClixEmAll();
						break;
					case UPDATING:
						Log.d("HANDLER", "UPDATING");
						updateView.setText("Updating...");
						break;
					case INFO:
						String set = msg.getData().getString(SET, "unknown");
						Log.d("HANDLER", "INFO: " + set);
						String setName;
						try {
							setName = getSetTitleByFilename(set);
						} catch (RuntimeException e) {
							setName = "Secret set";
						}
						updateView.setText("Updating " + setName);
						break;
				}
			}
		};

		Thread persistentSetThread = new Thread(new Runnable() {

			List<String> updatableSets;

			@Override
			public void run() {
				long start = System.currentTimeMillis();
				String list = HTTPUtil.getVersionFromServer(getApplicationContext());
				updatableSets = new ArrayList<String>();
				try {
					JSONObject jsonList = new JSONObject(list);
					Iterator<String> keys = jsonList.keys();
					while (keys.hasNext()) {
						String key = keys.next();
						checkForUpdates(key, jsonList.optInt(key, 1));
					}
				} catch (JSONException e) {
					Log.e("JSON", "Can't convert string to json, " + list);
				}
				Log.d("TIMER", "Retrieving version list took " + (System.currentTimeMillis() - start) + " ms");
				if (updatableSets.isEmpty()) {
					handler.sendEmptyMessage(DONE);
					return;
				} else {
					start = System.currentTimeMillis();
					handler.sendEmptyMessage(UPDATING);
					getUpdatesFromServer();
					Log.d("TIMER", "Updating all the sets took " + (System.currentTimeMillis() - start) + " ms");
					handler.sendEmptyMessage(DONE);
				}
			}

			private void checkForUpdates(String set, int latestVersion) {
				JSONObject jsonObject = JsonParser.getJsonSet(getApplicationContext(), set);
				if (jsonObject.optInt("version", 1) < latestVersion) {
					updatableSets.add(set);
				}
			}

			private void getUpdatesFromServer() {
				Message message = new Message();
				message.what = INFO;
				Bundle bundle = new Bundle();
				for (String set : updatableSets) {
					Log.d("UPDATES", set);
					bundle.putString(SET, set);
					message.setData(bundle);
					handler.sendMessage(message);
					String jsonString = HTTPUtil.getUpdateFromServer(set);
					JsonParser.saveJsonToAsset(getApplicationContext(), jsonString, set);
				}
			}
		});
		persistentSetThread.start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				lagSok(getApplicationContext());
			}
		}).start();
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

	private String getSetTitleByFilename(String string) {
		string += ".json";
		if (modernArray == null) {
			modernArray= getResources().getStringArray(R.array.json_modern);
		}

		for (int i = 0; i < modernArray.length; i++) {
			if (modernArray[i].equals(string)) {
				return getResources().getStringArray(R.array.titles_modern)[i];
			}
		};

		if (otherArray == null) {
			otherArray = getResources().getStringArray(R.array.json_other);
		}

		for (int i = 0; i < otherArray.length; i++) {
			if (otherArray[i].equals(string)) {
				return getResources().getStringArray(R.array.titles_other)[i];
			}
		};

		if (goldenArray == null) {
			goldenArray = getResources().getStringArray(R.array.json_golden);
		}

		for (int i = 0; i < goldenArray.length; i++) {
			if (goldenArray[i].equals(string)) {
				return getResources().getStringArray(R.array.titles_golden)[i];
			}
		};

		throw new RuntimeException("No such set is found");
	}

}
