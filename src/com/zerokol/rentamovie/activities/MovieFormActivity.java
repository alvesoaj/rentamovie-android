package com.zerokol.rentamovie.activities;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zerokol.rentamovie.R;
import com.zerokol.rentamovie.dao.MovieDAO;
import com.zerokol.rentamovie.models.Movie;
import com.zerokol.rentamovie.utils.RentAMovieHelper;

public class MovieFormActivity extends ActionBarActivity {
	private static final int SELECT_PICTURE = 1777;
	private static final int CAMERA_REQUEST = 1888;

	private MovieDAO movieDataAccessObject;
	private Movie movie;

	private TextView name;
	private TextView description;
	private TextView quantity;
	private ImageView image;
	private Button send;

	private Uri imageUri;

	private Bitmap imageBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_form);

		movieDataAccessObject = new MovieDAO(this);

		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateMovieAsyncTask createMovieTask = new CreateMovieAsyncTask(
						MovieFormActivity.this);
				createMovieTask.execute();
			}
		});
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

	private class CreateMovieAsyncTask extends
			AsyncTask<Context, Integer, JSONObject> {
		private Context context;
		private ProgressDialog progressDialog;

		public CreateMovieAsyncTask(Context context) {
			this.context = context;
			this.progressDialog = new ProgressDialog(context);
		}

		protected void onPreExecute() {
			progressDialog.setTitle(getResources().getString(
					R.string.request_title));
			progressDialog.setMessage(getResources().getString(
					R.string.request_message));

			progressDialog.show();
		}

		@Override
		protected void onCancelled() {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			super.onCancelled();
		}

		@Override
		protected JSONObject doInBackground(Context... params) {
			String url = RentAMovieHelper.DOMAIN + "/movies.json";

			JSONObject json = new JSONObject();

			try {
				MultipartEntityBuilder meb = MultipartEntityBuilder.create();

				meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				if (imageBitmap != null) {
					meb.addBinaryBody("movie[image]", RentAMovieHelper
							.convertBitmapToByteArray(imageBitmap),
							ContentType.DEFAULT_BINARY, "picture.jpg");
				}
				meb.addTextBody("movie[name]", name.getText().toString());
				meb.addTextBody("movie[description]", description.getText()
						.toString());
				meb.addTextBody("movie[quantity]", quantity.getText()
						.toString());

				String response = RentAMovieHelper.makePOSTRequest(url, meb);
				if (response != null) {
					json = new JSONObject(response);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			try {
				if (json != null) {
					movie = new Movie();

					movie.setId(json.getInt("id"));
					movie.setName(json.getString("name"));
					movie.setDescription(json.getString("description"));
					movie.setQuantity(json.getInt("quantity"));
					movie.setCreatedAt(RentAMovieHelper
							.convertStringDateToDate(json
									.getString("created_at")));
					movie.setUpdatedAt(RentAMovieHelper
							.convertStringDateToDate(json
									.getString("updated_at")));

					movieDataAccessObject.open();
					movieDataAccessObject.create(movie);
					movieDataAccessObject.close();

					Intent intent = new Intent();
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context,
							"Um erro aconteeu ao salvar o filme!",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
}