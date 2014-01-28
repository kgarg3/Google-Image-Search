package com.codepath.googleimagesearcher.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.codepath.googleimagesearcher.EndlessScrollListener;
import com.codepath.googleimagesearcher.ImageAdapter;
import com.codepath.googleimagesearcher.model.Image;
import com.codepath.googleimagesearcher.model.ImageFilter;
import com.example.codepath.webimagesearcher.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * 
 * @author gargka
 *
 * Main activity class that shows the search results.
 */
public class MainActivity extends Activity {

	//strings needed for the intent to show image details.
	public static final String IMAGE_FILTERS = "ImageFilters";
	public static final String IMAGE_FULL_URL = "fullUrl";
	public static final String IMAGE_TITLE = "Imagetitle";

	private final int REQUEST_CODE = 10;

	private GridView gvImageResults;
	private TextView tvFiltersApplied;

	private ArrayList<Image> listOfImageResults = new ArrayList<Image>();
	private ImageAdapter imageAdapter;

	//used in api call
	private int start = 0;
	private String queryString;

	private ImageFilter filter = new ImageFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvFiltersApplied = (TextView) findViewById(R.id.tvFilters);
		tvFiltersApplied.setVisibility(TextView.INVISIBLE);
		
		setupGridView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		setupSearchView(menu);
		return true;
	}

	/**
	 * Set up the grid view and listeners
	 */
	private void setupGridView() {
		gvImageResults = (GridView) findViewById(R.id.gvImages);
		imageAdapter = new ImageAdapter(this, listOfImageResults);
		gvImageResults.setAdapter(imageAdapter);

		//scrolling should load more images 
		gvImageResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				customLoadMoreDataFromApi(page); 
			}
		});

		//clicking on an individual item should show the full image in a separate acitivity.
		gvImageResults.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Image image = listOfImageResults.get(position);

				Intent intent = new Intent(getApplicationContext(), ImageDetailsActivity.class);
				intent.putExtra(IMAGE_FULL_URL, image.getFullUrl());
				intent.putExtra(IMAGE_TITLE, image.getTitle());

				startActivity(intent);
			}
		});
	}

	/**
	 *  Append more data into the adapter
	 *  This method probably sends out a network request and appends new data items to your adapter. 
	 *  Use the offset value and add it as a parameter to your API request to retrieve paginated data.
	 *  Deserialize API response and then construct new objects to append to the adapter
	 * @param offset
	 */
	public void customLoadMoreDataFromApi(int offset) {	
		start = offset;
		getDataFromApi();
	}


	/**
	 * This sets up the search widget in the actionbar
	 * @param menu
	 */
	public void setupSearchView(Menu menu) {
		SearchView svQuery = (SearchView) menu.findItem(R.id.action_search).getActionView();
		svQuery.setQueryHint(getResources().getString(R.string.lblSearch));
		svQuery.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				//initialize the url parameters here as this is a new search
				start = 0;
				queryString = query;
				imageAdapter.clear();
				
				getDataFromApi();
				showFiltersApplied();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	/**
	 * Launch the settings activity when the settings icon is clicked
	 * @param mi
	 */
	public void onSettingsAction(MenuItem mi) {		
		Intent i = new Intent(this, SettingsActivity.class);
		i.putExtra(IMAGE_FILTERS, filter);
		startActivityForResult(i, REQUEST_CODE);
	} 

	/**
	 * Store the settings back in the filter object which will be used to filter the next search.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//extract the custom tip from the intent and set it tipPercent
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {			
			filter = (ImageFilter) data.getSerializableExtra(IMAGE_FILTERS);	
		}
	} 

	/**
	 * Performs the network request to fetch the data from the api and adds the result to the adapter.
	 */
	private void getDataFromApi() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(this.new GoogleSearchApi().constructApiUrl(), new JsonHttpResponseHandler() {
			//parse the results from the response
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageResultsArr  = null;
				try {
					imageResultsArr = response.getJSONObject(GoogleSearchApi.RESPONSE_DATA).getJSONArray(GoogleSearchApi.RESULTS);							
					if(imageResultsArr != null && imageResultsArr.length() == 0){
						alertUser(getString(R.string.noResults));
						return;
					}
					
					for(int i=0; i<imageResultsArr.length()-1; i++) {
						imageAdapter.add(new Image(imageResultsArr.getJSONObject(i)));
					}
					
				} catch (JSONException e) {
					Log.e("Error", e.getMessage());
				}
			}

			//alert the user something bad has happened
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				alertUser(getString(R.string.jsonError));
				//also log
				Log.e("Error", e.getMessage());
			}
		});
	}

	private void showFiltersApplied() {
		boolean isFilterApplied = false;

		String color = filter.getColorFilter();
		String imageSize = filter.getImageSize();
		String imageType = filter.getImageType();
		String siteSearch = filter.getSiteSearch();

		isFilterApplied |= (color != null) | (imageSize != null) | (imageType != null) |
				(siteSearch != null && !siteSearch.equals("") );

		StringBuilder filter = new StringBuilder(getString(R.string.lblFilters) + " ");
		if(isFilterApplied) {			
			filter.append(color != null ? color : "")
			.append(imageSize != null ? " | " + imageSize : "")
			.append(imageType != null ? " | " + imageType : "")
			.append(siteSearch != null && !siteSearch.equals("") ? " | " + siteSearch : "");
		}
		else
			filter.append(getString(R.string.lblFiltersNone));
		
		tvFiltersApplied.setText(filter.toString());
		tvFiltersApplied.setVisibility(TextView.VISIBLE);
	}
	
	private void alertUser(String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
		alertDialogBuilder.setMessage(message).setCancelable(true);
		alertDialogBuilder.setPositiveButton(getString(R.string.dlgOK),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}


	/**
	 * 
	 * Class that handles construction of google api url.
	 *
	 */
	private class GoogleSearchApi {
		//google search api
		private static final String GOOGLE_IMAGE_API_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&q=";

		//response strings
		private static final String RESPONSE_DATA = "responseData";
		private static final String RESULTS = "results";

		//url params
		private static final String URL_PARAM_START = "&start=";
		private static final String URL_PARAM_IMG_COLOR = "&imgcolor=";
		private static final String URL_PARAM_IMG_SIZE = "&imgsz=";
		private static final String URL_PARAM_IMG_TYPE = "&imgtype=";
		private static final String URL_PARAM_SITE_SERACH = "&as_sitesearch=";

		/**
		 * Constructs the api call with the url parameters. 
		 * @return constructed url
		 */
		private String constructApiUrl() {
			StringBuffer url = new StringBuffer(GOOGLE_IMAGE_API_URL);
			url.append(Uri.encode(queryString))
			.append(URL_PARAM_START + Integer.toString(start));

			if(filter.getColorFilter() != null)
				url.append(URL_PARAM_IMG_COLOR + filter.getColorFilter());

			if(filter.getImageSize() != null)
				url.append(URL_PARAM_IMG_SIZE + filter.getImageSize());

			if(filter.getImageType() != null)
				url.append(URL_PARAM_IMG_TYPE + filter.getImageType());

			String siteSearch = filter.getSiteSearch();
			if(siteSearch != null && !siteSearch.equals(""))
				url.append(URL_PARAM_SITE_SERACH + siteSearch);

			return url.toString();
		}
	}

}
