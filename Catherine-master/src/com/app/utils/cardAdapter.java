package com.app.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.app.addActivityPack.CircularImage;
import com.app.catherine.R;
import com.app.ui.loginAndreg;
import com.app.ui.menu.MyEvents.EventMainPage;


import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class cardAdapter extends BaseAdapter
{
	private static final String TAG = "cardAdapter";
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater mInflater;
	
	private Context context;
	private int resource;
	private String from[];
	private int to[];	
	private int screenW;
	private int toAvatar[];
	
	public cardAdapter() {
		// TODO Auto-generated constructor stub
		Log.e(TAG, "cardAdapter constructor error: no params");
	}
	
	public cardAdapter(Context context, ArrayList<HashMap<String, Object>> list, int resource,
			String []from, int []to, int screenW, int []toAvatar) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.resource = resource;
		this.from = from;
		this.to = to;
		this.screenW = screenW;
		
		this.toAvatar = toAvatar;
	}
	
	private void SetContentWidth(View main, View v)
    {
		//reference
		View dateView = main.findViewById(R.id.activityDate);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 100 * dateW / 55;
    	int screenWidth = screenW;
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = screenWidth - delPix;
		v.setLayoutParams(params);
	}
	
	private void SetUserInfoWidth(View main, View v)
    {
		//reference
		View dateView = main.findViewById(R.id.activityDate);
		RelativeLayout.LayoutParams paramsdate = (RelativeLayout.LayoutParams)dateView.getLayoutParams();
		int dateW = paramsdate.width;

		int delPix = 40 * dateW / 55;
    	int screenWidth = screenW;
    	
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)v.getLayoutParams();
		params.width = screenWidth - delPix;
		v.setLayoutParams(params);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {
		// TODO Auto-generated method stub
		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) 
	{

		// TODO Auto-generated method stub
		if (view==null) {
			view = mInflater.inflate(resource, null);
		}
		
		//set text or set something else about the view
		init(view, position);
		for (int i = 0; i < from.length; i++) 
		{
			TextView Title = (TextView)view.findViewById( to[i] );
			Title.setText( (String)list.get(position).get( from[i] ) );
		}
		
		final int pos = position;
		
		
		//move to MyEvents.java
//		view.setOnClickListener( new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				HashMap<String, Object> EventItem = list.get(pos);			
//				
//				Intent intent = new Intent();
//				intent.setClass(context, EventMainPage.class);
//					intent.putExtra("theme", (String)EventItem.get("title"));
//					intent.putExtra("location", (String)EventItem.get("location"));
//					intent.putExtra("participantsNum", (String)EventItem.get("participantsNum"));
//					intent.putExtra("launcher", (String)EventItem.get("launcher"));
//					intent.putExtra("remark", (String)EventItem.get("remark"));
//					intent.putExtra("date", (String)EventItem.get("date"));			
//					intent.putExtra("photolistJsonArray", EventItem.get("photolistJsonArray").toString());
//					
//					intent.putExtra("id", (Integer)EventItem.get("id"));
//					intent.putExtra("event_id", (Integer)EventItem.get("event_id"));
//				context.startActivity(intent);
//			}
//		} );
		
		return view;
	}
	
	
	
	private void init(View view, int pos)
	{
		CircularImage join = (CircularImage)view.findViewById(R.id.joinBtn);
		join.setImageResource(R.drawable.join);

		HashMap<String, Object> item = list.get(pos);
		JSONArray avatarJsonArray = (JSONArray) item.get("photolistJsonArray");
		
		int length = avatarJsonArray.length();
		try {
			int i=0;
			for ( ; i < length; i++) 
			{
				int id = avatarJsonArray.getInt(i);
				if( imageUtil.fileExist(id) )       //������ͷ����ñ���ͷ��
				{
					CircularImage photo = (CircularImage)view.findViewById( toAvatar[i] );
					Bitmap bitmap = imageUtil.getLocalBitmapBy(id);
					photo.setImageBitmap(bitmap);
				}
				else  									//ûͷ����Ĭ��ͷ��
				{
					CircularImage photo = (CircularImage)view.findViewById( toAvatar[i] );
					photo.setImageResource(R.drawable.defaultavatar);
				}
			}
			//����ûͷ��ĳ�ԱҲ��ΪĬ��ͷ��
			for( ; i<4; i++)
			{
				CircularImage photo = (CircularImage)view.findViewById( toAvatar[i] );
				photo.setImageResource(R.drawable.defaultavatar);
			}
		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		View activityInfoAllView = view.findViewById(R.id.activityInfoAll);
		SetContentWidth(view, activityInfoAllView);
		View userInfoView = view.findViewById(R.id.userInfo);
		SetUserInfoWidth(view, userInfoView);
		
		join.setOnClickListener(BtnListener);
		view.findViewById(R.id.comment_btn).setOnClickListener(BtnListener);
		view.findViewById(R.id.takephoto_btn).setOnClickListener(BtnListener);
	}
	
	private OnClickListener BtnListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch ( v.getId() ) {
			case R.id.joinBtn:
				Toast.makeText(context, "join", Toast.LENGTH_SHORT).show();
				break;
			case R.id.comment_btn:
				Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
				break;
			case R.id.takephoto_btn:
				Toast.makeText(context, "take photo", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};

}
