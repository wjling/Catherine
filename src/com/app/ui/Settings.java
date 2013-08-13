package com.app.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.jar.Attributes.Name;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;
import com.app.utils.ReturnCode;
import com.app.widget.AvatarDialog;

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
        description = (EditText)settingsView.findViewById(R.id.settings_description);
        description.setFocusable(false);
        description.setFocusableInTouchMode(false);
        description.setLongClickable(false);
        myName = (TextView)settingsView.findViewById(R.id.settings_name);
        myEmail = (TextView)settingsView.findViewById(R.id.settings_email);
        handler = new MessageHandler(Looper.myLooper());
    }

    private void init() {
        // TODO Auto-generated method stub
        setLayout();
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
        new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
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
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, CASE_PHOTO); 
            avatarDialog.dismiss();
        }
    };
    
    OnClickListener uploadByCameraListener = new OnClickListener() {
        
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intentCam, CASE_CAMERA);
            avatarDialog.dismiss();
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
                Bitmap bigAvatar = scaleBitmap(Bitmap.createBitmap(avatar.getDrawingCache()), 300, 300);
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
    
    public Bitmap scaleBitmap(Bitmap bm,int newWidth,int newHeight)
    {
        // 原始图像的宽和高
        int width = bm.getWidth();
        int height = bm.getHeight();
        
        // 计算缩放率
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        
        return resizedBitmap;
    }

    
    //string -> byte[] -> bitmap -> setImageBitmap to show
    public void setImage(String str)
    {
        byte[] temp = String2Bytes(str);
        try {
            if(temp!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                Bitmap new_bitmap = scaleBitmap(bitmap, 100, 100);
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
    }
    
    private void get_avatar() 
    {
        JSONObject params = new JSONObject();
        
        try
        {
            params.put("id", userId);
            params.put("operation", 0);
            new HttpSender().Httppost(OperationCode.GET_AVATAR, params, handler);
            Log.i(TAG, "upload param: " + params.toString());
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }       
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
    
    //string to bytes array
    public byte[] String2Bytes(String imgStr) 
    {
        byte[] bytes = null;
        try {
            bytes = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i]<0) {
                    bytes[i] += 256;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bytes;
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
                        Bitmap new_bitmap = scaleBitmap(bitmap, 100, 100);
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
                    get_avatar();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
