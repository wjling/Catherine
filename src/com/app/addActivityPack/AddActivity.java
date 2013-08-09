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
	
	//提交按钮
	private OnClickListener submitActivityListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String hint = "请输入完整信息：";
			activityTheme = themeEditText.getText().toString().trim();
			boolean getDurationFlag = getDuration();
			remarkStr = remarkEditText.getText().toString().trim();
			visibility = visibilityCheckBox.isChecked();
			
			if ("".equals(activityTheme))
			{
				hint += "活动主题";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( dateList.size()==0)
			{
				hint += "活动时间";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( locationList.size()==0)
			{
				hint += "活动地点";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if( participantSet.size()==0)
			{
				hint += "参与者";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else if ( getDurationFlag==false) {
				hint += "活动持续时间";
				Toast.makeText(AddActivity.this, hint, Toast.LENGTH_SHORT).show();
			}
			else {
				hint = "发起活动信息完整，正在发送请求...";
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
	 *		发起活动的JSON格式：（C->S)
			id:发起者的id
			suject:活动主题
			time:[time1,time2,...]（time的时间格式对应mysql的datetime格式YYYY-MM-DD HH:MM:SS）
			location:[location1,location2,...]
			duration:活动持续时间（天）int
			visibility:可见性bool
			status:活动状态（0代表筹备中 1代表进行中 2代表已结束）
			remark(备注)：（可为空）
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
//			Log.i(ADD_ACTIVITY_TAG, "时间数："+dateList.size());		
//			int i = 0;
//			for (TimeStruct dateItem : dateList)
//			{
//				i++;
//				paramsAddActivity.put("time"+i, dateItem.toString());				
//				Log.i(ADD_ACTIVITY_TAG, "time"+i+":"+dateItem.toString());
//			}
			paramsAddActivity.put("location", locationList.iterator().next().toString());
//			paramsAddActivity.put("location_count", locationList.size());
//			Log.i(ADD_ACTIVITY_TAG, "地点数："+locationList.size());
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
//			Log.i(ADD_ACTIVITY_TAG, "朋友数："+participantSet.size());
			
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
	
	//添加参与者
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
				.setPositiveButton("确定", chooseFriPosListener)
				.setNegativeButton("取消", null)
				.create();
			
			dialogShowContact.show();
		}
	};
	
	//看用户点击选择了哪些参与者
	//participantSet获取的是u_id
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
					//输出的是u_name，但是存在participantSet里面的是u_id
					//如果想要得到u_name,那么participantSet里面可以存放index,然后使用index调用
					//getItem(index)方法获取FriendInfor
					//再通过FriendInfor对象获取名字即可
					output += ((FriendInfor)adapter.getItem(i)).u_name + "\n";
					participantSet.add( ((FriendInfor)adapter.getItem(i)).u_id );
				}
			}
			
			TextView participantTV = (TextView)findViewById(R.id.participantList);
			participantTV.setText(output);
		}
	};
	
	//Item点击的时候，添加或删除
	private OnItemClickListener lvListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			// TODO Auto-generated method stub
			ViewHolder vHolder = (ViewHolder)view.getTag();
			vHolder.cBox.toggle();
			
			//cBox.isChecked()判断是否被选择，选中为true，没选中为false
			AdapterForPaticipantList.isSelected.put(position, vHolder.cBox.isChecked());
		}
		
	};
	
	private OnClickListener addLocationListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{

			LayoutInflater factory = LayoutInflater.from(AddActivity.this);
			//得到自定义对话框
			final View dialogView = factory.inflate(R.layout.addlocation, null);
			
			dialogAddLocation = new AlertDialog.Builder(AddActivity.this)
				.setTitle("创建活动")
				.setMessage("请输入活动地点：")
				.setView(dialogView)
				.setPositiveButton("确定", addLocationPositiveListener)
				.setNegativeButton("取消", null)
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
			//Toast.makeText(AddActivity.this, "添加了："+newLocationStr, Toast.LENGTH_SHORT).show();
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
			Log.i("AddActivity", "发起活动返回值"+msg.obj.toString());
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
								//跳转到用户自己的活动页面
								Log.i("AddActivity", "成功发起活动,evenId:"+evenID);
								//跳转到UserInterface.java
								Intent intent = new Intent();
								intent.setClass(AddActivity.this, UserInterface.class);
								intent.putExtra("userId", userId);
								intent.putExtra("email", email);
								startActivity(intent);
							}
							else {
								Toast.makeText(AddActivity.this, "发起活动失败，请重试", Toast.LENGTH_SHORT).show();
							}
					}
					else 
						Toast.makeText(AddActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
