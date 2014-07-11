package net.fifthfloorstudio.gotta.clix.em.all;

import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonParser {

	public final static String SET_TITLE = "set_title";
	public final static String NAME = "name";
	public final static String VERSION = "version";
	public final static String FOLDER = "sets";

	private static JSONObject getMeSomeJson(Context context, String file)
			throws IOException, JSONException {
		file = checkForFileJsonExtension(file);

		byte[] buffer;
		try {
			FileInputStream fileInputStream = context.openFileInput(file);
			buffer = new byte[fileInputStream.available()];
			fileInputStream.read(buffer);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			InputStream inputStream = context.getAssets().open(FOLDER + "/" + file);
			buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			inputStream.close();
		}

		return new JSONObject(new String(buffer));
	}
	
	public static String getSetTitle(Context context, String jsonFile) {
		try {
			return getMeSomeJson(context, jsonFile).getString(SET_TITLE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject getJsonSet(Context context, String jsonFile) {
		jsonFile = checkForFileJsonExtension(jsonFile);
		try {
			JSONObject set = getMeSomeJson(context, jsonFile);
			set.remove(SET_TITLE);
			set.remove(VERSION);
			return set;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int getJsonSetVersion(Context context, String jsonFile) {
		jsonFile = checkForFileJsonExtension(jsonFile);
		try {
			JSONObject set = getMeSomeJson(context, jsonFile);
			return set.optInt(VERSION, 1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return 1;
	}

	public static void saveJsonToAsset(Context context, String jsonString, String set) {
		set = checkForFileJsonExtension(set);
		try {
			FileOutputStream outputStream = context.openFileOutput(set, Context.MODE_PRIVATE);
			outputStream.write(jsonString.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String checkForFileJsonExtension(String set) {
		if (!set.endsWith(".json")) {
			set += ".json";
		}
		return set;
	}
}
