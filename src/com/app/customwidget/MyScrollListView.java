package com.app.customwidget;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyScrollListView extends ListView{

	private int motionLastX;
	private int motionLastY;
	private int listTopPosition;//列表正数第listTopPosition位置
	private int listBottomPosition; //列表倒数第listTopPosition位置
	public MyScrollListView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	
	private void init() {
		// TODO Auto-generated method stub
		listTopPosition = 0;
		listBottomPosition = 1;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		boolean forReturn = true;
		final int motionCurrentY = (int)ev.getY();
		final int motionCurrentX = (int) ev.getX();
//		int deltaX = 0;
//		int deltaY = 0;
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
			Log.i("MyScrollListView","ACTION_DOWN");
//			int deltaX = motionCurrentX - motionLastX;
			
			motionLastY = motionCurrentY;
			motionLastX = motionCurrentX;
			boolean isMotionDown = myScrollListViewListner.onMotionDown(ev);
//			if(isMotionDown)
//			{
//				motionLastX = motionCurrentX;
//				motionLastY = motionCurrentY;
//				return isMotionDown;
//			}
			forReturn = true;
//			return true;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i("MyScrollListView","ACTION_MOVE");
			
			final int childCount = getChildCount();//当前页面可见的child的个数
			if(childCount == 0) return super.onTouchEvent(ev);
			
			int firstItemTop = getChildAt(0).getTop();
			int lastItemBottom = getChildAt(childCount-1).getBottom();
			
			float listBegin = getListPaddingTop();
			float listEnd = getHeight() - getListPaddingBottom();
			
			int totalItemCount = getAdapter().getCount() - listBottomPosition;
//			deltaX = motionCurrentX - motionLastX;
			int deltaY = motionCurrentY - motionLastY;
//			Log.i("SLV", "Move: dx= "+ deltaX + ", dy = "+ deltaY);
//			if( Math.abs(deltaX) < Math.abs(deltaY))
//			{
			int firstVisiblePosition = getFirstVisiblePosition();
			Log.i("MyScrollListView", "dy = "+deltaY);
			boolean isMotionMoveHandled = myScrollListViewListner.onMotionMove(ev, deltaY);
			boolean isOnListOnTopAndPullDownHandled;
			boolean isOnListOnBottomAndPullUpHandled;
			
			if(firstVisiblePosition <= listTopPosition && firstItemTop >= listBegin && deltaY > 0)
			{
				Log.i("MyScrollListView", "---------------Pull Down");
				isOnListOnTopAndPullDownHandled = myScrollListViewListner.onListViewTopAndPullDown(deltaY);
				motionLastX = motionCurrentX;
				motionLastY = motionCurrentY;
//				return isMotionMoveHandled && isOnListOnTopAndPullDownHandled;
			}
			
			if(firstVisiblePosition + childCount >= totalItemCount && lastItemBottom <= listEnd && deltaY < 0)
			{
				Log.i("MyScrollListView", "---------------Pull Up");
				isOnListOnBottomAndPullUpHandled = myScrollListViewListner.onListViewBottomAndPullUp(deltaY);
				motionLastX = motionCurrentX;
				motionLastY = motionCurrentY;
//				return isMotionMoveHandled && isOnListOnBottomAndPullUpHandled;
			}
//			}
			forReturn = false;
			break;
		case MotionEvent.ACTION_UP:
			Log.i("MyScrollListView","ACTION_UP");
//			deltaX = motionCurrentX - motionLastX;
//			deltaY = motionCurrentY - motionLastY;
//			Log.i("SLV", "Up: dx= "+ deltaX + ", dy = "+ deltaY);
//			if( Math.abs(deltaX) < Math.abs(deltaY))
//			{
//			motionLastX = motionCurrentX;
//			motionLastY = motionCurrentY;
			
			boolean isMotionUp = myScrollListViewListner.onMotionUp(ev);
//			if(isMotionUp)
//			{
//				motionLastY = motionCurrentY;
//				return isMotionUp;
//			}
//			return true;
//			}
//			else
//			{
//			motionLastX = motionCurrentX;
//			motionLastY = motionCurrentY;
//			return false;
//			}
			forReturn = false;
			break;
			default: break;
		}
		motionLastY = motionCurrentY;
		super.onTouchEvent(ev);
//		forReturn = super.onTouchEvent(ev);
//		Log.i("MyScrollListView","onTouchEvent(ev): "+forReturn);
		Log.i("MyScrollListView", "forReturn: "+ forReturn);
//		return super.onTouchEvent(ev);
		
		return forReturn;
	}
	
	/**
	 * @author WJL
	 * 设置MyScrollListView的监听器，暂时为空
	 */
	onScrollListViewListener myScrollListViewListner = new onScrollListViewListener() {
		
		@Override
		public boolean onMotionUp(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionMove(MotionEvent ev, int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionDown(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewTopAndPullDown(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewBottomAndPullUp(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	public void setOnScrollListViewListener(onScrollListViewListener listener)
	{
		myScrollListViewListner = listener;
	}
	
	/**
	 * @author WJL
	 * MyScrollListView 的监听器接口
	 */
	public interface onScrollListViewListener
	{
		/**
		 * @author WJL
		 * 从顶部下拉的时候触发
		 * @param deltaY
		 * @return
		 */
		boolean onListViewTopAndPullDown(int deltaY);
		
		/**
		 * @author WJL
		 * 从底部上拉的时候触发
		 * @param deltaY
		 * @return
		 */
		boolean onListViewBottomAndPullUp(int deltaY);
		
		/**
		 * @author WJL
		 * 当在屏幕按下的时候触发
		 * @param ev
		 * @return
		 */
		boolean onMotionDown(MotionEvent ev);
		
		/**
		 * @author WJL
		 * 当在屏幕上滑动的时候触发
		 * @param ev
		 * @param deltaY
		 * @return
		 */
		boolean onMotionMove(MotionEvent ev, int deltaY);
		
		/**
		 * @author WJL
		 * 当从屏幕上抬起的时候触发
		 * @param ev
		 * @return
		 */
		boolean onMotionUp(MotionEvent ev);
	}

}
