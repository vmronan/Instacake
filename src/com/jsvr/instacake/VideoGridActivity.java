package com.jsvr.instacake;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.jsvr.instacake.adapters.ImageAdapter;

public class VideoGridActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_grid);
		
		String imagePath = "/storage/emulated/0/Pictures/Instacake/Me/IMG_503722631812733366_467380373.jpg";
		String[] thumbs = new String[]{ imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath,
										imagePath, imagePath, imagePath};
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this, thumbs));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(VideoGridActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
		
//		ImageView image = (ImageView) findViewById(R.id.imageView);
//		String imagePath = "/storage/emulated/0/Pictures/Instacake/Me/IMG_503722631812733366_467380373.jpg";
//  	image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//		image.setImageURI(Uri.parse(imagePath));
	}



}
