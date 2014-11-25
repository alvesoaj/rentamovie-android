package com.zerokol.rentamovie.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.zerokol.rentamovie.R;
import com.zerokol.rentamovie.dao.MovieDAO;
import com.zerokol.rentamovie.models.Movie;

public class MovieFormActivity extends ActionBarActivity {
	private MovieDAO movieDataAccessObject;
	private Movie movie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_form);

		movieDataAccessObject = new MovieDAO(this);
	}
}