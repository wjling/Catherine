package com.app.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;

import android.content.Context;
import android.util.Log;

public class NotificationUtil 
{
	protected static final String TAG = "NotificationUtil";
	private Context context;
	
	public NotificationUtil(Context mContext)
	{
		this.context = mContext;
	}
	
	public void insertNotification(String msg)
	{
		NotificationTableAdapter adapter = new NotificationTableAdapter(context);
		JSONObject msgJson = null;
		int msgCMD;
		
		try {
			 msgJson = new JSONObject(msg);
			 msgCMD = msgJson.getInt("cmd");
			 	switch (msgCMD) {
			 			case 999:
			 				//好友请求通知，写入数据库
			 				adapter.insertData(1, "ADD_FRIEND_REQUEST", msg);
			 				break;
			 			case 998:
			 				//同意或者拒绝添加，写入消息数据库
			 				adapter.insertData(1, "ADD_FRIEND_VERIFY", msg);
			 				//如果同意，写入好友数据库
			 				Boolean result = msgJson.getBoolean("result");
							if(result.equals(true))
							{
								Log.i(TAG, "同意添加我，那我把好友信息加入数据库"+msg);
								FriendStruct newFriend = new FriendStruct(msgJson);
								TableFriends tableFriends = new TableFriends(context);
								tableFriends.add(newFriend);
							}	
							else {
								Log.i(TAG, "拒绝了添加我"+msg);
							}
			 				break;
			 			case 997:
			 				//新活动邀请通知，写入消息数据库
			 				adapter.insertData(1, "ADD_ACTIVITY_INVITATION", msg);
			 				break;
			 			case 996:
			 				adapter.insertData(1, "ADD_ACTIVITY_FEEDBACK", msg);
			 				break;
			 			case 995:
			 				//陌生人申请加入活动
			 				adapter.insertData(1, "REQUEST_INTO_ACTIVITY", msg);
			 				break;
			 			case 994:
			 				//陌生人申请加入活动反馈
			 				adapter.insertData(1, "RESPONSE_INTO_ACTIVITY", msg);
			 				break;
			 			default:
			 				break;
			}
			 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
