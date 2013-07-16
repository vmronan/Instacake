package com.jsvr.instacake.adapters;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jsvr.instacake.R;

public class ThumbnailArrayAdapter extends ArrayAdapter<String> {
	
	Context context;
	int layoutResourceId;
	String data[] = null;
	ViewHolder holder;
	
	public ThumbnailArrayAdapter(Context context, int layoutResourceId, String[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		holder = null;
		
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
		
		
//		try {
//			holder.thumbnailView.setImageBitmap(BitmapFactory.decodeStream((InputStream)new URL(data[position]).getContent()));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		new ImageLoader().execute(v, data[position]);		// data[position] is a URI string
		
		return v;
	}
	
	static class ViewHolder {
		ImageView thumbnailView;
	}
	
	public class ImageLoader extends AsyncTask<Object, String, Void> {

		private View view;
		Bitmap bitmap;
		
		@Override
		protected Void doInBackground(Object... parameters) {
			view = (View) parameters[0];
			String uri = (String) parameters[1];
			
			try {
				bitmap = BitmapFactory.decodeStream(new URL(uri).openConnection() .getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(bitmap != null && view != null) {
				holder.thumbnailView.setImageBitmap(bitmap);
			}
		}
	}
}
