package com.app.ui;

import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FriendsCenter {

	private Context context;
	private View friendsCenterView;
	private Button addFriendsBtn;
	
	private int userId = -1;
	
	public FriendsCenter(Context context, View friendsCenterView, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friendsCenterView = friendsCenterView;
		this.userId = userId;
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
		setLayout();
	}
	
	public void setLayout()
	{
		addFriendsBtn = (Button)friendsCenterView.findViewById(R.id.menu_friends_center_addfriendsBtn);
		addFriendsBtn.setOnClickListener(buttonsOnClickListener);
	}

	
	OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.menu_friends_center_addfriendsBtn:
				Intent intent = new Intent();
				intent.setClass(context, searchFriend.class);
				intent.putExtra("userId", userId);
				context.startActivity(intent);
				break;
				
				default: break;
			}
		}
	};
	
}
