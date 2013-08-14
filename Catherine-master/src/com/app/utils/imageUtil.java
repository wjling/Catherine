package com.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.widget.ImageView;

public class imageUtil 
{
	 public static byte[] String2Bytes(String imgStr) 
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
	 
    public static Bitmap scaleBitmap(Bitmap bm,int newWidth,int newHeight)
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
	 
	 public static void setImage(String str, ImageView avatar)
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
}
