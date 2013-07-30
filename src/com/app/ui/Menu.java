package com.app.ui;

import com.app.catherine.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Menu {
	private Context context;
	private LinearLayout ui_content;
	private Button myEvents;
	private Button privateEvents;
	private View view;	//基础ui布局界面
	private View currentUI;
	private Handler handler;
	
	public Menu(Context context, View v, Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		view = v;
		this.handler = handler;
	}
	
	public void setMenu()
	{
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		ui_content = (LinearLayout)view.findViewById(R.id.ui_content_thecontent);
		myEvents = (Button)view.findViewById(R.id.ui_menu_myevents);
		privateEvents = (Button)view.findViewById(R.id.ui_menu_privateevents);
		currentUI = LayoutInflater.from(context).inflate(R.layout.my_events, null);
		
		myEvents.setOnClickListener(menuListener);
		privateEvents.setOnClickListener(menuListener);
		
		ui_content.addView(currentUI);
	}
	
	OnClickListener menuListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.ui_menu_myevents:
				currentUI = LayoutInflater.from(context).inflate(R.layout.my_events, null);
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
				Toast.makeText(context, "My Events", Toast.LENGTH_SHORT).show();
				break;
			case R.id.ui_menu_privateevents:
				currentUI = LayoutInflater.from(context).inflate(R.layout.private_events, null);
				ui_content.removeAllViews();
				ui_content.addView(currentUI);
				Toast.makeText(context, "Private Events", Toast.LENGTH_SHORT).show();
				break;
				default: break;
			}
			Message msg = new Message(); 
			msg.what = -2;
			handler.sendMessage(msg);
			
		}
	};
	
	
	
}
