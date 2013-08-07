package com.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.app.catherine.R;
import com.app.customwidget.PullUpDownView;
import com.app.customwidget.PullUpDownView.onPullListener;

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
	
	public ArrayAdapter<String> myEventsAdapter;
	public List<String> myEventsList = new ArrayList<String>();
	
	private Handler uiHandler;
	
	//My Events 
	private static final int MSG_WHAT_ON_LOAD_DATA = -3;
	private static final int MSG_WHAT_LOAD_DATA_DONE = -4;
	private static final int MSG_WHAT_REFRESH_DONE = -5;
	private static final int MSG_WHAT_GET_MORE_DONE = -6;
	
	public MyEvents(Context context, View myEventsView, Handler uiHandler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.myEventsView = myEventsView;
		this.uiHandler = uiHandler;
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
		
		myEventsAdapter = new ArrayAdapter<String>(context, R.layout.pulldown_item, myEventsList);
		myEventsListView.setAdapter(myEventsAdapter);
		
	}
	
	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				myEventsList.clear();
				Message msg1 = uiHandler.obtainMessage(MSG_WHAT_ON_LOAD_DATA);
				msg1.sendToTarget();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<String> strings = new ArrayList<String>();
				for (String body : mStringArray) {
					strings.add(body);
				}
				Message msg2 = uiHandler.obtainMessage(MSG_WHAT_LOAD_DATA_DONE);
				msg2.obj = strings;
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
	
//	public ListView getMyEventsListView()
//	{
//		return myEventsListView;
//	}
//	
//	public PullUpDownView getMyEventsPullUpDownView()
//	{
//		return myEventsPullUpDownView;
//	}
//	
//	public ArrayAdapter<String> getMyEventsAdapter()
//	{
//		return myEventsAdapter;
//	}
//	
//	public List<String> getMyEventsList()
//	{
//		return myEventsList;
//	}
	
//	public void setMyOnTouchListener(OnTouchListener listener)
//	{
//		myEventsListView.setOnTouchListener(listener);		//非常重要的一步，聪明人秒懂
//	}
	
	
	
//	public void notifyOnLoadData()
//	{
//		myEventsPullUpDownView.notifyOnLoadData();
//	}
//	
//	public void notifyLoadDataDone()
//	{
//		myEventsPullUpDownView.notifyLoadDataDone();
//	}
//	
//	public void notifyRefreshDone()
//	{
//		myEventsPullUpDownView.notifyRefreshDone();
//	}
//	
//	public void notifyGetMoreDone()
//	{
//		myEventsPullUpDownView.notifyGetMoreDone();
//	}
	
}
