package com.zerokol.rentamovie.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zerokol.rentamovie.R;
import com.zerokol.rentamovie.models.Movie;

public class MovieAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<Movie> movies;
	private ViewHolder holder;

	public MovieAdapter(Context context, List<Movie> movies) {
		this.layoutInflater = LayoutInflater.from(context);
		this.movies = movies;
	}

	@Override
	public int getCount() {
		return movies.size();
	}

	@Override
	public Movie getItem(int position) {
		return movies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return movies.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Movie movie = movies.get(position);

		holder = new ViewHolder();

		convertView = layoutInflater.inflate(R.layout.adapter_movie, null);

		return convertView;
	}

	static class ViewHolder {
	}
}
