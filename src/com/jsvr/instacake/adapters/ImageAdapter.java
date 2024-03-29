package com.jsvr.instacake.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mThumbs;

    public ImageAdapter(Context c, String[] thumbs) {
        mContext = c;
        mThumbs = thumbs;
    }
    
    public ImageAdapter setThumbs(String[] thumbs){
    	mThumbs = thumbs;
    	return this;
    }

    public int getCount() {
        return mThumbs.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(175, 175));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0,0,0,0);
        } else {
            imageView = (ImageView) convertView;
        }
    	
    	BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = 5;
		Bitmap previewBitmap = Bitmap.createScaledBitmap(
		                BitmapFactory.decodeFile(mThumbs[position], o), 85, 85, false);
		imageView.setImageBitmap(previewBitmap);
        return imageView;
    }


}
