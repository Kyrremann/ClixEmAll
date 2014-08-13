package net.fifthfloorstudio.gotta.clix.em.all.honeycomb;

import net.fifthfloorstudio.gotta.clix.em.all.GlobalSearch;
import net.fifthfloorstudio.gotta.clix.em.all.R;
import net.fifthfloorstudio.gotta.clix.em.all.adapters.GlobalSearchAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class GlobalSearchHoneyComb extends GlobalSearch implements OnQueryTextListener {

	private SearchView searchView;
	private TextView searh_hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searh_hint = (TextView) findViewById(R.id.global_searh_text);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.global_search_menu, menu);
		searchView = (SearchView) menu.findItem(R.id.menu_global_search)
				.getActionView();
		searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText.length() > 2) {
			((GlobalSearchAdapter) list.getAdapter()).getFilter().filter(
					newText);
			list.setVisibility(View.VISIBLE);
			searh_hint.setVisibility(View.GONE);
		} else {
			searh_hint.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}
}
