package com.any.wmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.text.format.DateFormat;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.core.LatLonPoint;
import com.amap.api.search.poisearch.PoiItem;
import com.amap.api.search.poisearch.PoiPagedResult;
import com.amap.api.search.poisearch.PoiSearch;
import com.amap.api.search.poisearch.PoiSearch.SearchBound;
import com.amap.api.search.poisearch.PoiTypeDef;
import com.amap.api.search.route.Route;
import com.amap.wmap.util.AMapUtil;
import com.amap.wmap.util.Constants;
import com.amap.wmap.util.ToastUtil;
import com.bt.btapp.R;

public class WMapUIDActivity extends FragmentActivity implements
OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener, InfoWindowAdapter,AMapLocationListener,LocationSource{
	
	private AMap aMap;
	private Marker mymarker;
	private Marker myLocMarker;
	private LatLng myLocation;
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.CHINA);
	static JSONObject result_1 = null;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;	
	private String Response = null;
	private String Point_info = null;
	private String update_time = null;
	private String Marker_info = null;
	private PoiPagedResult result;
	private ProgressDialog progDialog = null;
	private int curpage = 1;
	private int cnt = 0;
	private String Location_info;
	private String upload_state;
	private String Init_state;
	private Button button_trace;
	private Button button_pause;
	private Button button_resume;
	private Button button_init;
	private LatLng marker;
	private String Scanner;
	private String Content;
	
	private String uid = null;
	private String username = null;
	
	private boolean trace_color = false;
	int num = 0;
	double[] dd=new double[4];
	double lat=0;
	double lng=0;
	private boolean init_flag = false;
	private boolean init_flag_start = false;
	private boolean pause_flag = false;
	private static int zoomLevel = 13;
	//private String TargetURL_MapTrace = "http://101.5.193.253:8080/btDemo2/com/android/conn/ServletTest";
	private String TargetURL_MapTrace = "http://166.111.96.153:20251/bt/com/conn/android/locusDownload";
	private String TargetURL_PointUpload = "http://166.111.96.153:20251/bt/com/conn/android/locusUpload";
	private String TargetURL_UserInit = "http://166.111.96.153:20251/bt/com/conn/android/locusInit";
	private String TargetURL_MapMarker = "http://166.111.96.153:20251/bt/com/conn/android/locusMark";
//	private String TargetURL_MapTrace = "http://101.5.192.119:8080/bt/com/conn/android/locusDownload";
//	private String TargetURL_PointUpload = "http://101.5.192.119:8080/bt/com/conn/android/locusUpload";
//	private String TargetURL_UserInit = "http://101.5.192.119:8080/bt/com/conn/android/locusInit";
//	private String TargetURL_MapMarker = "http://101.5.192.119:8080/bt/com/conn/android/locusMark";
//	private String TargetURL_MapTrace = "http://192.168.11.241:8080/bt/com/conn/android/locusDownload";
//	private String TargetURL_PointUpload = "http://192.168.11.241:8080/bt/com/conn/android/locusUpload";
//	private String TargetURL_UserInit = "http://192.168.11.241:8080/bt/com/conn/android/locusInit";
//	private String TargetURL_MapMarker = "http://192.168.11.241:8080/bt/com/conn/android/locusMark";
	/*
	 * 路径导航变量
	 */
	private List<Route> routeResult;
	private RouteOverlay routeOverlay;
	private Route route;
	private LinearLayout routeNav;
	private ImageButton routePre, routeNext;
	private int mode = Route.BusDefault;//BusDefault 路径为公交模式。DrivingDefault 路径为自驾模式。WalkDefault 路径为步行模式。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wmap_uid);
		
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		uid = bundle.getString("uid");
		username = bundle.getString("username");
		
		
		init();
		button_trace = (Button) findViewById(R.id.button_trace);
		/*button_init = (Button) findViewById(R.id.button_init);
		button_pause = (Button) findViewById(R.id.button_pause);
		button_resume = (Button) findViewById(R.id.button_resume);*/
		button_trace.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	
                CountDownLatch threadSignal = new CountDownLatch(1);
        		new Map_trace(threadSignal).start();
        		try {
        			threadSignal.await();
        			
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}//等待所有子线程执行完
        		
        		//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
        		try{
        			Point_info = result_1.getString("HEXLE");
        			//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        		//Toast.makeText(WMapActivity.this, Point_info, Toast.LENGTH_SHORT).show();
        		String point_info_package[] = Point_info.split(";");
        		
        	    for(int j=0; j < point_info_package.length; j++)
        	    {	
        	    	
        	    	String point_info[] = point_info_package[j].split(",");
        	    	
        	    	trace_color = !trace_color;
        	    	
        	    	if(point_info.length>=5){
                		dd[0]=Double.parseDouble(point_info[1]);
                		dd[1] = Double.parseDouble(point_info[2]);
                	    for(int i=5; i < point_info.length; i += 4)
                	    {	
                	    	//point_info[i] = point_info[i].substring(1,point_info[i].length()-1);
                	    	dd[2] = Double.parseDouble(point_info[i]);;
                	    	dd[3] = Double.parseDouble(point_info[i + 1]);
                	    	//dd[i] = Double.parseDouble(point_info[i]);
                	    	if(trace_color == true)
                	    	{
                     			 aMap.addPolyline((new PolylineOptions()).add(new LatLng(dd[0],dd[1]),       	               
                      					 new LatLng(dd[2],dd[3]))  
                    	                 .width(5).color(Color.RED)); 
                	    	}
                	    	else
                	    	{
                     			 aMap.addPolyline((new PolylineOptions()).add(new LatLng(dd[0],dd[1]),       	               
                      					 new LatLng(dd[2],dd[3]))  
                    	                 .width(5).color(Color.GREEN)); 
                	    	}

                  			 dd[0] = dd[2];
                  			 dd[1] = dd[3];
                	    }
        	    	}       
        	    }
        		
                CountDownLatch threadSignal_1 = new CountDownLatch(1);
        		new Map_Marker(threadSignal_1).start();
        		try {
        			threadSignal_1.await();
        			
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}//等待所有子线程执行完
        		
        		//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
        		try{
        			Marker_info = result_1.getString("HEXLE");
        			//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        		String marker_info[] = Marker_info.split(",");       		
    	    	if(marker_info.length>=5){
            		dd[0]=Double.parseDouble(marker_info[4]);
            		dd[1] = Double.parseDouble(marker_info[5]);
            	    for(int i=6; i < marker_info.length; i += 6)
            	    {	
            	    	//point_info[i] = point_info[i].substring(1,point_info[i].length()-1);
            	    	dd[2] = Double.parseDouble(marker_info[i+4]);;
            	    	dd[3] = Double.parseDouble(marker_info[i+5]);
              			marker = new LatLng(dd[2],dd[3]);  
              			Scanner = "处理人：" + marker_info[i];
              			Content = marker_info[i+1] + "," + marker_info[i+2] + "," + marker_info[i+3];
               			mymarker=aMap.addMarker(new MarkerOptions().position(marker).title(Scanner)
               					 .snippet(Content)
               			         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
               			         .draggable(false));
               			mymarker.showInfoWindow();
               			aMap.moveCamera(CameraUpdateFactory
               					.newLatLngZoom(marker, zoomLevel));
            	    }
    	    	}   

        		
            }  
        });  
/*
		button_init.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	init_flag_start = true;
            }  
        });
		
		button_pause.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	pause_flag = true;
            }  
        });
		
		button_resume.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	pause_flag = false;
            }  
        });
		*/
		
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}	*/
	/*
	 * 地图初始化
	 */
	public void init(){
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.wmap)).getMap();
			if (AMapUtil.checkReady(this, aMap)) {
				setupMap();
			}
		}		
	}
	/*
	 * 地图设置
	 */
	public void setupMap(){
		UiSettings uiSettings=aMap.getUiSettings();
		uiSettings.setCompassEnabled(true);
		//uiSettings.setMyLocationButtonEnabled(true);
		uiSettings.setScaleControlsEnabled(true);
		//uiSettings.setScrollGesturesEnabled(false);默认为true
		//uiSettings.setLogoPosition(int arg0);
		//uiSettings.setRotateGesturesEnabled(false);//默认为true
		uiSettings.setTiltGesturesEnabled(true);//倾斜旋转以获取3D效果
		//指定北京经纬度
		LatLng sh=new LatLng(39.999744,116.330124);
		//CameraUpdateFactory.newLatLng(sh);//返回一个移动目的地的屏幕中心点的经纬度的CameraUpdate 对象。
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sh,12));
		
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器	
		//aMap.getScalePerPixel();//返回每像素代表的距离
		//长按地图
		aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),"您正在长按地图", Toast.LENGTH_SHORT).show();
			}
		});
		//-----自定义定位图标--------------------------------------
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.GRAY);
		myLocationStyle.strokeWidth(3);
		aMap.setMyLocationStyle(myLocationStyle);
		mAMapLocationManager = LocationManagerProxy
				.getInstance(WMapUIDActivity.this);
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false		
	}

	@Override
	protected void onPause() {
		super.onPause();
		deactivate();
		JPushInterface.onPause(this);
	}
	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
	/*
	 * 菜单响应事件 
	 */
	/*
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		 case R.id.menu1:
			 aMap.getMapPrintScreen(new AMap.onMapPrintScreenListener() {
				
				@Override
				public void onMapPrint(Drawable r) {
					// TODO Auto-generated method stub
					Bitmap bitmap=((BitmapDrawable)r).getBitmap();
					try {  
			            FileOutputStream fos = new FileOutputStream(Environment  
			                    .getExternalStorageDirectory() + "/test_"  
			                    + sdf.format(new Date()) + ".png");  
			            boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);  
			            try {  
			                fos.flush();  
			            } catch (IOException e) {  
			                e.printStackTrace();  
			            }  
			            try {  
			                fos.close();  
			            } catch (IOException e) {  
			                e.printStackTrace();  
			            }  
			            if (b)  
			                Toast.makeText(WMapActivity.this, "截屏成功",  
			                        Toast.LENGTH_SHORT).show();  
			        } catch (FileNotFoundException e) {  
			                    e.printStackTrace();  
			        }
				}
			});
			 break;
		 case R.id.menu2:
			 //添加Marker  东方明珠：31.23983,121.499924
			 LatLng marker = new LatLng(31.23983,121.499924);  
			 mymarker=aMap.addMarker(new MarkerOptions().position(marker).title("东方明珠电视塔")
					 .snippet("上海最高的电视塔")  
			         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
			         .draggable(true)); 			
			 break;	
		 case R.id.menu3:
			 //添加多段线
			 aMap.addPolyline((new PolylineOptions()).add(new LatLng(31.238142,121.501512),   
	                 new LatLng(31.239114,121.506533),   
	                 new LatLng(31.230307,121.503786))  
	                 .width(5).color(Color.RED));  
			 break;		
		 case R.id.menu4:
			 //添加多边形
			 aMap.addPolygon(new PolygonOptions().addAll(createRectangle  
		                (new LatLng(31.233335,121.497284),0.01, 0.01)).fillColor(Color.YELLOW)  
		                .strokeColor(Color.GREEN).strokeWidth(3));
			 break;
		 case R.id.menu5:
			 aMap.addCircle(new CircleOptions().center(new LatLng(31.232546,121.473328))  
                     .radius(1000).strokeColor(Color.BLUE).strokeWidth(3).fillColor(Color.TRANSPARENT).visible(true));  
			 break;
		 case R.id.menu6:
			 //Toast.makeText(getApplicationContext(),"高德地图遥感影像", Toast.LENGTH_SHORT).show();
			 aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
			 aMap.setTrafficEnabled(false);
			 break;	
		 case R.id.menu7:
			 aMap.setMapType(AMap.MAP_TYPE_NORMAL);
			 aMap.setTrafficEnabled(false);
			 break;
		 case R.id.menu8:
			 aMap.setTrafficEnabled(true);
			 break;
		 case R.id.menu9:
			 //mymarker.remove();
			 aMap.clear();//清除所有覆盖物
			 break;
		 case R.id.menu10:
			 //位置跳转，可以从当前视图跳到定位的位置
			 //动画放大效果的代码
			 //CameraUpdate cu=CameraUpdateFactory.zoomIn();
			// aMap.animateCamera(cu);
			 //aMap.moveCamera(cu);//无动画效果
			 //………………………………………………………………………………
			 CameraUpdate newLoc=CameraUpdateFactory.newLatLngZoom(new LatLng(31.209333,121.62659),12);//可以改为GPS获取的位置
			 aMap.animateCamera(newLoc);
			 break;
		 case R.id.menu11:
			//POI搜索
			 doSearchQuery("KTV");
			 break;
		 case R.id.menu12:
			 //测试查询个人位置到东方明珠(31.240655,121.499727)的路线，给定两个经纬度点：LatLngPoint
			 routeSearch(new LatLonPoint(lat,lng),new LatLonPoint(31.240655,121.499727));
		}
		return super.onMenuItemSelected(featureId, item);
	}
	*/
	
	private List<LatLng> createRectangle(LatLng center, double halfWidth,
			double halfHeight) {
		return Arrays.asList(new LatLng(center.latitude - halfHeight,
				center.longitude - halfWidth), new LatLng(center.latitude
				- halfHeight, center.longitude + halfWidth), new LatLng(
				center.latitude + halfHeight, center.longitude + halfWidth),
				new LatLng(center.latitude + halfHeight, center.longitude
						- halfWidth));
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		/*String curDes = "Draged current position:(lat,lng)\n("
			+ arg0.getPosition().latitude + ","
			+ arg0.getPosition().longitude + ")";
	    Toast.makeText(this,arg0.getTitle() + curDes , Toast.LENGTH_SHORT).show();*/
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		String curDes = "Draged current position:(lat,lng)\n("
			+ arg0.getPosition().latitude + ","
			+ arg0.getPosition().longitude + ")";
	    Toast.makeText(this,arg0.getTitle() + curDes , Toast.LENGTH_SHORT).show();
		Toast.makeText(this,arg0.getTitle() +"拖动完毕" , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		String curDes = "Draged current position:(lat,lng)\n("
			+ arg0.getPosition().latitude + ","
			+ arg0.getPosition().longitude + ")";
	    Toast.makeText(this,arg0.getTitle() + curDes , Toast.LENGTH_SHORT).show();
		Toast.makeText(this,arg0.getTitle() + "开始拖动" , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		//必须使用equals
		if(marker.equals(mymarker)){
			Toast.makeText(this,"在这里执行点击事件" , Toast.LENGTH_SHORT).show();
		}
		return false;
	}
    //定位所需实现的方法
	//……………废弃的方法…………………………
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
    //---------------------------------------------
	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (mListener != null) {
			mListener.onLocationChanged(location);
		}
		lat=location.getLatitude();
		lng=location.getLongitude();
		Location_info = location.getLatitude()+"," + location.getLongitude();
		num ++;
			
		if(init_flag_start == true)
		{
			if(init_flag == false)
			{
	            CountDownLatch threadSignal = new CountDownLatch(1);
	    		new User_init(threadSignal).start();
	    		try {
	    			threadSignal.await();
	    			
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}//等待所有子线程执行完
	    		
	    		//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
	    		try{
	    			Init_state = result_1.getString("HEXLE");
	    			//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    		Toast.makeText(WMapUIDActivity.this, "Init_State: "+Init_state, Toast.LENGTH_SHORT).show();
	    		init_flag = true;
			}
		
			if(pause_flag == false)
			{
		        CountDownLatch threadSignal_1 = new CountDownLatch(1);
				new Point_upload(threadSignal_1).start();
				try {
					threadSignal_1.await();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//等待所有子线程执行完
				
				//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
				try{
					upload_state = result_1.getString("HEXLE");
					//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
				Toast.makeText(WMapUIDActivity.this, "Upload_State: " + upload_state, Toast.LENGTH_SHORT).show();
			}
		
		
		}

		
		
		
		
		
		//显示抓取的经纬度信息
		//Toast.makeText(WMapActivity.this, Location_info, Toast.LENGTH_SHORT).show();
		
		
		//获取位置后再加一个marker-myLoc,先清除上次的标记（这样可能会引起路线图标变成马赛克）
		/*if(myLocMarker!=null){
			myLocMarker.remove();
		}
		myLocation = new LatLng(lat,lng);  
		myLocMarker=aMap.addMarker(new MarkerOptions().position(myLocation).title("我的位置")
				 .snippet("上海最高的电视塔")  
		         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
		         .draggable(false)); */
		         
	}
    //………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………………
	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
		}
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
		 */
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		// 发送定位请求。LocationProviderProxy.AMapNetwork，定位提供者名称；5000，位置变化通知时间，单位毫秒，
		//设置数值为5000以上；10，位置变化通知距离，单位米;this，监听listener。
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}		
	//-----------------------------------------------------------------------
	/**
	 * 一次性打印多个Marker出来
	 */
	private void addMarkers(List<PoiItem> poiItems) {
		for (int i = 0; i < poiItems.size(); i++) {
			aMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(
									poiItems.get(i).getPoint().getLatitude(),
									poiItems.get(i).getPoint().getLongitude()))
					.title(poiItems.get(i).getTitle())
					.snippet(poiItems.get(i).getSnippet())
					.icon(BitmapDescriptorFactory.defaultMarker()));
		}

	}

	private LatLngBounds getLatLngBounds(List<PoiItem> poiItems) {
		LatLngBounds.Builder b = LatLngBounds.builder();
		for (int i = 0; i < poiItems.size(); i++) {
			b.include(new LatLng(poiItems.get(i).getPoint().getLatitude(),
					poiItems.get(i).getPoint().getLongitude()));
		}
		return b.build();
	}

	private void showPoiItem(List<PoiItem> poiItems) {
		if (poiItems != null && poiItems.size() > 0) {
			if (aMap == null)
				return;
			aMap.clear();
			LatLngBounds bounds = getLatLngBounds(poiItems);
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
			addMarkers(poiItems);
		} else {
			ToastUtil.show(getApplicationContext(), "没有搜索到数据！");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.POISEARCH) {
				dissmissProgressDialog();// 隐藏对话框

				if (result != null) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								final List<PoiItem> poiItems = result
										.getPage(1);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										showPoiItem(poiItems);// 每页显示10个poiitem
									}
								});

							} catch (AMapException e) {
								e.printStackTrace();

							}
						}
					}).start();
				}

			} else if (msg.what == Constants.ERROR) {
				dissmissProgressDialog();// 隐藏对话框
				ToastUtil.show(getApplicationContext(), "搜索失败,请检查网络连接！");
			} else if(msg.what == Constants.ROUTE_SEARCH_RESULT){
				progDialog.dismiss();
				if (routeResult != null && routeResult.size() > 0) {
					route = routeResult.get(0);
					Log.v("路线条数：", String.valueOf(routeResult.size()));
					if (route != null) {
						routeOverlay = new RouteOverlay(WMapUIDActivity.this,
								aMap, route);
						routeOverlay.removeFormMap();
						routeOverlay.addMarkerLine();
						routeNav.setVisibility(View.VISIBLE);
						routePre.setBackgroundResource(R.drawable.prev_disable);
						routeNext.setBackgroundResource(R.drawable.next);
					}
				}
			}else if (msg.what == Constants.POISEARCH_NEXT) {//POI搜索结果之翻页功能
				curpage++;
				new Thread(new Runnable() {

					@Override
					public void run() {
						final List<PoiItem> poiItems;
						try {
							poiItems = result.getPage(curpage);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showPoiItem(poiItems);// 每页显示10个poiitem
								}
							});
						} catch (AMapException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	};
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索:\n");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	
	public void doSearchQuery(final String searchName) {
		
		curpage = 1;
		cnt = 0;
		showProgressDialog();// 显示进度框
		new Thread(new Runnable() {
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch(
							WMapUIDActivity.this, new PoiSearch.Query(
									searchName, PoiTypeDef.All, "021")); // 设置搜索字符串，poi搜索类型，poi搜索区域（空字符串代表全国）
					poiSearch.setPageSize(10);// 设置搜索每次最多返回结果数
					//以自己的定位位置为中心设置搜索 区域
					if(lat!=0&&lng!=0){
						poiSearch.setBound(new SearchBound(new LatLonPoint(lat,lng), 5000));
					}				
					result = poiSearch.searchPOI();
					if (result != null) {
						cnt = result.getPageCount();
					}
					handler.sendMessage(Message.obtain(handler,
							Constants.POISEARCH));
				} catch (AMapException e) {
					handler.sendMessage(Message
							.obtain(handler, Constants.ERROR));
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void routeSearch(LatLonPoint startPoint, LatLonPoint endPoint){
		progDialog = ProgressDialog.show(WMapUIDActivity.this, null,
				"正在获取线路", true, true);
		final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
				endPoint);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					routeResult = Route.calculateRoute(WMapUIDActivity.this,
							fromAndTo, mode);
					if (progDialog.isShowing()) {
						if (routeResult != null || routeResult.size() > 0)
							handler.sendMessage(Message
									.obtain(handler,Constants.ROUTE_SEARCH_RESULT));
					}
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constants.ROUTE_SEARCH_ERROR;
					msg.obj = e.getErrorMessage();
					handler.sendMessage(msg);
				}
			}
		});
		t.start();

	}

	
	
	class Map_trace extends Thread{
		private CountDownLatch count;
		
		public Map_trace(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			//String TargetURL = "http://101.5.146.68:8080/btDemo2/com/android/conn/ServletTest";
			HttpClient httpClient = new DefaultHttpClient();
			
			try
			{
				HttpPost request = new HttpPost(TargetURL_MapTrace);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("locusDownload", "bid,187992361049861"));
				params.add(new BasicNameValuePair("locusDownload", uid));
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
				//request.getParam("res");
				
				
				
//				HttpEntity entity1 = request.getEntity();
//				String retSrc = EntityUtils.toString(entity1);
//				result_1 = new JSONObject(retSrc);
				
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					result_1 = new JSONObject(retSrc);

				}
				else
				{


				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			count.countDown();
		}
	}
	
	
	
	
	class Point_upload extends Thread{
		private CountDownLatch count;
		
		public Point_upload(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			//String TargetURL = "http://101.5.146.68:8080/btDemo2/com/android/conn/ServletTest";
			HttpClient httpClient = new DefaultHttpClient();
			
			try
			{
				HttpPost request = new HttpPost(TargetURL_PointUpload);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				update_time = (String) new DateFormat().format("yyyy-MM-dd kk:mm:ss",Calendar.getInstance(Locale.CHINA));
				String order = Integer.toString(num);
				//params.add(new BasicNameValuePair("Location_info", Location_info));
				//params.add(new BasicNameValuePair("num", Integer.toString(num)));
				//params.add(new BasicNameValuePair("update_time", update_time));
				String Upload = "Manufactering_001," + Init_state + "," + Location_info + "," + order + "," + update_time ;
				params.add(new BasicNameValuePair("upload", Upload));
				
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					result_1 = new JSONObject(retSrc);

				}
				else
				{

				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			count.countDown();
		}
	}
	
	class User_init extends Thread{
		private CountDownLatch count;
		
		public User_init(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			//String TargetURL = "http://101.5.146.68:8080/btDemo2/com/android/conn/ServletTest";
			HttpClient httpClient = new DefaultHttpClient();
			
			try
			{
				HttpPost request = new HttpPost(TargetURL_UserInit);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("init", "Manufactering_001,187992361049861"));
                
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
				//request.getParam("res");
				
				
				
//				HttpEntity entity1 = request.getEntity();
//				String retSrc = EntityUtils.toString(entity1);
//				result_1 = new JSONObject(retSrc);
				
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					result_1 = new JSONObject(retSrc);

				}
				else
				{


				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			count.countDown();
		}
	}
	
	class Map_Marker extends Thread{
		private CountDownLatch count;
		
		public Map_Marker(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			//String TargetURL = "http://101.5.146.68:8080/btDemo2/com/android/conn/ServletTest";
			HttpClient httpClient = new DefaultHttpClient();
			
			try
			{
				HttpPost request = new HttpPost(TargetURL_MapMarker);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
//				params.add(new BasicNameValuePair("locusMark", "bid,aa"));
				params.add(new BasicNameValuePair("locusMark", "bid,"));
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
				//request.getParam("res");
				
				
				
//				HttpEntity entity1 = request.getEntity();
//				String retSrc = EntityUtils.toString(entity1);
//				result_1 = new JSONObject(retSrc);
				
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					result_1 = new JSONObject(retSrc);

				}
				else
				{


				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			count.countDown();
		}
	}
	
	
}
