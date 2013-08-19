package com.app.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes.Name;


import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.app.adapters.ListWheelAdapter;
import com.app.catherine.R;
import com.app.dataStructure.Area;
import com.app.dataStructure.City;
import com.app.dataStructure.State;
import com.app.utils.AreaXMLParser;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.utils.imageUtil;
import com.app.widget.AvatarDialog;
import com.app.widget.OnWheelChangedListener;
import com.app.widget.WheelView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings{
    private Activity activity;
    private View settingsView;
    private final String TAG = "imageupload";
    private String path;
    private Uri uri;
    private String imageStr;
    private ImageView avatar;
    private Button individualInfoBtn, accountInfoBtn;
    private boolean individualInfoState, accountInfoState;
    private EditText gender, location, description;
    private TextView myName, myEmail;
    private AvatarDialog avatarDialog;
    int userId;
    private MessageHandler handler;
    private boolean isFirstVisit;
    private ArrayList<State> stateList;
    public final int CASE_PHOTO = 0;
    public final int CASE_CAMERA = 1;
    
    public Settings(Activity activity, View settingsView, int userId) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.settingsView = settingsView;
        this.userId = userId;
        individualInfoState = false;
        accountInfoState = false;
        isFirstVisit = true;
        handler = new MessageHandler(Looper.myLooper());
        this.stateList = null;
        init();
    }
    
    public void setLayout()
    {
        avatar = (ImageView)settingsView.findViewById(R.id.avatar);
        avatar.setOnClickListener(avatarListener);
        individualInfoBtn = (Button)settingsView.findViewById(R.id.individual_info_bt);
        accountInfoBtn = (Button)settingsView.findViewById(R.id.account_info_bt);
        individualInfoBtn.setOnClickListener(modifyBtnListener);
        accountInfoBtn.setOnClickListener(modifyBtnListener);
        gender = (EditText)settingsView.findViewById(R.id.settings_gender);
        gender.setFocusable(false);
        gender.setFocusableInTouchMode(false);
        gender.setLongClickable(false);
        location = (EditText)settingsView.findViewById(R.id.settings_location);
        location.setFocusable(false);
        location.setFocusableInTouchMode(false);
        location.setLongClickable(false);
        location.setOnClickListener(locationOnClickListener);
        description = (EditText)settingsView.findViewById(R.id.settings_description);
        description.setFocusable(false);
        description.setFocusableInTouchMode(false);
        description.setLongClickable(false);
        myName = (TextView)settingsView.findViewById(R.id.settings_name);
        myEmail = (TextView)settingsView.findViewById(R.id.settings_email);

    }

    private void init() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                setLayout();
                
            }        
        }).start();
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (stateList == null)
                {
                    try {
                        stateList = AreaXMLParser.doParse(activity);
                    } catch (XmlPullParserException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("In Settings", "XmlPullParserException");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i("In Settings", "IOException");
                    }
                }
            }        
        }).start();
    }
    
    public void initData() {
        if (!isFirstVisit)
        {
            return;
        }
        JSONObject params = new JSONObject();
        try {
            params.put("id", userId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new HttpSender().Httppost(OperationCode.GET_USER_INFO, params, handler);
        try {
            params.put("operation", 0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (imageUtil.fileExist(userId))
        {
            Bitmap bitmap = imageUtil.getLocalBitmapBy(userId);
            int scale = UserInterface.dip2px(activity, 70);
            Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
            avatar.setImageBitmap(new_bitmap);
        }
        else
        {
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
        }
        isFirstVisit = false;
    }
    
    
    public void onAvatarsetFromPhoto(Intent data)
    {
        uri = data.getData();          
        Log.i(TAG, "uri: " + uri.toString());            
        getPath(uri);
        getImageStr(path);       
        uploadImage();
    }
    
    public void onAvatarsetFromCamera(Intent data)
    {
        Bundle extras = data.getExtras();
        Bitmap camera_bitmap = (Bitmap) extras.get("data");
        String saveFilePath = "";
      
        try {
            File sdCardDir = Environment.getExternalStorageDirectory();
            File filePath = new File(sdCardDir.getAbsolutePath() + "/Catherine" );
            if(!filePath.exists())
                filePath.mkdirs();
          
            saveFilePath = filePath + "/" + filename() + ".png";
            FileOutputStream baos= new FileOutputStream(saveFilePath);
            camera_bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            baos.flush();
            baos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        getImageStr(saveFilePath);
        uploadImage();
    }
    
    public String filename() {
        Random random = new Random(System.currentTimeMillis());
        java.util.Date dt = new java.util.Date(System.currentTimeMillis()); 
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String fileName = fmt.format(dt)+ Math.abs(random.nextInt()) % 100000;      
        return fileName;
    }      
    
    public void getPath( Uri uri)
    {
        String []proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);                  Log.i(TAG, "path: " + path);
    }
    
    OnClickListener modifyBtnListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
           switch (v.getId()) {
        case R.id.individual_info_bt:
            individualInfoState = !individualInfoState;
            if (individualInfoState)
            {
                individualInfoBtn.setText(R.string.ack);
            }
            else {
                individualInfoBtn.setText(R.string.modify);
            }
            gender.setFocusable(individualInfoState);
            gender.setFocusableInTouchMode(individualInfoState);
            location.setFocusable(individualInfoState);
            location.setFocusableInTouchMode(individualInfoState);
            description.setFocusable(individualInfoState);
            description.setFocusableInTouchMode(individualInfoState);
            break;
        case R.id.account_info_bt:
            accountInfoState = !accountInfoState;   
            if (accountInfoState)
            {
                accountInfoBtn.setText(R.string.ack);
            }
            else {
                accountInfoBtn.setText(R.string.modify);
            }
        default:
            break;
        }     
        }
    };
    
    OnClickListener uploadByPhotosListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            avatarDialog.dismiss();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, CASE_PHOTO); 
        }
    };
    
    OnClickListener uploadByCameraListener = new OnClickListener() {
        
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            avatarDialog.dismiss();
            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intentCam, CASE_CAMERA);
        }
    };
    
    OnClickListener avatarListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (!individualInfoState) 
            {
                LayoutInflater inflater = activity.getLayoutInflater();
                View layout = inflater.inflate(R.layout.show_avatar,
                        (ViewGroup)activity. findViewById(R.id.show_avatar));
                new AlertDialog.Builder(activity).setTitle("Avatar").setView(layout)
                .setPositiveButton(R.string.ack, null).show();
                ImageView big_avatar = (ImageView)layout.findViewById(R.id.big_avatar);
                avatar.setDrawingCacheEnabled(true);
                Bitmap bigAvatar = imageUtil.scaleBitmap(Bitmap.createBitmap(avatar.getDrawingCache()), 300, 300);
                big_avatar.setImageBitmap(bigAvatar);
                //big_avatar.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_launcher));
                avatar.setDrawingCacheEnabled(false);
            }
            else {
                avatarDialog = new AvatarDialog(activity, R.style.avatar_dialog);//创建Dialog并设置样式主题
               // Window win = avatarDialog.getWindow();
                //LayoutParams params = new LayoutParams();
                //params.x = 0;//设置x坐标
                //params.y = 0;//设置y坐标
                //win.setAttributes(params);
                avatarDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog         
                avatarDialog.show();
                avatarDialog.setAlbumButtonListener(uploadByPhotosListener);
                avatarDialog.setCameraButtonListener(uploadByCameraListener);              
            }
        }
    };
    
    OnClickListener locationOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            LayoutInflater inflater = activity.getLayoutInflater();
            View layout = inflater.inflate(R.layout.area_layout,
                    (ViewGroup)activity. findViewById(R.id.area_layout));
            new AlertDialog.Builder(activity).setView(layout)
            .setPositiveButton(R.string.ack, null).setNegativeButton(R.string.cancel, null).show();
            final WheelView stateItem = (WheelView)layout.findViewById(R.id.state);
            final WheelView cityItem = (WheelView)layout.findViewById(R.id.city);
            final WheelView areaItem = (WheelView)layout.findViewById(R.id.area);
//            if (stateList == null)
//            {
//                try {
//                    stateList = AreaXMLParser.doParse(activity);
//                } catch (XmlPullParserException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Log.i("In Settings", "XmlPullParserException");
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Log.i("In Settings", "IOException");
//                }
//            }
            stateItem.setVisibleItems(5);
            stateItem.setCyclic(true);
            stateItem.setInterpolator(new AnticipateOvershootInterpolator());
            stateItem.setAdapter(new ListWheelAdapter<State>(stateList));
            cityItem.setVisibleItems(5);
            cityItem.setCyclic(true);
            cityItem.setInterpolator(new AnticipateOvershootInterpolator());
            areaItem.setVisibleItems(5);
            areaItem.setCyclic(true);
            areaItem.setInterpolator(new AnticipateOvershootInterpolator());
            stateItem.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    ArrayList<City> tmpList = stateList.get(newValue).getAreaList();
                    if (tmpList.size() > 0) {
                        cityItem.setAdapter(new ListWheelAdapter<City>(tmpList));
                        cityItem.setCurrentItem(0);
    
    
                        ArrayList<Area> tmpAreas  = tmpList.get(0).getAreaList();
                        if (tmpAreas.size() > 0){
                            areaItem.setAdapter(new ListWheelAdapter<Area>(tmpAreas));
                            areaItem.setCurrentItem(0);
                        }else {
                            areaItem.setAdapter(null);
                        }
                    }
                    else {
                        cityItem.setAdapter(null);
                    }
                }
            });
            cityItem.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    int stateIndex = stateItem.getCurrentItem();
                    ArrayList<City> tmpCities = stateList.get(stateIndex).getAreaList();

                    ArrayList<Area> tmpAreas  = tmpCities.get(newValue).getAreaList();
                    if (tmpAreas.size() > 0){
                        areaItem.setAdapter(new ListWheelAdapter<Area>(tmpAreas));
                        areaItem.setCurrentItem(0);
                    }else {
                        areaItem.setAdapter(null);
                    }

                }
            });
        }
    };
    
  
    //string -> byte[] -> bitmap -> setImageBitmap to show
    public void setImage(String str)
    {
        byte[] temp = imageUtil.String2Bytes(str);
        try {
            if(temp!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                imageUtil.savePhoto(userId, bitmap);
                int scale = UserInterface.dip2px(activity, 70);
                Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
                avatar.setImageBitmap(new_bitmap);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }           
    }
    
    private void uploadImage()
    {
        JSONObject params = new JSONObject();
        
        try
        {
            params.put("id", userId);
            params.put("avatar", imageStr); 
            params.put("operation", 1);
            new HttpSender().Httppost(OperationCode.UPLOAD_AVATAR, params, handler);
            Log.i(TAG, "upload param: " + params.toString());
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
//        new Thread(new Runnable()
//        {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                setImage(imageStr);
//            }
//            
//        }).start();
        
    }
    
    
    //image to string
    public String getImageStr(String imfFilePath) 
    {
        byte[] data = null;
        try {
            InputStream in = new FileInputStream(imfFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
        imageStr = Base64.encodeToString(data, Base64.DEFAULT);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    
    
 // 从资源中获取Bitmap
    public Bitmap getBitmapFromResources(int resId) {
        Resources res = activity.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }
    

    
    class MessageHandler extends Handler
    {       
        public MessageHandler(Looper looper) {
            // TODO Auto-generated constructor stub
            super(looper);
        }
        
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
            case OperationCode.GET_AVATAR:       
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject( msg.obj.toString() );
                    if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
                    {
                        String returnStr = returnJson.getString("avatar");  
                        setImage(returnStr);
                    }
                    else 
                    {
                        Bitmap bitmap = getBitmapFromResources(R.drawable.defaultavatar);
                        int scale = UserInterface.dip2px(activity, 70);
                        Bitmap new_bitmap = imageUtil.scaleBitmap(bitmap, scale, scale);
                        avatar.setImageBitmap(new_bitmap);
                    }
                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }                                           
                break;
            case OperationCode.UPLOAD_AVATAR:
                try {
                    JSONObject returnJson;
                    returnJson = new JSONObject( msg.obj.toString() );
                    Log.i("upload_avatar", returnJson.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                setImage(imageStr);
                break;
            case OperationCode.GET_USER_INFO:
                JSONObject returnJson;
                try {
                    returnJson = new JSONObject(msg.obj.toString());
                    Log.i("get_user_info", returnJson.toString());
                    if (returnJson.getInt("cmd") == ReturnCode.NORMAL_REPLY)
                    {
                        myName.setText(returnJson.getString("name"));
                        myEmail.setText(returnJson.getString("email"));
                        if (returnJson.getInt("gender") == 1)
                        {
                            gender.setText("男");
                        }
                        else {
                            gender.setText("女");
                        }   
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            default:
                break;
            }
        }
    }
}
