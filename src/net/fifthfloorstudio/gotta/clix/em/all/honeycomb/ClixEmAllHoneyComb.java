package net.fifthfloorstudio.gotta.clix.em.all.honeycomb;

import net.fifthfloorstudio.gotta.clix.em.all.ClixEmAll;
import net.fifthfloorstudio.gotta.clix.em.all.R;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ClixEmAllHoneyComb extends ClixEmAll implements
		OnQueryTextListener {

	private SearchView searchView;

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if (item.getItemId() == R.id.menu_global_search) {
			startActivity(new Intent(this, GlobalSearchHoneyComb.class));
			return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.menu_as_list).setChecked(
				settings.getBoolean(ASLIST, true));
		MenuItem item = menu.findItem(R.id.menu_search);
		if (!galleryView) {
			item.setVisible(true);
			item.setEnabled(true);
			searchView = (SearchView) menu.findItem(R.id.menu_search)
					.getActionView();
			searchView.setOnQueryTextListener(this);
		} else {
			item.setVisible(false);
			item.setEnabled(false);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_as_list).setChecked(
				settings.getBoolean(ASLIST, true));

		if (galleryView) {
			menu.findItem(R.id.menu_search).setEnabled(false);
			menu.findItem(R.id.menu_set_all).setEnabled(false);
			menu.findItem(R.id.menu_modern).setEnabled(false);
			menu.findItem(R.id.menu_golden).setEnabled(false);
			menu.findItem(R.id.menu_other).setEnabled(false);
			return true;
		} else if (justCreatedListView) {
			menu.findItem(R.id.menu_search).setEnabled(true);
			menu.findItem(R.id.menu_set_all).setEnabled(true);
			menu.findItem(R.id.menu_modern).setEnabled(true);
			menu.findItem(R.id.menu_golden).setEnabled(true);
			menu.findItem(R.id.menu_other).setEnabled(true);
			justCreatedListView = false;
		}
		menu.findItem(R.id.menu_search).setEnabled(true);
		menu.findItem(R.id.menu_search).setVisible(true);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		((Filterable) adapter).getFilter().filter(newText);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
}
