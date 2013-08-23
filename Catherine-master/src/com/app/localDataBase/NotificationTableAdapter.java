package com.app.localDataBase;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotificationTableAdapter
{
	private static final String NotificationDB = "notificationDB"; 
	//����context����
	private Context mContext = null;
	//�����ݿ�ʱ�����淵�ص����ݿ����
	private SQLiteDatabase mSqLiteDatabase = null;
	//�̳���SQLiteOpenHelper
	private MySQLiteOpenHelper mySQLiteOpenHelper = null;
	
	public NotificationTableAdapter(Context context)
	{
		Log.i(NotificationDB, "����helper����");
		mContext = context;
		mySQLiteOpenHelper = new MySQLiteOpenHelper(mContext);
	}
	
	public long insertData(int status, int uid, String tag, String msg)
	{
		long resultValue = 0;		
		if ( findMsgFromDB(msg)==false) {			
				Log.i(NotificationDB, "��������"+msg);
				SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		
				ContentValues initValues = new ContentValues();
					initValues.put("uid", uid);
					initValues.put("status", status);
					initValues.put("tag", tag);
					initValues.put("msg", msg);
					resultValue = db.insert("notifications", null, initValues);
					db.close();
		}
		return resultValue;
	}
	
	public boolean findMsgFromDB(String msg)
	{
		Log.i(NotificationDB, "��������"+msg);
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		Cursor cursor;
		cursor = db.query(true, "notifications", new String[]{"msg"}, "msg='"+msg+"'", null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			Log.i(NotificationDB, "�������ݴ���");
			db.close();
			return true;
		}
		else {
			Log.i(NotificationDB, "�������ݲ�����");
			db.close();
			return false;
			}
	}
	
	
	public boolean deleteData(long rowId)
	{
		Log.i(NotificationDB, "ɾ���������idΪ��"+rowId);
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		boolean resultValue;
			resultValue = db.delete("notifications", "_id=" + rowId, null)>0;
			db.close();
		return resultValue;
	}
	
	public boolean updateData(long rowId, int status)
	{
		Log.i(NotificationDB, "��idΪ"+rowId+"��״̬����Ϊ"+status);
		ContentValues cv = new ContentValues();
		boolean resultValue;
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
			cv.put("status", status);
			resultValue = db.update("notifications", cv, "_id=" + rowId, null)>0;
		return resultValue;
	}
	
	//����_id��msg
	public ArrayList<notificationObject> queryData(String tag, int uid)
	{
		Log.i(NotificationDB, "��ȡtagΪ"+tag+"��������");
		SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
		ArrayList<notificationObject> returnArrayList = 
				new ArrayList<notificationObject>();	
				
		if (db!=null) {
					Cursor cursor = db.rawQuery(
							"SELECT * FROM notifications WHERE uid = ? and tag=?", new String[]{uid+"", tag});
	
					if( cursor!=null )
					{
						if( cursor.moveToFirst() )
						{
							do{
								int ID;
								String msg = null;
								notificationObject item = null;
								
								ID = cursor.getInt( cursor.getColumnIndex("_id") );
								msg = cursor.getString( cursor.getColumnIndex("msg"));
			
								item = new notificationObject(ID, msg);
								returnArrayList.add(item);
								Log.i(NotificationDB, "�����"+msg);
							}while(cursor.moveToNext());
						}
						else
						{
							Log.i(NotificationDB, "moveToFirst()Ϊfalse");
						}
					}
					else
					{
						Log.i(NotificationDB, "cursorΪnull");
					}
		}
		else {
			Log.i(NotificationDB, "dbΪnull");
		}
		 
		db.close();
		return returnArrayList;
	}

}
