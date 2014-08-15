package net.fifthfloorstudio.gotta.clix.em.all.honeycomb;

import android.os.Bundle;
import net.fifthfloorstudio.gotta.clix.em.all.AddClix;

public class AddClixHoneyComb extends AddClix {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
}
