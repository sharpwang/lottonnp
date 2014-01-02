package com.example.androidtest1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;  
	final String SQL = "create table draws(draw integer primary key, " +
			"redball1 integer, redball2 integer, redball3 integer," +
			"redball4 integer, redball5 integer, redball6 integer, " +
			"blueball integer, openday integer, winners integer, " +
			"bonus integer, pools integer)";
			
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
   
	public DatabaseHelper(Context context, String name, int version){  
        this(context,name,null,version);  
    }  
   
   
	public DatabaseHelper(Context context, String name){  
        this(context,name,VERSION);  
    }
   
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(SQL);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
