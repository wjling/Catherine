package com.app.ui.menu.FriendCenter;

import java.util.ArrayList;

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
//		recommendedFriendsBtn = (Button)friendCenterView.findViewById(R.id.menu)
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
					Toast.makeText(context, "好友列表已经最新，不需要同步", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(context, "好友列表同步成功", Toast.LENGTH_SHORT).show();
				}
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "你已经被外星人劫持~", Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
