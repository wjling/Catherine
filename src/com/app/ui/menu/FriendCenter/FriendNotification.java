package com.app.ui.menu.FriendCenter;

import com.app.catherine.R;
import com.app.ui.UserInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class FriendNotification extends Activity{

	private int userId;
	private View contentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		contentView = LayoutInflater.from(this).inflate(R.layout.friend_center_notification, null);
		contentView.setId(R.layout.friend_center_notification);
		setContentView(contentView);
		init();
	}
	
	private void init()
	{
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		UserInterface.notificationCenter.setNotificationView(contentView);
		UserInterface.notificationCenter.setContext(this);
		UserInterface.notificationCenter.getNotifications();
	}
}
