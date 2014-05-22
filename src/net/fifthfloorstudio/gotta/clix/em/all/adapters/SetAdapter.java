package net.fifthfloorstudio.gotta.clix.em.all.adapters;

import java.util.List;

import net.fifthfloorstudio.gotta.clix.em.all.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("hiding")
public class SetAdapter<String> extends ArrayAdapter<String> {
	
	private Context context;
	private int resource;

	public SetAdapter(Context context, int resource, int textViewResourceId,
			List<String> sets) {
		super(context, resource, textViewResourceId, sets);
		this.context = context;
		this.resource = resource;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, resource, null);
			holder.title = (TextView) convertView.findViewById(R.id.set_title);
			holder.count = (TextView) convertView.findViewById(R.id.set_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.title.setText((CharSequence) getItem(position));
		holder.count.setVisibility(View.GONE);
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView title;
		TextView count;
	}
}
