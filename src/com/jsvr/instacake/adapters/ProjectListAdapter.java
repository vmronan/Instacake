package com.jsvr.instacake.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jsvr.instacake.R;
import com.jsvr.instacake.data.Project;

public class ProjectListAdapter extends ArrayAdapter<Project> {
	
	Context context;
	int layoutResourceId;
	Project projects[] = null;
	
	public ProjectListAdapter(Context context, int layoutResourceId, Project[] projects) {
		super(context, layoutResourceId, projects);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.projects = projects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder = null;
		
		if(v == null) {
			System.out.println("view is null");
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			v = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new ViewHolder();
			holder.titleView = (TextView) v.findViewById(R.id.title);
			holder.usersView = (TextView) v.findViewById(R.id.users);
			v.setTag(holder);
		}
		else {
			System.out.println("view is not null");
			holder = (ViewHolder)v.getTag();
		}
		
		Project project = projects[position];
		holder.titleView.setText(project.getTitle());
		
		ArrayList<String> users = project.getUsers();
		int numUsers = users.size();
		String usersStr = "";
		if(users.size() > 0) {
			usersStr = users.get(0);
			for(int i = 1; i < numUsers; i++) {
				usersStr += ", " + users.get(i);
			}
		}
		holder.usersView.setText(usersStr);
		
		return v;
	}
	
	class ViewHolder {
		TextView titleView;
		TextView usersView;
	}
}
