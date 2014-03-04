package net.fifthfloorstudio.gotta.clix.em.all;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AllHaveWant extends ListActivity {

	private Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_list);
		setTitle(R.string.havewant);
		database = new Database(this);
		getListView().setAdapter(
				new CollectionAdapter(this, R.layout.listrow, "havewant"));
	}

	private void setListClicker() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View v,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(AllHaveWant.this);
				builder.setTitle(R.string.collection_dialog_have_want);
				final View dialog = getLayoutInflater().inflate(
						R.layout.dialog_have_want, null);
				builder.setView(dialog);
				final String number = ((TextView) v.findViewById(R.id.id))
						.getText().toString();
				final String set = (String) v.findViewById(R.id.id).getTag();
				System.out.println(set);

				// TODO database action
				database.open();

				final EditText editHave = (EditText) dialog
						.findViewById(R.id.dialog_have);
				editHave.setText(Integer.toString(database.getFigureHaveCount(
						set, number)));
				final EditText editWant = (EditText) dialog
						.findViewById(R.id.dialog_want);
				editWant.setText(Integer.toString(database.getFigureWantCount(
						set, number)));
				final EditText editTrade = (EditText) dialog
						.findViewById(R.id.dialog_trade);
				editTrade.setText(Integer.toString(database
						.getFigureTradeCount(set, number)));
				database.close();
				ImageButton button = (ImageButton) dialog
						.findViewById(R.id.dialog_have_plus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editHave.setText(Integer.toString(Integer
								.parseInt(editHave.getText().toString()) + 1));
					}
				});
				button = (ImageButton) dialog
						.findViewById(R.id.dialog_have_minus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editHave.setText(Integer.toString(Math.max(
								0,
								Integer.parseInt(editHave.getText().toString()) - 1)));
					}
				});
				button = (ImageButton) dialog
						.findViewById(R.id.dialog_want_plus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editWant.setText(Integer.toString(Integer
								.parseInt(editWant.getText().toString()) + 1));
					}
				});
				button = (ImageButton) dialog
						.findViewById(R.id.dialog_want_minus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editWant.setText(Integer.toString(Math.max(
								0,
								Integer.parseInt(editWant.getText().toString()) - 1)));
					}
				});
				button = (ImageButton) dialog
						.findViewById(R.id.dialog_trade_plus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editTrade.setText(Integer.toString(Integer
								.parseInt(editTrade.getText().toString()) + 1));
					}
				});
				button = (ImageButton) dialog
						.findViewById(R.id.dialog_trade_minus);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						editTrade.setText(Integer.toString(Math.max(0, Integer
								.parseInt(editTrade.getText().toString()) - 1)));
					}
				});

				builder.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog2) {
						// updateTitle();
						// saveDialog(dialog, number);
					}
				});
				builder.setNeutralButton(R.string.done, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog2, int which) {
						// updateTitle();
						//saveDialog(dialog, set, number);
						dialog2.cancel();
					}
				});
				builder.create().show();
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
}
