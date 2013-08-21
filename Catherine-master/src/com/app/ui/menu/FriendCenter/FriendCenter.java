package com.app.ui.menu.FriendCenter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.adapters.AdapterForFriendList;
import com.app.addFriendPack.searchFriend;
import com.app.catherine.R;
import com.app.localDataBase.FriendStruct;
import com.app.localDataBase.TableFriends;
import com.app.ui.NotificationCenter;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;
import com.app.widget.LetterSidebar;
import com.app.widget.LetterSidebar.OnTouchingLetterChangedListener;


public class FriendCenter {

	private final String TAG = "FriendCenter";
	private Context context;
	private View friendCenterView;
	private View friendNotificationView;
//	private Button recommendedFriendsBtn;
//	private Button notificationBtn;
	private EditText searchEditText;
	private ListView functionsListView;
	private ListView friendListView;
	private LetterSidebar sidebar;
	
	private int userId = -1;
	private Handler uiHandler;
	private myHandler fcHandler = new myHandler();
	private static final int MSG_WHAT_ON_UPDATE_LIST = -1;
	
	ArrayList<FriendStruct> friends;
	ArrayList<HashMap<String, Object>> functionsList = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String, Object>> friendList = new ArrayList<HashMap<String,Object>>();
	AdapterForFriendList friendListAdapter, functionsAdapter;
	HashMap<String, Integer> alphaIndex = new HashMap<String, Integer>();
	Comparator<Object> chinese_Comparator = Collator.getInstance(Locale.CHINA);
//	Comparator<HashMap> myComparator = new Comparator<HashMap>() {
//		
//		@Override
//		public int compare(HashMap arg0, HashMap arg1) {
//			// TODO Auto-generated method stub
//			String name0 = arg0.get("fname").toString();
//			String name1 = arg1.get("fname").toString();
//			return chinese_Comparator.compare(name0, name1);
//		}
//	};
	
	private PinYinComparator myPinYinComparator = new PinYinComparator();
	
	public FriendCenter(Context context, View friendsCenterView, Handler uiHandler, int userId) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friendCenterView = friendsCenterView;
		this.userId = userId;
		this.uiHandler = uiHandler;
	}
	
	
	public void init() {
		// TODO Auto-generated method stub
	    new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setLayout();
                askServerForFriendList();
            }        
        }).start();
	}
	
	public void setLayout()
	{
		friendNotificationView = LayoutInflater.from(context).inflate(R.layout.friend_center_notification, null);
//		recommendedFriendsBtn = (Button)friendCenterView.findViewById(R.id.menu_friend_center_recommendfriendBtn);
//		notificationBtn = (Button)friendCenterView.findViewById(R.id.menu_friend_center_notificationBtn);
		searchEditText = (EditText)friendCenterView.findViewById(R.id.menu_friend_center_searchmyfriend);
		
		searchEditText.setOnClickListener(editTextOnClickListener);
		searchEditText.setLongClickable(false);
		functionsListView = (ListView)friendCenterView.findViewById(R.id.menu_friend_center_functions);
		functionsListView.setDivider(null);
		functionsAdapter = new AdapterForFriendList(context, functionsList, 
	            R.layout.friend_list_item, 
	            new String[] {"avatar", "fname"}, 
	            new int[] {R.id.friend_list_item_avatar, R.id.friend_list_item_fname});
		functionsListView.setOnItemClickListener(functionsListListener);
		friendListView = (ListView)friendCenterView.findViewById(R.id.menu_friend_center_friendlist);
		friendListView.setDivider(null);
		friendListAdapter = new AdapterForFriendList(context, friendList, 
				R.layout.friend_list_item, 
				new String[] {"avatar", "fname"}, 
				new int[] {R.id.friend_list_item_avatar, R.id.friend_list_item_fname});
		functionsListView.setAdapter(functionsAdapter);
		friendListView.setAdapter(friendListAdapter);
		friendListView.setOnScrollListener(friendListScrollListener);
		sidebar=(LetterSidebar)friendCenterView.findViewById(R.id.lettersidebar);
        sidebar.setOnTouchingLetterChangedListener(letterChangedListener);
        
        initFunctionsList();
	}
	
	OnScrollListener friendListScrollListener = new OnScrollListener() {
        
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            if ( visibleItemCount > 0 )
            {
                HashMap<String, Object> map = (HashMap<String, Object>)friendListAdapter.getItem(firstVisibleItem);
                if (map != null)
                {
                    int letter = PinYinComparator.getPinYin((String)(map.get("fname"))).toUpperCase().charAt(0) - 'A';
                    sidebar.OnScrollChangedLetter(letter);
                }
            }
        }
    };
	
	OnItemClickListener functionsListListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // TODO Auto-generated method stub
            if (arg2 == 0)
            {
//              Intent intent1 = new Intent();
//              intent1.setClass(context, searchFriend.class);
//              intent1.putExtra("userId", userId);
//              context.startActivity(intent1);
            }
            else if (arg2 == 1) 
            {
                Intent intent2 = new Intent();
                intent2.setClass(context, NotificationCenter.class);
                intent2.putExtra("userId", userId);
                context.startActivity(intent2);
            }
            else {
                Log.i("Friend Center", "error position in functions list");
            }
        }
	    
	};

	OnTouchingLetterChangedListener letterChangedListener = new OnTouchingLetterChangedListener() {
	   
	    @Override
        public void onTouchingLetterChanged(String s) {
            // TODO Auto-generated method stub
            Log.i("Letter Sidebar letter is : ", s);
            //int position = s.charAt(0) - 'A';
            //Log.i("Letter Sidebar position is : ", position + "");
            Integer position = alphaIndex.get(s);
            if (position != null)
            {
                friendListView.setSelection(position);
            }
        }
	};
	
OnClickListener editTextOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            searchEditText.setFocusable(true);
            searchEditText.setFocusableInTouchMode(true);
        }
};
	
	
	public void initFunctionsList()
	{
	    String[] functionTitle = { "好友推荐", "消息中心" };
	    for (String title : functionTitle)
	    {
	        HashMap<String, Object> map = new HashMap<String, Object>();
	        map.put("fname", title);
	        map.put("fid", 0);
	        functionsList.add(map);
	    }
	}
	
	public void askServerForFriendList()
	{
		TableFriends tbFriends = new TableFriends(context);
		JSONObject params = new JSONObject();
		try {
			params.put("id", userId);
			params.put("friends_number", tbFriends.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSender httpSender = new HttpSender();
		httpSender.Httppost(OperationCode.SYNCHRONIZE, params, fcHandler);
	}
	
	public void showFriendList()
	{
	    friendList.clear();
        int[] alpha_counter = new int[26];
        TableFriends tf = new TableFriends(context);
        friends = tf.getAllFriends(userId+"");
        if(friends.size() == 0)
        {
            Toast.makeText(context, "你暂时还没有好友哦", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (final FriendStruct fs : friends)
            {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("uid", userId);
                    map.put("fname", fs.fname);
//                    int gender = Integer.parseInt(fs.gender);
//                    if(gender == 1)
//                    {
//                        map.put("gender", "男");
//                    }
//                    else
//                    {
//                        map.put("gender", "女");
//                    }
//                    map.put("gender", fs.gender);
//                    map.put("email", fs.email);
                    map.put("fid",fs.fid);
                    friendList.add(map);
                     
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                             if (!imageUtil.fileExist(fs.fid))
                             {
                                 retrieve_avatar(fs.fid);
                             }
                        }
                       
                    }).start();
            }
        }
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Collections.sort(friendList, myPinYinComparator);
                insertLetterTag();
                Message msg = fcHandler.obtainMessage(MSG_WHAT_ON_UPDATE_LIST);
                msg.sendToTarget();
            }
           
        }).start();
			
		friendListAdapter.notifyDataSetChanged();
		
//		friendListAdapter = new AdapterForFriendList(this, friendList, 
//				R.layout.friend_list_item, 
//				new String[] {"fname","gender","email"}, 
//				new int[] {R.id.friend_list_item_fname,R.id.friend_list_item_gender,R.id.friend_list_item_email});
//		
//		friendListView.setAdapter(friendListAdapter);
	}
	
	public void insertLetterTag()
	{
	    if (friendList == null)
	    {
	        Log.i("insertLetterTag: ","friendList is null");
	        return;
	    }
	    alphaIndex.clear();
	    ArrayList<HashMap<String, Object>> tempList = new ArrayList<HashMap<String,Object>>();
	    String currentTag = "";
	    String tempTag;
	    for (HashMap<String, Object> hMap : friendList)
	    {
	        tempTag = PinYinComparator.getPinYin((String)hMap.get("fname")).charAt(0) + "";
	        tempTag = tempTag.toUpperCase();
	        if (tempTag != currentTag)
	        {
	            currentTag = tempTag;
	            HashMap<String, Object> tempMap = new HashMap<String, Object>();
	            tempMap.put("fid", -1);
	            tempMap.put("fname", currentTag);
	            int pos = tempList.size();
	            alphaIndex.put(currentTag, pos);
	            tempList.add(tempMap);
	        }
	        tempList.add(hMap);
	    }
	    friendList.clear();
	    friendList.addAll(tempList);
	}
	
	public void sychronizeFriendsList(Message msg)
	{
		try{
			TableFriends tbFriends = new TableFriends(context);
			ArrayList<FriendStruct> friends = new ArrayList<FriendStruct>();
			JSONObject jsResponse = new JSONObject(msg.obj.toString());
			int cmd = jsResponse.getInt("cmd");
			Log.i(TAG, "同步好友收到的json: "+jsResponse);
			if(cmd == ReturnCode.NORMAL_REPLY)
			{
				JSONArray jsArray = jsResponse.getJSONArray("friend_list");
				int length = jsArray.length();
				if(length == 0)
				{
					Toast.makeText(context, "好友列表已经最新，不需要同步", Toast.LENGTH_SHORT).show();
				}
				else
				{
					tbFriends.deleteFriendsTable();
					for(int i=0;i<length;i++)
					{
						JSONObject jo = jsArray.getJSONObject(i);
						jo.put("uid", userId);
						Log.i(TAG, "一个好友的信息: "+jo.toString());
						FriendStruct fs = new FriendStruct();
						fs = FriendStruct.getFromJSON(jo);
//						fs.uid = userId;
						friends.add(fs);
					}
					tbFriends.add(friends);	//add to the friends table of the local database
					Toast.makeText(context, "好友列表同步成功", Toast.LENGTH_SHORT).show();
				}
			}
			else if(cmd == ReturnCode.SERVER_FAIL)
			{
				Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(context, "你已经被外星人劫持~", Toast.LENGTH_SHORT).show();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void retrieve_avatar(int uid)
    {
        JSONObject params = new JSONObject();
        try
        {
            params.put("id", uid);
            params.put("operation", 0);
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, fcHandler);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void writeAvatar(String str)
    {
        JSONObject returnJson;
        try {
            returnJson = new JSONObject(str );
            if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
            {
                String returnStr = returnJson.getString("avatar");  
                byte[] temp = imageUtil.String2Bytes(returnStr);
                if(temp!=null)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                    imageUtil.savePhoto(returnJson.getInt("id"), bitmap);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Message msg = fcHandler.obtainMessage(MSG_WHAT_ON_UPDATE_LIST);
        msg.sendToTarget();

    }
	
	public static class PinYinComparator implements Comparator<HashMap<String,Object>>
	{

		@Override
		public int compare(HashMap<String,Object> arg0, HashMap<String,Object> arg1) {
			// TODO Auto-generated method stub
			String pinYinName0 = getPinYin(arg0.get("fname").toString());
			String pinYinName1 = getPinYin(arg1.get("fname").toString());
			return pinYinName0.compareTo(pinYinName1);
		}
		
		
		public static String getPinYin(String inputString) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
 
            char[] input = inputString.trim().toCharArray();// 把字符串转化成字符数组
            String output = "";
 
            try {
                for (int i = 0; i < input.length; i++) {
                    // \\u4E00是unicode编码，判断是不是中文
                    if (java.lang.Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        // 将汉语拼音的全拼存到temp数组
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        // 取拼音的第一个读音
                        output += temp[0];
                    }
                    // 大写字母转化成小写字母
                    else if (input[i] > 'A' && input[i] < 'Z') {
                        output += java.lang.Character.toString(input[i]);
                        output = output.toLowerCase();
                    }
                    output += java.lang.Character.toString(input[i]);
                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return output;
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
			case OperationCode.SYNCHRONIZE:
				sychronizeFriendsList(msg);
//				showFriendList();
				break;
			case OperationCode.GET_AVATAR:
                final String final_mes = msg.obj.toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    writeAvatar(final_mes);
                }
                }).start();
                break;
            case MSG_WHAT_ON_UPDATE_LIST:
                friendListAdapter.notifyDataSetChanged();
                break;
				default: break;
			}
			super.handleMessage(msg);
		}
	}
	
	public View getFriendCenterView()
	{
		return friendCenterView;
	}
	
	public View getFriendNotificationView()
	{
		return friendNotificationView;
	}
	
	
}
