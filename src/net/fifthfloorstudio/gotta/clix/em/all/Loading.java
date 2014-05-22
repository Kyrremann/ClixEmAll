package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Message;
import android.util.Log;
import net.fifthfloorstudio.gotta.clix.em.all.honeycomb.ClixEmAllHoneyComb;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class Loading extends Activity {

	private static final int DONE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case DONE:

						lagSok(getApplicationContext());
						startClixEmAll();
						break;
				}
			}
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
//				String list = HTTPUtil.getVersionListFromServer(getApplicationContext());
//				try {
//					JSONObject jsonList = new JSONObject(list);
//					Iterator<String> keys = jsonList.keys();
//					while (keys.hasNext()) {
//						String key = keys.next();
//						checkForUpdates(key, jsonList.optInt(key, 1));
//					}
//				} catch (JSONException e) {
//					Log.e("JSON", "Can't convert string to json, " + list);
//				}

				handler.sendEmptyMessage(DONE);
			}

			private void checkForUpdates(String set, int latestVersion) {
				JSONObject jsonObject = JsonParser.getJsonSet(getApplicationContext(), set);
				if (jsonObject.optInt("version", 1) < latestVersion) {
					System.out.println(HTTPUtil.getUpdateFromServer(set));
				}
			}
		}).start();

//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				startClixEmAll();
//			}
//		}, 1200);
	}

	private void startClixEmAll() {
		Intent intent = null;
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
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					// TODO: Missing team ability
					if (figure.has("keywords")) {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s, figure
								.getString("keywords")));
					} else {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		for (String s : getResources().getStringArray(
				R.array.json_golden)) {
			JSONObject set = JsonParser.getJsonSet(context, s);
			s = s.replace(".json", "");
			Iterator<String> keys = set.keys();
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					// TODO: Missing team ability
					if (figure.has("keywords")) {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s, figure
								.getString("keywords")));
					} else {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		for (String s : getResources().getStringArray(
				R.array.json_other)) {
			JSONObject set = JsonParser.getJsonSet(context, s);
			s = s.replace(".json", "");
			Iterator<String> keys = set.keys();
			String key = null;
			while (keys.hasNext()) {
				key = keys.next();
				if (notPassableJSON(key)) {
					continue;
				}
				try {
					JSONObject figure = set.getJSONObject(key);
					// TODO: Missing team ability
					if (figure.has("keywords")) {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s, figure
								.getString("keywords")));
					} else {
						all.add(new GlobalSearchNode(key, figure
								.getString("name"), s));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(System.currentTimeMillis() - start);
		application.setSearchNodes(all);
		application.setGlobalSearchReady(true);
		synchronized (syncToken) {
			application.getSyncToken().notify();
		}
		// application.setGlobalParseDone(true);
	}

	private boolean notPassableJSON(String key) {
		return key.equals("version")
				|| key.equals("set_title");
	}

}
