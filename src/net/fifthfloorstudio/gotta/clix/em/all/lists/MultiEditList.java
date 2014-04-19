package net.fifthfloorstudio.gotta.clix.em.all.lists;

import net.fifthfloorstudio.gotta.clix.em.all.R;
import net.fifthfloorstudio.gotta.clix.em.all.adapters.MultiEditAdapter;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MultiEditList extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_list);
		setTitle(R.string.menu_multi_edit);

		setListAdapter(
				new MultiEditAdapter(this, R.layout.multieditrow, getIntent()
						.getExtras().getString(CollectionList.COLLECTION)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.multi_edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_multi_save) {
			((MultiEditAdapter) getListAdapter()).saveMultipleEdits();
			setResult(Activity.RESULT_OK);
			finish();
		}

		return true;
	}
}
