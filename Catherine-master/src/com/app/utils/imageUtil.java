package com.app.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class imageUtil 
{
	private final static String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Catherine/Avatar/";
	
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
        // ԭʼͼ��Ŀ�͸�
        int width = bm.getWidth();
        int height = bm.getHeight();
        
        // ����������
        float scaleWidth = ((float)newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        
        return resizedBitmap;
    }
    
	public static void savePhoto(int uid, Bitmap bmp)
	{
		//when you need to save the image inside your own folder in the sd card
		File imageFileFolder = new File( IMAGE_PATH );

		if( !imageFileFolder.exists() )
		{
			imageFileFolder.mkdir();
			Log.i("myevent", "************����һ��Ŀ¼" + IMAGE_PATH);
		}
		
		FileOutputStream out = null;
		File imageFileName = new File(imageFileFolder, uid+".jpg");
		try
		{
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			out = null;
			Log.i("myevent", "************������һ��ͼƬ" + uid);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static Bitmap getLocalBitmapBy(int uid)
	{
		Bitmap bitmap = null;
		
		if( fileExist(uid) )
			bitmap = BitmapFactory.decodeFile( IMAGE_PATH + uid + ".jpg" );
	
		return bitmap;
	}
	
	public static boolean fileExist(int uid)
	{
		File file = new File( IMAGE_PATH + uid + ".jpg" );
		return file.exists();
	}

}
