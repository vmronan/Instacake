package com.jsvr.instacake.adapters;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jsvr.instacake.R;
import com.jsvr.instacake.data.Constants;

public class ThumbnailGridArrayAdapter extends ArrayAdapter<String> {
	
	Context context;
	int layoutResourceId;
	String data[] = null;			// data[] is array of URI strings
	
	public ThumbnailGridArrayAdapter(Context context, int layoutResourceId, String[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		
		if(v == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			v = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new ViewHolder();
			holder.thumbnailView = (ImageView)v.findViewById(R.id.thumbnail);
			v.setTag(holder);
		}
		else {
			holder = (ViewHolder)v.getTag();  
		}
		
		Uri uri = Uri.parse("file://" + Constants.getMyThumbsDir().getPath() + File.separator + data[position]);
		String path = Constants.getMyThumbsDir().getPath() + File.separator + data[position];
		Log.v("getView", "path: " + uri);

		try {
			holder.thumbnailView.setImageBitmap(meh(path, 100, 100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return v;
	}
	
	public static Bitmap meh(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	class ViewHolder {
		ImageView thumbnailView;
	}
}
