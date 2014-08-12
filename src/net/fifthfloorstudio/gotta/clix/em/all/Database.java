package net.fifthfloorstudio.gotta.clix.em.all;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.fifthfloorstudio.gotta.clix.em.all.lists.CollectionList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private Context context;
	private static final int DATABASE_VERSION = 2;
	private static final String DB_NAME = "ClixEmAll";

	private static final String COLLECTION_TABLE_NAME = "COLLECTION";
	private static final String _id = "_id";
	private static final String COLLECTION_SET = "SET_ID";
	private static final String COLLECTION_NUMBER = "FIGURE";
	private static final String COLLECTION_HAVE = "HAVE";
	private static final String COLLECTION_WANT = "WANT";
	private static final String COLLECTION_TRADE = "TRADE";

	private static final String CREATE_COLLECTION = "CREATE TABLE "
			+ COLLECTION_TABLE_NAME + " (" + _id
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLLECTION_SET
			+ " TEXT, " + COLLECTION_NUMBER + " TEXT, " + COLLECTION_HAVE
			+ " INTEGER, " + COLLECTION_WANT + " INTEGER, " + COLLECTION_TRADE
			+ " INTEGER);";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_COLLECTION);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion == 1 && newVersion == 2) {
				Log.w("UPGRADE", "Upgrading database from version "
						+ oldVersion + " to " + newVersion
						+ ", which will add the column trade");
				db.execSQL("ALTER TABLE " + COLLECTION_TABLE_NAME
						+ " ADD COLUMN " + COLLECTION_TRADE + " INTEGER;");
			}
			// Kills the table and existing data
			// db.execSQL("DROP TABLE IF EXISTS " + COLLECTION_TABLE_NAME);

			// Recreates the database with a new version
			// onCreate(db);
		}
	}

	public Database(Context context) {
		this.context = context;
	}

	public Database open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		if (db.getVersion() != DATABASE_VERSION)
			dbHelper.onUpgrade(db, db.getVersion(), DATABASE_VERSION);
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public boolean isOpen() {
		return db.isOpen();
	}

	public void setHaveAll(String id, Iterator<String> keys) {
		ContentValues values = new ContentValues(3);
		values.put(COLLECTION_SET, id);
		values.put(COLLECTION_HAVE, 1);
		while (keys.hasNext()) {
			values.put(COLLECTION_NUMBER, keys.next().toString());
			if (db.update(COLLECTION_TABLE_NAME, values, COLLECTION_SET
					+ "=? AND " + COLLECTION_NUMBER + "=?", new String[] { id,
					values.getAsString(COLLECTION_NUMBER) }) == 0) {
				db.insert(COLLECTION_TABLE_NAME, null, values);
			}
		}
	}

	public long setFigureHave(String set, String id, int count) {
		ContentValues values = new ContentValues(4);
		values.put(COLLECTION_SET, set);
		values.put(COLLECTION_NUMBER, id);
		values.put(COLLECTION_HAVE, count);

		int result = db.update(COLLECTION_TABLE_NAME, values, COLLECTION_SET
				+ "=? AND " + COLLECTION_NUMBER + "=?",
				new String[] { set, id });
		if (result == 0)
			return db.insert(COLLECTION_TABLE_NAME, null, values);
		return result;
	}

	public long setFigureWant(String id, String value, int count) {
		ContentValues values = new ContentValues(3);
		values.put(COLLECTION_SET, id);
		values.put(COLLECTION_NUMBER, value);
		values.put(COLLECTION_WANT, count);

		int result = db.update(COLLECTION_TABLE_NAME, values, COLLECTION_SET
				+ "=? AND " + COLLECTION_NUMBER + "=?", new String[] { id,
				value.toString() });
		if (result == 0)
			return db.insert(COLLECTION_TABLE_NAME, null, values);
		return result;
	}

	public long setFigureTrade(String id, String value, int count) {
		ContentValues values = new ContentValues(3);
		values.put(COLLECTION_SET, id);
		values.put(COLLECTION_NUMBER, value);
		values.put(COLLECTION_TRADE, count);

		int result = db.update(COLLECTION_TABLE_NAME, values, COLLECTION_SET
				+ "=? AND " + COLLECTION_NUMBER + "=?", new String[] { id,
				value.toString() });
		if (result == 0)
			return db.insert(COLLECTION_TABLE_NAME, null, values);
		return result;
	}

	public void removeFigure(String id, String value) {
		db.delete(COLLECTION_TABLE_NAME, COLLECTION_SET + "=? AND "
				+ COLLECTION_NUMBER + "=?", new String[] { id, value });
	}

	public boolean isNone(String id, String value) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
				COLLECTION_HAVE, COLLECTION_WANT }, COLLECTION_SET + "=? AND "
				+ COLLECTION_NUMBER + "=?", new String[] { id, value }, null,
				null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			cursor.close();
			return true;
		}

		cursor.close();
		return false;
	}

	public int getCheckedItem(String id, String value) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
				COLLECTION_HAVE, COLLECTION_WANT }, COLLECTION_SET + "=? AND "
				+ COLLECTION_NUMBER + "=?", new String[] { id, value }, null,
				null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			cursor.close();
			return -1;
		}

		if (cursor.getInt(0) > 0) {
			cursor.close();
			return 0;
		}

		cursor.close();
		return 1;
	}

	public Cursor getHave(String set) {
		return db.query(COLLECTION_TABLE_NAME,
				new String[] { COLLECTION_NUMBER }, COLLECTION_HAVE
						+ " >? AND " + COLLECTION_SET + " =?", new String[] {
						"0", set }, null, null, null);
	}

	public Cursor getWant(String set) {
		return db.query(COLLECTION_TABLE_NAME,
				new String[] { COLLECTION_NUMBER }, COLLECTION_WANT
						+ " >? AND " + COLLECTION_SET + " =?", new String[] {
						"0", set }, null, null, null);
	}

	public Cursor getHaveWant(String set) {
		return db.query(COLLECTION_TABLE_NAME, new String[] {
				COLLECTION_NUMBER, COLLECTION_HAVE, COLLECTION_WANT }, "("
				+ COLLECTION_HAVE + " >? OR " + COLLECTION_WANT + " >?) AND "
				+ COLLECTION_SET + " =?", new String[] { "0", "0", set }, null,
				null, null);
	}

	public int getHaveCount(String set) {
		return getHave(set).getCount();
	}

	public int getWantCount(String set) {
		return getWant(set).getCount();
	}

	public Cursor getSetCounts(String set) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
				COLLECTION_NUMBER, COLLECTION_HAVE, COLLECTION_WANT,
				COLLECTION_TRADE }, "( " + COLLECTION_HAVE + " >? OR "
				+ COLLECTION_WANT + " >? OR " + COLLECTION_TRADE + " >? "
				+ ") AND " + COLLECTION_SET + " =?", new String[] { "0", "0",
				"0", set }, null, null, null);
		cursor.close();
		return null;
	}

	public int getFigureHaveCount(String set, String id) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME,
				new String[] { COLLECTION_HAVE }, COLLECTION_HAVE + " >? AND ("
						+ COLLECTION_SET + " =? AND " + COLLECTION_NUMBER
						+ " =?)", new String[] { "0", set, id }, null, null,
				null);
		if (cursor.getCount() == 0) {
			cursor.close();
			return 0;
		}

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public int getFigureWantCount(String set, String id) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME,
				new String[] { COLLECTION_WANT }, COLLECTION_WANT + " >? AND ("
						+ COLLECTION_SET + " =? AND " + COLLECTION_NUMBER
						+ " =?)", new String[] { "0", set, id }, null, null,
				null);
		if (cursor.getCount() == 0) {
			cursor.close();
			return 0;
		}

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public int getFigureTradeCount(String set, String id) {
		Cursor cursor = db.query(COLLECTION_TABLE_NAME,
				new String[] { COLLECTION_TRADE }, COLLECTION_TRADE
						+ " >? AND (" + COLLECTION_SET + " =? AND "
						+ COLLECTION_NUMBER + " =?)", new String[] { "0", set,
						id }, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
			return 0;
		}

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public String getStringOfHave(String set) {
		Cursor cursor = null;
		if (set == null) {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_HAVE },
					COLLECTION_HAVE + " >?", new String[] { "0" }, null, null,
					COLLECTION_SET + " ASC");
		} else {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_HAVE },
					COLLECTION_HAVE + " >? AND " + COLLECTION_SET + " =?",
					new String[] { "0", set }, null, null, COLLECTION_SET
							+ " ASC");
		}
		if (cursor.getCount() == 0) {
			cursor.close();
			return "I don't have any clix :(";
		}

		List<Pair> figures = new ArrayList<Pair>();
		cursor.moveToFirst();
		String lastSet = cursor.getString(1);
		String result = "";
		while (!cursor.isAfterLast()) {
			if (cursor.getString(1).equals(lastSet) == false) {
				Collections.sort(figures);
				result += addToResult(lastSet, figures, CollectionList.HAVE);

				figures.clear();
				lastSet = cursor.getString(1);
			}

			figures.add(new Pair(cursor.getString(0), cursor.getInt(2)));
			cursor.moveToNext();
		}
		result += addToResult(lastSet, figures, CollectionList.HAVE);

		cursor.close();
		return result;
	}

	/**
	 * 
	 * @return a string of all the wants
	 */
	public String getStringOfWant(String set) {
		Cursor cursor = null;
		if (set == null) {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_WANT },
					COLLECTION_WANT + " >?", new String[] { "0" }, null, null,
					COLLECTION_SET + " ASC");
		} else {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_WANT },
					COLLECTION_WANT + " >? AND " + COLLECTION_SET + " =?",
					new String[] { "0", set }, null, null, COLLECTION_SET
							+ " ASC");
		}
		if (cursor.getCount() == 0) {
			cursor.close();
			return "I don't want any clix. I have them all!";
		}

		List<Pair> figures = new ArrayList<Pair>();
		cursor.moveToFirst();
		String lastSet = cursor.getString(1);
		String result = "";
		while (!cursor.isAfterLast()) {
			if (cursor.getString(1).equals(lastSet) == false) {
				Collections.sort(figures);
				result += addToResult(lastSet, figures, CollectionList.WANT);

				figures.clear();
				lastSet = cursor.getString(1);
			}

			figures.add(new Pair(cursor.getString(0), cursor.getInt(2)));
			cursor.moveToNext();
		}
		result += addToResult(lastSet, figures, CollectionList.WANT);

		cursor.close();
		return result;
	}

	/**
	 * 
	 * @return a string of all the trade
	 */
	public String getStringOfTrade(String set) {
		Cursor cursor = null;
		if (set == null) {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_TRADE },
					COLLECTION_TRADE + " >?", new String[] { "0" }, null, null,
					COLLECTION_SET + " ASC");
		} else {
			cursor = db.query(COLLECTION_TABLE_NAME, new String[] {
					COLLECTION_NUMBER, COLLECTION_SET, COLLECTION_TRADE },
					COLLECTION_TRADE + " >? AND " + COLLECTION_SET + " =?",
					new String[] { "0", set }, null, null, COLLECTION_SET
							+ " ASC");
		}

		if (cursor.getCount() == 0) {
			cursor.close();
			return "Sorry, nothing for trade!";
		}

		List<Pair> figures = new ArrayList<Pair>();
		cursor.moveToFirst();
		String lastSet = cursor.getString(1);
		String result = "";
		while (!cursor.isAfterLast()) {
			if (cursor.getString(1).equals(lastSet) == false) {
				Collections.sort(figures);
				result += addToResult(lastSet, figures, CollectionList.TRADE);

				figures.clear();
				lastSet = cursor.getString(1);
			}

			figures.add(new Pair(cursor.getString(0), cursor.getInt(2)));
			cursor.moveToNext();
		}
		result += addToResult(lastSet, figures, CollectionList.TRADE);

		cursor.close();
		return result;
	}

	private String addToResult(String lastSet, List<Pair> figures, int type) {
		// TODO: Sort the list of sets correctly
		String result = "";
		if (type == CollectionList.HAVE) {
			result = "I have the following from "
					+ JsonParser.getSetTitle(context, lastSet + ".json") + "\n";
		} else if (type == CollectionList.WANT) {
			result = "I want the following from "
					+ JsonParser.getSetTitle(context, lastSet + ".json") + "\n";
		} else if (type == CollectionList.TRADE) {
			result = "I have the following from "
					+ JsonParser.getSetTitle(context, lastSet + ".json") + "\n";
		}
		JSONObject jsonSet = JsonParser.getJsonSet(context, lastSet);
		Collections.sort(figures);
		for (Pair p : figures) {
			StringBuilder builder = new StringBuilder();
			int count = p.getCount();
			if (count > 1) {
				builder.append(count);
				builder.append(" x ");
			}
			builder.append(p.getId());
			builder.append(": ");
			try {
				builder.append(jsonSet.getJSONObject(p.getId()).getString(
						JsonParser.NAME));
			} catch (JSONException e) {
				builder.append(p.getId());
			}
			builder.append("\n");
			result += builder.toString();
		}
		return result;
	}

	private class Pair implements Comparable<Pair> {

		private String id;
		private int count;

		public Pair(String id, int count) {
			this.id = id;
			this.count = count;
		}

		public String getId() {
			return id;
		}

		public int getCount() {
			return count;
		}

		@Override
		public int compareTo(Pair another) {
			return id.compareTo(another.getId());
		}
	}

	public void removeFigureHave(String set, String number) {
		int value = db.delete(COLLECTION_TABLE_NAME, "(" + COLLECTION_SET
				+ " =? AND " + COLLECTION_NUMBER + " =?) AND "
				+ COLLECTION_HAVE + " =?" + " AND " + COLLECTION_WANT + " =?"
				+ " =?" + " AND " + COLLECTION_TRADE + " =?", new String[] {
				set, number, "0", "0" });

		if (value == 0) {
			setFigureHave(set, number, 0);
		}
	}

	public void removeFigureWant(String set, String number) {
		int value = db.delete(COLLECTION_TABLE_NAME, "(" + COLLECTION_SET
				+ " =? AND " + COLLECTION_NUMBER + " =?) AND "
				+ COLLECTION_HAVE + " =?" + " AND " + COLLECTION_WANT + " =?"
				+ " =?" + " AND " + COLLECTION_TRADE + " =?", new String[] {
				set, number, "0", "0" });

		if (value == 0) {
			setFigureWant(set, number, 0);
		}
	}

	public void removeFigureTrade(String set, String number) {
		int value = db.delete(COLLECTION_TABLE_NAME, "(" + COLLECTION_SET
				+ " =? AND " + COLLECTION_NUMBER + " =?) AND "
				+ COLLECTION_HAVE + " =?" + " AND " + COLLECTION_WANT + " =?"
				+ " AND " + COLLECTION_TRADE + " =?", new String[] { set,
				number, "0", "0" });

		if (value == 0) {
			setFigureTrade(set, number, 0);
		}
	}

	public Cursor getAllFigures() {
		return db.query(COLLECTION_TABLE_NAME,
				null, null, null, null, null, null);
	}
}
