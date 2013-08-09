package com.app.addActivityPack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.addActivityPack.AdapterForPaticipantList.FriendInfor;
import com.app.addActivityPack.AdapterForPaticipantList.ViewHolder;
import com.app.catherine.R;
import com.app.ui.UserInterface;
import com.app.utils.HttpSender;
import com.app.utils.ListViewUtility;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends Activity
{
	private static final String ADD_ACTIVITY_TAG = "AddActivity";
	
	private Button addTimeBtn;
	private Button addLocationBtn;
	private Button addPaticipantBtn;
	private Button submitActivityBtn;
	private EditText themeEditText;
	private EditText durationEditText;
	private EditText remarkEditText;
	private CheckBox visibilityCheckBox;
	
	private Calendar calendar;
	private String outputList = "";
	private String outputLocList = "";
	
	private String activityTheme = "";
	private Set<TimeStruct> dateList = new HashSet<TimeStruct>();
	private AlertDialog dialogAddLocation;
	private Set<String> locationList = new HashSet<String>();
	private ListView participantList;
	private AdapterForPaticipantList adapter;
	private Vector<String> participantSet = new Vector<String>();
	private int duration;
	private String remarkStr;
	private boolean visibility;
	
	private int userId;
	private String email;
	private MessageHandler messageHandler;
	
	private HttpSender sender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addactivity);
		
		calendar = Calendar.getInstance();
		addTimeBtn = (Button)findViewById(R.id.addTime);
		addTimeBtn.setOnClickListener(addTimeListener);
		
		addLocationBtn = (Button)findViewById(R.id.addLocation);
		addLocationBtn.setOnClickListener(addLocationListener);
		
		addPaticipantBtn = (Button)findViewById(R.id.addParticipant);
		addPaticipantBtn.setOnClickListener(addPaticipantListener);
		
		submitActivityBtn = (Button)findViewById(R.id.submitActivityBtn);
		submitActivityBtn.setOnClickListener(submitActivityListener);
		
		themeEditText = (EditText)findViewById(R.id.activityTheme);
		durationEditText = (EditText)findViewById(R.id.activityDuration);
		remarkEditText = (EditText)findViewById(R.id.activityRemarks);
		visibilityCheckBox = (CheckBox)findViewById(R.id.activityVisibility);
		visibilityCheckBox.setChecked(true);
		
		userId = getIntent().getIntExtra("userId", 0);
		email = getIntent().getStringExtra("email");
		
		messageHandler = new MessageHandler(Looper.myLooper());
		sender = new HttpSender();
	}
	
	//�ύ��ť
	private OnClickListener submitActivityListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String hint = "������������Ϣ��";
			activityTheme = themeEditText.getText().toString().trim();
			boolean getDurationFlag = getDuration();
			remarkStr = remarkEditText.getText().toString().trim();
			visibility = visibilityCheckBox.isChecked();
			
			if ("".equals(activityTheme))
			{
				hint += "�����";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( dateList.size()==0)
			{
				hint += "�ʱ��";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( locationList.size()==0)
			{
				hint += "��ص�";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( participantSet.size()==0)
			{
				hint += "������";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if ( getDurationFlag==false) {
				hint += "�����ʱ��";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else {
				hint = "������Ϣ���������ڷ�������...";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
				sendRequest();
			}
		}
	};
	
	private boolean getDuration()
	{
		duration = -1;
		String str = durationEditText.getText().toString().trim();
		if (str.length()==0 ) 
			return false;
		else {
			duration = Integer.parseInt( str );
			if ( duration<0 ) 
				return false;
			else 
				return true;			
		}
	}
	
	/**
	 *		������JSON��ʽ����C->S)
			id:�����ߵ�id
			suject:�����
			time:[time1,time2,...]��time��ʱ���ʽ��Ӧmysql��datetime��ʽYYYY-MM-DD HH:MM:SS��
			location:[location1,location2,...]
			duration:�����ʱ�䣨�죩int
			visibility:�ɼ���bool
			status:�״̬��0����ﱸ�� 1��������� 2�����ѽ�����
			remark(��ע)������Ϊ�գ�
			friends:[friend_id1,.....]
	 */
	private void sendRequest()
	{
		JSONObject paramsAddActivity = new JSONObject();
		
		try
		{
			paramsAddActivity.put("id", userId);
			paramsAddActivity.put("subject", activityTheme);		
			
			paramsAddActivity.put("time", dateList.iterator().next().toString());
//			paramsAddActivity.put("time_count", dateList.size());
//			Log.i(ADD_ACTIVITY_TAG, "ʱ������"+dateList.size());		
//			int i = 0;
//			for (TimeStruct dateItem : dateList)
//			{
//				i++;
//				paramsAddActivity.put("time"+i, dateItem.toString());				
//				Log.i(ADD_ACTIVITY_TAG, "time"+i+":"+dateItem.toString());
//			}
			paramsAddActivity.put("location", locationList.iterator().next().toString());
//			paramsAddActivity.put("location_count", locationList.size());
//			Log.i(ADD_ACTIVITY_TAG, "�ص�����"+locationList.size());
//			
//			int k = 0;
//			for(String loc : locationList)
//			{
//				k++;	
//				paramsAddActivity.put("location"+k, loc);
//				Log.i(ADD_ACTIVITY_TAG, "location"+k+":"+loc);
//			}
			
			paramsAddActivity.put("duration", duration);
			paramsAddActivity.put("visibility", visibility);
			paramsAddActivity.put("status", 0);
			paramsAddActivity.put("remark", remarkStr);
			
			ArrayList<Integer> participantsParam = new ArrayList<Integer>();
			for ( String participant : participantSet) 
				participantsParam.add( Integer.parseInt(participant) );
			paramsAddActivity.put("friends", participantsParam);
			
//			paramsAddActivity.put("friends_count", participantSet.size());
//			Log.i(ADD_ACTIVITY_TAG, "��������"+participantSet.size());
			
//			int m=0;
//			for(String participant : participantSet)
//			{
//				m++;
//				paramsAddActivity.put("friend"+m, participant);
//				Log.i(ADD_ACTIVITY_TAG, "friend"+m+":"+participant);
//			}
			
			//response
			Log.i("AddActivity params:", paramsAddActivity.toString());
			sender.Httppost(OperationCode.LAUNCH_EVENT, paramsAddActivity, messageHandler);
		} catch (JSONException e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	//��Ӳ�����
	private OnClickListener addPaticipantListener = new OnClickListener()
	{
		private LayoutInflater factory;
		private View dialogView;
		private AlertDialog dialogShowContact;
		
		@Override
		public void onClick(View v)
		{
			factory = LayoutInflater.from(AddActivity.this);
			dialogView = factory.inflate(R.layout.showcontactlist, null);
			
			participantList = (ListView)dialogView.findViewById(R.id.show_contact_list);
			adapter = new AdapterForPaticipantList(AddActivity.this);
			
			participantList.setAdapter(adapter);
			participantList.setItemsCanFocus(false);
			participantList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			participantList.setOnItemClickListener(lvListener);
			
			dialogShowContact = new AlertDialog.Builder(AddActivity.this)
				.setTitle("ChooseFriends:")
				.setView(dialogView)
				.setPositiveButton("ȷ��", chooseFriPosListener)
				.setNegativeButton("ȡ��", null)
				.create();
			
			dialogShowContact.show();
		}
	};
	
	//���û����ѡ������Щ������
	//participantSet��ȡ����u_id
	private DialogInterface.OnClickListener chooseFriPosListener = new DialogInterface.OnClickListener()
	{
		private String output = "";
		
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			output = "";
			participantSet.clear();
			
			for (int i = 0; i < participantList.getCount(); i++)
			{
				if(AdapterForPaticipantList.isSelected.get(i))
				{
					//�������u_name�����Ǵ���participantSet�������u_id
					//�����Ҫ�õ�u_name,��ôparticipantSet������Դ��index,Ȼ��ʹ��index����
					//getItem(index)������ȡFriendInfor
					//��ͨ��FriendInfor�����ȡ���ּ���
					output += ((FriendInfor)adapter.getItem(i)).u_name + "\n";
					participantSet.add( ((FriendInfor)adapter.getItem(i)).u_id );
				}
			}
			
			TextView participantTV = (TextView)findViewById(R.id.participantList);
			participantTV.setText(output);
		}
	};
	
	//Item�����ʱ����ӻ�ɾ��
	private OnItemClickListener lvListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			// TODO Auto-generated method stub
			ViewHolder vHolder = (ViewHolder)view.getTag();
			vHolder.cBox.toggle();
			
			//cBox.isChecked()�ж��Ƿ�ѡ��ѡ��Ϊtrue��ûѡ��Ϊfalse
			AdapterForPaticipantList.isSelected.put(position, vHolder.cBox.isChecked());
		}
		
	};
	
	private OnClickListener addLocationListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{

			LayoutInflater factory = LayoutInflater.from(AddActivity.this);
			//�õ��Զ���Ի���
			final View dialogView = factory.inflate(R.layout.addlocation, null);
			
			dialogAddLocation = new AlertDialog.Builder(AddActivity.this)
				.setTitle("�����")
				.setMessage("�������ص㣺")
				.setView(dialogView)
				.setPositiveButton("ȷ��", addLocationPositiveListener)
				.setNegativeButton("ȡ��", null)
				.create();
			
			dialogAddLocation.show();
	
		}
	};
	
	private DialogInterface.OnClickListener addLocationPositiveListener = new DialogInterface.OnClickListener()
	{
		private EditText newLocationET;
		private String newLocationStr;
		
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			// TODO Auto-generated method stub
			newLocationET = (EditText)dialogAddLocation.findViewById(R.id.addLocationText);
			newLocationStr = newLocationET.getText().toString().trim();
			if( !"".equals(newLocationStr) )
			{
				locationList.add(newLocationStr);
				displayLocationList();
			}
			//Toast.makeText(AddActivity.this, "����ˣ�"+newLocationStr, Toast.LENGTH_SHORT).show();
		}
	};
	
	private void displayLocationList()
	{
		outputLocList = "";
		TextView locListTV = (TextView)findViewById(R.id.locationList);
		
		for (String location : locationList)
		{
			outputLocList += location + "\n";
		}
		
		locListTV.setText(outputLocList);
	}
	
	private OnClickListener addTimeListener = new OnClickListener()
	{		
		private TimeStruct timeStruct;
		private TimePickerDialog timePickerDialog;
		private DatePickerDialog datePickerDialog;
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			timeStruct = new TimeStruct();
			
			datePickerDialog = new DatePickerDialog(
					AddActivity.this, 
					dateCallback, 
					calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), 
					calendar.get(Calendar.DAY_OF_MONTH)
					);
			
			timePickerDialog = new TimePickerDialog(
					AddActivity.this, 
					timeCallBack, 
					calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE), 
					true);	
			
			datePickerDialog.show();
		}
		
		private DatePickerDialog.OnDateSetListener dateCallback = 
				new DatePickerDialog.OnDateSetListener()
				{	
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth)
					{
						timeStruct.setDate(year, monthOfYear, dayOfMonth);
						timePickerDialog.show();
					}
				};
		
		private TimePickerDialog.OnTimeSetListener timeCallBack = 
				new TimePickerDialog.OnTimeSetListener()
				{
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
							timeStruct.setTime(hourOfDay, minute);
							dateList.add(timeStruct);
							displayDateList();						
					}
				};
	};
	
	private void displayDateList()
	{
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		ListView lv = (ListView)findViewById(R.id.timeList);
		
		for (TimeStruct dateItem : dateList)			
		{
			Log.i("AddActivity date:", dateItem.toString());
			HashMap<String, Object> map = new HashMap<String, Object>();		
			map.put("activityDate", dateItem);
			map.put("content", dateItem.toString());
			list.add(map);
		}
	
		AdapterForTimeItem adapter = new AdapterForTimeItem(this, list, 
				R.layout.time, 
				new String[] {"content"}, 
				new int[] {R.id.timeContent}, 
				dateList,
				R.id.timeList);
		lv.setAdapter(adapter);
		new ListViewUtility().setListViewHeightBasedOnChildren(lv);
	}
	
	class MessageHandler extends Handler
	{
		String resAddActivity = null;
		int cmdAddActivity;
		JSONObject resAddActivityJson = null;
		
		public MessageHandler(Looper looper) {
			super(looper);
		}
		public void handleMessage(Message msg) 
		{
			Log.i("AddActivity", "��������ֵ"+msg.obj.toString());
			switch (msg.what) {
			case OperationCode.LAUNCH_EVENT:
				
				try {
					resAddActivity = msg.obj.toString();
					if ( resAddActivity!="DEFAULT") 
					{
							resAddActivityJson = new JSONObject(resAddActivity);
							
							cmdAddActivity = resAddActivityJson.getInt("cmd");
							int evenID = resAddActivityJson.getInt("event_id");
//							int evenID = 0;
							if( ReturnCode.NORMAL_REPLY==cmdAddActivity )
							{
								//��ת���û��Լ��Ļҳ��
								Log.i("AddActivity", "�ɹ�����,evenId:"+evenID);
								//��ת��UserInterface.java
								Intent intent = new Intent();
								intent.setClass(AddActivity.this, UserInterface.class);
								intent.putExtra("userId", userId);
								intent.putExtra("email", email);
								startActivity(intent);
							}
							else {
								Toast.makeText(AddActivity.this, "����ʧ�ܣ�������", Toast.LENGTH_SHORT).show();
							}
					}
					else 
						Toast.makeText(AddActivity.this, "�����쳣", Toast.LENGTH_SHORT).show();
				} catch (JSONException e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}

}
