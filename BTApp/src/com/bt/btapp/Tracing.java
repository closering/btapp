package com.bt.btapp;
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

import cn.jpush.android.api.JPushInterface;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
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
import com.amap.api.search.poisearch.PoiTypeDef;
import com.amap.api.search.poisearch.PoiSearch.SearchBound;
import com.amap.api.search.route.Route;
import com.amap.wmap.util.AMapUtil;
import com.amap.wmap.util.Constants;
import com.amap.wmap.util.ToastUtil;
import com.any.wmap.RouteOverlay;
import com.any.wmap.WMapUIDActivity;
import com.bt.btapp.R;
import com.timeline.OneStatusEntity;
import com.timeline.StatusExpandAdapter;
import com.timeline.TwoStatusEntity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;


public class Tracing extends FragmentActivity implements OnClickListener,OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener, InfoWindowAdapter,AMapLocationListener,LocationSource{
	LayoutInflater 		inflater;
	View			 	first;
	View 				second;
	TextView			textView1;  //��������һ������ 
	TextView			textView2;  //�������ڶ������� 
	ViewPager 			viewPager;  
	ImageView 			image;      //�ƶ����ǵ����ߣ���ʵ��һ��imageviwe
	int 				moveX;    //�����������ƫ�ƿ��
	int 				width;    //��������Ƚϴֵ��ߵĿ��
	int 				index;    //��ǰ��һ��view
	List<View> 			viewList=new ArrayList<View>();
	
	//**************************Messge tracing*******************************//
	//private TextView UidInfo;
	private static final String TAG = "MainActivity";
	private List<OneStatusEntity> oneList;
	private ExpandableListView expandlistView;
	private StatusExpandAdapter statusAdapter;
	private Context context;
	
	private String productName = null;
	private String logisticsInfo = null;
	//private String uidInfo = null;
	//**************************Messge tracing*******************************//
	//**************************GIS tracing*******************************//
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
	private String TargetURL_MapTrace = "http://192.168.11.253:8080/bt/com/conn/android/locusDownload";
	private String TargetURL_PointUpload = "http://192.168.11.253:8080/bt/com/conn/android/locusUpload";
	private String TargetURL_UserInit = "http://192.168.11.253:8080/bt/com/conn/android/locusInit";
	private String TargetURL_MapMarker = "http://192.168.11.253:8080/bt/com/conn/android/locusMark";
	/*
	 * ·����������
	 */
	private List<Route> routeResult;
	private RouteOverlay routeOverlay;
	private Route route;
	private LinearLayout routeNav;
	private ImageButton routePre, routeNext;
	private int mode = Route.BusDefault;//BusDefault ·��Ϊ����ģʽ��DrivingDefault ·��Ϊ�Լ�ģʽ��WalkDefault ·��Ϊ����ģʽ��
	//**************************GIS tracing*******************************//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tracing);
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		//uidInfo = bundle.getString("uidInfo");
		productName = bundle.getString("productName");
		logisticsInfo = bundle.getString("logisticsInfo");
		init();	
		
		/*************************************GIS tracing***************************************/
		uid = bundle.getString("uid");
		username = bundle.getString("username");
		init_trace();
		button_trace = (Button) second.findViewById(R.id.button_map_tracing);
		button_trace.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
            	System.out.println("tracing!!!!!!!!!!!!!!11");
                CountDownLatch threadSignal = new CountDownLatch(1);
        		new Map_trace(threadSignal).start();
        		try {
        			threadSignal.await();
        			
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}//�ȴ��������߳�ִ����
        		
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
                	    	dd[2] = Double.parseDouble(point_info[i]);;
                	    	dd[3] = Double.parseDouble(point_info[i + 1]);
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
                /*CountDownLatch threadSignal_1 = new CountDownLatch(1);
        		new Map_Marker(threadSignal_1).start();
        		try {
        			threadSignal_1.await();
        			
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}//�ȴ��������߳�ִ����
        		try{
        			Marker_info = result_1.getString("HEXLE");
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        		String marker_info[] = Marker_info.split(",");       		
    	    	if(marker_info.length>=5){
            		dd[0]=Double.parseDouble(marker_info[4]);
            		dd[1] = Double.parseDouble(marker_info[5]);
            	    for(int i=6; i < marker_info.length; i += 6)
            	    {	
            	    	dd[2] = Double.parseDouble(marker_info[i+4]);;
            	    	dd[3] = Double.parseDouble(marker_info[i+5]);
              			marker = new LatLng(dd[2],dd[3]);  
              			Scanner = "�����ˣ�" + marker_info[i];
              			Content = marker_info[i+1] + "," + marker_info[i+2] + "," + marker_info[i+3];
               			mymarker=aMap.addMarker(new MarkerOptions().position(marker).title(Scanner)
               					 .snippet(Content)
               			         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
               			         .draggable(false));
               			mymarker.showInfoWindow();
               			aMap.moveCamera(CameraUpdateFactory
               					.newLatLngZoom(marker, zoomLevel));
            	    }
    	    	} */  
            }  
        });  
		/*************************************GIS tracing***************************************/
	}
	public void init(){
		viewPager	=(ViewPager) findViewById(R.id.viewpager1);
		inflater	=this.getLayoutInflater().from(this);
		first		=inflater.inflate(R.layout.view_msg_racing, null);
		second		=inflater.inflate(R.layout.view_gis_tracing,null);
		textView1	=(TextView) findViewById(R.id.MsgTracing);
		textView2	=(TextView) findViewById(R.id.GISTracing);
		image		=(ImageView) findViewById(R.id.iamge1);

		textView1	.setOnClickListener(this);
		textView2	.setOnClickListener(this);
		viewList	.add(first);  
		viewList	.add(second);
		viewPager	.setAdapter(new MyPagerAdapter(viewList));
		viewPager	.setOnPageChangeListener(new MyPageListener());
		
		
		
		DisplayMetrics dm 	= new DisplayMetrics();  //��ȡ�ֻ��ֱ���
		getWindowManager()	.getDefaultDisplay().getMetrics(dm);
		int screenW 		= dm.widthPixels;// ��ȡ�ֱ��ʿ��

		width	= BitmapFactory.decodeResource(getResources(), R.drawable.mm).getWidth();// ��ȡͼƬ���
		// ����ƫ������Ҳ���������ֵ��»��߾�����Ļ����ľ��룬���������view��screeW/3���Դ�����
		moveX 	= (screenW / 2 - width) / 2;	
		Matrix matrix = new Matrix();
		matrix	.postTranslate(moveX, 0);
		image	.setImageMatrix(matrix);	// ���ö�����ʼλ��
		
		
		expandlistView = (ExpandableListView) first.findViewById(R.id.expandlist_tracing);
		//UidInfo = (TextView)first.findViewById(R.id.UidInfoTracing);
		//UidInfo.setText(uidInfo);
		putInitData();
		statusAdapter = new StatusExpandAdapter(first.getContext(), oneList);
		expandlistView.setAdapter(statusAdapter);
		expandlistView.setGroupIndicator(null); // ȥ��Ĭ�ϴ��ļ�ͷ
		// ��������group,�����������ó�Ĭ��չ��
		int groupCount = expandlistView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandlistView.expandGroup(i);
		}
		expandlistView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.MsgTracing:
			viewPager.setCurrentItem(0);  //���first��ʱ�����õ�һ��view��ʾ
			break;
		case R.id.GISTracing:
			viewPager.setCurrentItem(1);  //���second��ʱ�����õ�һ��view��ʾ
			break;
		}
	}


	class MyPageListener implements  OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int arg0) {
			int x=moveX*2+width;   //�ӵ�һ�����ڶ���view���ֵ��»��ߵ�ƫ����
			/**
			 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
			 * ��     float fromXDelta:���������ʾ������ʼ�ĵ��뵱ǰView X�����ϵĲ�ֵ��
				float toXDelta, ���������ʾ���������ĵ��뵱ǰView X�����ϵĲ�ֵ��
				float fromYDelta, ���������ʾ������ʼ�ĵ��뵱ǰView Y�����ϵĲ�ֵ��
				float toYDelta)���������ʾ������ʼ�ĵ��뵱ǰView Y�����ϵĲ�ֵ��
			 */
			Log.v("index��ֵΪ:", index+"");
			Log.v("arg0��ֵΪ:", arg0+"");
			Animation animation=
					new TranslateAnimation(x*index, x*arg0, 0, 0);
			index=arg0;
			
			animation	.setFillAfter(true);   //���ö���ֹͣ�ڽ���λ��
			animation	.setDuration(300);     //���ö���ʱ��
			image		.startAnimation(animation);  //��������
		}
	}
	private void putInitData() 
	{
		String[] strArray = new String[]{"PRODUCT:"+productName};
		String[] Info = logisticsInfo.split(",");
		String[] str1 = new String[Info.length/4];
		String[] timeStr1= new String[Info.length/4];
		String[] locationStr1= new String[Info.length/4];
		int k = 0;
		for(int i = 0;i<Info.length;i+=4)
		{
			
				str1[k] = "STAFF:"+Info[i];
				timeStr1[k] = "SCAN TIME:"+Info[i+1];
				locationStr1[k] = "LOCATION:"+Info[i+2]+","+Info[i+3];
				k++;
		}
		oneList = new ArrayList<OneStatusEntity>();
		for(int i=0 ; i<strArray.length ; i++){
			OneStatusEntity one = new OneStatusEntity();
			one.setStatusName(strArray[i]);
			List<TwoStatusEntity> twoList = new ArrayList<TwoStatusEntity>();
			String[] order = str1;
			String[] time = timeStr1;
			String[] location = locationStr1;
			
			for(int j=0 ; j<order.length ; j++){
				TwoStatusEntity two = new TwoStatusEntity();
				two.setStatusName(order[j]);
				two.setStatusLocation(location[j]);
				if(time[j].equals("")){
					two.setCompleteTime("����");
					two.setIsfinished(false);
				}else{
					two.setCompleteTime(time[j]+" ���");
					two.setIsfinished(true);
				}
				
				twoList.add(two);
			}
			one.setTwoList(twoList);
			oneList.add(one);
		}
		Log.i(TAG, "����״̬��"+oneList.get(0).getTwoList().get(0).getStatusName());
	}
	//*************************************GIS tracing***************************************//
	public void init_trace(){
		if (aMap == null) {
			System.out.println(1);
			aMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.wmap_tracing)).getMap();
			System.out.println(2);
			if (AMapUtil.checkReady(this, aMap)) {
				setupMap();
			}		
		}		
	}

	public void setupMap(){
		UiSettings uiSettings=aMap.getUiSettings();
		uiSettings.setCompassEnabled(true);
		uiSettings.setScaleControlsEnabled(true);
		uiSettings.setTiltGesturesEnabled(true);//��б��ת�Ի�ȡ3DЧ��
		//ָ��������γ��
		LatLng sh=new LatLng(39.999744,116.330124);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sh,12));
		
		aMap.setOnMarkerClickListener(this);// ���õ��marker�¼�������
		aMap.setOnInfoWindowClickListener(this);// ���õ��infoWindow�¼�������
		aMap.setInfoWindowAdapter(this);// �����Զ���InfoWindow��ʽ
		aMap.setOnMarkerDragListener(this);// ����marker����ק�¼�������	
		//������ͼ
		aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),"�����ڳ�����ͼ", Toast.LENGTH_SHORT).show();
			}
		});
		//-----�Զ��嶨λͼ��--------------------------------------
		// �Զ���ϵͳ��λС����
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.GRAY);
		myLocationStyle.strokeWidth(3);
		aMap.setMyLocationStyle(myLocationStyle);
		mAMapLocationManager = LocationManagerProxy
				.getInstance(Tracing.this);
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾϵͳ��λ��ť��ʾ����Ӧ�����false��ʾ���أ�Ĭ����false		
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
		Toast.makeText(this,arg0.getTitle() +"�϶����" , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		String curDes = "Draged current position:(lat,lng)\n("
			+ arg0.getPosition().latitude + ","
			+ arg0.getPosition().longitude + ")";
	    Toast.makeText(this,arg0.getTitle() + curDes , Toast.LENGTH_SHORT).show();
		Toast.makeText(this,arg0.getTitle() + "��ʼ�϶�" , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		//����ʹ��equals
		if(marker.equals(mymarker)){
			Toast.makeText(this,"������ִ�е���¼�" , Toast.LENGTH_SHORT).show();
		}
		return false;
	}
    //��λ����ʵ�ֵķ���
	//���������������ķ�����������������������
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
	    		}//�ȴ��������߳�ִ����
	    		
	    		//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
	    		try{
	    			Init_state = result_1.getString("HEXLE");
	    			//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    		Toast.makeText(Tracing.this, "Init_State: "+Init_state, Toast.LENGTH_SHORT).show();
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
				}//�ȴ��������߳�ִ����
				
				//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
				try{
					upload_state = result_1.getString("HEXLE");
					//Toast.makeText(WMapActivity.this, Response, Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					e.printStackTrace();
				}
				Toast.makeText(Tracing.this, "Upload_State: " + upload_state, Toast.LENGTH_SHORT).show();
			}
		
		}
	}
		/**
		 * ���λ
		 */
		@Override
		public void activate(OnLocationChangedListener listener) {
			mListener = listener;
			if (mAMapLocationManager == null) {
				mAMapLocationManager = LocationManagerProxy.getInstance(this);
			}
			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true
			 */
			// Location API��λ����GPS�������϶�λ��ʽ��ʱ�������5000����
			// ���Ͷ�λ����LocationProviderProxy.AMapNetwork����λ�ṩ�����ƣ�5000��λ�ñ仯֪ͨʱ�䣬��λ���룬
			//������ֵΪ5000���ϣ�10��λ�ñ仯֪ͨ���룬��λ��;this������listener��
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 5000, 10, this);

		}

		/**
		 * ֹͣ��λ
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
		 * һ���Դ�ӡ���Marker����
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
				ToastUtil.show(getApplicationContext(), "û�����������ݣ�");
			}
		}

		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == Constants.POISEARCH) {
					dissmissProgressDialog();// ���ضԻ���

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
											showPoiItem(poiItems);// ÿҳ��ʾ10��poiitem
										}
									});

								} catch (AMapException e) {
									e.printStackTrace();

								}
							}
						}).start();
					}

				} else if (msg.what == Constants.ERROR) {
					dissmissProgressDialog();// ���ضԻ���
					ToastUtil.show(getApplicationContext(), "����ʧ��,�����������ӣ�");
				} else if(msg.what == Constants.ROUTE_SEARCH_RESULT){
					progDialog.dismiss();
					if (routeResult != null && routeResult.size() > 0) {
						route = routeResult.get(0);
						Log.v("·��������", String.valueOf(routeResult.size()));
						if (route != null) {
							routeOverlay = new RouteOverlay(Tracing.this,
									aMap, route);
							routeOverlay.removeFormMap();
							routeOverlay.addMarkerLine();
							routeNav.setVisibility(View.VISIBLE);
							routePre.setBackgroundResource(R.drawable.prev_disable);
							routeNext.setBackgroundResource(R.drawable.next);
						}
					}
				}else if (msg.what == Constants.POISEARCH_NEXT) {//POI�������֮��ҳ����
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
										showPoiItem(poiItems);// ÿҳ��ʾ10��poiitem
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
		 * ��ʾ���ȿ�
		 */
		private void showProgressDialog() {
			if (progDialog == null)
				progDialog = new ProgressDialog(this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setIndeterminate(false);
			progDialog.setCancelable(true);
			progDialog.setMessage("��������:\n");
			progDialog.show();
		}

		/**
		 * ���ؽ��ȿ�
		 */
		private void dissmissProgressDialog() {
			if (progDialog != null) {
				progDialog.dismiss();
			}
		}
		
		public void doSearchQuery(final String searchName) {
			
			curpage = 1;
			cnt = 0;
			showProgressDialog();// ��ʾ���ȿ�
			new Thread(new Runnable() {
				public void run() {
					try {
						PoiSearch poiSearch = new PoiSearch(
								Tracing.this, new PoiSearch.Query(
										searchName, PoiTypeDef.All, "021")); // ���������ַ�����poi�������ͣ�poi�������򣨿��ַ�������ȫ����
						poiSearch.setPageSize(10);// ��������ÿ����෵�ؽ����
						//���Լ��Ķ�λλ��Ϊ������������ ����
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
			progDialog = ProgressDialog.show(Tracing.this, null,
					"���ڻ�ȡ��·", true, true);
			final Route.FromAndTo fromAndTo = new Route.FromAndTo(startPoint,
					endPoint);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						routeResult = Route.calculateRoute(Tracing.this,
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
					params.add(new BasicNameValuePair("locusDownload","uid,"+uid));
					System.out.println("traing uid:"+uid);
					HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
					request.setEntity(entity);
					HttpResponse httpResponse = httpClient.execute(request);
					//request.getParam("res");
					
					
					
//					HttpEntity entity1 = request.getEntity();
//					String retSrc = EntityUtils.toString(entity1);
//					result_1 = new JSONObject(retSrc);
					
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
					
					
					
//					HttpEntity entity1 = request.getEntity();
//					String retSrc = EntityUtils.toString(entity1);
//					result_1 = new JSONObject(retSrc);
					
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
					params.add(new BasicNameValuePair("locusMark", "uid,"+uid));
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
		//*************************************GIS tracing***************************************//
}
