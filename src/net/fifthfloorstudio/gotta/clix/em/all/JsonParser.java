package net.fifthfloorstudio.gotta.clix.em.all;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonParser {

	public final static String SET_TITLE = "set_title";
	public final static String NAME = "name";

	private static JSONObject getMeSomeJson(Context context, String file)
			throws IOException, JSONException {
		if (file.endsWith(".json") == false) {
			file += ".json";
		}
		InputStream inputStream = context.getAssets().open(file);
		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);
		inputStream.close();
		return new JSONObject(new String(buffer));
	}
	
	public static String getSetTitle(Context context, String jsonFile) {
		try {
			return JsonParser.getMeSomeJson(context, jsonFile).getString(SET_TITLE); 
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
			JSONObject set = JsonParser.getMeSomeJson(context, jsonFile);
			set.remove(SET_TITLE);
			return set;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
