package net.fifthfloorstudio.gotta.clix.em.all.honeycomb;

import android.os.Bundle;
import net.fifthfloorstudio.gotta.clix.em.all.BackupActivity;
import net.fifthfloorstudio.gotta.clix.em.all.R;

public class BackupActivityHoneyComb extends BackupActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
}
