package com.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;

public class FriendCenter {

	private Context context;
	private View friendCenterView;
	private Button addFriendsBtn;
	private Button showFriendListBtn;
	private Button searchFriendBtn;
	
	private int userId = -1;
	private Handler uiHandler;
//	private NotificationCenter notificationCenter;
	
	public FriendCenter(Context context, View friendsCenterView, Handler uiHandler, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friendCenterView = friendsCenterView;
		this.userId = userId;
		this.uiHandler = uiHandler;
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
//		notificationCenter = new NotificationCenter(context, friendCenterView, uiHandler, userId);
		setLayout();
	}
	
	public void setLayout()
	{
		addFriendsBtn = (Button)friendCenterView.findViewById(R.id.menu_friends_center_addfriendsBtn);
		showFriendListBtn = (Button)friendCenterView.findViewById(R.id.menu_friends_center_friendlistBtn);
		searchFriendBtn = (Button)friendCenterView.findViewById(R.id.menu_friends_center_searchfriendBtn);
		addFriendsBtn.setOnClickListener(buttonsOnClickListener);
		showFriendListBtn.setOnClickListener(buttonsOnClickListener);
		searchFriendBtn.setOnClickListener(buttonsOnClickListener);
	}

	
	OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.menu_friends_center_addfriendsBtn:
				Intent intent1 = new Intent();
				intent1.setClass(context, searchFriend.class);
				intent1.putExtra("userId", userId);
				context.startActivity(intent1);
				break;
			case R.id.menu_friends_center_searchfriendBtn:
				break;
			case R.id.menu_friends_center_friendlistBtn:
				Intent intent2 = new Intent();
				intent2.putExtra("userId", userId);
				intent2.setClass(context, FriendList.class);
				context.startActivity(intent2);
				break;
				default: break;
			}
		}
	};
	
	
}
