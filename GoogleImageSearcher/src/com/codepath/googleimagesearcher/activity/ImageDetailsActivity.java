package com.codepath.googleimagesearcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

import com.example.codepath.webimagesearcher.R;
import com.loopj.android.image.SmartImageView;

/**
 * 
 * @author gargka
 *
 * Activity that shows the image details. 
 */
public class ImageDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_details);

		//get the intent and the extras
		Intent i = getIntent();
		String url = i.getStringExtra(MainActivity.IMAGE_FULL_URL);
		String title = i.getStringExtra(MainActivity.IMAGE_TITLE);

		SmartImageView imgView = (SmartImageView) findViewById(R.id.ivFullImg);
		TextView etTitle = (TextView) findViewById(R.id.tvImageTitle);

		//set the extras in the views
		imgView.setImageUrl(url);    
		etTitle.setText(Html.fromHtml(title));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
