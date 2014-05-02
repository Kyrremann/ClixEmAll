package net.fifthfloorstudio.gotta.clix.em.all;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import static net.fifthfloorstudio.gotta.clix.em.all.JsonParser.saveJsonToAsset;

public class HTTPUtil {

	private static final String TAG = "HTTP";
	private static final String SERVER_ADDRESS = "http://clixemall.heroku.com";

	public static String getVersionListFromServer(Context applicationContext) {
		return defaultHTTPConnection("/set/versions");
	}

	public static String getUpdateFromServer(String set) {
		set = removeFileExtension(set);
		return defaultHTTPConnection("/set/" + set);
	}

	private static String defaultHTTPConnection(String addressPostfix) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		URI website = null;

		try {
			website = new URI(SERVER_ADDRESS + addressPostfix);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			return bufferedReader.readLine();
		} catch (URISyntaxException e) {
			Log.e(TAG, "Can't create URI");
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Something went wrong when connection to client");
		} catch (IOException e) {
			Log.e(TAG, "IOException");
		}

		throw new RuntimeException("Could not load version list from server");
	}

	private static String removeFileExtension(String set) {
		return set.replaceAll("\\.json", "");
	}
}
