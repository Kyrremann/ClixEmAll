package net.fifthfloorstudio.gotta.clix.em.all.lists;

import java.util.Iterator;

import net.fifthfloorstudio.gotta.clix.em.all.AddClix;
import net.fifthfloorstudio.gotta.clix.em.all.Database;
import net.fifthfloorstudio.gotta.clix.em.all.JsonParser;
import net.fifthfloorstudio.gotta.clix.em.all.R;
import net.fifthfloorstudio.gotta.clix.em.all.adapters.CollectionAdapter;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CollectionList extends ListActivity {
	public static final String COLLECTION = "COLLECTION";
	public static final String POSITION = "POSITION";
	// public static final String JSON = "JSON";
	public static final String TITLE = "TITLE";
	public static final String SET_ID = "set_id";

	private SharedPreferences settings;
	private final String VISIBILITY = "VISIBILITY";
	public static final int ALL = 0;
	public static final int HAVE = 1;
	public static final int WANT = 2;
	public static final int HAVE_WANT = 3;
	public static final int REST = 4;
	public static final int TRADE = 5;

	// private int position;
	private String id, set;
	// private String[] sets, jsonFiles;
	private Database database;
	private int setCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_list);

		// sets = getResources().getStringArray(R.array.titles_modern);
		// jsonFiles = getResources().getStringArray(R.array.json_modern);
		id = getIntent().getExtras().getString(COLLECTION);
		// position = getIntent().getExtras().getInt(POSITION);
		database = new Database(this);
		settings = getSharedPreferences(id, MODE_PRIVATE);
		set = id.split(".json")[0];
		database.open();
		database.close();

		// setTitle(sets[position]);
		@SuppressWarnings("unchecked")
		Iterator<String> keys = JsonParser.getJsonSet(this, id).keys();
		while (keys.hasNext()) {
			setCount++;
			keys.next();
		}

		updateTitle();
		generateCollectionAdapter();
	}

	private void generateCollectionAdapter() {
		setListAdapter(new CollectionAdapter(this, R.layout.listrow, id));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View v,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(CollectionList.this);
				builder.setTitle(R.string.collection_dialog_have_want);
				final View dialog = getLayoutInflater().inflate(
						R.layout.dialog_have_want, null);
				builder.setView(dialog);
				final String number = ((TextView) v.findViewById(R.id.id))
						.getText().toString();

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
						updateTitle();
						saveDialog(dialog, number);
					}
				});
				builder.setNeutralButton(R.string.done, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog2, int which) {
						updateTitle();
						saveDialog(dialog, number);
						dialog2.cancel();
					}
				});
				builder.create().show();
			}
		});

		try {
			((CollectionAdapter) getListAdapter()).filterList(settings.getInt(
					VISIBILITY, 0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void saveDialog(View dialog, String number) {
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

		((CollectionAdapter) getListAdapter()).refreshListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collection_menu, menu);
//		MenuItem item = menu.findItem(R.id.menu_collection_search);
//		item.setVisible(false);
//		item.setEnabled(false);

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_share_have) {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setCancelable(false);
			pd.setMessage("Crunching your haves");
			pd.show();
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intent.EXTRA_SUBJECT, "Check out my haves!");
			database.open();
			intent.putExtra(Intent.EXTRA_TEXT,
					database.getStringOfHave(this.id.split(".json")[0]));
			database.close();
			pd.cancel();
			startActivity(Intent.createChooser(intent,
					"Where to share your haves?"));
		} else if (id == R.id.menu_share_want) {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setCancelable(false);
			pd.setMessage("Crunching your wants");
			pd.show();
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intent.EXTRA_SUBJECT, "This is what I want!");
			database.open();
			intent.putExtra(Intent.EXTRA_TEXT,
					database.getStringOfWant(this.id.split(".json")[0]));
			database.close();
			pd.cancel();
			startActivity(Intent.createChooser(intent,
					"Where to share your trades?"));
		} else if (id == R.id.menu_share_trade) {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setCancelable(false);
			pd.setMessage("Crunching your trades");
			pd.show();
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intent.EXTRA_SUBJECT, "This is what I have for trade!");
			database.open();
			intent.putExtra(Intent.EXTRA_TEXT,
					database.getStringOfTrade(this.id.split(".json")[0]));
			database.close();
			pd.cancel();
			startActivity(Intent.createChooser(intent,
					"Where to share your trades?"));
		} else if (id == R.id.menu_add_all) {
			database.open();
			database.setHaveAll(this.id.split(".json")[0], JsonParser
					.getJsonSet(this, this.id).keys());
			database.close();
			((CollectionAdapter) getListAdapter()).notifyDataSetChanged();
			updateTitle();
		} else if (id == R.id.menu_all) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(VISIBILITY, ALL);
			editor.commit();
			filterList();
		} else if (id == R.id.menu_have) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(VISIBILITY, HAVE);
			editor.commit();
			filterList();
		} else if (id == R.id.menu_want) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(VISIBILITY, WANT);
			editor.commit();
			filterList();
		} else if (id == R.id.menu_rest) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(VISIBILITY, REST);
			editor.commit();
			filterList();
		} else if (id == R.id.menu_multi_edit) {
			Intent intent = new Intent(this, MultiEditList.class);
			intent.putExtra(COLLECTION, this.set);
			startActivityForResult(intent,
					Activity.RESULT_FIRST_USER);
		} else if (id == R.id.menu_add_clix) {
			startActivity(new Intent(this, AddClix.class));
		}

		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			((CollectionAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	private void filterList() {
		try {
			((CollectionAdapter) getListAdapter()).filterList(settings.getInt(
					VISIBILITY, 0));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateTitle() {
		database.open();
		int count = database.getHaveCount(id.split(".json")[0]);
		setTitle(count + "/" + setCount + " - "
				+ getIntent().getExtras().getString(TITLE));
		database.close();
	}
}
