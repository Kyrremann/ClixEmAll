package net.fifthfloorstudio.gotta.clix.em.all;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class BackupActivity extends Activity {

	private static final String TAG = "backup";
	private static final String FTYPE = ".txt";

	private String[] mFileList;
	private File mPath;
	private String mChosenFile;
	private Dialog findFileDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);
		mPath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS);

		if (!isExternalStorageReadable()
			|| !isExternalStorageWritable()) {
			Toast.makeText(this, R.string.backup_toast_error, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_import:
				loadFileList();
				showFindFileDialog();
				break;
			case R.id.button_export:
				// TODO Add filter for checkboxes
				saveDatabaseToFile();
				break;
		}
	}

	private void showFindFileDialog() {
		if (findFileDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(getString(R.string.backup_dialog_title));
			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mChosenFile = mFileList[which];
					// TODO you can do stuff with the file here too
				}
			});
			findFileDialog = builder.create();
		}
		findFileDialog.show();
	}

	private void loadFileList() {
		try {
			mPath.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card " + e.toString());
		}

		if (mPath.exists()) {
			FilenameFilter filenameFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.contains(FTYPE);
				}
			};

			mFileList = mPath.list(filenameFilter);
		} else {
			mFileList = new String[0];
		}
	}


	private void saveDatabaseToFile() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
		File file = new File(mPath, "ClixEmAll-backup-" + simpleDateFormat.format(new Date()) + ".txt");

		try {
			mPath.mkdirs();
			Database database = new Database(this);
			database.open();
			Cursor cursor = database.getAllFigures();
			cursor.moveToFirst();
			FileWriter fileWriter = new FileWriter(file);
			while (!cursor.isAfterLast()) {
				fileWriter.write(createRowString(cursor));
				cursor.moveToNext();
			}
			fileWriter.close();
			cursor.close();
			database.close();

			MediaScannerConnection.scanFile(this,
					new String[]{file.toString()}, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
							Log.i("ExternalStorage", "Scanned " + path + ":");
							Log.i("ExternalStorage", "-> uri=" + uri);
						}
					});
		} catch (IOException e) {
			Log.w("ExternalStorage", "Error writing " + file, e);
		}

		Toast.makeText(this, "Database exported to " + file.getName(), Toast.LENGTH_SHORT).show();
		finish();
	}

	private String createRowString(Cursor cursor) {
		StringBuilder builder = new StringBuilder(cursor.getString(0));
		builder.append(";");
		builder.append(cursor.getString(1)).append(";");
		builder.append(cursor.getString(2)).append(";");
		builder.append(cursor.getString(3)).append(";");
		builder.append(cursor.getString(4)).append(";");
		builder.append(cursor.getString(5)).append(";");
		builder.append("\n");
		return builder.toString();
	}

	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	private boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
