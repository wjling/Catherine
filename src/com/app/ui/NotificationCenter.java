package com.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.adapters.AdapterForRequest;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;
import com.app.ui.menu.FriendCenter.FriendCenter;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NotificationCenter {
	
	private final String TAG = "NotificationCenter";
	private Context context;
	private FriendCenter UI_friendCenter;
	private View notificationView;
//	private View notificationCenterView;
	
	private int userId;
	private int deletePositionOfActivityResultList;
	private Handler uiHandler;
	private myHandler ncHandler = new myHandler();

	private ArrayList<JSONObject> requests = new ArrayList<JSONObject>();
	private ArrayList<JSONObject> verifications = new ArrayList<JSONObject>();
	private ArrayList<HashMap<String, Object>> friendRequests = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> friendRequestResults = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> eventInvitations = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> eventInvitationResults = new ArrayList<HashMap<String,Object>>();
	
	private SimpleAdapter friendRequestAdapter;
	private SimpleAdapter eventInvitationAdapter;
	
	public NotificationCenter(Context context, FriendCenter UI_friendCenter, Handler uiHandler, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.UI_friendCenter = UI_friendCenter;
		this.uiHandler = uiHandler;
		this.userId = userId;
	}
	
	
	
	public void getNotifications() {
		// TODO Auto-generated method stub
		getNotificationFromDB();
		showFriendRequests();
		showRequestResult();
		
	}
	
	//��ȡ���ݱ�����
	public void getNotificationFromDB()
	{
		
		NotificationTableAdapter adapter = new NotificationTableAdapter(context);
		ArrayList<notificationObject> addFriendRequestList = adapter.queryData("ADD_FRIEND_REQUEST", userId);
		ArrayList<notificationObject> addFriendVerifyList = adapter.queryData("ADD_FRIEND_VERIFY", userId);
		ArrayList<notificationObject> addActivityRequestList = adapter.queryData("ADD_ACTIVITY_INVITATION", userId);
		ArrayList<notificationObject> addActivityFeedBackList = adapter.queryData("ADD_ACTIVITY_FEEDBACK", userId);
		ArrayList<notificationObject> requestIntoActivityList = adapter.queryData("REQUEST_INTO_ACTIVITY", userId);
		ArrayList<notificationObject> responseIntoActivityList = adapter.queryData("RESPONSE_INTO_ACTIVITY", userId);
		
		requests.clear();
		for (notificationObject item : addFriendRequestList) {
			String msg = item.msg;
			int id = item.item_ID;
			try {
				JSONObject msgJson = new JSONObject(msg);
					msgJson.put("item_id", id);
					msgJson.put("tag", "ADD_FRIEND_REQUEST");
				requests.add(msgJson);
				Log.i(TAG, "���ݱ��д�ŵĺ��������ǣ�"+msgJson.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		for (notificationObject item : addActivityRequestList) {
			String msg = item.msg;
			int id = item.item_ID;
			try {
				JSONObject msgJson = new JSONObject(msg);
					msgJson.put("item_id", id);
					msgJson.put("tag", "ADD_ACTIVITY_INVITATION");
				requests.add(msgJson);
				Log.i(TAG, "���ݱ��д�ŵĻ�����ǣ�"+msgJson.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		for (notificationObject item : requestIntoActivityList) {
			String msg = item.msg;
			int id = item.item_ID;
			try {
				JSONObject msgJson = new JSONObject(msg);
					msgJson.put("item_id", id);
					msgJson.put("tag", "REQUEST_INTO_ACTIVITY");
				requests.add(msgJson);
				Log.i(TAG, "���ݱ��д�ŵĻ���������Ϣ�ǣ�"+msgJson.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		verifications.clear();
		for (notificationObject itemVerify : addFriendVerifyList) {
			String msg = itemVerify.msg;
			int id = itemVerify.item_ID;
			try {
				JSONObject msgJson = new JSONObject(msg);
				Boolean result = msgJson.getBoolean("result");
				String userName = msgJson.getString("name");
				JSONObject outputJson = new JSONObject();
				if (result.equals(true)) {//����ͬ�����//����Ϣд�����鹩��ʾ
					outputJson.put("item_id", id);
					outputJson.put("content", "���"+userName+"�ɹ���");
				}
				else//����ܾ����
				{
					outputJson.put("item_id", id);
					outputJson.put("content", "���"+userName+"ʧ��@_@");				
				}
				outputJson.put("tag", "ADD_FRIEND_VERIFY"); //Ϊ��Ӻ��ѵĽ�����ϱ�ʶ
				verifications.add(outputJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		for (notificationObject item : addActivityFeedBackList) {
			String msg = item.msg;
			int itemID = item.item_ID;
			String inviteeName;
			Boolean result;
			int eventID;
			try {
				JSONObject msgJson = new JSONObject(msg);
				result = msgJson.getBoolean("result");
				inviteeName = msgJson.getString("name");
				eventID = msgJson.getInt("event_id");
				JSONObject outputJson = new JSONObject();
				if (result.equals(true)) {
					outputJson.put("item_id", itemID);
					outputJson.put("content", inviteeName + "ͬ��μӻ:" + eventID);
				}
				else {
					outputJson.put("item_id", itemID);
					outputJson.put("content", inviteeName + "��Ȼ�ܾ��˲μӻ:" + eventID);
				}
				outputJson.put("tag", "ADD_ACTIVITY_FEEDBACK"); //Ϊ�μӻ�Ľ�����ϱ�ʶ
				verifications.add(outputJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		for (notificationObject item : responseIntoActivityList) {
			String msg = item.msg;
			int itemID = item.item_ID;
			Boolean result;
			String subject;
			Integer launcherID;
			try {
				JSONObject msgJson = new JSONObject(msg);
				result = msgJson.getBoolean("result");
				subject = msgJson.getString("subject");
				launcherID = msgJson.getInt("launcher");
				JSONObject outputJson = new JSONObject();
				outputJson.put("item_id", itemID);
				if (result.equals(true)) 				
					outputJson.put("content", launcherID + "ͬ����μӻ:" + subject);		
				else 
					outputJson.put("content", launcherID + "��Ȼ�ܾ�������μӻ:" + subject);
				
				outputJson.put("tag", "RESPONSE_INTO_ACTIVITY"); //Ϊ��;����μӻ�Ľ�����ϱ�ʶ
				verifications.add(outputJson);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void showFriendRequests()
	{
		int length = requests.size();
		friendRequests.clear();
//		activityInvitations.clear();
//		intoActivityRequests.clear();
		for(int i=0;i<length;i++)
		{
			try{
				JSONObject jo = requests.get(i);
				String tag = jo.getString("tag");
				
				if(tag =="ADD_FRIEND_REQUEST")
				{
					Log.v("json",jo.getString("tag"));
					HashMap<String, Object> map = new HashMap<String, Object>();
					if(jo.getInt("gender")==1)
						map.put("gender", "��");
					else 
						map.put("gender", "Ů");
					
					map.put("fname", jo.getString("name"));
					map.put("email", jo.getString("email"));
					map.put("id", jo.getString("item_id")); //������id
					map.put("fid", jo.getString("id"));//����id
					map.put("confirm_msg", jo.getString("confirm_msg"));
					map.put("friendObject", jo);
					Log.v("json", "�Ӻ������� "+jo.toString());
					friendRequests.add(map);
				}
				else if(tag == "ADD_ACTIVITY_INVITATION")
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					
					map.put("subject", jo.getString("subject"));
					map.put("time", jo.getString("time"));
					map.put("launcher", jo.getString("launcher"));//������id
					map.put("item_id", jo.getString("item_id")); //������id
					map.put("event_id",jo.getInt("event_id"));
					map.put("activityObject","�ӻ����: "+ jo);
					Log.v("json", i+jo.toString());
					eventInvitations.add(map);
				}
				else if (tag == "REQUEST_INTO_ACTIVITY") 
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("item_id", jo.getString("item_id")); //������id
					map.put("id", jo.getInt("id"));
					map.put("name", jo.getString("name"));
					if(jo.getInt("gender")==1)
						map.put("gender", "��");
					else 
						map.put("gender", "Ů");
					map.put("email", jo.getString("email"));
					map.put("confirm_msg", jo.getString("confirm_msg"));
					map.put("event_id", jo.getInt("event_id"));
//					intoActivityRequests.add(map);
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
//		Log.i("NotificationCenter","notificationView��idΪ�� "+ notificationView.getId());
		if(notificationView.getId() == R.layout.friend_center_notification)
		{
			Log.i(TAG,"��ʾ����֪ͨ�� "+friendRequests.toString());
			ListView lv1 = (ListView)notificationView.findViewById(R.id.friend_center_notification_friendrequests);
			friendRequestAdapter = new SimpleAdapter(context, friendRequests, 
					R.layout.friend_request, 
					new String[]{"fname","gender","email","confirm_msg"}, 
					new int[]{R.id.friend_name,R.id.friend_gender,R.id.friend_email,R.id.confirm_msg});
			lv1.setAdapter(friendRequestAdapter);
			lv1.setOnItemClickListener(friendRequestItemListener);
		}
		else if(notificationView.getId() == R.layout.my_events_notification)
		{
			Log.i(TAG,"��ʾ�֪ͨ�� "+eventInvitations.toString());
			ListView lv2 = (ListView)notificationView.findViewById(R.id.my_events_notification_eventsRequests);
			eventInvitationAdapter = new SimpleAdapter(context, eventInvitations, 
					R.layout.activity_request_item, 
					new String[]{"subject","time","launcher"}, 
					new int[]{R.id.activity_request_subject,R.id.activity_request_time,R.id.activity_request_launcher});
			
			lv2.setAdapter(eventInvitationAdapter);
			lv2.setOnItemClickListener(activityRequestItemListener);
		}
//		
//		
//		ListView lv3 = (ListView)findViewById(R.id.into_activity_request);
//		intoActivityReqAdapter = new SimpleAdapter(this, intoActivityRequests, 
//				R.layout.into_activity_request, 
//				new String[]{"id","name","gender","email","confirm_msg", "event_id"}, 
//				new int[]{R.id.requesterID, R.id.requesterName, R.id.requesterGender, R.id.requesterEmail, R.id.requesterMsgContent,R.id.requestevenid});
//		lv3.setAdapter(intoActivityReqAdapter);
//		lv3.setOnItemClickListener(intoActivityItemListener);
	}
	
	private OnItemClickListener friendRequestItemListener = new OnItemClickListener() {
		int result = 0;
		JSONObject friendObject;
		TableFriends tableFriends;
		String id;
		HashMap<String, Object> selectedItem;
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			final int position = arg2;
			selectedItem = friendRequests.get(arg2);
//			Log.v("json", "hehe "+selectedItem.toString());
			id = (String) selectedItem.get("id");
			friendObject = (JSONObject)selectedItem.get("friendObject");
//			Toast.makeText(context, "id:"+ id, Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new Builder(context);
			
			builder.setTitle("������֤").setIcon(R.drawable.ic_launcher).setMessage("�Ƿ����Ϊ���ѣ�").create();
			builder.setPositiveButton("�ð�", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					result = 1;
					//д���������ݱ�
					FriendStruct newFriend = new FriendStruct(friendObject);
					tableFriends = new TableFriends(context);
					tableFriends.add(newFriend);
					//ɾ����Ϣ���ݱ�
					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
					adapter.deleteData( Integer.parseInt(id) );
					//������ʾ��Ϣ
					friendRequests.remove(position);
					friendRequestAdapter.notifyDataSetChanged();
					
					int fid = Integer.parseInt((String) selectedItem.get("fid"));
					sendMessage(userId, fid, result);
				}
			});
			builder.setNegativeButton("�ܾ�", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					result = 0;
					//ɾ����Ϣ���ݱ�
					NotificationTableAdapter adapter = new NotificationTableAdapter(context);
					adapter.deleteData( Integer.parseInt(id) );
					//������ʾ��Ϣ
					friendRequests.remove(position);
					friendRequestAdapter.notifyDataSetChanged();
					
					int fid = Integer.parseInt((String) selectedItem.get("fid"));
					sendMessage(userId, fid, result);
				}
			});
			builder.show();
//			int uid = Integer.parseInt(userId);
//			int fid = Integer.parseInt((String) selectedItem.get("fid"));
//			sendMessage(uid, fid, result);	
		}
		
		private void sendMessage(int uid,int fid, int result)
		{
			JSONObject params = new JSONObject();
			try {
				params.put("id", uid);
				params.put("friend_id", fid);
				params.put("cmd", 998);
				if(result==1)
					params.put("result", true);
				else 
					params.put("result", false);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("FriendsCenter", "��ͬ����߾ܾ�ʱ�������������͵��ǣ�"+params.toString());
			HttpSender http = new HttpSender();
			http.Httppost(OperationCode.ADD_FRIEND, params, ncHandler);
		}
	};
	
	private OnItemClickListener activityRequestItemListener = new OnItemClickListener() {

		HashMap<String, Object> selectedItem;
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			deletePositionOfActivityResultList = -1;
			final int position = arg2;
			selectedItem = eventInvitations.get(arg2);
			AlertDialog.Builder builder = new Builder(context);
			
			builder.setTitle("���֤").setIcon(R.drawable.ic_launcher).setMessage("�Ƿ�ͬ�����").create();
			builder.setPositiveButton("ͬ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					try {
						params.put("cmd", 997);
						params.put("id", userId);
						params.put("event_id",selectedItem.get("event_id"));
						params.put("result", 1);
						deletePositionOfActivityResultList = position;
						HttpSender httpSender = new HttpSender();
						httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton("�ܾ�", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					JSONObject params = new JSONObject();
					try {
						params.put("cmd", 997);
						params.put("id", userId);
						params.put("event_id",selectedItem.get("event_id"));
						params.put("result", 0);
						HttpSender httpSender = new HttpSender();
						httpSender.Httppost(OperationCode.PARTICIPATE_EVENT, params, ncHandler);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			builder.show();
			
		}
	};
	
	public void showRequestResult()
	{
		//valifications.addAll(valificationsData());
		
		int size = verifications.size();
		HashMap<String, Object> map;
		friendRequestResults.clear();
		eventInvitationResults.clear();
		
		for(int i=0;i<size;i++)
		{
			JSONObject jo = verifications.get(i);
			try {
				String tag = jo.getString("tag");
				map = new HashMap<String, Object>();
//				String itemId = jo.getString("item_id");
				int itemId = jo.getInt("item_id");
				String content = jo.getString("content");
				map.put("item_id", itemId);
				map.put("content", content);
				if(tag == "ADD_FRIEND_VERIFY")
				{
					friendRequestResults.add(map);
				}
				else if( tag == "ADD_ACTIVITY_FEEDBACK")
				{
					eventInvitationResults.add(map);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(notificationView.getId() == R.layout.friend_center_notification)
		{
			ListView lv = (ListView)notificationView.findViewById(R.id.friend_center_notification_requestresults);
			AdapterForRequest adapter = new AdapterForRequest(context, friendRequestResults, 
					R.layout.request_result_item, 
					new String[]{"content"}, 
					new int[]{R.id.request_result_msg});
			lv.setAdapter(adapter);
		}
		else if(notificationView.getId() == R.layout.my_events_notification)
		{
			ListView lv = (ListView)notificationView.findViewById(R.id.my_events_notification_eventsRequestResults);
			AdapterForRequest adapter = new AdapterForRequest(context, eventInvitationResults, 
					R.layout.request_result_item, 
					new String[]{"content"}, 
					new int[]{R.id.request_result_msg});
			lv.setAdapter(adapter);
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
			super.handleMessage(msg);
			switch(msg.what)
			{
			case OperationCode.ADD_FRIEND:
				break;
			case OperationCode.PARTICIPATE_EVENT:
				break;
				default: break;
			}
		}
	}
	
	
	public void setNotificationView(View v)
	{
		this.notificationView = v;
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}
}
