package com.app.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.adapters.AdapterForFriendList.ViewHolder;
import com.app.catherine.R;
import com.app.localDataBase.NotificationTableAdapter;
import com.app.localDataBase.TableFriends;
import com.app.localDataBase.notificationObject;
import com.app.ui.UserInterface;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;

public class MessagaAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<notificationObject> list;
    private int layoutID;
    private int userId;
    private myHandler mHandler;
    
    public MessagaAdapter(Context context, 
            ArrayList<notificationObject> list,
            int uid)
    {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.userId = uid;
        mHandler = new myHandler();
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (list.size() == 0 || position < 0 || position > list.size() )
        {
            return null;
        }
        else {
            return list.get(position);
        }
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
        notificationObject item = list.get(position);
        JSONObject jo = null;
        int noticeStringId = -1;
        int type = -1;
        int fid = -1;
        final int item_id = item.item_ID;
        try {
            jo = new JSONObject(item.msg);
            type = jo.getInt("cmd");
            fid = jo.getInt("id");
            
            switch (type) {
            case ReturnCode.ADD_FRIEND_NOTIFICATION:
                layoutID = R.layout.friend_requests;
                noticeStringId = R.string.request_for_friend;
                break;
            case ReturnCode.ADD_FRIEND_RESULT:
                layoutID = R.layout.friend_requests;
                if (jo.getBoolean("result")) {
                    noticeStringId = R.string.pass_friend_request;
                }
                else {
                    noticeStringId = R.string.refuse_friend_request;
                }
                break;
            default:
                layoutID = -1;
                noticeStringId = -1;
                break;
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if (layoutID == -1 || noticeStringId == -1)
        {
            return null;
        }
        if(convertView == null)
        {
            convertView = mInflater.inflate(layoutID, null);
            viewHolder = new ViewHolder();
            viewHolder.fname = (TextView)convertView.findViewById(R.id.friend_name);
            viewHolder.confirm_msg = (TextView)convertView.findViewById(R.id.confirm_msg);
            viewHolder.notice = (TextView)convertView.findViewById(R.id.valication_result);
            viewHolder.avatar = (ImageView)convertView.findViewById(R.id.nc_avatar);
            viewHolder.leftBtn = (Button)convertView.findViewById(R.id.nc_pass);
            viewHolder.rightBtn = (Button)convertView.findViewById(R.id.nc_refuse);
            
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        try {
            viewHolder.fname.setText(jo.getString("name"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (imageUtil.fileExist(fid))
        {
            viewHolder.avatar.setImageBitmap(imageUtil.getLocalBitmapBy(fid));
        }
        else 
        {
            viewHolder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultavatar));    
        }
        try {
            viewHolder.confirm_msg.setText(jo.getString("confirm_msg"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        viewHolder.notice.setText(noticeStringId);
        
        final int final_type = type;
        final int final_fid = fid;
        OnClickListener passOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (final_type) {
                case ReturnCode.ADD_FRIEND_NOTIFICATION:
                    sendMessage(userId, final_fid, 998, 1, item_id);
                    break;
                default:
                    break;
                }
            }
        };
        
        OnClickListener refuseOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (final_type) {
                case ReturnCode.ADD_FRIEND_NOTIFICATION:
                    sendMessage(userId, final_fid, 998, 0, item_id);
                    break;
                default:
                    break;
                }
            }
        };
        
        OnClickListener deleteOnClickListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mHandler.removeItem(item_id);
                MessagaAdapter.this.notifyDataSetChanged();
            }
        };
        
        switch (type) {
        case ReturnCode.ADD_FRIEND_NOTIFICATION:
            viewHolder.leftBtn.setOnClickListener(passOnClickListener);
            viewHolder.leftBtn.setVisibility(View.VISIBLE);
            viewHolder.leftBtn.setText(R.string.pass);
            viewHolder.rightBtn.setOnClickListener(refuseOnClickListener);
            viewHolder.rightBtn.setVisibility(View.VISIBLE);
            viewHolder.rightBtn.setText(R.string.refuse);
            break;
        case ReturnCode.ADD_FRIEND_RESULT:
            viewHolder.leftBtn.setVisibility(View.GONE);
            viewHolder.rightBtn.setOnClickListener(deleteOnClickListener);
            viewHolder.rightBtn.setVisibility(View.VISIBLE);
            viewHolder.rightBtn.setText(R.string.delete);
            break;
        default:
            
            break;
        }
          
        return convertView;
    }
    
    private void sendMessage(int uid,int fid, int cmd, int result, int item_id)
    {
        JSONObject params = new JSONObject();
        try {
            params.put("id", uid);
            params.put("friend_id", fid);
            params.put("cmd", cmd);
            params.put("item_id", item_id);
            if(result==1)
                params.put("result", true);
            else 
                params.put("result", false);
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("FriendsCenter", "我同意或者拒绝时，给服务器发送的是："+params.toString());
        HttpSender http = new HttpSender();
        http.Httppost(OperationCode.ADD_FRIEND, params, mHandler);
    }
    
    static class ViewHolder
    {
        TextView fname;
        TextView confirm_msg;
        TextView notice;
        ImageView avatar;
        Button leftBtn, rightBtn;
        int type;
        
        public ViewHolder()
        {
            fname = null;
            confirm_msg = null;
            notice = null;
            avatar = null;
            leftBtn = null;
            rightBtn = null;
            type = 0;
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
            super.handleMessage(msg);
            switch(msg.what)
            {
            case OperationCode.ADD_FRIEND:
                try {
                    JSONObject jo = new JSONObject(msg.obj.toString());
                    if (ReturnCode.NORMAL_REPLY == jo.getInt("cmd"))
                    {                        
                        //更新显示信息
                        removeItem(jo.getInt("item_id"));
                        MessagaAdapter.this.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case OperationCode.PARTICIPATE_EVENT:
                break;
                default: break;
            }
        }
        
        public void removeItem(int item_id)
        {
          //删除消息数据表
            NotificationTableAdapter adapter = new NotificationTableAdapter(context);
            adapter.deleteData( item_id );
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).item_ID == item_id)
                {
                    list.remove(i);
                    break;
                }
            }
        }
    }
    
    
}

