package net.fifthfloorstudio.gotta.clix.em.all;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SynchronizeSetHandler extends Handler {

	public static final int DONE = 1;
	public static final int UPDATING = 2;
	public static final int INFO = 3;

	private String[] modernArray;
	private String[] otherArray;
	private String[] goldenArray;

	public static final String SET = "set";

	private Context context;
	private ProgressDialog progressDialog;

	public SynchronizeSetHandler(Context context, ProgressDialog progressDialog) {
		this.context = context;
		this.progressDialog = progressDialog;
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case DONE:
				Log.d("HANDLER", "DONE");
				progressDialog.setMessage(context.getString(R.string.handler_complete));
				break;
			case UPDATING:
				Log.d("HANDLER", "UPDATING");
				progressDialog.setMessage(context.getString(R.string.handler_updating));
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
				progressDialog.setMessage("Updating " + setName);
				break;
		}

	}

	private String getSetTitleByFilename(String string) {
		string += ".json";
		if (modernArray == null) {
			modernArray = context.getResources().getStringArray(R.array.json_modern);
		}

		for (int i = 0; i < modernArray.length; i++) {
			if (modernArray[i].equals(string)) {
				return context.getResources().getStringArray(R.array.titles_modern)[i];
			}
		}

		if (otherArray == null) {
			otherArray = context.getResources().getStringArray(R.array.json_other);
		}

		for (int i = 0; i < otherArray.length; i++) {
			if (otherArray[i].equals(string)) {
				return context.getResources().getStringArray(R.array.titles_other)[i];
			}
		}

		if (goldenArray == null) {
			goldenArray = context.getResources().getStringArray(R.array.json_golden);
		}

		for (int i = 0; i < goldenArray.length; i++) {
			if (goldenArray[i].equals(string)) {
				return context.getResources().getStringArray(R.array.titles_golden)[i];
			}
		}

		throw new RuntimeException("No such set is found");
	}

}
