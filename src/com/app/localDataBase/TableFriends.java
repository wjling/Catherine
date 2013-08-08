package com.app.localDataBase;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;



public class TableFriends {
	private Context context;
	private MySQLiteOpenHelper myHelper;
	
	public TableFriends(Context context)
	{
		myHelper = new MySQLiteOpenHelper(context);
		this.context = context;
	}
	
	public void add(ArrayList<FriendStruct> friends)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			int length = friends.size();
			for(int i=0;i<length;i++)
			{
				FriendStruct fs = friends.get(i);
				ContentValues turple = new ContentValues();
				turple.put("fid", fs.fid);
				turple.put("fname", fs.fname);
				turple.put("gender", fs.gender);
				turple.put("email", fs.email);
				db.insert("friends", null, turple);				
			}
			db.close();
		}
	}
	
	public void add(FriendStruct fs)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
				ContentValues turple = new ContentValues();
					turple.put("fid", fs.fid);
					turple.put("fname", fs.fname);
					turple.put("gender", fs.gender);
					turple.put("email", fs.email);
					db.insert("friends", null, turple);
				db.close();
		}
	}
	
	public void delete(String fid)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			db.delete("friends", "fid = ?", new String[]{fid});
			db.close();
		}
	}
	
	public void update(String oldFid, FriendStruct fs)
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			ContentValues turple = new ContentValues();
			turple.put("fid", fs.fid);
			turple.put("fname", fs.fname);
			turple.put("gender", fs.gender);
			turple.put("email", fs.email);
			db.update("friends", turple, "fid=?", new String[]{oldFid});
			db.close();
		}
	}
	
	public FriendStruct query(String fid)
	{
		FriendStruct fs = new FriendStruct();
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			Cursor cr = db.query("friends", null, "fid=?", new String[]{fid}, null, null, null);
			if(cr.moveToFirst())
			{
				fs.fid = cr.getInt(0);
				fs.fname = cr.getString(1);
				fs.gender = cr.getString(2);
				fs.email = cr.getString(3);
			}
			Toast.makeText(context, "没有该条记录...", Toast.LENGTH_SHORT).show();
			db.close();
		}
		return fs;
	}
	
	public ArrayList<FriendStruct> getAllFriends()
	{
		Log.i("getAllFriends", "get all friends");
		ArrayList<FriendStruct> friendArray = new ArrayList<FriendStruct>();
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			Cursor cr = db.query("friends", null, null, null, null, null, null);
			while(cr.moveToNext())
			{
				FriendStruct fs = new FriendStruct();
				fs.fid = cr.getInt(0);
				fs.fname = cr.getString(1);
				fs.gender = cr.getString(2);
				fs.email = cr.getString(3);
				friendArray.add(fs);
			}
			db.close();
		}
		return friendArray;
	}
	
	public int deleteFriendsTable()
	{
		SQLiteDatabase db = myHelper.getWritableDatabase();
		int result = -1;
		if(db.isOpen())
			 result = db.delete("friends", null, null);
		db.close();
		return result;
	}
	
	public int size()
	{
		int size = 0;
		SQLiteDatabase db = myHelper.getWritableDatabase();
		if(db.isOpen())
		{
			Cursor cr = db.query("friends", null, null, null, null, null, null);
			size = cr.getCount();
		}
		db.close();
		return size;
	}
}
