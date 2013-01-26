package com.example.hantim.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hantim.MainActivity.Job;
import com.example.hantim.R;

public class JobsAdapter extends ArrayAdapter<Job> {

	private LayoutInflater lInflater;
	private ArrayList<Job> objects;
	
	public JobsAdapter(Context context, int textViewResourceId, ArrayList<Job> objects) {
		super(context, textViewResourceId, objects);

		this.lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.job_item, parent, false);
	    }
	    
	    Job n = objects.get(position);
	    
	    TextView tv = (TextView) view.findViewById(R.id.job_title);
	    tv.setText(n.title);
	    
	    tv = (TextView) view.findViewById(R.id.job_published);
	    tv.setText("Опубликовано: " + n.published);
	    
	    view.setTag(n);
	    
	    return view;
	}
}
