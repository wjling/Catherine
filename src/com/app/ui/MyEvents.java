package com.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.app.catherine.R;
import com.app.customwidget.PullUpDownView;
import com.app.customwidget.PullUpDownView.onPullListener;
import com.app.utils.cardAdapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -3;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -4;
	private static final int MSG_WHAT_REFRESH_DONE = -5;
	private static final int MSG_WHAT_GET_MORE_DONE = -6;
	
	public MyEvents(Context context, View myEventsView, Handler uiHandler, int screenWidth) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myEventsView = myEventsView;
		this.uiHandler = uiHandler;
		this.screenWidth = screenWidth;
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
		getActivities();
	}
	
	//add by luo
	private void getActivities()
	{
		myEventsList.clear();
		Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
		msg1.sendToTarget();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.MONDAY);
		int second = calendar.get(Calendar.SECOND);
		int minute = calendar.get(Calendar.MINUTE);
		int hour = calendar.get(Calendar.HOUR);
		
		for(int i=0; i<10; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", "Activity: " + i);
			map.put("day", day+"");
			map.put("monthAndYear", month + "月" +year);
			map.put("time", hour+":"+minute+":"+second);
			map.put("location", "GOGO新天地三楼");
			map.put("launcher", "by " + "luo");
			map.put("remark", "今晚去唱k，有没人有兴趣捏？ 有没兴趣顺便一起吃个饭甘样捏？有没兴趣顺便看埋场《速6》呢？如果有兴趣，不妨渣车嚟信科院大楼接我，哈哈，哥你想多了.");
			map.put("participantsNum", 100+"");
			myEventsList.add(map);
		}
		
//			myEventsAdapter.notifyDataSetChanged();
	}
	
	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				myEventsList.clear();
				Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
				msg1.sendToTarget();
				
				//get data from server
				try {
					getActivities();  
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Message msg2 = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
				msg2.sendToTarget();
			}
		}).start();
	}
	
	// 模拟数据
	private String[] mStringArray = {
            "A", "B", "C", "D", "E"
            ,"F", "G", "H", "I", "J", "K"
//            ,"L", "M", "N", "O", "P"
    };
	
}
