package com.app.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.catherine.R;
import com.app.localDataBase.TableFriends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdapterForFriendList extends BaseAdapter{

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> list;
	private int layoutID;
	private String key[];
	private int viewID[];
	
	public AdapterForFriendList(Context context, 
			ArrayList<HashMap<String, Object>> list, 
			int layoutId, String key[], 
			int viewId[] )
	{
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutId;
		this.key = key;
		this.viewID = viewId;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		final int pos = position;
		if(convertView == null)
		{
			convertView = mInflater.inflate(layoutID, null);
			viewHolder = new ViewHolder();
			viewHolder.fname = (TextView)convertView.findViewById(R.id.friend_list_item_fname);
			viewHolder.gender = (TextView)convertView.findViewById(R.id.friend_list_item_gender);
			viewHolder.email = (TextView)convertView.findViewById(R.id.friend_list_item_email);
			viewHolder.deleteBtn = (Button)convertView.findViewById(R.id.friend_list_item_deleteBtn);
			
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		int viewIdLength = viewID.length;
		HashMap<String, Object> item = list.get(pos);
		for(int i=0;i<viewIdLength;i++)
		{
			switch(viewID[i])
			{
			case R.id.friend_list_item_fname:
				viewHolder.fname.setText((String)item.get(key[i]));
				break;
			case R.id.friend_list_item_gender:
				viewHolder.gender.setText((String)item.get(key[i]));
				break;
			case R.id.friend_list_item_email:
				viewHolder.email.setText((String)item.get(key[i]));
				break;
				default:
					Log.v("test1", "nothing");
					break;
			}
		}
		OnClickListener deleteBtnListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String uid = list.get(pos).get("uid").toString();
				String fid = list.get(pos).get("fid").toString();
				TableFriends tf = new TableFriends(context);
				tf.delete(uid,fid);
				list.remove(pos);
				AdapterForFriendList.this.notifyDataSetChanged();
			}
		};
		viewHolder.deleteBtn.setOnClickListener(deleteBtnListener);
		
		
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView fname;
		TextView gender;
		TextView email;
		Button deleteBtn;
		
		public ViewHolder()
		{
			fname = null;
			gender = null;
			email = null;
			deleteBtn = null;
		}
	}
	

}
