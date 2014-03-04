package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class GlobalSearchAdapter extends
		ArrayAdapter<ArrayList<GlobalSearchAdapter>> implements Filterable {

	public static final String NAME = "name";
	public static final String KEYWORDS = "keywords";
	public static final String TEAM_ABILITY = "team_ability";
	public static final int SET = 1;

	private Context context;
	private JSONObject jsonObject;
	private Database database;
	// private String set;
	List<GlobalSearchNode> collections, filteredCollections, complete;
	GlobalSearchFilter filter;

	public GlobalSearchAdapter(Context context, int textViewResourceId,
			ArrayList<GlobalSearchNode> objects) {
		super(context, textViewResourceId);

		this.context = context;
		complete = objects;
		filteredCollections = new ArrayList<GlobalSearchNode>();

		database = new Database(context);
		// TODO: Make a global getHave, getWant, and getTrade
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new GlobalSearchFilter();
		return filter;
	}

	public void refreshListView() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return filteredCollections.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listrow, null);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.keywords = (TextView) convertView
					.findViewById(R.id.keywords);
			holder.have_want = (LinearLayout) convertView
					.findViewById(R.id.have_want);
			// TODO: Team Ability
			// holder.ta = (ImageView) convertView.findViewById(R.id.ta);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GlobalSearchNode node = filteredCollections.get(position);
		holder.id.setText(node.getId());
		holder.id.setTag(node.getSet());
		holder.title.setText(node.getName());
		holder.keywords.setText(node.getKeywords());

		database.open();
		boolean coolored = false;
		String set = node.getSet();
		int count = database.getFigureHaveCount(set, node.getId());
		if (count > 0) {
			holder.have_want.findViewById(R.id.list_have_layout).setVisibility(
					View.VISIBLE);
			((TextView) holder.have_want.findViewById(R.id.list_have_layout)
					.findViewById(R.id.number_have)).setText(Integer
					.toString(count));
			convertView.setBackgroundColor(Color.parseColor("#7599CC00"));
			coolored = true;
		} else {
			holder.have_want.findViewById(R.id.list_have_layout).setVisibility(
					View.GONE);
		}

		count = database.getFigureWantCount(set, node.getId());
		database.close();
		if (count > 0) {
			holder.have_want.findViewById(R.id.list_want_layout).setVisibility(
					View.VISIBLE);
			((TextView) holder.have_want.findViewById(R.id.list_want_layout)
					.findViewById(R.id.number_want)).setText(Integer
					.toString(count));
			if (coolored) {
				convertView.setBackgroundColor(Color.parseColor("#7566C072"));
			} else {
				convertView.setBackgroundColor(Color.parseColor("#7533B5E5"));
				coolored = true;
			}
		} else {
			holder.have_want.findViewById(R.id.list_want_layout).setVisibility(
					View.GONE);
		}

		if (!coolored) {
			holder.have_want.setVisibility(View.GONE);
			convertView.setBackgroundColor(-1);
		} else {
			holder.have_want.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView id;
		TextView title;
		TextView keywords;
		LinearLayout have_want;
		// ImageView ta;
	}

	private class GlobalSearchFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase(Locale.getDefault());
			FilterResults results = new FilterResults();

			ArrayList<GlobalSearchNode> filteredItems = new ArrayList<GlobalSearchNode>();
			for (GlobalSearchNode n : complete) {

				if (n.getName().toLowerCase(Locale.ENGLISH)
						.contains(constraint)
						|| n.getKeywords().toLowerCase(Locale.ENGLISH)
								.contains(constraint)
						|| n.getSet().toLowerCase(Locale.ENGLISH)
								.contains(constraint)
						|| n.getId().toLowerCase(Locale.ENGLISH)
								.contains(constraint)) {
					filteredItems.add(n);
				}

				results.count = filteredItems.size();
				results.values = filteredItems;
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filteredCollections = (List<GlobalSearchNode>) results.values;
			notifyDataSetChanged();
		}
	}
}
