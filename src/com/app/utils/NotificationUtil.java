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
			 				//��������֪ͨ��д�����ݿ�
			 				adapter.insertData(1, "ADD_FRIEND_REQUEST", msg);
			 				break;
			 			case 998:
			 				//ͬ����߾ܾ���ӣ�д����Ϣ���ݿ�
			 				adapter.insertData(1, "ADD_FRIEND_VERIFY", msg);
			 				//���ͬ�⣬д��������ݿ�
			 				Boolean result = msgJson.getBoolean("result");
							if(result.equals(true))
							{
								Log.i(TAG, "ͬ������ң����ҰѺ�����Ϣ�������ݿ�"+msg);
								FriendStruct newFriend = new FriendStruct(msgJson);
								TableFriends tableFriends = new TableFriends(context);
								tableFriends.add(newFriend);
							}	
							else {
								Log.i(TAG, "�ܾ��������"+msg);
							}
			 				break;
			 			case 997:
			 				//�»����֪ͨ��д����Ϣ���ݿ�
			 				adapter.insertData(1, "ADD_ACTIVITY_INVITATION", msg);
			 				break;
			 			case 996:
			 				adapter.insertData(1, "ADD_ACTIVITY_FEEDBACK", msg);
			 				break;
			 			case 995:
			 				//İ�����������
			 				adapter.insertData(1, "REQUEST_INTO_ACTIVITY", msg);
			 				break;
			 			case 994:
			 				//İ���������������
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
