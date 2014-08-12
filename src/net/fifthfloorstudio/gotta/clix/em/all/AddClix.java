package net.fifthfloorstudio.gotta.clix.em.all;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddClix extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addclix);
	}

	private String parseInfo() {
		String info = "Set: "
				+ ((TextView) findViewById(R.id.add_edit_set)).getText();

		info += "\nId: "
				+ ((TextView) findViewById(R.id.add_edit_id)).getText();
		String json = "\""
				+ ((TextView) findViewById(R.id.add_edit_id)).getText()
				+ "\":{";

		info += "\nName: "
				+ ((TextView) findViewById(R.id.add_edit_name)).getText();
		json += "\"name\":\""
				+ ((TextView) findViewById(R.id.add_edit_name)).getText()
				+ "\",";

		info += "\nTA: "
				+ ((TextView) findViewById(R.id.add_edit_ta)).getText();
		json += "\"team_ability\":[\""
				+ ((TextView) findViewById(R.id.add_edit_ta)).getText()
				+ "\"],";

		info += "\nKeywords: "
				+ ((TextView) findViewById(R.id.add_edit_keywords)).getText();
		json += "\"keywords\":\""
				+ ((TextView) findViewById(R.id.add_edit_keywords)).getText()
				+ "\",";

		info += "\nPoints: "
				+ ((TextView) findViewById(R.id.add_edit_points)).getText();
		json += "\"points\":\""
				+ ((TextView) findViewById(R.id.add_edit_points)).getText()
				+ "\"}";

		return info + "\n" + json;
	}

	public void send(View v) {
		Intent send = new Intent(Intent.ACTION_SENDTO);
		String uriText = "mailto:"
				+ Uri.encode("heroclix@fifthfloorstudio.net") + "?subject="
				+ Uri.encode("ClixEmAll: Missing clix") + "&body="
				+ Uri.encode(parseInfo());
		Uri uri = Uri.parse(uriText);

		send.setData(uri);
		startActivity(Intent.createChooser(send, "Send e-mail"));
	}

}
