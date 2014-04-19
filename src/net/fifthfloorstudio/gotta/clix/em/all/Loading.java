package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		final Context context = this;

		new Thread(new Runnable() {

			// TODO Speed up the process, use more threads?
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
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
		}).start();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = null;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					intent = new Intent(Loading.this, ClixEmAllHoneyComb.class);
				} else {
					intent = new Intent(Loading.this, ClixEmAll.class);
				}
				startActivity(intent);
				finish();
			}
		}, 1200);
	}

}
