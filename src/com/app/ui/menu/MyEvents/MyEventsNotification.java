package com.app.ui.menu.MyEvents;

import com.app.catherine.R;
import com.app.ui.UserInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MyEventsNotification extends Activity{
	
	private int userId;
	private View contentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.my_events_notification, null);
		setContentView(contentView);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		UserInterface.notificationCenter.setContext(this);
		UserInterface.notificationCenter.setNotificationView(contentView);
		UserInterface.notificationCenter.getNotifications();
	}

}
