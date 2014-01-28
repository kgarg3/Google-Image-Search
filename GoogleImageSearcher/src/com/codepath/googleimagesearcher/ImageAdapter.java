package com.codepath.googleimagesearcher;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.googleimagesearcher.model.Image;
import com.example.codepath.webimagesearcher.R;
import com.loopj.android.image.SmartImageView;

/**
 * 
 * @author gargka
 * 
 * Adapter that takes an image from an array and converts it to the smart image view. 
 */
public class ImageAdapter extends ArrayAdapter<Image> {
	private Context mContext;

	public ImageAdapter(Context context, List<Image> objects) {
		super(context, R.layout.image_view, objects);
		this.mContext = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SmartImageView imageView;		
		if (convertView == null) { 
			LayoutInflater inflater = LayoutInflater.from(mContext);
			imageView = (SmartImageView) inflater.inflate(R.layout.image_view, parent, false);
		} else {
			imageView = (SmartImageView) convertView;
			imageView.setImageResource(android.R.color.transparent);
		}
	   
		imageView.setImageUrl(((Image)this.getItem(position)).getThumbUrl());
		return imageView;
	}

}
