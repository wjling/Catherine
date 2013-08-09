package com.app.ui;

import com.app.catherine.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class FriendList extends Activity{
	
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
	}
	
	
}
