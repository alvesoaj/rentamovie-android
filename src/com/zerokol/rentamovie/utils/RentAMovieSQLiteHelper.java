package com.zerokol.rentamovie.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RentAMovieSQLiteHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "rentamovie.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_MOVIES_TABLE = "CREATE TABLE [movies] ("
			+ " [id] INTEGER(10) UNIQUE," + " [name] VARCHAR(255) NOT NULL,"
			+ " [description] VARCHAR(255) NOT NULL,"
			+ " [image] VARCHAR(255) NOT NULL,"
			+ " [quantity] INTEGER(10) NOT NULL,"
			+ " [created_at] DATETIME NOT NULL,"
			+ " [updated_at] DATETIME NOT NULL);";

	public RentAMovieSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// v1
		database.execSQL(CREATE_MOVIES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(RentAMovieSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ".");

		for (int i = oldVersion; i < newVersion; i++) {
			switch (i) {
			case 1:
				break;
			}
		}
	}
}
