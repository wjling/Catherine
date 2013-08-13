package com.app.ui.menu.FriendCenter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.TableFriends;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;

public class FriendCenter {

	private Context context;
	private View friendCenterView;
	private Button recommendedFriendsBtn;
	private ListView friendListView;
	
	private int userId = -1;
	private Handler uiHandler;
	private myHandler fcHandler = new myHandler();
	
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
		askServerForFriendList();
	}
	
	public void setLayout()
	{
		recommendedFriendsBtn = (Button)friendCenterView.findViewById(R.id.menu_friend_center_recommendfriendBtn);
		friendListView = (ListView)friendCenterView.findViewById(R.id.menu_friend_center_friendlist);
		friendListAdapter = new AdapterForFriendList(context, friendList, 
				R.layout.friend_list_item, 
				new String[] {"fname","gender","email"}, 
				new int[] {R.id.friend_list_item_fname,R.id.friend_list_item_gender,R.id.friend_list_item_email});
		
		friendListView.setAdapter(friendListAdapter);
	}

	
	OnClickListener buttonsOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			switch(v.getId())
//			{
//			case R.id.menu_friends_center_addfriendsBtn:
//				Intent intent1 = new Intent();
//				intent1.setClass(context, searchFriend.class);
//				intent1.putExtra("userId", userId);
//				context.startActivity(intent1);
//				break;
//			case R.id.menu_friends_center_searchfriendBtn:
//				break;
//			case R.id.menu_friends_center_friendlistBtn:
//				Intent intent2 = new Intent();
//				intent2.putExtra("userId", userId);
//				intent2.setClass(context, FriendList.class);
//				context.startActivity(intent2);
//				break;
//				default: break;
//			}
		}
	};
	
	
	
	public void askServerForFriendList()
	{
		TableFriends tbFriends = new TableFriends(context);
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("friends_number", tbFriends.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSender httpSender = new HttpSender();
		httpSender.Httppost(OperationCode.SYNCHRONIZE, params, uiHandler);
	}
	
	public void showFriendList()
	{
		friendList.clear();
		TableFriends tf = new TableFriends(context);
		friends = tf.getAllFriends(userId+"");
		if(friends.size() == 0)
		{
			Toast.makeText(context, "����ʱ��û�к���Ŷ", Toast.LENGTH_SHORT).show();
		}
		else
		{
			for (FriendStruct fs : friends) {
//				if(fs.uid == userId)
//				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("fname", fs.fname);
					int gender = Integer.parseInt(fs.gender);
					if(gender == 1)
					{
						map.put("gender", "��");
					}
					else
					{
						map.put("gender", "Ů");
					}
	//				map.put("gender", fs.gender);
					map.put("email", fs.email);
					map.put("fid",fs.fid);
					friendList.add(map);
//				}
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
	
	public void sychronizeFriendsList(Message msg)
	{
		try{
			TableFriends tbFriends = new TableFriends(context);
			ArrayList<FriendStruct> friends = new ArrayList<FriendStruct>();
			JSONObject jsResponse = new JSONObject(msg.obj.toString());
			int cmd = jsResponse.getInt("cmd");
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				JSONArray jsArray = jsResponse.getJSONArray("friend_list");
				int length = jsArray.length();
				if(length == 0)
				{
					Toast.makeText(context, "�����б��Ѿ����£�����Ҫͬ��", Toast.LENGTH_SHORT).show();
				}
				else
				{
					tbFriends.deleteFriendsTable();
					for(int i=0;i<length;i++)
					{
						JSONObject jo = jsArray.getJSONObject(i);
						FriendStruct fs = new FriendStruct();
						fs = FriendStruct.getFromJSON(jo);
						friends.add(fs);
					}
					tbFriends.add(friends);	//add to the friends table of the local database
					Toast.makeText(context, "�����б�ͬ���ɹ�", Toast.LENGTH_SHORT).show();
				}
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(context, "����������", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "���Ѿ��������˽ٳ�~", Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case OperationCode.SYNCHRONIZE:
				sychronizeFriendsList(msg);
				showFriendList();
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	
	
	
	
}