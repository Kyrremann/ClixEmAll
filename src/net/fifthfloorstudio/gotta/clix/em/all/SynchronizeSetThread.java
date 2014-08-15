package net.fifthfloorstudio.gotta.clix.em.all;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.fifthfloorstudio.gotta.clix.em.all.SynchronizeSetHandler.*;

public class SynchronizeSetThread implements Runnable {

	private List<String> updatableSets;
	private Context context;
	private SynchronizeSetHandler handler;

	public SynchronizeSetThread(Context context, SynchronizeSetHandler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		String list;
		try {
			list = HTTPUtil.getVersionFromServer(context);
		} catch (RuntimeException e) {
			handler.sendEmptyMessage(DONE);
			Toast.makeText(context, context.getString(R.string.synchronization_problem_connecting), Toast.LENGTH_SHORT).show();
			Log.d("TIMER", "Can't retrieve version, it took " + (System.currentTimeMillis() - start) + " ms");
			return;
		}

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
		int setVersion = JsonParser.getJsonSetVersion(context, set);
		if (setVersion < latestVersion) {
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
			JsonParser.saveJsonToAsset(context, jsonString, set);
		}
	}
}
