package com.app.ui.menu.FriendCenter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.TableFriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class FriendList extends Activity{
	
	private final String TAG = "Friend List";
	private Button searchMyFriendBtn;
	private Button recommendFriendBtn;
	private EditText searchMyFriend;
	private ListView friendListView;
	
	private int userId;
	
	ArrayList<FriendStruct> friends;
	ArrayList<HashMap<String, Object>> friendList = new ArrayList<HashMap<String,Object>>();
	AdapterForFriendList friendListAdapter;
	Comparator<Object> chinese_Comparator = Collator.getInstance(Locale.CHINA);
	Comparator<HashMap> myComparator = new Comparator<HashMap>() {
		
		@Override
		public int compare(HashMap arg0, HashMap arg1) {
			// TODO Auto-generated method stub
			String name0 = arg0.get("fname").toString();
			String name1 = arg1.get("fname").toString();
			return chinese_Comparator.compare(name0, name1);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		init();
		showFriendList();
	}

	private void init() 
	{
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		
		searchMyFriend = (EditText)findViewById(R.id.friend_list_searchmyfriend);
		searchMyFriendBtn = (Button)findViewById(R.id.friend_list_searchmyfriendBtn);
		recommendFriendBtn = (Button)findViewById(R.id.friend_list_recommendfriendBtn);
		friendListView = (ListView)findViewById(R.id.friend_list_friendlist);
		friendListAdapter = new AdapterForFriendList(this, friendList, 
				R.layout.friend_list_item, 
				new String[] {"fname","gender","email"}, 
				new int[] {R.id.friend_list_item_fname,R.id.friend_list_item_gender,R.id.friend_list_item_email});
		
		friendListView.setAdapter(friendListAdapter);
//		LinearLayout.LayoutParams searchMyFriendBtnParams = (LayoutParams) searchMyFriendBtn.getLayoutParams();
//		LinearLayout.LayoutParams searchMyFriendParams = (LayoutParams) searchMyFriend.getLayoutParams();
//		Log.i(TAG,"origin: "+searchMyFriendParams.height+", now: "+ searchMyFriendBtnParams.height);
//		searchMyFriendParams.height = searchMyFriendBtnParams.height;
//		searchMyFriend.setLayoutParams(searchMyFriendParams);
	}

	public void showFriendList()
	{
		friendList.clear();
		TableFriends tf = new TableFriends(this);
		friends = tf.getAllFriends();
		if(friends.size() == 0)
		{
			Toast.makeText(this, "你暂时还没有好友哦", Toast.LENGTH_SHORT).show();
		}
		else
		{
			for (FriendStruct fs : friends) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("fname", fs.fname);
				int gender = Integer.parseInt(fs.gender);
				if(gender == 0)
				{
					map.put("gender", "男");
				}
				else
				{
					map.put("gender", "女");
				}
//				map.put("gender", fs.gender);
				map.put("email", fs.email);
				map.put("fid",fs.fid);
				friendList.add(map);
			}
		}
		Collections.sort(friendList, myComparator);
		
		friendListAdapter.notifyDataSetChanged();
		
//		friendListAdapter = new AdapterForFriendList(this, friendList, 
//				R.layout.friend_list_item, 
//				new String[] {"fname","gender","email"}, 
//				new int[] {R.id.friend_list_item_fname,R.id.friend_list_item_gender,R.id.friend_list_item_email});
//		
//		friendListView.setAdapter(friendListAdapter);
	}
	
}
