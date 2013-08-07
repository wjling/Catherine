package com.app.addFriendPack;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class addFriend extends Activity {

	private Button addFriendBtn;
	private TextView friendInfoTV;
	
	private int friendUid;
	private int id;
	private String friendEmail;
	private String friendName;
	private int gender;
	
	private EditText dialogInputET;
	private String inputStr;
	
	JSONObject addFriendParams = new JSONObject();
	String respStr;
	JSONObject respJson;
	int respCMD;
	MessageHandler handler;
	
	@Override
	protected void onCreate(Bundle b) {
		// TODO Auto-generated method stub
		super.onCreate(b);
		setContentView(R.layout.addfriend);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
			
		friendEmail = bundle.getString("friendEmail");
		friendName = bundle.getString("friendName");
		friendUid = bundle.getInt("friendUid");
		id = bundle.getInt("id");
		gender = bundle.getInt("gender");
		
		addFriendBtn = (Button)findViewById(R.id.addFriendBtn);
		friendInfoTV = (TextView)findViewById(R.id.friendInfo);
		
		setFriendInfo();
	    addFriendBtn.setOnClickListener(addFriendListener);
	    handler = new MessageHandler(Looper.myLooper());
	}
	
	private void setFriendInfo()
	{
		if( gender ==1 )
			friendInfoTV.setText( "����: " + friendEmail + "\n����:" + friendName + "\n�Ա�: ��");
		else 
			friendInfoTV.setText( "����: " + friendEmail + "\n����:" + friendName + "\n�Ա�: Ů");
	}
	
	private OnClickListener addFriendListener = new OnClickListener() {
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/**
			 * ����������֤��Ϣ
			 */
//			Toast.makeText(addFriend.this, "�����...", Toast.LENGTH_SHORT).show();
			
			dialogInputET = new EditText(addFriend.this);
			new AlertDialog.Builder(addFriend.this)
				.setTitle("�����������֤��Ϣ:")
				.setView(dialogInputET)
				.setPositiveButton("ȷ��", posClickListener)
				.setNegativeButton("ȡ��", null)
				.show();			
		}
	};
	
	private android.content.DialogInterface.OnClickListener posClickListener = 
			new android.content.DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			if( id==friendUid )
				Toast.makeText(addFriend.this, "��������Լ�Ŷ@_@", Toast.LENGTH_SHORT).show();
			else {			
				inputStr = dialogInputET.getText().toString().trim();
				new Thread()
				{
					public void run()
					{
						sendAddFriendRequest();
					}
				}.start();			
			}
		}
	};
	
	private void sendAddFriendRequest()
	{
			try {
				addFriendParams.put("id", id);
				addFriendParams.put("friend_id", friendUid);
				addFriendParams.put("cmd", 999);
				addFriendParams.put("confirm_msg", inputStr);
			
				HttpSender http = new HttpSender();
				http.Httppost(OperationCode.ADD_FRIEND, addFriendParams, handler);							
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	class MessageHandler extends Handler
	{
		public MessageHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
			case OperationCode.ADD_FRIEND:
				try
				{
					respStr = msg.obj.toString();
					if( respStr != "DEFAULT")
					{
						respJson = new JSONObject(respStr);
						respCMD = respJson.getInt("cmd");
						
						if( ReturnCode.NORMAL_REPLY==respCMD )
							Toast.makeText(addFriend.this, "��������ѷ���", Toast.LENGTH_SHORT).show();
						else if ( ReturnCode.ALREADY_FRIENDS==respCMD) 
							Toast.makeText(addFriend.this, "���û��Ѿ�����ĺ�����=_=", Toast.LENGTH_SHORT).show();					
						else
							Toast.makeText(addFriend.this, "��Ӳ��ɹ���������"+respCMD, Toast.LENGTH_SHORT).show();
					}
					else 
						Toast.makeText(addFriend.this, "���󲻳ɹ�"+respCMD, Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}				
				break;

			default:
				break;
			}
		}
	}

}
