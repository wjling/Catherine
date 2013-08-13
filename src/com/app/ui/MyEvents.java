package com.app.ui;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.customwidget.PullUpDownView;
import com.app.customwidget.PullUpDownView.onPullListener;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.RegUtils;
import com.app.utils.ReturnCode;
import com.app.utils.cardAdapter;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyEvents {
	private Context context;
	public View myEventsView;
	public PullUpDownView myEventsPullUpDownView;
	public ListView myEventsListView;
	private onPullListener myEventsPullUpDownViewListener;
	private OnItemClickListener myEventsListViewListener;
	
	public cardAdapter myEventsAdapter;
	public ArrayList<HashMap<String, Object>> myEventsList = new ArrayList<HashMap<String,Object>>();
	
	private Handler uiHandler;
	private int screenWidth;
	private int userId;
	private HttpSender sender;
	private MsgHandler handler;
//	private int []EventIDList;
	private String datePattern = "yyyy-MM-dd HH:mm:ss";
	private JSONArray seqJsonArray = null;
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -3;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -4;
	private static final int MSG_WHAT_REFRESH_DONE = -5;
	private static final int MSG_WHAT_GET_MORE_DONE = -6;
	
	public MyEvents(Context context, View myEventsView, Handler uiHandler, int screenWidth, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myEventsView = myEventsView;
		this.uiHandler = uiHandler;
		this.screenWidth = screenWidth;
		this.userId = userId;
		
		sender = new HttpSender();
		handler = new MsgHandler( Looper.myLooper() );
	}

	public void init() {
		// TODO Auto-generated method stub
		myEventsListViewListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "点击的是第"+arg2+"个.", Toast.LENGTH_SHORT).show();
			}
		};
		
		myEventsPullUpDownViewListener = new onPullListener() {
			
			@Override
			public void Refresh() {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}				
						Message msg = uiHandler.obtainMessage(MSG_WHAT_REFRESH_DONE);
						msg.obj = "After refresh " + System.currentTimeMillis();
						msg.sendToTarget();
					}
				}).start();
				
			}
			
			@Override
			public void GetMore() {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}				
						Message msg = uiHandler.obtainMessage(MSG_WHAT_GET_MORE_DONE);
						msg.obj = "After more " + System.currentTimeMillis();
						msg.sendToTarget();
					}
				}).start();
			}
		};
		
		myEventsPullUpDownView = (PullUpDownView)myEventsView.findViewById(R.id.my_events_pull_up_down_view);
		myEventsListView = myEventsPullUpDownView.getListView();
		myEventsPullUpDownView.setOnPullListener(myEventsPullUpDownViewListener);
		myEventsListView.setOnItemClickListener(myEventsListViewListener);
		
//		myEventsAdapter = new ArrayAdapter<String>(context, R.layout.pulldown_item, myEventsList);
//		myEventsListView.setAdapter(myEventsAdapter);
		
		//edit by luo
		myEventsAdapter = new cardAdapter(context, 
				myEventsList,
				R.layout.activity_item, 
				new String[]{"title", "day", "monthAndYear","time", "location", "launcher", "remark", "participantsNum"}, 
				new int[]{R.id.activityTitle, R.id.day, R.id.monthAndYear, R.id.time, R.id.location, R.id.launcher, R.id.remark, R.id.participantsNum},
				screenWidth 
		);
		
		myEventsListView.setAdapter(myEventsAdapter);
//		getActivities();
	}
	
	//add by luo
	private void getActivityFrom( String str)
	{
		JSONObject eventInforJson;
		String subject="主题", time="", location="未定", launcher="谁发起的?", remark="没有备注哦oo";
		int member_count = 0;
		String year="0000", month="00", day="00", hour="00", minute="00", second="00";
		
		try{
			eventInforJson = new JSONObject(str);
			subject = eventInforJson.optString("subject");
			time = eventInforJson.optString("time");      //活动开始时间
			location = eventInforJson.optString("location");
			launcher = eventInforJson.optString("launcher");
			remark = eventInforJson.optString("remark");
			member_count  = eventInforJson.optInt("member_count");
		}
		catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		//为空的时候，后台返回字符串None
		if( !"None".equals(time) && time.length()>0 )
		{
			year = time.substring(0, 4);
			month = time.substring(5, 7);
			day = time.substring(8, 10);
			hour = time.substring(11, 13);
			minute = time.substring(14, 16);
			second = time.substring(17,19);
		}
				
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", subject);
		map.put("day", day+"");
		map.put("monthAndYear", month + "月" +year);
		map.put("time", hour+":"+minute+":"+second);
		map.put("location", location);
		map.put("launcher", "by " + launcher);
		map.put("remark", remark);
		map.put("participantsNum", member_count+"");
		myEventsList.add(map);		
	}
	
	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				myEventsList.clear();
				Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
				msg1.sendToTarget();
				
				//get data from server, send a request~
				sendRequest(OperationCode.GET_MY_EVENTS);
			}
		}).start();
	}
	
	private void sendRequest(int opCode)
	{
		JSONObject params = new JSONObject();
		try{
			switch (opCode) {
			case OperationCode.GET_MY_EVENTS:
				params.put("id", userId);
				sender.Httppost(OperationCode.GET_MY_EVENTS, params, handler);
				break;
			case OperationCode.GET_EVENTS:
				params.put("sequence", seqJsonArray);
				Log.e("test", params.toString());
				sender.Httppost(OperationCode.GET_EVENTS, params, handler);
			default:
				break;
			}
			
		}
		catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	class MsgHandler extends Handler
	{
		public MsgHandler(Looper looper)
		{
			super(looper);
		}
		
		public void handleMessage(Message msg)
		{
			String returnStr = msg.obj.toString();
			JSONObject returnJson = null;
			
			JSONArray eventJsonArray = null;
			int returnCMD;
			
			if( returnStr!="DEFAULT")
			{
				
				try{
					returnJson = new JSONObject( returnStr );
					returnCMD = returnJson.optInt("cmd");
					
						switch ( msg.what ) {
							case OperationCode.GET_MY_EVENTS:																			
								
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{						
									seqJsonArray = returnJson.optJSONArray("sequence");
									int length = seqJsonArray.length();
									if( length>0 )
									{
										Log.i("my events", "seq length: " + length);
//										EventIDList = new int[length];
//										for( int i=0; i<length; i++)									
//											EventIDList[i] = seqJsonArray.getInt(i);
										
										//使用events sequence请求活动内容
										sendRequest(OperationCode.GET_EVENTS);
									}
									else										
										Toast.makeText(context, "当前没有活动", Toast.LENGTH_SHORT).show();									
								}
								else								
									Toast.makeText(context, "get my events返回其他值了"+returnCMD, Toast.LENGTH_SHORT).show();																	
							break;
								
							case OperationCode.GET_EVENTS:
								if( returnCMD==ReturnCode.NORMAL_REPLY )
								{
									eventJsonArray = returnJson.optJSONArray("event_list");
									int length = eventJsonArray.length();
									
									//先清空event list
									myEventsList.clear();
									
									
										for( int k=0; k<length; k++)
											getActivityFrom( eventJsonArray.getString(k) );
										
									
									//load data done; inform user interface
									Message msg2 = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
									msg2.sendToTarget();
								}
								else
									Toast.makeText(context, "get events返回其他值了"+returnCMD, Toast.LENGTH_SHORT).show();	
								break;
			
							default:
								break;
							}
						}
						catch (JSONException e) {
				            // TODO Auto-generated catch block
				            e.printStackTrace();
				        }
				
			}
			else
			{
				Toast.makeText(context, "服务器请求超时", Toast.LENGTH_SHORT).show();
			}	
		}
	}
}
