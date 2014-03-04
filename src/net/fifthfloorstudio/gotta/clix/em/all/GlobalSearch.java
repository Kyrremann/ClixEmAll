package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GlobalSearch extends Activity {

	protected ListView list;
	private Database database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_globalsearch);
		setTitle(R.string.global_search);
		database = new Database(this);

		MyApplication myApplication = (MyApplication) getApplicationContext();
		
		Object syncToken = myApplication.getSyncToken();
		while (true && !myApplication.isGlobalSearchReady()) {
			synchronized (syncToken) {
				try {
					syncToken.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		list = (ListView) findViewById(R.id.global_search_list);
		list.setAdapter(new GlobalSearchAdapter(this, R.layout.listrow,
				((MyApplication) getApplicationContext()).getSearchNodes()));
		setListClicker();
	}

	private void setListClicker() {
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View v,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(GlobalSearch.this);
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
						saveDialog(dialog, set, number);
						dialog2.cancel();
					}
				});
				builder.create().show();
			}
		});
	}

	private void saveDialog(View dialog, String set, String number) {
		database.open();
		int count;
		EditText edit = (EditText) dialog.findViewById(R.id.dialog_have);
		count = Integer.parseInt(edit.getText().toString());
		if (count > 0) {
			database.setFigureHave(set, number, count);
		} else {
			database.removeFigureHave(set, number);
		}
		edit = (EditText) dialog.findViewById(R.id.dialog_want);
		count = Integer.parseInt(edit.getText().toString());
		if (count > 0) {
			database.setFigureWant(set, number, count);
		} else {
			database.removeFigureWant(set, number);
		}
		edit = (EditText) dialog.findViewById(R.id.dialog_trade);
		count = Integer.parseInt(edit.getText().toString());
		if (count > 0) {
			database.setFigureTrade(set, number, count);
		} else {
			database.removeFigureTrade(set, number);
		}
		database.close();

		((GlobalSearchAdapter) list.getAdapter()).refreshListView();
	}

	public void search(View v) {
		String searchString = ((EditText) findViewById(R.id.global_search_edit))
				.getText().toString();
		if (searchString.length() > 2) {
			((GlobalSearchAdapter) list.getAdapter()).getFilter().filter(
					searchString);
			list.setVisibility(View.VISIBLE);
		} else {
			list.setVisibility(View.GONE);
		}
	}
}
