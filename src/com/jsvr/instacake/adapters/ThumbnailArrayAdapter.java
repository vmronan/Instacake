package com.jsvr.instacake.adapters;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jsvr.instacake.Constants;
import com.jsvr.instacake.R;

public class ThumbnailArrayAdapter extends ArrayAdapter<String> {
	
	Context context;
	int layoutResourceId;
	String data[] = null;			// data[] is array of URI strings
	ArrayList<ViewHolder> holders;
	
	public ThumbnailArrayAdapter(Context context, int layoutResourceId, String[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		Log.v("adapter contructor", "layoutResourceId is " + layoutResourceId);
		this.context = context;
		this.data = data;
		holders = new ArrayList<ViewHolder>();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		
		if(v == null) {
			System.out.println("view is null");
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			v = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new ViewHolder();
			holders.add(holder);
			holder.thumbnailView = (ImageView)v.findViewById(R.id.thumbnail);
			v.setTag(holder);
		}
		else {
			System.out.println("view is not null");
			holders.set(position, (ViewHolder)v.getTag());  
		}
		
		if(data[position].contains("http")) {
			// URL is online
			new ImageLoader().execute(v, position);
		}
		else {
			// URL is local and in Pictures/Instagram directory
			Uri uri = Uri.parse(Constants.DIR_MY_THUMBS.getPath() + File.separator + data[position]);
			holder.thumbnailView.setImageURI(uri);
		}
		
		return v;
	}
	
	class ViewHolder {
		ImageView thumbnailView;
	}
	
	public class ImageLoader extends AsyncTask<Object, String, Integer> {

		private View view;
		Bitmap bitmap;
		
		@Override
		protected Integer doInBackground(Object... parameters) {
			view = (View) parameters[0];
			int position = (Integer) parameters[1];
			System.out.println(view.getTag().toString());
			String uri = data[position];
			
			try {
				bitmap = BitmapFactory.decodeStream(new URL(uri).openConnection().getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return position;
		}
		
		@Override
		protected void onPostExecute(Integer position) {
			if(bitmap != null && view != null) {
				holders.get(position).thumbnailView.setImageBitmap(bitmap);
			}
		}
	}
}
