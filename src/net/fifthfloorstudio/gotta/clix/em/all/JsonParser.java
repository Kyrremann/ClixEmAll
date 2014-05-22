package net.fifthfloorstudio.gotta.clix.em.all;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;
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
		if (file.endsWith(".json") == false) {
			file += ".json";
		}

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
		if (!jsonFile.endsWith(".json")) {
			jsonFile += ".json";
		}
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

	public static void saveJsonToAsset(Context context, JSONObject jsonObject) {
		System.out.println("JSON " + jsonObject.toString());
		// context.open
	}
}
