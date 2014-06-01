package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.*;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
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

	private TextView updateView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		updateView = (TextView) findViewById(R.id.updateView);
/*
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case DONE:
						// lagSok(getApplicationContext());
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
						// String setName = getStringResourceByName(set);
						updateView.setText("Updating " + set);
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
				Log.d("TIMER", "" + (System.currentTimeMillis() - start));
				if (updatableSets.isEmpty()) {
					handler.sendEmptyMessage(DONE);
					return;
				} else {
					start = System.currentTimeMillis();
					handler.sendEmptyMessage(UPDATING);
					getUpdatesFromServer();
					Log.d("TIMER", "" + (System.currentTimeMillis() - start));
					handler.sendEmptyMessage(DONE);
				}
			}

			private void checkForUpdates(String set, int latestVersion) {
				JSONObject jsonObject = JsonParser.getJsonSet(getApplicationContext(), set);
				if (jsonObject.optInt("version", 1) < latestVersion + 1) {
					// System.out.println(HTTPUtil.getUpdateFromServer(set));
					updatableSets.add(set);
				}
			}

			private void getUpdatesFromServer() {
//				Message message = new Message();
//				message.what = INFO;
//				Bundle bundle = new Bundle();
				for (String set : updatableSets) {
					System.out.println(set);
//					bundle.putString(SET, set);
//					message.setData(bundle);
//					handler.sendMessage(message);
					// System.out.println(HTTPUtil.getUpdateFromServer(set));
				}
			}
		});
		persistentSetThread.start();
*/
		lagSok(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startClixEmAll();
			}
		}, 1200);
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
			Log.d("LOADING", "Reading from set " + set);
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

	private String getStringResourceByName(String string) {
		String packageName = getPackageName();
		int resId = getResources().getIdentifier(string, "string", packageName);
		return getString(resId);
	}

}
