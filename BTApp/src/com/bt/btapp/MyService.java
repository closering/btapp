/**
 * 
 */
package com.bt.btapp;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.bt.btapp.LoginActivity.Login;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author bucengyaoyuan
 *
 */
public class MyService extends Service {
	private Double longitude;//��ǰ�ľ���
	private Double latitude;//��ǰ��γ��
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Map<String, Double> localHashMap = new HashMap<String, Double>();
	private WGSTOGCJ02 wgstogcj02 = new WGSTOGCJ02();
	private Double oldlat;
	private Double oldlng;
	@Override
	public void onCreate()
	{
		super.onCreate();
		System.out.println("service start");
		MyMethod();
	}
	public void MyMethod(){
	    locationManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationListener = new LocationListener(){
				@Override  
		        public void onStatusChanged(String provider, int status, Bundle extras) {  
		                      
		        }  
				// Provider��enableʱ�����˺���������GPS����  
	            @Override  
	            public void onProviderEnabled(String provider) {  
	                  
	            }  
	              
	            // Provider��disableʱ�����˺���������GPS���ر�   
	            @Override  
	            public void onProviderDisabled(String provider) {  
	                  
	            }  
	            
	            //������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����   
	            @Override  
	            public void onLocationChanged(Location location) {  
	                if (location != null) {     
	                	latitude = location.getLatitude();
	                	longitude = location.getLongitude();
	                	//System.out.println("get pos");
						if(!latitude.equals(oldlat)||!longitude.equals(oldlng))
	                	{
	                		oldlat = latitude;
	                		oldlng = longitude;
	                	localHashMap = wgstogcj02.transform(longitude, latitude);
	                	
	                    latitude= localHashMap.get("lat"); 
	                    longitude = localHashMap.get("lon"); 
	                    //System.out.println(" GPS listen before transform:"+location.getLatitude()+","+location.getLongitude());
	                    //System.out.println(" GPS listen after transform:"+latitude+","+longitude);
	                    new uploadPosition(longitude, latitude).start();
	                    check();
	                	}
	                }  
	            }//end  onLocationChanged                         
			};
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0,locationListener);     
		    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   
		    if(location != null){   
		    	latitude = location.getLatitude();
            	longitude = location.getLongitude();
            	//System.out.println("get pos");
				if(!latitude.equals(oldlat)||!longitude.equals(oldlng))
            	{
            		oldlat = latitude;
            		oldlng = longitude;
            		localHashMap = wgstogcj02.transform(longitude, latitude);
            	
            		latitude= localHashMap.get("lat"); 
            		longitude = localHashMap.get("lon"); 
            		//System.out.println(" GPS listen before transform:"+location.getLatitude()+","+location.getLongitude());
            		//System.out.println(" GPS listen after transform:"+latitude+","+longitude);
		        new uploadPosition(longitude, latitude).start();
		        check();
            	}
		       // System.out.println("GPS:"+lat+" , "+lng);
		    }     
		}
		else{
			locationListener = new LocationListener(){
				@Override  
				public void onStatusChanged(String provider, int status, Bundle extras) {  
                     
				}     
				// Provider��enableʱ�����˺���������GPS����  
				@Override  
				public void onProviderEnabled(String provider) {  
                     
				}   
				// Provider��disableʱ�����˺���������GPS���ر�   
				@Override  
				public void onProviderDisabled(String provider) {  
                     
				}  
				//������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����   
				@Override  
				public void onLocationChanged(Location location) {  
					if (location != null) {     
						latitude = location.getLatitude();
	                	longitude = location.getLongitude();
	                	//System.out.println("get pos");
						if(!latitude.equals(oldlat)||!longitude.equals(oldlng))
	                	{
	                		oldlat = latitude;
	                		oldlng = longitude;
	                	localHashMap = wgstogcj02.transform(longitude, latitude);
	                	
	                    latitude= localHashMap.get("lat"); 
	                    longitude = localHashMap.get("lon"); 
	                   // System.out.println(" GPS listen before transform:"+location.getLatitude()+","+location.getLongitude());
	                    //System.out.println(" GPS listen after transform:"+latitude+","+longitude);

						new uploadPosition(longitude, latitude).start();
						check();
	                	}
					}  
				}//end  onLocationChanged
			};		 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 0,locationListener);     
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
			if(location != null){  
				latitude = location.getLatitude();
            	longitude = location.getLongitude();
            	//System.out.println("get pos");
				if(!latitude.equals(oldlat)||!longitude.equals(oldlng))
            	{
            		oldlat = latitude;
            		oldlng = longitude;
            		localHashMap = wgstogcj02.transform(longitude, latitude);
            	
            		latitude= localHashMap.get("lat"); 
            		longitude = localHashMap.get("lon"); 
            		//System.out.println(" GPS listen before transform:"+location.getLatitude()+","+location.getLongitude());
            		//System.out.println(" GPS listen after transform:"+latitude+","+longitude);
            		new uploadPosition(longitude, latitude).start();
            		check();
            	}
			}    
		}
    }
	/*
	 * ��ǰ��λ�øı�֮����ѯ���໤����Ϣ���ж��Ƿ���磬��������Ӧ����
	 */
	public void check(){
		//System.out.println(new Date()+" check lat and lon");
		JpushApplication JpushApp = (JpushApplication)getApplicationContext();
	    List<Monitor> monitorList = JpushApp.getMonitorList();
	    Monitor mo;
	    String message;
    	if(monitorList.size() == 0){
    		//û�м໤��
    	}
    	else{
    		for(int i=0; i<monitorList.size(); i++){
    			mo = monitorList.get(i);
    			if(isOutOfBound(longitude, latitude, mo)){//���������
    				if((Boolean) JpushApp.getMonitorListSend().get(mo)){//���������Ϣ�Ѿ����͹�
    					
    				}else{
    					JpushApp.setMonitorListSend(mo, true);
    					//���͸����������໤�˳������Ϣ
    					message = mo.getUsername()+"; Alarm: " + JpushApp.getUsername() + " is out of bound, longitude: " + longitude + ", latitude: " + latitude;
    					new Alarm(message).start();	
    				}
    			}else{
    				if((Boolean) JpushApp.getMonitorListSend().get(mo)){//���û�г��磬��������Ϣ�Ѿ����͹�,��˼�ǳ�ȥ֮���ֻ�����
    					JpushApp.setMonitorListSend(mo, false);
    					//���͸����������໤�������»ص��໤��Χ��
    					message = mo.getUsername()+"; Alarm: " + JpushApp.getUsername() + " is back bound, longitude: " + longitude + ", latitude; " + latitude;
    					new Alarm(message).start();
    				}else{
    					
    				}	
    			}
    		}
    	}
	}
	/*
	 * ÿ��λ�øı�ʱ����λ����Ϣ�ϴ������������������û��������ȡ�γ��
	 */
	public class uploadPosition extends Thread{
		private Double longitude;
		private Double latitude;
		public uploadPosition(Double longitude, Double latitude){
			this.longitude = longitude;
			this.latitude = latitude;
		}
		public void run(){
			String TargetURL = "http://192.168.11.253:8080/bt/com/conn/android/uploadPosition";/////////////////////////////
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(TargetURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JpushApplication JpushApp = (JpushApplication)getApplicationContext();
			params.add(new BasicNameValuePair("monitorName", JpushApp.getUsername()));
			params.add(new BasicNameValuePair("longitude", longitude.toString()));
			params.add(new BasicNameValuePair("latitude", latitude.toString()));
			HttpEntity entity;
			try {
				entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
			} catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
	}
	/*
	 * ����͹��ʱ֪ͨ�໤��
	 */
	public class Alarm extends Thread{
		private String message;
		public Alarm(String message) {   
	        this.message = message;  
	    } 
		public void run(){
			String TargetURL = "http://192.168.11.253:8080/bt/com/conn/android/boundAlarm";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(TargetURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("alarm", message));
			HttpEntity entity;
			try {
				entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/*
	 * �жϵ�ǰλ���Ƿ���磬������緵��true,���򷵻�false
	 */
	public boolean isOutOfBound(Double longitude, Double latitude, Monitor mo){
		
		double C = java.lang.Math.sin(latitude) * java.lang.Math.sin(mo.getLatitude())* java.lang.Math.cos(longitude-mo.getLongitude()) + java.lang.Math.cos(latitude) * java.lang.Math.cos(mo.getLatitude());

		if( (6371.004  * java.lang.Math.acos(C) * java.lang.Math.PI /180 ) > mo.getRadius())
			return true;
		else
			return false;
	}
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		System.out.println("service stop");
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
}
