package com.example.cghpw.demo.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	private static ContentValues values=new ContentValues();
	//待存入的天气编码列表
	public static List<String> weatherlList=new ArrayList<String>(Arrays.asList("晴","多云","阴","阵雨","雷阵雨","雷阵雨伴有冰雹",
																															"雨夹雪","小雨","中雨","大雨","暴雨","大暴雨",
																															"特大暴雨","阵雪","小雪","中雪","大雪","暴雪",
																															"雾","冻雨","沙尘暴","小到中雨","中到大雨","大到暴雨",
																															"暴雨到大暴雨","大暴雨到特大暴雨","小到中雪","中到大雪","大到暴雪","浮尘",
																															"扬沙","强沙尘暴 ","霾"));
	
	//待存入的风力编码列表
	public static List<String>  windPowerlList=new ArrayList<String>(Arrays.asList("微风","3-4 级","4-5 级","5-6 级",
																																"6-7 级","7-8 级","8-9 级","9-10 级",
																																"10-11 级","11-12 级"));
	
	public static List<String> windDirectionlList=new ArrayList<String>(Arrays.asList("无持续风向","东北风","东风","东南风",
																																	"南风","西南风","西风","西北风",
																																	"北风","旋转风"));
	
	//province建表语句
	public static final String CREATE_PROVINCE="create table Province ("+
																				"id integer primary key autoincrement,"+
																				"province_name text,"+
																				"province_code text)";
	//city表的建表语句
	public static final String CREATE_CITY="create table City ("+
																	"id integer primary key autoincrement,"+
																	"city_name text,"+
																	"city_code text,"+
																	"province_id integer)";
	//county建表语句
	public static final String CREATE_COUNTY="create table County ("+
																			"id integer primary key autoincrement,"+
																			"county_name text,"+
																			"county_code text,"+
																			"city_id integer)";
	
	//天气现象编码表
	public static final String CREATE_WEATHER="create table WeatherCode ("+
																				"id integer primary key autoincrement,"+
																				"weather_name text)";
	
	//风力编码表
	public static final String CREATE_WIND_POWER="create table WindPower ("+
			"id integer primary key autoincrement,"+
			"windpower_name text)";
	
	//风向编码表
	public static final String CREATE_WIND_DIRECTION="create table WindDirection ("+
			"id integer primary key autoincrement,"+
			"winddirection_name text)";
	
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override//数据库建成时会调用此方法
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);//创建表
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_WEATHER);
		db.execSQL(CREATE_WIND_POWER);
		db.execSQL(CREATE_WIND_DIRECTION);
		
		//将天气编码和其对应的名称存入数据库
		for (String w : weatherlList) {
			values.clear();
			values.put("weather_name", w);
			db.insert("WeatherCode", null, values);
		}
		
		//将风力编码和其对应的名称存入数据库
		for (String wp : windPowerlList) {
			values.clear();
			values.put("windpower_name", wp);
			db.insert("WindPower", null, values);
		}
		
		//将风向编码和其对应的名称存入数据库
		for (String wd : windDirectionlList) {
			values.clear();
			values.put("winddirection_name", wd);
			db.insert("WindDirection", null, values);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}


}
