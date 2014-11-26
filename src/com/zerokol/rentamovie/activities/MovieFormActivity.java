package com.zerokol.rentamovie.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zerokol.rentamovie.R;
import com.zerokol.rentamovie.dao.MovieDAO;
import com.zerokol.rentamovie.models.Movie;

public class MovieFormActivity extends ActionBarActivity {
	private static final int SELECT_PICTURE = 1777;
	private static final int CAMERA_REQUEST = 1888;

	private MovieDAO movieDataAccessObject;
	private Movie movie;

	private ImageView image;

	private Uri imageUri;

	private Bitmap imageBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_form);

		movieDataAccessObject = new MovieDAO(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(getResources().getText(
				R.string.fragment_image_select_image));

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.context_image_select, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.context_image_select_from_archive) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(
							intent,
							getResources().getText(
									R.string.action_select_image_from_archive)),
					SELECT_PICTURE);
		} else if (item.getItemId() == R.id.context_image_select_from_camera) {
			ContentValues values = new ContentValues();
			imageUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			startActivityForResult(cameraIntent, CAMERA_REQUEST);
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_REQUEST) {
				loadImageFromArchive(imageUri);
			} else if (requestCode == SELECT_PICTURE) {
				imageUri = data.getData();

				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(imageUri,
						filePathColumn, null, null, null);

				cursor.moveToFirst();

				String imagePath = cursor.getString(cursor
						.getColumnIndex(filePathColumn[0]));

				cursor.close();

				BitmapFactory.decodeFile(imagePath);

				imageBitmap = BitmapFactory.decodeFile(imagePath);

				image.setImageBitmap(imageBitmap);
			}
		}
	}

	private void loadImageFromArchive(Uri imageUri) {
		try {
			imageBitmap = MediaStore.Images.Media.getBitmap(
					getContentResolver(), imageUri);

			image.setImageBitmap(imageBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}