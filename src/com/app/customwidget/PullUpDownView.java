package com.app.customwidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Attributes;

import com.app.catherine.R;
import com.app.customwidget.MyScrollListView.onScrollListViewListener;

import android.R.color;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class PullUpDownView extends LinearLayout implements onScrollListViewListener{

	private static final float PULL_DISTANCE_LIMIT = 50;
	private static final float AUTO_INCREMENT = 10;
	private static final int DEFAULT_HEADER_HEIGHT = 105;
	
	private static final int MSG_WHAT_LOAD_DATA_DONE = -2;
	private static final int MSG_WHAT_ON_REFRESH = -3;
	private static final int MSG_WHAT_REFRESH_DONE = -4;
	private static final int MSG_WHAT_GET_MORE_DONE = -5;
	private static final int MSG_WHAT_SET_HEADER_HEIGHT = -6;
	
	private static final int HEADER_STATE_IDLE = 0;
	private static final int HEADER_STATE_OVER_HEIGHT = 1;
	private static final int HEADER_STATE_NOT_OVER_HEIGHT = 2;
	
	private int headerIncrement;
	private int headerState = HEADER_STATE_IDLE;
	private float motionDownY;
	
	private boolean isDown;
	private boolean isRefreshing;
	private boolean isGetingMore;
	private boolean isPullBackDone;
	
	private View headerView;
	private LayoutParams headerViewParams;	
	private TextView headerViewDateView;
	private TextView headerTextView;
	private ImageView headerArrowView;
	private View headerLoadingView;
	private View footerView;
	private TextView footerTextView;
	private View footerLoadingView;
	private MyScrollListView myListView;
	
	private onPullListener myOnPullListener;
	private RotateAnimation rotate0To180Animation;
	private RotateAnimation rotate180To0Animation;

	private myHandler viewHandler = new myHandler();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
	public interface onPullListener 
	{
		/**
		 * @author WJL
		 * 
		 */
		void Refresh();
		
		/**
		 * @author WJL
		 * 
		 */
		void GetMore();
	}
	
	public PullUpDownView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initHeaderAndFooterAndListView(context);
	}
	
	public PullUpDownView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initHeaderAndFooterAndListView(context);
	}
	
	public void notifyLoadDataDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_LOAD_DATA_DONE);
	}
	
	public void notifyRefreshDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_REFRESH_DONE);
	}
	
	public void notifyGetMoreDone()
	{
		viewHandler.sendEmptyMessage(MSG_WHAT_GET_MORE_DONE);
	}
	
	public void setOnPullListener(onPullListener listener)
	{
		myOnPullListener = listener;
	}
	
	public ListView getListView()
	{
		return myListView;
	}
	

	
	private void initHeaderAndFooterAndListView(Context context)
	{
		setOrientation(LinearLayout.VERTICAL);
		
		headerView = LayoutInflater.from(context).inflate(R.layout.pulldown_header, null);
		headerViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(headerView, 0, headerViewParams);
		
		headerTextView = (TextView)headerView.findViewById(R.id.pulldown_header_text);
		headerLoadingView = headerView.findViewById(R.id.pulldown_header_loading);
		headerArrowView = (ImageView)headerView.findViewById(R.id.pulldown_header_arrow);
		headerViewDateView = (TextView)headerView.findViewById(R.id.pulldown_header_date);
		
		footerView = LayoutInflater.from(context).inflate(R.layout.pulldown_footer, null);
		footerTextView = (TextView)footerView.findViewById(R.id.pulldown_footer_text);
		footerLoadingView = footerView.findViewById(R.id.pulldown_footer_loading);
		footerLoadingView.setVisibility(View.VISIBLE);
		footerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isGetingMore)
				{
					isGetingMore = true;
					footerLoadingView.setVisibility(View.VISIBLE);
					myOnPullListener.GetMore();
				}
			}
		});
		
		rotate0To180Animation = new RotateAnimation(0, 180, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate0To180Animation.setDuration(250);
		rotate0To180Animation.setFillAfter(true);
		
		rotate180To0Animation = new RotateAnimation(180, 0, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, 
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate180To0Animation.setDuration(250);
		rotate180To0Animation.setFillAfter(true);
		
		myListView = new MyScrollListView(context);
		myListView.setOnScrollListViewListener(this);
		myListView.setCacheColorHint(color.white);
		addView(myListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		myOnPullListener = new onPullListener() {
			
			@Override
			public void Refresh() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void GetMore() {
				// TODO Auto-generated method stub
				
			}
		};
		
	}
	
	private void checkHeaderViewState()
	{
		if(headerViewParams.height >= DEFAULT_HEADER_HEIGHT)
		{
			if(headerState == HEADER_STATE_OVER_HEIGHT) return;
			headerState = HEADER_STATE_OVER_HEIGHT;
			headerTextView.setText("松开可以刷新");
			headerArrowView.startAnimation(rotate0To180Animation);
		}
		else
		{
			if(headerState == HEADER_STATE_NOT_OVER_HEIGHT || headerState == HEADER_STATE_IDLE) return;
			headerState = HEADER_STATE_NOT_OVER_HEIGHT;
			headerTextView.setText("下拉可以刷新");
			headerArrowView.startAnimation(rotate180To0Animation);
		}
	}
	
	private void setHeaderHeight(int height)
	{
		headerIncrement = height;
		headerViewParams.height = height;
		headerView.setLayoutParams(headerViewParams);
	}
	
	private class HideHeaderTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isDown)
			{
				headerIncrement -= AUTO_INCREMENT;
				if(headerIncrement < 0)			//////
				{
					headerIncrement = 0;
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
					cancel();
					
				}
				else
				{
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
				}
			}
			else
			{
				cancel();
			}
		}
		
	}
	
	private class ShowHeaderTimerTask extends TimerTask
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isDown)
			{
				headerIncrement -= AUTO_INCREMENT;
				if(headerIncrement <= DEFAULT_HEADER_HEIGHT)			//////
				{
					headerIncrement = DEFAULT_HEADER_HEIGHT;
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
					if(!isRefreshing)
					{
						isRefreshing = true;
						viewHandler.sendEmptyMessage(MSG_WHAT_ON_REFRESH);
					}
					cancel();
					
				}
				else
				{
					viewHandler.sendEmptyMessage(MSG_WHAT_SET_HEADER_HEIGHT);
				}
			}
			else
			{
				cancel();
			}
		}
		
	}
	
	private void showFooterView()
	{
		if(myListView.getFooterViewsCount() == 0 && isFillScreen())
		{
			myListView.addFooterView(footerView);
			myListView.setAdapter(myListView.getAdapter());
		}
	}
	
	private boolean isFillScreen()////////
	{
		int firstVisiblePosition = myListView.getFirstVisiblePosition();
		int lastVisiblePosition = myListView.getLastVisiblePosition() - myListView.getFooterViewsCount();
		int visibleItemsCount = lastVisiblePosition - firstVisiblePosition + 1;
		int totalItemsCount = myListView.getCount() - myListView.getFooterViewsCount();
		Log.i("PDV","visibleCount: "+visibleItemsCount+", totalCount: "+ totalItemsCount);
		if(visibleItemsCount < totalItemsCount) return true;
//		int visibleLastItem = lastVisiblePosition + 1;
//		int lastItem = myListView.getCount() - myListView.getFooterViewsCount();
//		if(visibleLastItem < lastItem) return false;
		else return false;
	}
	
	private class myHandler extends Handler
	{
		public myHandler() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case MSG_WHAT_LOAD_DATA_DONE:
				headerViewParams.height = 0;
				headerLoadingView.setVisibility(View.GONE);
				headerTextView.setText("下拉可以刷新");
				headerViewDateView.setText("更新于: "+ dateFormat.format(new Date(System.currentTimeMillis())));
				headerViewDateView.setVisibility(View.VISIBLE);
				headerArrowView.setVisibility(View.VISIBLE);
				showFooterView();
				break;
			case MSG_WHAT_ON_REFRESH:
				headerArrowView.clearAnimation();
				headerArrowView.setVisibility(View.INVISIBLE);
				headerLoadingView.setVisibility(View.VISIBLE);
				myOnPullListener.Refresh();
				break;
			case MSG_WHAT_REFRESH_DONE:
				isRefreshing = false;
				headerState = HEADER_STATE_IDLE;
				headerArrowView.setVisibility(View.VISIBLE);
				headerLoadingView.setVisibility(View.GONE);
				headerTextView.setText("下拉可以刷新");
				headerViewDateView.setText("更新于: "+ dateFormat.format(new Date(System.currentTimeMillis())));
				setHeaderHeight(0);
				showFooterView();
				break;
			case MSG_WHAT_GET_MORE_DONE:
				isGetingMore = false;
				footerTextView.setText("还有更多哦~~亲");
				footerLoadingView.setVisibility(View.GONE);
				break;
			case MSG_WHAT_SET_HEADER_HEIGHT:
				setHeaderHeight(headerIncrement);
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	
	@Override
	public boolean onListViewTopAndPullDown(int deltaY) {
		// TODO Auto-generated method stub
		if(isRefreshing || myListView.getCount() - myListView.getFooterViewsCount() == 0) return false;
		int absDeltaY = Math.abs(deltaY);
		final int i = (int) Math.ceil((double)absDeltaY / 2);
		headerIncrement += i;
		if(headerIncrement > 0)
		{
			setHeaderHeight(headerIncrement);
			checkHeaderViewState();
		}
		return true;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int deltaY) {
		// TODO Auto-generated method stub
		if(isGetingMore) return false;
		if(isFillScreen())
		{
			isGetingMore = true;
			footerLoadingView.setVisibility(View.VISIBLE);
			footerTextView.setText("加载中...");
			myOnPullListener.GetMore();
		}
		return true;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev) {
		// TODO Auto-generated method stub
		isDown = true;
		isPullBackDone = false;
		motionDownY = ev.getY();
		return false;
	}

	@Override
	public boolean onMotionMove(MotionEvent ev, int deltaY) {
		// TODO Auto-generated method stub
		if(isPullBackDone) return true;
		
		int absMotionY = (int) Math.abs(ev.getY() - motionDownY);
		if(absMotionY < PULL_DISTANCE_LIMIT) return true;
		
		int absDeltaY = Math.abs(deltaY);
		int i = (int) Math.ceil((double)absDeltaY/2);
		
		if(headerViewParams.height > 0 && deltaY < 0)
		{
			headerIncrement -= i;
			if(headerIncrement > 0)
			{
				setHeaderHeight(headerIncrement);
				checkHeaderViewState();
			}
			else
			{
				headerState = HEADER_STATE_IDLE;
				headerIncrement = 0;
				setHeaderHeight(headerIncrement);
				isPullBackDone = true;				
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev) {
		// TODO Auto-generated method stub
		isDown = false;
		
		if( headerIncrement > 0)
		{
			int deltaY = headerIncrement - DEFAULT_HEADER_HEIGHT;
			Timer timer = new Timer();
			if(deltaY < 0)
			{
				timer.schedule(new HideHeaderTimerTask(), 0, 20);
			}
			else
			{
				timer.schedule(new ShowHeaderTimerTask(), 0, 20);
			}
			return true;
		}
		return false;
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return false;
//	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i("PullUD", "onTouchEvent: event.action: "+ event.getAction());
		return super.onTouchEvent(event);
	}

}
