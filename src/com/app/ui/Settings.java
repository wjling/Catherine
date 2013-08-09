package com.app.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.catherine.R;
import com.app.utils.HttpSender;
import com.app.utils.OperationCode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Settings{
    private Activity activity;
    private View settingsView;
    private final String TAG = "imageupload";
    private String path;
    private Uri uri;
    private String imageStr;
    ImageView imageView;
    Button uploadByPhotosBtn, uploadByCameraBth;
    int userId;
    private MessageHandler handler;
    public final int CASE_PHOTO = 0;
    public final int CASE_CAMERA = 1;
    
    public Settings(Activity activity, View settingsView, int userId) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.settingsView = settingsView;
        this.userId = userId;
        init();
    }
    
    public void setLayout()
    {
        imageView = (ImageView)settingsView.findViewById(R.id.avatar);
        uploadByPhotosBtn = (Button)settingsView.findViewById(R.id.uploadbyphotos);
        uploadByCameraBth = (Button)settingsView.findViewById(R.id.uploadbycamera);
        uploadByPhotosBtn.setOnClickListener(uploadByPhotosListener);
        uploadByCameraBth.setOnClickListener(uploadByCameraListener);
        Log.i("settings","hehe");
        handler = new MessageHandler(Looper.myLooper());
    }

    private void init() {
        // TODO Auto-generated method stub
        setLayout();
    }
    
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        if (requestCode == CASE_PHOTO && resultCode == activity.RESULT_OK && null != data)
//        {
//            uri = data.getData();          
//            Log.i(TAG, "uri: " + uri.toString());            
//            getPath(uri);
//            getImageStr(path);       
//            uploadImage();
//        }
//        else if (requestCode == CASE_CAMERA && resultCode == activity.RESULT_OK && null != data)
//        {
//            Bundle extras = data.getExtras();
//            Bitmap camera_bitmap = (Bitmap) extras.get("data");
//            String saveFilePath = "";
//            
//            try {
//                File sdCardDir = Environment.getExternalStorageDirectory();
//                File filePath = new File(sdCardDir.getAbsolutePath() + "/evan" );
//                if(!filePath.exists())
//                    filePath.mkdirs();
//                
//                saveFilePath = filePath + "/" + filename() + ".png";
//                FileOutputStream baos= new FileOutputStream(saveFilePath);
//                camera_bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
//                baos.flush();
//                baos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            getImageStr(saveFilePath);
//            uploadImage();
//        }
//        else {
//            Toast.makeText(this.activity, "请重新选择头像", Toast.LENGTH_SHORT);
//        }
//    }
    
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
    
    OnClickListener uploadByPhotosListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.i("In settings: ", "In settings on button click");
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
            Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intentCam, CASE_CAMERA);
        }
    };
       
    
    //string -> byte[] -> bitmap -> setImageBitmap to show
    public void setImage(String str)
    {
        byte[] temp = String2Bytes(str);
        try {
            if(temp!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                imageView.setImageBitmap(bitmap);
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
                    String returnStr = returnJson.getString("avatar");  
                    setImage(returnStr);
                } catch (JSONException e) {
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
            default:
                break;
            }
        }
    }
}
