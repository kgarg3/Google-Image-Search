package com.codepath.googleimagesearcher.model;

import java.io.Serializable;

/**
 * 
 * @author gargka
 * 
 * Model for filters that are selected by the user and are applied to the search. 
 */
public class ImageFilter implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String colorFilter, imageSize, imageType, siteSearch;

	public String getColorFilter() {
		return colorFilter;
	}

	public void setColorFilter(String colorFilter) {
		this.colorFilter = colorFilter;
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getSiteSearch() {
		return siteSearch;
	}

	public void setSiteSearch(String siteSearch) {
		this.siteSearch = siteSearch;
	}
	
}
