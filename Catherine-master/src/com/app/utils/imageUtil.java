package com.app.utils;

import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;


public class imageUtil 
{
    private final static String APP_PATH = Environment.getExternalStorageDirectory() + "/Catherine/";
	private final static String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Catherine/Avatar/";
//	private HashMap<Integer, Bitmap> imageMap = new HashMap<Integer, Bitmap>();
	
	private LruCache<Integer, Bitmap> mMemoryCache;
	private int maxMemory;
	private int cacheSize;
	
	public imageUtil()
	{
		//get max available vm memory
		maxMemory = (int )(Runtime.getRuntime().maxMemory() / 1024);
		//use 1/8th of the available memory for this memory cache
		cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				//the cache size will be measured in kilobytes 
				return bitmap.getRowBytes()*bitmap.getHeight() / 1024;
//				return bitmap.getByteCount() / 1024;
			}
		};
	}
	
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
    
	public static void savePhoto(int uid, Bitmap bmp)
	{		
		//when you need to save the image inside your own folder in the sd card
	    File imageFileFolder = new File( APP_PATH );

        if( !imageFileFolder.exists() )
        {
            imageFileFolder.mkdir();
            Log.i("myevent", "************创建一个目录" + APP_PATH);
        }
        
        imageFileFolder = new File(IMAGE_PATH);

		if( !imageFileFolder.exists() )
		{
			imageFileFolder.mkdir();
			Log.i("myevent", "************创建一个目录" + IMAGE_PATH);
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
			Log.i("myevent", "************保存了一张图片" + uid);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * precondition:
	 * 		fileExist return true;
	 * @param uid
	 * @return
	 */
	public static Bitmap getLocalBitmapBy(int uid)
	{
		Bitmap bitmap = null;
		
		bitmap = BitmapFactory.decodeFile( IMAGE_PATH + uid + ".jpg" );
	
		return bitmap;
	}
	
	public static boolean fileExist(int uid)
	{
		File file = new File( IMAGE_PATH + uid + ".jpg" );
		return file.exists();
	}
	
//	//map
//	public boolean fileExistInMap(int uid)
//	{
//		return imageMap.containsKey(uid) || fileExist(uid);
//	}
//	
//	public Bitmap getBitmapInMap(int uid)
//	{
//		Bitmap bitmap = null;
//		
//		if( imageMap.containsKey(uid) )	
//		{
//			bitmap = imageMap.get(uid);
//			Log.e("imageUtil", "in map: " + uid);
//		}			
//		else if( fileExist(uid) )
//		{
//			bitmap = getLocalBitmapBy(uid);
//			putBitmapInMap(uid, bitmap);
//			Log.e("imageUtil", "in local: " + uid);
//		}
//		
//		return bitmap;
//	}
//	
//	public void putBitmapInMap(int uid, Bitmap bitmap)
//	{
//		imageMap.put(uid, bitmap);
//	}
	
	//LruCache
	public void addBitmapToMemoryCache(int uid, Bitmap bitmap) {
		//if image not in cache, add to cache
		if( mMemoryCache.get(uid)==null )
		{
			mMemoryCache.put(uid, bitmap);
			Log.e("imageUtil", "put an image: " + uid);
		}
	}

	/**
	 * precondition:
	 * 		imageExistInCache return true;
	 * @param uid
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(int uid) 
	{	
		Log.e("imageUtil", "get an image: " + uid);
		return mMemoryCache.get(uid);
	}
	
	//image in cache or not
	public boolean imageExistInCache( int uid )
	{
		if( mMemoryCache.get(uid)==null ) 
		{
			if( fileExist(uid)  )
			{
				Bitmap bitmap = getLocalBitmapBy(uid);
				if( bitmap!=null )
					mMemoryCache.put(uid, bitmap);
			}
				
			return false;
		}
		else 
			return true;
	}
}
