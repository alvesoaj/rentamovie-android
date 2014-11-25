package com.zerokol.rentamovie.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zerokol.rentamovie.models.Movie;
import com.zerokol.rentamovie.utils.RentAMovieHelper;
import com.zerokol.rentamovie.utils.RentAMovieSQLiteHelper;

public class MovieDAO {
	public static final String TABLE = "movies";
	public static final String TABLE_ID = "id";
	public static final String TABLE_NAME = "name";
	public static final String TABLE_DESCRIPTION = "description";
	public static final String TABLE_IMAGE = "image";
	public static final String TABLE_QUANTITY = "quantity";
	public static final String TABLE_CREATED_AT = "created_at";
	public static final String TABLE_UPDATED_AT = "updated_at";

	public static final String[] ALL_COLUMNS = { TABLE_ID, TABLE_NAME,
			TABLE_DESCRIPTION, TABLE_IMAGE, TABLE_QUANTITY, TABLE_CREATED_AT,
			TABLE_UPDATED_AT };

	private SQLiteDatabase database;
	private RentAMovieSQLiteHelper dbHelper;

	public MovieDAO(Context context) {
		dbHelper = new RentAMovieSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		// Para ativar o uso de chave estrangeira e seus gatilhos
		// database.execSQL("PRAGMA foreign_keys=ON;");
	}

	public void close() {
		dbHelper.close();
	}

	public long create(Movie movie) {
		return database.insert(TABLE, null, buildArguments(movie));
	}

	public int update(Movie movie) {
		ContentValues values = buildArguments(movie);
		return database.update(TABLE, values,
				String.format("%s = %d", TABLE_ID, movie.getId()), null);
	}

	private ContentValues buildArguments(Movie movie) {
		ContentValues values = new ContentValues();

		values.put(TABLE_ID, movie.getId());
		values.put(TABLE_NAME, movie.getName());
		values.put(TABLE_DESCRIPTION, movie.getDescription());
		values.put(TABLE_IMAGE, movie.getImage());
		values.put(TABLE_QUANTITY, movie.getQuantity());
		values.put(TABLE_CREATED_AT,
				RentAMovieHelper.getFormatedData(movie.getCreatedAt()));
		values.put(TABLE_UPDATED_AT,
				RentAMovieHelper.getFormatedData(movie.getUpdatedAt()));

		return values;
	}

	public void delete(Integer id) {
		database.delete(TABLE, String.format("%s = %d", TABLE_ID, id), null);
	}

	public Movie selectById(Integer id) {
		Cursor cursor = database.query(TABLE, ALL_COLUMNS,
				String.format("%s = %d", TABLE_ID, id), null, null, null, null);

		Movie movie = null;
		if (cursor.moveToFirst()) {
			movie = cursorToList(cursor);
		}
		cursor.close();
		return movie;
	}

	public ArrayList<Movie> selectAll() {
		ArrayList<Movie> movies = new ArrayList<Movie>();

		Cursor cursor = database.query(TABLE, ALL_COLUMNS, null, null, null,
				null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			Movie movie = cursorToList(cursor);
			movies.add(movie);
			cursor.moveToNext();
		}
		cursor.close();

		return movies;
	}

	private Movie cursorToList(Cursor cursor) {
		Movie movie = new Movie();

		movie.setId(cursor.getInt(0));
		movie.setName(cursor.getString(1));
		movie.setDescription(cursor.getString(2));
		movie.setImage(cursor.getString(3));
		movie.setQuantity(cursor.getInt(4));
		movie.setCreatedAt(RentAMovieHelper.convertStringDateToDate(cursor
				.getString(5)));
		movie.setUpdatedAt(RentAMovieHelper.convertStringDateToDate(cursor
				.getString(6)));

		return movie;
	}
}