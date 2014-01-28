package com.codepath.googleimagesearcher.activity;

import com.codepath.googleimagesearcher.model.ImageFilter;
import com.example.codepath.webimagesearcher.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * 
 * @author gargka
 * 
 * Activity to allow users to apply filters.
 */
@SuppressLint("DefaultLocale")
public class SettingsActivity extends Activity {
	private static final String ALL = "All";
	private Spinner spColorFilter, spImageSize, spImageType;
	private EditText etSiteSearch;

	private ImageFilter filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		//get the filter from the intent
		filter = (ImageFilter) getIntent().getSerializableExtra(MainActivity.IMAGE_FILTERS);

		initViews();
		initSpinners();
		initializeViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}


	/**
	 * Save onclick handler
	 */
	public void save(View v) {		
		// Create an intent to pass the data back to the main activity 
		Intent data = new Intent();	
		filter.setSiteSearch(etSiteSearch.getText().toString());
		data.putExtra(MainActivity.IMAGE_FILTERS, filter);
		setResult(RESULT_OK, data); 
		finish(); 
	}

	/**
	 * Init the views
	 */
	private void initViews() {
		spColorFilter = (Spinner) findViewById(R.id.spColorFiler);
		spImageSize = (Spinner) findViewById(R.id.spImageSize);
		spImageType = (Spinner) findViewById(R.id.spImageType);
		etSiteSearch = (EditText) findViewById(R.id.etSite);
	}

	private void initSpinners() {
		initSpinner(spColorFilter, R.array.color_filter);
		initSpinner(spImageSize, R.array.image_size);
		initSpinner(spImageType, R.array.image_type);
	}

	/**
	 * Init the spinners with the entries from the resource file and add event handler.
	 * @param spinner spinner to init
	 * @param textArrayResId the resource file 
	 */
	private void initSpinner(final Spinner spinner, int textArrayResId) {
		// Create an ArrayAdapter using the string array and a default spinner layout
		// Specify the layout to use when the list of choices appears
		// Apply the adapter to the spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, textArrayResId, android.R.layout.simple_spinner_item);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener( new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// An item was selected. You can retrieve the selected item using
				String value =  (String) parent.getItemAtPosition(position);
				value = value.toLowerCase();
				//we dont care about 'all' as it wont be set as the url param
				if(!value.equalsIgnoreCase(ALL)) {
					switch(spinner.getId()) {
					case R.id.spColorFiler : 
						filter.setColorFilter(value);
						break;
					case R.id.spImageSize : 
						filter.setImageSize(value);
						break;
					case R.id.spImageType :
						filter.setImageType(value);
						break;
					}
				}							
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});

	}
	
	/**
	 * Set the selections of the views to the ones the user had selected
	 */
	@SuppressWarnings("unchecked")
	private void initializeViews() {
		if(filter.getColorFilter() != null)
			spColorFilter.setSelection(((ArrayAdapter<CharSequence>) spColorFilter.getAdapter())
					.getPosition(getCamelCase(filter.getColorFilter())));
		
		if(filter.getImageSize() != null)
			spImageSize.setSelection(((ArrayAdapter<CharSequence>) spImageSize.getAdapter())
					.getPosition(getCamelCase(filter.getImageSize())));
		
		if(filter.getImageType() != null)
			spImageType.setSelection(((ArrayAdapter<CharSequence>) spImageType.getAdapter())
					.getPosition(getCamelCase(filter.getImageType())));
			
		if(filter.getSiteSearch() != null)
			etSiteSearch.setText(filter.getSiteSearch());			
	}

	private String getCamelCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
