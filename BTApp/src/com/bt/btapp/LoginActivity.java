package com.bt.btapp;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.LocationManager;
import android.location.LocationListener;


import cn.jpush.android.api.JPushInterface;

import com.bt.btapp.R;


public class LoginActivity extends Activity 
{
	
	private String Response = null;
	private String username = null;
	private String password = null;
	static JSONObject result = null;	
		
	
	 private LocationManager locationManager;
	 private LocationListener locationListener;
	 
	 private double lat=0;
	 private double lng=0;
	 private boolean login = false;
	
	    
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		locationManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			 System.out.println("GPS");
			 locationListener = new LocationListener(){
				 @Override  
			        public void onStatusChanged(String provider, int status, Bundle extras) {  
			                      
			        }  
					// Provider被enable时触发此函数，比如GPS被打开  
		            @Override  
		            public void onProviderEnabled(String provider) {  
		                  
		            }  
		              
		            // Provider被disable时触发此函数，比如GPS被关闭   
		            @Override  
		            public void onProviderDisabled(String provider) {  
		                  
		            }  
		            
		            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
		            @Override  
		            public void onLocationChanged(Location location) {  
		                if (location != null) {     
		                   lat= location.getLatitude(); //经度     
		                   lng = location.getLongitude(); //纬度  
		                   //System.out.println("GPS:lat:"+lat+"-lng:"+lng);
		                }  
		            }//end  onLocationChanged 
			 };
			 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0,locationListener);     
			 Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   
			 if(location != null){   
				 lat= location.getLatitude(); //经度     
				 lng = location.getLongitude(); //纬度  
				 System.out.println("GPS:"+lat+" , "+lng);
			 }     
		}
		else{
			System.out.println("wifi");
			locationListener = new LocationListener(){
				@Override  
				public void onStatusChanged(String provider, int status, Bundle extras) {  
                     
				}     
				// Provider被enable时触发此函数，比如GPS被打开  
				@Override  
				public void onProviderEnabled(String provider) {  
                     
				}   
				// Provider被disable时触发此函数，比如GPS被关闭   
				@Override  
				public void onProviderDisabled(String provider) {  
                     
				}  
				//当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发   
				@Override  
				public void onLocationChanged(Location location) {  
					if (location != null) {     
						lat= location.getLatitude(); //经度     
						lng = location.getLongitude(); //纬度  
						//System.out.println("wifi:lat:"+location.getLatitude()+"-lng:"+location.getLongitude()); 
					}  
				}//end  onLocationChanged
			};		 
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000, 0,locationListener);     
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
			if(location != null){  
				lat= location.getLatitude(); //经度     
				lng = location.getLongitude(); //纬度  
				System.out.println("WIFI:"+lat+" , "+lng);
			}    	 	 
		}
		final	EditText edittext1 = (EditText)findViewById(R.id.edittext_username);		 		
		final	EditText edittext2 = (EditText)findViewById(R.id.edittext_password);
		edittext1.setText("ab");
		edittext2.setText("ab");
		Button mButton1 = (Button)findViewById(R.id.buttonLogin);
	
	mButton1.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v){
			username = edittext1.getText().toString();
			password = edittext2.getText().toString();
	        CountDownLatch threadSignal = new CountDownLatch(1);
			new Login(threadSignal).start();
			try {
				threadSignal.await();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//等待所有子线程执行完
			
			//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
			try{
				Response = result.getString("HEXLE");

				//Toast.makeText(LoginActivity.this, Response, Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(Response.equals("1")==true)	//如果登陆成功，则从服务器得到所有的监护信息，监护信息存放在JpushApplication下的monitorList中									
			{
				Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
				JpushApplication JpushApp = (JpushApplication)getApplicationContext();
				
				try {
					//如果有monitor信息，则执行以下程序，否则抛出异常
					String str = result.getString("monitor");										
					String []tempStr = str.split(";");
					Monitor m = new Monitor();
					for(int i=0; i<tempStr.length; i++){
						String []monitorStr = tempStr[i].split(",");
						m.setUsername(monitorStr[0]);
						m.setLatitude(Double.parseDouble(monitorStr[1]));
						m.setLongitude(Double.parseDouble(monitorStr[2]));
						m.setRradius(Double.parseDouble(monitorStr[3]));
						m.setTime(Timestamp.valueOf(monitorStr[4]));
						JpushApp.addMonitor(m);
						//System.out.println(m.getUsername()+"  "+m.getLongitude()+"  "+m.getLatitude()+"  "+m.getRadius());
					}
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					System.out.println("there is not monitor info");
					e.printStackTrace();
				}
				
				JpushApp.setUsername(username);
				JpushApp.JpushInit();
				
				Intent _intent = 
					new  Intent(LoginActivity.this,FunctionModuleActivity.class);
				startActivity(_intent);
				LoginActivity.this.finish();
			}
			else if(Response.equals("-1")==true)
			{
				Toast.makeText(LoginActivity.this, "the username is not existed!", Toast.LENGTH_SHORT).show();
				edittext1.setText("");
		        edittext1.requestFocus();
			}
			else if(Response.equals("-2")==true)
			{
				Toast.makeText(LoginActivity.this, "the password is error!", Toast.LENGTH_SHORT).show();
				edittext2.setText("");
		        edittext2.requestFocus();
			}
			else if(Response.equals("-3")==true)
			{
				Toast.makeText(LoginActivity.this, " far distance!", Toast.LENGTH_SHORT).show();
				edittext1.setText("");
	            edittext2.setText("");
	            edittext1.requestFocus();
			}
			else
			{
				Toast.makeText(LoginActivity.this, "input error!", Toast.LENGTH_SHORT).show();
				edittext1.setText("");
	            edittext2.setText("");
	            edittext1.requestFocus();
			}
		}
	}); 
	Button button0 = (Button)findViewById(R.id.buttonClear);   
    button0.setOnClickListener(new OnClickListener() {  
        @Override  
        public void onClick(View arg0) 
        {  		    
        	edittext1.setText("");
            edittext2.setText("");
            edittext1.requestFocus();
        }  
    });  	 
		 
	}

	class Login extends Thread{
		private CountDownLatch count;
		
		public Login(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			//String TargetURL = "http://101.5.203.217:8080/bt/com/conn/android/login";	
			//String TargetURL = "http://166.111.96.153:20251/bt/com/conn/android/login";
			String TargetURL = "http://192.168.11.253:8080/bt/com/conn/android/login";
			HttpClient httpClient = new DefaultHttpClient();
			
			try
			{
				HttpPost request = new HttpPost(TargetURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("Key","login" ));
				params.add(new BasicNameValuePair("login",username+","+password+","+lng+","+lat ));
				
                
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					result = new JSONObject(retSrc);
					String asd;
					asd = "123";
				}
				else
				{
					String asd;
					asd = "123";
				}	
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			count.countDown();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

    @Override
    protected void onPause()
    {
   	 if(locationManager!=null)
   	 {
   		 locationManager.removeUpdates(locationListener);
   	 }
   	 super.onPause();
   	 JPushInterface.onPause(this);
    }
	
    @Override
    protected void onResume()
    {
   	 super.onResume();
   	 JPushInterface.onResume(this);
    }
}
