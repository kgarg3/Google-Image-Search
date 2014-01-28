package com.codepath.googleimagesearcher.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 
 * @author gargka
 *
 * Model for image objects.
 */
public class Image {
	private static final String URL = "url";
	private static final String THUMBNAIL_URL = "tbUrl";
	private static final String TITLE = "title";
		
	private String thumbUrl;
	private String url;
	private String title;
	
	public Image(JSONObject object) {
		try {
			this.url = object.getString(URL);
			this.thumbUrl = object.getString(THUMBNAIL_URL);
			this.title = object.getString(TITLE);
		} catch (JSONException e) {
			Log.e("Error", e.getMessage());
		}	
	}
		
	public String getThumbUrl() {
		return thumbUrl;
	}
	
	public String getFullUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
}
