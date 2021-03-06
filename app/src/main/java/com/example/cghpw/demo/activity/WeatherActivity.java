package com.example.cghpw.demo.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cghpw.demo.R;
import com.example.cghpw.demo.utils.HttpUtil;
import com.example.cghpw.demo.utils.Utility;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private LinearLayout weatherInfoLayout;
	
	//显示城市名
	private TextView cityNameText;
	
	//显示发布时间
	private TextView publishText;
	
	//显示天气描述信息
	private TextView weatherDespText;
	
	//显示最低温度
	private TextView temp1Text;
	
	//显示最高温度
	private TextView temp2Text;
	
	//显示当前的日期
	private TextView currentDateText;
	
	//切换城市按钮
	private Button switchCity;
	
	//更新天气按钮
	private Button refreshWeather;
	
	//波浪号
	private TextView to;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
		setContentView(R.layout.weather_layout);
		//初始化各个控件
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		currentDateText=(TextView)findViewById(R.id.current_date);
		switchCity=(Button)findViewById(R.id.switch_city);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		to=(TextView)findViewById(R.id.to);
		//获取县级代号用于查询天气
		String countyCode=getIntent().getStringExtra("county_code");
		//若有天气信号就去查询天气
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中...");
			//设置布局不可见
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			
			
			queryWeatherCode(countyCode);//根据地区编码查询天气代号
		}else {
			//没有的话就显示存储过的天气 本地的
			
			to.setVisibility(View.INVISIBLE);
			showWeather();
			
		}
		
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent=new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();//跳转之后结束当前的界面
			break;

		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	
	//根据地区编码查询天气
	private void queryWeatherCode(String countyCode) {
		String address="http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}
	
	//根据天气代号查询对应的天气
	private void queryWeatherInfo(String weatherCode) {
		
		//再次做出更改 ，改用 中国天气网新的API 获取更全面的天气
		
		//得到当前的时间 以 yyyyMMddHHmm 格式
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm", Locale.CHINA);
		String currentDate=sdf.format(new Date());
		
		//此地址用于加密运算
		String whiteUrlS="http://open.weather.com.cn/data/?areaid="+weatherCode+"&type=forecast_v&date="+currentDate+"&appid=bcbccb513492a6c5";
		
		//此地址用于最后的地址拼接，因为最终地址只取appid的前六位
		String whiteUrl="http://open.weather.com.cn/data/?areaid="+weatherCode+"&type=forecast_v&date="+currentDate+"&appid=bcbccb";
		
		//得到最终的 访问地址
		String finalUrl= Utility.getSecretiveUrl(whiteUrlS, whiteUrl);
		
//		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html"; 
		if (finalUrl != null) {
			queryFromServer(finalUrl, "weatherCode"); 
		}else {
			Log.d("Error", "fail to get finalUrl");
		}
		
	}
	
	//根据传入的地址与类型来查询对应的 天气代号或者天气
	private void queryFromServer(final String address,final String type) {
		HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array=response.split("\\|");
						if (array!=null&&array.length==2) {
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//回到主线程更新UI
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//此方法 在子线程中执行，若要更新UI，需回UI线程
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishText.setText("同步失败，点击右上角更新");
					}
				});
				
			}
		});
	}
	
	//从sharedPrefrences文件中读取天气信息，显示到界面上
	public void showWeather() {
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
			cityNameText.setText( prefs.getString("city_name",""));   
			temp1Text.setText(prefs.getString("temp1", ""));  
			temp2Text.setText(prefs.getString("temp2", ""));   
			weatherDespText.setText(prefs.getString("weather_desp", ""));   
			publishText.setText( prefs.getString("publish_time", "请点左上角选择城市"));   
			currentDateText.setText(prefs.getString("current_date", ""));   
			
			weatherInfoLayout.setVisibility(View.VISIBLE);   
			cityNameText.setVisibility(View.VISIBLE);
			
			
			if (prefs.getString("city_name","") != null) {
				to.setVisibility(View.VISIBLE);
			}
			
			
		 
	}
	
}
