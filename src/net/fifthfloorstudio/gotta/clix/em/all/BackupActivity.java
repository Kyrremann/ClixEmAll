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
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.*;
import java.text.SimpleDateFormat;
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
					importFile(mChosenFile);
				}
			});
			findFileDialog = builder.create();
		}
		findFileDialog.show();
	}

	private void importFile(String filename) {
		Database database = new Database(this);
		database.open();
		try {
			FileReader fileReader = new FileReader(mPath + "/" + filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.equals(getString(R.string.backup_header))) {
					continue;
				}
				writeLineToDatabase(line, database);
			}
			bufferedReader.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(this, String.format(getString(R.string.cant_read_file), filename), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, String.format(getString(R.string.problem_reading_file), filename), Toast.LENGTH_SHORT).show();
		}
		database.close();
		Toast.makeText(this, R.string.reading_file_complete, Toast.LENGTH_SHORT).show();
	}

	private void writeLineToDatabase(String line, Database database) {
		String[] array = line.split(";");
		database.setFigure(array[0], array[1], array[2], array[3], array[4]);
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
		File file = new File(mPath, "ClixEmAll-" + simpleDateFormat.format(new Date()) + ".txt");

		try {
			mPath.mkdirs();
			Database database = new Database(this);
			database.open();
			FileWriter fileWriter = new FileWriter(file, false);
			fileWriter.write(getString(R.string.backup_header));

			CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox_have);
			if (checkBox.isChecked()) {
				writeCursorToFile(database.getAllHave(), fileWriter);
			}

			checkBox = (CheckBox) findViewById(R.id.checkBox_want);
			if (checkBox.isChecked()) {
				writeCursorToFile(database.getAllWant(), fileWriter);
			}

			checkBox = (CheckBox) findViewById(R.id.checkBox_trade);
			if (checkBox.isChecked()) {
				writeCursorToFile(database.getAllTrade(), fileWriter);
			}

			fileWriter.close();
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

		Toast.makeText(this, String.format(getString(R.string.database_exported_to), file.getName()), Toast.LENGTH_SHORT).show();
		finish();
	}

	private void writeCursorToFile(Cursor cursor, FileWriter fileWriter) throws IOException {
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			fileWriter.write(createRowString(cursor));
			cursor.moveToNext();
		}
		cursor.close();
	}

	private String createRowString(Cursor cursor) {
		StringBuilder builder = new StringBuilder(cursor.getString(0));
		builder.append(";");
		builder.append(cursor.getString(1)).append(";");
		builder.append(cursor.getString(2)).append(";");
		builder.append(cursor.getString(3)).append(";");
		builder.append(cursor.getString(4)).append(";");
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
