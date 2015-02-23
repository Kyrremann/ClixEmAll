package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.os.Looper;
import net.fifthfloorstudio.gotta.clix.em.all.adapters.GalleryAdapter;
import net.fifthfloorstudio.gotta.clix.em.all.adapters.SetAdapter;
import net.fifthfloorstudio.gotta.clix.em.all.honeycomb.lists.CollectionListHoneyComb;
import net.fifthfloorstudio.gotta.clix.em.all.lists.CollectionList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import static android.content.DialogInterface.OnClickListener;

public class ClixEmAll extends Activity {

	protected SharedPreferences settings;
	protected final String ASLIST = "asList";
	private final String LIST = "list";
	private final String SHOW_ABOUT = "show_about";
	private final int ALL = 0;
	private final int MODERN = 1;
	private final int GOLDEN = 2;
	private final int OTHER = 3;
	private final int HAVE = 0;
	private final int WANT = 1;
	private final int TRADE = 2;
	private final String PREFERENCES = "CLIX_PREFS";

	private List<String> setList;
	private String[] jsonFiles, titles;
	private ListView listView;
	private GridView gridView;
	protected BaseAdapter adapter;
	protected boolean galleryView;
	protected boolean justCreatedListView;
	private Database database;
	private Dialog shareDialog;
	private Dialog infoDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_activity_front);

		settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		if (settings.getBoolean(ASLIST, true)) {
			createListView();
		} else {
			createGalleryView();
		}
		jsonFiles = getJsonFiles();
		database = new Database(this);
		int vc;
		try {
			vc = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			vc = 1;
			e.printStackTrace();
		}

		if (settings.getInt(SHOW_ABOUT, 0) < vc) {
			showInfoDialog();
			Editor editor = settings.edit();
			editor.putInt(SHOW_ABOUT, vc);
			editor.commit();
		}
	}

	private String[] getJsonFiles() {
		switch (settings.getInt(LIST, -1)) {
		case MODERN:
			return getResources().getStringArray(R.array.json_modern);
		case OTHER:
			return getResources().getStringArray(R.array.json_other);
		case GOLDEN:
			return getResources().getStringArray(R.array.json_golden);
		default:
			String[] modern = getResources()
					.getStringArray(R.array.json_modern),
			other = getResources().getStringArray(R.array.json_other),
			golden = getResources().getStringArray(R.array.json_golden);

			String[] result = new String[modern.length + other.length
					+ golden.length];
			System.arraycopy(modern, 0, result, 0, modern.length);
			System.arraycopy(other, 0, result, modern.length, other.length);
			System.arraycopy(golden, 0, result, modern.length + other.length,
					golden.length);
			return result;
		}
	}

	private String[] getSetTitles(int value) {
		value = (value == -1) ? settings.getInt(LIST, -1) : value;
		switch (value) {
		case MODERN:
			return getResources().getStringArray(R.array.titles_modern);
		case OTHER:
			return getResources().getStringArray(R.array.titles_other);
		case GOLDEN:
			return getResources().getStringArray(R.array.titles_golden);
		default:
			String[] modern = getResources().getStringArray(
					R.array.titles_modern),
			other = getResources().getStringArray(R.array.titles_other),
			golden = getResources().getStringArray(R.array.titles_golden);

			String[] result = new String[modern.length + other.length
					+ golden.length];
			System.arraycopy(modern, 0, result, 0, modern.length);
			System.arraycopy(other, 0, result, modern.length, other.length);
			System.arraycopy(golden, 0, result, modern.length + other.length,
					golden.length);
			return result;
		}
	}

	private void createListView() {
		setContentView(R.layout.activity_clixemall);
		justCreatedListView = true;
		galleryView = false;

		listView = (ListView) findViewById(R.id.listview);
		setList = new ArrayList<String>();
		adapter = new SetAdapter<String>(this, R.layout.setrow, R.id.set_title, setList);
		toggleList(settings.getInt(LIST, 0));
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				onSetItemClick(((TextView) v.findViewById(R.id.set_title)).getText().toString());
			}
		});
	}

	private void createGalleryView() {
		galleryView = true;
		titles = getSetTitles(ALL);
		setContentView(R.layout.activity_set_gallery);

		gridView = (GridView) findViewById(R.id.gallery_grid);
		adapter = new GalleryAdapter(this);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Editor edit = settings.edit();
				edit.putInt(LIST, ALL);
				edit.commit();
				onSetItemClick(titles[position]);
			}
		});
	}

	private void onSetItemClick(String value) {
		jsonFiles = getJsonFiles();
		int position = findPosition(value);
		Intent intent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			intent = new Intent(ClixEmAll.this, CollectionListHoneyComb.class);
		} else {
			intent = new Intent(ClixEmAll.this, CollectionList.class);
		}
		intent.putExtra(CollectionList.COLLECTION, jsonFiles[position]);
		intent.putExtra(CollectionList.TITLE, value);
		startActivity(intent);
	}

	private int findPosition(String name) {
		String[] names = getSetTitles(settings.getInt(LIST, -1));
		for (int i = 0; 0 < names.length; i++)
			if (names[i].equals(name))
				return i;

		return 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.menu_as_list).setChecked(
				settings.getBoolean(ASLIST, true));

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_as_list).setChecked(
				settings.getBoolean(ASLIST, true));

		if (galleryView) {
			menu.findItem(R.id.menu_set_all).setEnabled(false);
			menu.findItem(R.id.menu_modern).setEnabled(false);
			menu.findItem(R.id.menu_golden).setEnabled(false);
			menu.findItem(R.id.menu_other).setEnabled(false);
			return true;
		} else if (justCreatedListView) {
			menu.findItem(R.id.menu_set_all).setEnabled(true);
			menu.findItem(R.id.menu_modern).setEnabled(true);
			menu.findItem(R.id.menu_golden).setEnabled(true);
			menu.findItem(R.id.menu_other).setEnabled(true);
			justCreatedListView = false;
		}

		menu.findItem(R.id.menu_search).setEnabled(false);
		menu.findItem(R.id.menu_search).setVisible(false);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		SharedPreferences.Editor editor = settings.edit();
		switch (id) {
			case R.id.menu_share:
				showShareDialog();
				break;
			case R.id.menu_as_list:
				if (item.isChecked()) {
					item.setChecked(false);
					editor.putBoolean(ASLIST, false);
					createGalleryView();
				} else {
					item.setChecked(true);
					editor.putBoolean(ASLIST, true);
					createListView();
				}
				break;
			case R.id.menu_set_all:
				toggleList(ALL);

				break;
			case R.id.menu_modern:
				toggleList(MODERN);

				break;
			case R.id.menu_golden:
				toggleList(GOLDEN);

				break;
			case R.id.menu_other:
				toggleList(OTHER);
				break;
			case R.id.menu_global_search:
				startActivity(new Intent(this, GlobalSearch.class));
				break;
			case R.id.menu_add_clix:
				startActivity(new Intent(this, AddClix.class));
				break;
			case R.id.menu_backup:
				startActivity(new Intent(this, BackupActivity.class));
				break;
		}
		editor.commit();

		return true;
	}

	private void toggleList(int value) {
		SharedPreferences.Editor editor = settings.edit();
		setList.clear();
		switch (value) {
		case MODERN:
			editor.putInt(LIST, MODERN);
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_modern)));
			break;
		case GOLDEN:
			editor.putInt(LIST, GOLDEN);
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_golden)));
			break;
		case OTHER:
			editor.putInt(LIST, OTHER);
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_other)));
			break;
		default:
			editor.putInt(LIST, ALL);
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_modern)));
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_other)));
			setList.addAll(Arrays.asList(getResources().getStringArray(
					R.array.titles_golden)));
			break;
		}
		adapter.notifyDataSetChanged();
		editor.commit();
	}

	private void showShareDialog() {
		if (shareDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(getString(R.string.share_dialog_title));
			builder.setItems(getResources().getStringArray(R.array.list_share_types), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					share(which);
				}
			});
			builder.setNeutralButton(android.R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					dialogInterface.cancel();
				}
			});
			shareDialog = builder.create();
		}
		shareDialog.show();
	}

	private void share(int which) {
		ProgressDialog pd;
		Intent intent;
		switch (which) {
			case HAVE:
				pd = new ProgressDialog(this);
				pd.setCancelable(false);
				pd.setMessage("Crunching your haves");
				pd.show();
				intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_SUBJECT, "Check out my haves!");
				database.open();
				intent.putExtra(Intent.EXTRA_TEXT, database.getStringOfHave(null));
				database.close();
				pd.cancel();
				startActivity(Intent.createChooser(intent,
						"Where to share your haves?"));
				break;
			case WANT:
				pd = new ProgressDialog(this);
				pd.setCancelable(false);
				pd.setMessage("Crunching your wants");
				pd.show();
				intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_SUBJECT, "This is what I wants!");
				database.open();
				intent.putExtra(Intent.EXTRA_TEXT, database.getStringOfWant(null));
				database.close();
				pd.cancel();
				startActivity(Intent.createChooser(intent,
						"Where to share your wants?"));
				break;
			case TRADE:
				pd = new ProgressDialog(this);
				pd.setCancelable(false);
				pd.setMessage("Crunching your trades");
				pd.show();
				intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Intent.EXTRA_SUBJECT,
						"This is what I have for trade!");
				database.open();
				intent.putExtra(Intent.EXTRA_TEXT, database.getStringOfTrade(null));
				database.close();
				pd.cancel();
				startActivity(Intent.createChooser(intent,
						"Where to share your trade?"));
				break;
		}
	}

	private void showInfoDialog() {
		if (infoDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About");
			builder.setMessage(R.string.about_dialog);
			builder.setPositiveButton("Rate",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent browserIntent = new Intent(
									"android.intent.action.VIEW",
									Uri.parse(getString(R.string.url_playstore)));
							        //Uri.parse(getString(R.string.url_amazon)));
							startActivity(browserIntent);
						}
					});

			builder.setNeutralButton("Okay", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			infoDialog = builder.create();
		}
		infoDialog.show();
	}

	private void synchronizeSets() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.synchronizing));
		progressDialog.show();

		// TODO check for wifi or net
		SynchronizeSetHandler handler = new SynchronizeSetHandler(getApplicationContext(), progressDialog);
		SynchronizeSetRunnable runnable = new SynchronizeSetRunnable(getApplicationContext(), handler);

		new Thread(runnable).start();
	}
}
