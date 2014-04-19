package net.fifthfloorstudio.gotta.clix.em.all.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.fifthfloorstudio.gotta.clix.em.all.Database;
import net.fifthfloorstudio.gotta.clix.em.all.JsonParser;
import net.fifthfloorstudio.gotta.clix.em.all.R;
import net.fifthfloorstudio.gotta.clix.em.all.lists.CollectionList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectionAdapter extends ArrayAdapter<String> implements
		Filterable {

	public static final String NAME = "name";
	public static final String KEYWORDS = "keywords";
	public static final String TEAM_ABILITY = "team_ability";
	public static final String POINTS = "points";

	private Context context;
	private JSONObject jsonObject;
	private Database database;
	private String set;
	List<String> collections, filteredCollections;
	CollectionFilter filter;

	public CollectionAdapter(Context context, int textViewResourceId, String id) {
		super(context, textViewResourceId, new ArrayList<String>());
		this.context = context;
		this.set = id;
		collections = new ArrayList<String>();
		filteredCollections = new ArrayList<String>();
		try {
			filterList(CollectionList.ALL);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		database = new Database(context);
		getFilter();
		// TODO: Make a global getHave, getWant, and getTrade
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new CollectionFilter();
		return filter;
	}

	public void refreshListView() {
		notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	public void filterList(int visibility) throws JSONException {
		notifyDataSetChanged();
		collections.clear();
		filteredCollections.clear();
		super.clear();
		jsonObject = JsonParser.getJsonSet(context, set);
		Cursor cursor;
		Iterator<String> keys;

		switch (visibility) {
		case CollectionList.ALL:
			keys = jsonObject.keys();
			while (keys.hasNext()) {
				collections.add(keys.next());
			}
			break;
		case CollectionList.HAVE:
			database.open();
			cursor = database.getHave(set.split(".json")[0]);
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				do {
					collections.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			break;
		case CollectionList.WANT:
			database.open();
			cursor = database.getWant(set.split(".json")[0]);
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				do {
					collections.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			break;
		case CollectionList.HAVE_WANT:
			database.open();
			cursor = database.getHaveWant(set.split(".json")[0]);
			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				do {
					collections.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			break;
		default:
			database.open();
			cursor = database.getHaveWant(set.split(".json")[0]);
			keys = jsonObject.keys();
			while (keys.hasNext())
				collections.add(keys.next());

			if (cursor.getCount() != 0) {
				cursor.moveToFirst();
				do {
					collections.remove(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			break;
		}
		java.util.Collections.sort(collections);
		filteredCollections.addAll(collections);

		for (String s : filteredCollections) {
			add(s);
		}

		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.listrow2, null);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.keywords = (TextView) convertView
					.findViewById(R.id.keywords);
//			holder.have_want = (LinearLayout) convertView
//					.findViewById(R.id.have_want);
			holder.have = (TextView) convertView.findViewById(R.id.have);
			holder.want = (TextView) convertView.findViewById(R.id.want);
			holder.trade = (TextView) convertView.findViewById(R.id.trade);
			// TODO: Team Ability
			// holder.ta = (ImageView) convertView.findViewById(R.id.ta);
			holder.points = (TextView) convertView.findViewById(R.id.points);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String id = filteredCollections.get(position);
		holder.id.setText(id);
		try {
			JSONObject object = jsonObject.getJSONObject(id);
			holder.title.setText(object.getString(NAME));
			String keywords = "";
			try {
				keywords = object.getString(KEYWORDS);
				if (keywords.length() == 0) {
					holder.keywords.setText("No keywords");
				} else {
					holder.keywords.setText(keywords);
				}
			} catch (JSONException e) {
				holder.keywords.setText("No keywords");
			}
			if (object.has(POINTS)) {
				holder.points.setText(object.getString(POINTS));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		database.open();
		boolean colored = false;
		String set = this.set.split(".json")[0];
		int count = database.getFigureHaveCount(set, id);
		if (count > 0) {
			// holder.have_want.setVisibility(View.VISIBLE);
			//holder.have_want.findViewById(R.id.list_have_layout).setVisibility(
				//	View.VISIBLE);
//			((TextView) holder.have_want.findViewById(R.id.list_have_layout)
//					.findViewById(R.id.number_have)).setText(Integer
//					.toString(count));
			// ImageView child = new ImageView(context);
			// child.setImageResource(R.drawable.ic_item_have);
			// holder.have_want.addView(child);
			// holder.have_want.setVisibility(View.VISIBLE);
			holder.have.setText(Integer.toString(count));
			convertView.setBackgroundColor(Color.parseColor("#7599CC00"));
			colored = true;
		} else {
			// holder.have_want.findViewById(R.id.list_have).setVisibility(
			// View.GONE);
//			holder.have_want.findViewById(R.id.list_have_layout).setVisibility(
//					View.GONE);
		}

		count = database.getFigureWantCount(set, id);
		database.close();
		if (count > 0) {
			// holder.have_want.setVisibility(View.VISIBLE);
//			holder.have_want.findViewById(R.id.list_want_layout).setVisibility(
//					View.VISIBLE);
//			((TextView) holder.have_want.findViewById(R.id.list_want_layout)
//					.findViewById(R.id.number_want)).setText(Integer
//					.toString(count));
			// ImageView child = new ImageView(context);
			// child.setImageResource(R.drawable.ic_star);
			// holder.have_want.addView(child);
			// holder.have_want.setVisibility(View.VISIBLE);
			holder.want.setText(Integer.toString(count));
			if (colored) {
				convertView.setBackgroundColor(Color.parseColor("#7566C072"));
			} else {
				convertView.setBackgroundColor(Color.parseColor("#7533B5E5"));
				colored = true;
			}
		} else {
			// holder.have_want.findViewById(R.id.list_want).setVisibility(
			// View.GONE);
//			holder.have_want.findViewById(R.id.list_want_layout).setVisibility(
//					View.GONE);
		}

		if (!colored) {
//			holder.have_want.setVisibility(View.GONE);
			convertView.setBackgroundColor(-1);
		} else {
//			holder.have_want.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView id;
		TextView title;
		TextView keywords;
		TextView points;
		TextView have, want, trade;
		LinearLayout have_want;
		// ImageView ta;
	}

	private class CollectionFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase(Locale.getDefault());
			FilterResults results = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<String> filteredItems = new ArrayList<String>();
				for (String s : collections) {
					try {
						JSONObject object = jsonObject.getJSONObject(s);
						if (object.getString("name")
								.toLowerCase(Locale.ENGLISH)
								.contains(constraint)
								|| object.getString("keywords")
										.toLowerCase(Locale.ENGLISH)
										.contains(constraint)
								|| s.toLowerCase(Locale.ENGLISH).contains(
										constraint))
							filteredItems.add(s);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				results.count = filteredItems.size();
				results.values = filteredItems;
			} else {
				synchronized (this) {
					results.count = collections.size();
					results.values = collections;
				}
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filteredCollections = (ArrayList<String>) results.values;
			notifyDataSetChanged();
			clear();
			for (String s : filteredCollections)
				add(s);
			// notifyDataSetInvalidated();
			notifyDataSetChanged();
		}

	}
}
