package com.app.ui;

import com.app.catherine.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class FriendList extends Activity{
	
	private final String TAG = "Friend List";
	private Button searchMyFriendBtn;
	private Button recommendFriendBtn;
	private EditText searchMyFriend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		searchMyFriend = (EditText)findViewById(R.id.friend_list_searchmyfriend);
		searchMyFriendBtn = (Button)findViewById(R.id.friend_list_searchmyfriendBtn);
		recommendFriendBtn = (Button)findViewById(R.id.friend_list_recommendfriendBtn);
		LinearLayout.LayoutParams searchMyFriendBtnParams = (LayoutParams) searchMyFriendBtn.getLayoutParams();
		LinearLayout.LayoutParams searchMyFriendParams = (LayoutParams) searchMyFriend.getLayoutParams();
		Log.i(TAG,"origin: "+searchMyFriendParams.height+", now: "+ searchMyFriendBtnParams.height);
		searchMyFriendParams.height = searchMyFriendBtnParams.height;
		searchMyFriend.setLayoutParams(searchMyFriendParams);
	}
	
	
}
