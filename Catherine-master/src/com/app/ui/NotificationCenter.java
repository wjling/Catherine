package com.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.adapters.AdapterForRequest;
import com.app.adapters.MessagaAdapter;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;
import com.app.ui.menu.FriendCenter.FriendCenter;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NotificationCenter extends Activity{
	
	private final String TAG = "NotificationCenter";
	private ArrayList<notificationObject> messageList;
	private int userId;
	private ListView notificationListView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_center_notification);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        messageList = new ArrayList<notificationObject>();
        init();
    }
	
	public void init()
	{
	    notificationListView = (ListView)this.findViewById(R.id.notification_list);
	    getNotificationFromDB();
	    MessagaAdapter messagaAdapter = new MessagaAdapter(this, messageList, userId);
	    notificationListView.setAdapter(messagaAdapter);
	    notificationListView.setDivider(getResources().getDrawable(R.drawable.settings_sep3));
	}
	
	
	//��ȡ���ݱ�����
	public void getNotificationFromDB()
	{
		
		NotificationTableAdapter adapter = new NotificationTableAdapter(this);
		ArrayList<notificationObject> addFriendRequestList = adapter.queryData("ADD_FRIEND_REQUEST", userId);
		ArrayList<notificationObject> addFriendVerifyList = adapter.queryData("ADD_FRIEND_VERIFY", userId);
//		ArrayList<notificationObject> addActivityRequestList = adapter.queryData("ADD_ACTIVITY_INVITATION", userId);
//		ArrayList<notificationObject> addActivityFeedBackList = adapter.queryData("ADD_ACTIVITY_FEEDBACK", userId);
//		ArrayList<notificationObject> requestIntoActivityList = adapter.queryData("REQUEST_INTO_ACTIVITY", userId);
//		ArrayList<notificationObject> responseIntoActivityList = adapter.queryData("RESPONSE_INTO_ACTIVITY", userId);
		
		messageList.clear();
		messageList.addAll(addFriendRequestList);
		messageList.addAll(addFriendVerifyList);
		
	}

}
