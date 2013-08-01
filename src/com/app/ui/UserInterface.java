package com.app.ui;

import com.app.catherine.R;
import com.app.catherine.R.id;
import com.app.catherine.R.layout;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.GestureDetector;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnDrawListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class UserInterface extends Activity implements OnTouchListener,
GestureDetector.OnGestureListener
{
	private static final int MENU_CLICKED = -2;
	private LinearLayout contentLayout;
	private LinearLayout menuLayout;
	private LinearLayout UILayout;
	private Button menuButton;
	private GestureDetector UIGestureDetector;
	private int window_width;
	private static float FLIP_DISTANCE_X = 400;
	private int speed = 30;
	private int menu_width = 0;
	private int mScrollX;
	private boolean isScrolling = false;
	private boolean isFinish = true;
	private boolean isMenuOpen = false;
	private boolean hasMeasured = false;
	
	private Menu UI_Menu;
	private myHandler uiHandler = new myHandler();
	
	
	private Button myEvents;
	private Button privateEvents;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ui, null);
		setContentView(v);
		init();
		setLayout();
		UI_Menu = new Menu(getApplicationContext(),v,uiHandler);
		UI_Menu.setMenu();
		
	}
	
	public void init()
	{
		contentLayout = (LinearLayout)findViewById(R.id.ui_content);
		menuLayout = (LinearLayout)findViewById(R.id.ui_menu);
		UILayout = (LinearLayout)findViewById(R.id.ui_myui);
		menuButton = (Button)findViewById(R.id.ui_content_menuBtn);
		
		menuButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				jump();
			}
		});
	}
	
	public void jump()
	{
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
		if(layoutParams.leftMargin>= 0)
		{
			new AsynMove().execute(-speed);
		}
		else
		{
			new AsynMove().execute(speed);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setLayout()
	{
		UILayout.setOnTouchListener(this);
		UIGestureDetector = new GestureDetector(this);
		UIGestureDetector.setIsLongpressEnabled(false);
		getMaxX();
	}
	
	//单位从dip转化成px
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	
	//单位从px转化成dip
	public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
	
	public void getMaxX()
	{
		ViewTreeObserver viewTreeObserver = UILayout.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				if(!hasMeasured)
				{
					window_width = getWindowManager().getDefaultDisplay().getWidth();
					RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
					LinearLayout.LayoutParams layoutParams_content = (LinearLayout.LayoutParams)contentLayout.getLayoutParams();
					LinearLayout.LayoutParams layoutParams_menu = (LinearLayout.LayoutParams)menuLayout.getLayoutParams();
//					
//					layoutParams_menu.width = (int) (window_width*0.6);
					layoutParams_menu.width = dip2px(UserInterface.this, 200);
					Log.i("myUI", "dp width: "+layoutParams_menu.width);
					menuLayout.setLayoutParams(layoutParams_menu);
					
					menu_width = layoutParams_menu.width;
					layoutParams_UI.width = window_width+menu_width;
					layoutParams_UI.leftMargin = -menu_width;
					UILayout.setLayoutParams(layoutParams_UI);
					
					layoutParams_content.width = window_width;
					contentLayout.setLayoutParams(layoutParams_content);
					
					Log.i("myUI", "UI width: "+UILayout.getWidth());
					Log.i("myUI", "content width: "+contentLayout.getWidth());
//					UILayout.invalidate();
					
					hasMeasured = true;
					
				}
				return true;
			}
		});
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return UIGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		mScrollX = 0;
		isScrolling = false;
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
//		Log.i("myUI","onFlip: arg2:"+arg2+", arg3: "+arg3);
		int currentX = (int)arg1.getX();
		int lastX = (int)arg0.getX();
		if(isMenuOpen)
		{
			if(!isScrolling && currentX - lastX >= 0)
			{
				return false;
			}
		}
		else
		{
			if(!isScrolling && currentX - lastX <= 0)
			{
				return false;
			}
		}
		
		boolean speedEnough = false;
		if(arg2 > FLIP_DISTANCE_X || arg2 < -FLIP_DISTANCE_X)
		{
			speedEnough = true;
		}
		else
		{
			speedEnough = false;
		}
		
		doCloseScroll(speedEnough);
		return false;
	}

	private void doCloseScroll(boolean speedEnough) {
		// TODO Auto-generated method stub
		if(isFinish)
		{
			RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			int currentSpeed = this.speed;
			if(isMenuOpen)
			{
				currentSpeed = -currentSpeed;
			}
			
			if(speedEnough || (!isMenuOpen && (layoutParams_UI.leftMargin > window_width/2- menu_width))
					|| (isMenuOpen && layoutParams_UI.leftMargin < window_width/2 - menu_width))
			{
				new AsynMove().execute(currentSpeed);
			}
			else
			{
				new AsynMove().execute(-currentSpeed);
			}
		}
	}
	
	

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
//		Log.i("myUI","onScroll: arg2:"+arg2+", arg3: "+arg3);
		if(isFinish)
		{
			float distanceX = arg2;
			doScrolling(distanceX);
		}
		return true;
	}

	private void doScrolling(float distanceX) {
		// TODO Auto-generated method stub
		isScrolling = true;
		mScrollX += distanceX;// distanceX: negative for right, positive for left
		RelativeLayout.LayoutParams layoutParams_UI = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
		layoutParams_UI.leftMargin -= mScrollX;
//		layoutParams_UI.rightMargin += mScrollX;
		
		if(layoutParams_UI.leftMargin <= -menu_width)//向左拉过头
		{
			isScrolling = false;
			layoutParams_UI.leftMargin = -menu_width;
//			layoutParams_UI.rightMargin = 0;
		}
		else if(layoutParams_UI.leftMargin  >= 0)//向右拉过头
		{
			isScrolling = false;
			layoutParams_UI.leftMargin = 0;
		}
		UILayout.setLayoutParams(layoutParams_UI);
//		menuLayout.invalidate();
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
//		Log.i("myUI","onSingleTapUp");
//		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
//		if(layoutParams.leftMargin>= 0)
//		{
//			new AsynMove().execute(-speed);
//		}
//		else
//		{
//			new AsynMove().execute(speed);
//		}
		return false;
	}
	
	class AsynMove extends AsyncTask<Integer, Integer, Void>
	{

		@Override
		protected Void doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			isFinish = false;
			int times;
			times = menu_width/Math.abs(params[0]);
			for(int i=0; i<times; i++)
			{
				publishProgress(params[0]);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			isFinish = true;
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			if(layoutParams.leftMargin >= 0)
			{
				isMenuOpen = true;
			}
			else
			{
				isMenuOpen = false;
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)UILayout.getLayoutParams();
			if(values[0]>0)//右移
			{
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin + values[0], 0);
//				layoutParams.rightMargin = Math.max(layoutParams.rightMargin - values[0], -menu_width);
				
			}
			else//左移
			{
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin + values[0], -menu_width);
			}
			UILayout.setLayoutParams(layoutParams);
//			menuLayout.invalidate();
			super.onProgressUpdate(values);
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
			switch(msg.what)
			{
			case MENU_CLICKED:
				jump();
				break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}

}
