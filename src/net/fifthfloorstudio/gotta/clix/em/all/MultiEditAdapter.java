package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MultiEditAdapter extends ArrayAdapter<String> {

	private static final int BOTH = 3, WANT = 2, HAVE = 1;
	private Context context;
	private JSONObject jsonObject;
	private Database database;
	private Cursor cursor;
	private List<String> collections;
	private int[] checked;
	private String set;

	@SuppressWarnings("unchecked")
	public MultiEditAdapter(Context context, int textViewResourceId, String set) {
		super(context, textViewResourceId);

		this.context = context;
		this.set = set;
		collections = new ArrayList<String>();
		collections.clear();
		super.clear();
		jsonObject = JsonParser.getJsonSet(context, set);
		Iterator<String> keys;
		keys = jsonObject.keys();
		while (keys.hasNext()) {
			collections.add(keys.next());
		}

		java.util.Collections.sort(collections);

		for (String s : collections) {
			add(s);
		}

		checked = new int[collections.size()];
		database = new Database(context);
		database.open();
		cursor = database.getHaveWant(set);
		for (int position = 0; position < cursor.getCount(); position++) {
			cursor.moveToPosition(position);
			int index = 0;
			while (index < collections.size()) {
				if (cursor.getString(0).equals(collections.get(index))) {
					if (cursor.getInt(2) > 0) {
						checked[index] = 2;
					}
					if (cursor.getInt(1) > 0) {
						checked[index]++;
					}

					break;
				}
				index++;
			}
		}

		database.close();
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.multieditrow, null);
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.have = (CheckBox) convertView.findViewById(R.id.check_have);
			holder.want = (CheckBox) convertView.findViewById(R.id.check_want);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String id = collections.get(position);
		holder.id.setText(id);
		try {
			JSONObject object = jsonObject.getJSONObject(id);
			holder.title.setText(object.getString(CollectionAdapter.NAME));
		} catch (JSONException e) {
			holder.title.setText("unknown");
			e.printStackTrace();
		}
		holder.have.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox box = (CheckBox) v;
				if (box.isChecked()) {
					checked[position] += HAVE;
				} else {
					checked[position] -= HAVE;
				}
			}
		});
		holder.want.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox box = (CheckBox) v;
				if (box.isChecked()) {
					checked[position] += WANT;
				} else {
					checked[position] -= WANT;
				}
			}
		});

		if (checked[position] == BOTH) {
			holder.have.setChecked(true);
			holder.want.setChecked(true);
		} else if (checked[position] == WANT) {
			holder.want.setChecked(true);
			holder.have.setChecked(false);
		} else if (checked[position] == HAVE) {
			holder.have.setChecked(true);
			holder.want.setChecked(false);
		} else {
			holder.have.setChecked(false);
			holder.want.setChecked(false);
		}

		return convertView;
	}

	public void saveMultipleEdits() {
		database.open();
		for (int i = 0; i < checked.length; i++) {
			switch (checked[i]) {
			case BOTH:
				database.setFigureHave(set, collections.get(i), 1);
				database.setFigureWant(set, collections.get(i), 1);
				break;
			case WANT:
				database.setFigureWant(set, collections.get(i), 1);
				database.setFigureHave(set, collections.get(i), 0);
				break;
			case HAVE:
				database.setFigureHave(set, collections.get(i), 1);
				database.setFigureWant(set, collections.get(i), 0);
				break;
			default:
				database.setFigureHave(set, collections.get(i), 0);
				database.setFigureWant(set, collections.get(i), 0);
				break;
			}
		}
		database.close();
	}

	private class ViewHolder {
		TextView id;
		TextView title;
		CheckBox have;
		CheckBox want;
	}
}
