package com.app.ui.menu.FriendCenter;

import com.app.catherine.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FriendNotification extends Activity{

	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_center_notification);
		init();
	}
	
	private void init()
	{
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
	}
}
