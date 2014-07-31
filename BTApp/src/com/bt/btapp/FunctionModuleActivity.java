package com.bt.btapp;
import java.io.IOException;
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
import org.json.JSONObject;

import com.bt.btapp.ScanActivity.Check;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class FunctionModuleActivity extends Activity{
	LayoutInflater 		inflater;
	View			 	first;	//write视图
	View 				second;	//read视图
	View 				third;	//tracing视图
	View 				fourth;	//message视图
	View 				fifth;	//setup视图
	
	TextView			textView1;  //导航栏write标题 
	TextView			textView2;  //导航栏read标题 
	TextView			textView3;	//导航栏trcing标题
	TextView			textView4;	//导航栏message标题
	TextView			textView5;	//导航栏setup标题
	
	ViewPager 			viewPager;  
	ImageView 			image;      //移动的那调横线，其实是一个imageview
	int 				moveX;    //导航下面横线偏移宽度
	int 				width;    //导航下面比较粗的线的宽度
	int 				index;    //当前第一个view
	List<View> 			viewList=new ArrayList<View>();
	
	private String username;
	private String Response = null;
	static JSONObject result = null;
	private String productEpc = null;
	
	private String tagInfo = null;//tag中的人为write进去的信息
	private String tagId = null;//tag固有ID
	private String epc = "";
	private String bid = "";
	private String uid = "";
	private String epcTracing = "";
	private String bidTracing = "";
	private String uidTracing = "";
	
	private boolean mResumed = false;
	private boolean mWriteMode = false;
	private boolean mReadMode = false;
	
	private String idList = null ;
	private int idCount = 0;
	private String firstId;
	private boolean isFirstId = true;
	private boolean findFirstBid = false;
	private boolean read_trace = false;//代表是“扫描读取tag”还是“追踪读取tag”，如果是前者则为false,如果为后者，则为true
	
	private String locationX = "123.00";//貌似是经度
	private String locationY = "145.74";//纬度
	
	
	EditText etEpc;//write视图中的EPC EditText控件
	EditText etBid;//write视图中的BID EditText控件
	EditText etUid;//write视图中的UID EditText控件
	RadioGroup raGroup;
	Builder dialog;
	TextView mNoteRead;//显示扫描信息
	TextView mNoteReadTracing;//Tracing视图中显示扫描信息
	Button button_WMap;//还没用到，不知道是什么控件
	
	NfcAdapter mNfcAdapter;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;
	String opt = "no operation";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.function_module);
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		username = bundle.getString("username");
		init();
	}

	public void init(){
		init_viewPager();
		init_NFC();
		init_childView();
		init_onClick();
	}
	/*
	 * viewPager组件初始化
	 */
	public void init_viewPager()
	{
		viewPager	=(ViewPager) findViewById(R.id.viewpager);
		inflater	=this.getLayoutInflater().from(this);
		first		=inflater.inflate(R.layout.view_one, null);
		second		=inflater.inflate(R.layout.view_two,null);
		third		=inflater.inflate(R.layout.view_three,null);
		fourth		=inflater.inflate(R.layout.view_four,null);
		fifth		=inflater.inflate(R.layout.view_five,null);
		
		textView1	=(TextView) findViewById(R.id.text1);
		textView2	=(TextView) findViewById(R.id.text2);
		textView3	=(TextView) findViewById(R.id.text3);
		textView4	=(TextView) findViewById(R.id.text4);
		textView5	=(TextView) findViewById(R.id.text5);
		image		=(ImageView) findViewById(R.id.iamge);
		viewList	.add(first);  
		viewList	.add(second);
		viewList	.add(third);
		viewList	.add(fourth);
		viewList	.add(fifth);
		
		viewPager.setAdapter(new MyPagerAdapter(viewList));
		viewPager.setOnPageChangeListener(new MyPageListener());
		
		DisplayMetrics dm 	= new DisplayMetrics();  //获取手机分辨率
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度

		width	= BitmapFactory.decodeResource(getResources(), R.drawable.mm).getWidth();// 获取图片宽度
		// 计算偏移量，也就是那条粗的下划线距离屏幕坐标的距离，如果有三个view则screeW/3，以此类推
		moveX 	= (screenW / 5 - width) / 2;	
		Matrix matrix = new Matrix();
		matrix	.postTranslate(moveX, 0);
		image	.setImageMatrix(matrix);	// 设置动画初始位置
	}
	public void init_NFC()
	{
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);//获得手机默认的NFC适配器
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try
		{
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e)
		{
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
	}
	/*
	 * 主Activity所包含的子视图的控件初始化
	 */
	public void init_childView()

	{
		etEpc = ((EditText) first.findViewById(R.id.et_epc));
		etBid = ((EditText) first.findViewById(R.id.et_bid));
		etUid = ((EditText) first.findViewById(R.id.et_uid));
	   	mNoteRead = ((TextView) second.findViewById(R.id.noteRead));
	   	mNoteReadTracing = ((TextView) third.findViewById(R.id.noteReadTracing));
	   	raGroup = (RadioGroup)second.findViewById(R.id.radioGroup);
	}
	/*
	 * 各个控件的响应函数
	 */
	public void init_onClick()
	{
		textView1.setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	viewPager.setCurrentItem(0);  //点击Write的时候设置第1个view显示
            } 
		});
		textView2.setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	viewPager.setCurrentItem(1);  //点击Read的时候设置第2个view显示
            } 
		});
		textView3.setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	viewPager.setCurrentItem(2);  //点击Tracing的时候设置第3个view显示
            } 
		});
		textView4.setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	viewPager.setCurrentItem(3);  //点击Message的时候设置第4个view显示
            } 
		});
		textView5.setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	viewPager.setCurrentItem(4);   //点击Setup的时候设置第5个view显示
            } 
		});
		first.findViewById(R.id.write_tag).setOnClickListener(new OnClickListener(){   //第一个view中的"WRITE TO TAG"按钮的点击响应函数
            @Override
            public void onClick(View v) {
            	 System.out.println("click write tag!!!");
            	 disableNdefExchangeMode();
     			 enableTagWriteMode();
     			 dialog = new AlertDialog.Builder(FunctionModuleActivity.this);
     			 dialog.setTitle("please put the tag on the back of your phone.").setOnCancelListener(new DialogInterface.OnCancelListener()
     			 {
     				 @Override
     				 public void onCancel(DialogInterface dialog)
     				 {
     					 disableTagWriteMode();
     					 enableNdefExchangeMode();
     				 }
     			 }).create().show();
            } 
		});
		second.findViewById(R.id.read_tag).setOnClickListener(new OnClickListener(){  //第二个view中的"READ FROM TAG"按钮的点击响应函数 
            @Override
            public void onClick(View v) {
            	 System.out.println("click read tag!!!");
            	 disableTagWriteMode();
     			enableNdefExchangeMode();
     			enableTagReadMode();

     			dialog = new AlertDialog.Builder(FunctionModuleActivity.this);

     			dialog.setTitle("please put the tag on the back of your phone.").setOnCancelListener(new DialogInterface.OnCancelListener()
     			{
     				@Override
     				public void onCancel(DialogInterface dialog)
     				{
     					disableTagWriteMode();
     					enableNdefExchangeMode();
     				}
     			}).create().show();
            } 
		});
		second.findViewById(R.id.submit).setOnClickListener(new OnClickListener(){  //第二个view中的"READ FROM TAG"按钮的点击响应函数 
            @Override
            public void onClick(View v) {
        		idList = locationX + ","+locationY +","+username +","
        				+ opt+ "," +idList;
        		idCount =0;
        		isFirstId =true;
        		if(findFirstBid)
        		{
        			Intent _intent = 
        				new  Intent(FunctionModuleActivity.this,SubmitActivity.class);
        				Bundle bundle = new Bundle();
        				bundle.putCharSequence("idList", idList);
        				_intent.putExtras(bundle);
        				startActivity(_intent);
        				idList = null;
        				findFirstBid = false;
        				mNoteRead.setText("waiting for read tag.");
        		}
        		else
        			{
        				Toast.makeText(FunctionModuleActivity.this, "Scan error! you must scan a tag that contains" +
        								"a Bid code.", Toast.LENGTH_SHORT).show();
        				idList = null;
        				mNoteRead.setText("waiting for read tag.");
        			}
            } 
		});
		third.findViewById(R.id.read_tag_tracing).setOnClickListener(new OnClickListener(){  //第三个view中的"READ FROM TAG"按钮的点击响应函数 
            @Override
            public void onClick(View v) {
            	System.out.println("click read tracing tag!!!");
            	disableTagWriteMode();
     			enableNdefExchangeMode();
     			enableTagReadMode();
     			read_trace = true;
     			dialog = new AlertDialog.Builder(FunctionModuleActivity.this);
     			dialog.setTitle("please put the tag on the back of your phone.").setOnCancelListener(new DialogInterface.OnCancelListener()
     			{
     				@Override
     				public void onCancel(DialogInterface dialog)
     				{
     					disableTagWriteMode();
     					enableNdefExchangeMode();
     					read_trace = false;
     				}
     			}).create().show();
            } 
		});
		third.findViewById(R.id.check).setOnClickListener(new OnClickListener(){   
            @Override
            public void onClick(View v) {
            	CountDownLatch threadSignal = new CountDownLatch(1);
        		new Check(threadSignal).start();
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
        		if(epcTracing.equals("null")!=true&&!epcTracing.equals(""))
        		{
        			if(Response==null)
        			{
        				Toast.makeText(FunctionModuleActivity.this, "the epc code is not existed!", 
        						Toast.LENGTH_SHORT).show();
        				initTracing();
        			}
        			else if(Response.equals("-1"))
        			{
        				Toast.makeText(FunctionModuleActivity.this, "error!", Toast.LENGTH_SHORT).show();
        				initTracing();
        			}
        			else
        				{
        					Intent _intent = 				
        							new  Intent(FunctionModuleActivity.this,SearchEpcActivity.class);
        						Bundle bundle =new Bundle();
        						bundle.putCharSequence("idList",Response);
        						bundle.putCharSequence("username",username);
        						_intent.putExtras(bundle);
        						startActivity(_intent);
        						initTracing();
        				}
        		}
        		else if(bidTracing.equals("null")!=true&&!bidTracing.equals(""))
        		{
        			if(Response.equals(""))
        			{
        				Toast.makeText(FunctionModuleActivity.this, "the bid code is not existed!", 
        						Toast.LENGTH_SHORT).show();
        				initTracing();
        			}
        			else if(Response.equals("-1"))
        			{
        				Toast.makeText(FunctionModuleActivity.this, "error!", Toast.LENGTH_SHORT).show();
        				initTracing();
        			}
        			else
        			{
        				Intent _intent = 
        					new  Intent(FunctionModuleActivity.this,SearchBidActivity.class);
        				Bundle bundle =new Bundle();
        				bundle.putCharSequence("idList", bidTracing+","+Response);
        				bundle.putCharSequence("username",username);
        				_intent.putExtras(bundle);
        				startActivity(_intent);
        				initTracing();
        			}
        		}
        		else
        		{
        			Toast.makeText(FunctionModuleActivity.this, "error!", Toast.LENGTH_SHORT).show();
        			initTracing();
        		}
            } 
		});
		raGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            @Override 
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                // TODO Auto-generated method stub  
                if(checkedId==R.id.radioBtn1){                    
                    opt = "1";  
                    //Toast.makeText(ScanActivity.this, opt, Toast.LENGTH_SHORT).show();
                }  
                else if(checkedId==R.id.radioBtn2){  
                	opt = "0"; 
                	//Toast.makeText(ScanActivity.this, opt, Toast.LENGTH_SHORT).show();
                }  
                else{
                	opt = "no operation";
                	//Toast.makeText(ScanActivity.this, opt, Toast.LENGTH_SHORT).show();
                }
            }  
        });
	}
	public void initTracing()
	{
		this.findFirstBid = false;
		this.idList = null;
		this.isFirstId = true;
		this.idCount = 0;
		this.epcTracing = "";
		this.bidTracing = "";
		this.uidTracing = "";
		this.mNoteReadTracing.setText("waiting for read tag.");
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		mResumed = true;	
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mResumed = false;
		mNfcAdapter.disableForegroundNdefPush(this);
	}
	/*
	 * 手机检测到NFC tag时的程序入口
	 */
	@Override
	protected void onNewIntent(Intent intent)
	{
		if (mReadMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			NdefMessage[] msgs = getNdefMessages(intent);
			tagInfo = new String(msgs[0].getRecords()[0].getPayload());
			byte [] bytesid = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			tagId = bytesToHexString(bytesid);
			String firstBid = null;
			String []tokens = tagInfo.split(",");
			if(read_trace)//Tracing界面读取时
			{
				if(tokens.length==3)
				{
					mNoteReadTracing.setText("TagId(HEX):" + tagId + "\n" 
						+"EPC Code:"+tokens[0]+"\n"
						+"BID Code:"+tokens[1]+"\n"
						+"UID Code:"+tokens[2]+"\n"
						+ "User:" + username+"\n");
					epcTracing = tokens[0];
					bidTracing = tokens[1];
					uidTracing = tokens[2];
					System.out.println("epc:"+epcTracing+'\n'+"bid"+bidTracing+'\n'+"uid:"+uidTracing);
				}
				else
					mNoteReadTracing.setText("TagId(HEX):" + tagId + "\n" 
							+"Tag Info:"+tagInfo+"\n"
							+ "User:" + username+"\n");
			}
			else
			{
				if(tokens.length==3)
				{
					mNoteRead.setText("TagId(HEX):" + tagId + "\n" 
						+"EPC Code:"+tokens[0]+"\n"
						+"BID Code:"+tokens[1]+"\n"
						+"UID Code:"+tokens[2]+"\n"
						+ "User:" + username+"\n");
					if(isFirstId) //扫描第一个id
					{
						if(tokens[1].equals("null")!=true)//第一个id是Bid
						{
							bid = tokens[1];
							epc = tokens[0];
							idList = tokens[1];
							findFirstBid = true;
							isFirstId = false;
							idCount++;
						}
						else //第一个id不是Bid
						{
							idList = tokens[2];
							isFirstId = false;
							idCount++;
							uid = tokens[2];
							epc = tokens[0];
							bid = tokens[1];
						}
					}
					else//不是第一个id
					{
						if(findFirstBid)//已经找到了Bid
						{
							if(tokens[2].equals("null")!=true&&idList.indexOf(tokens[2]) < 0&&idList.indexOf(tokens[1]) < 0)
							{
								idList +=(","+tokens[2]);
								idCount++;
							}
						}
						else//没有找到Bid
						{
							if(tokens[1].equals("null")!=true)//找到Bid
							{
								idList = tokens[1]+(","+idList);
								idCount++;
								findFirstBid = true;
							}
							else
							{
								if(idList.indexOf(tokens[2]) < 0)
								{
									idList +=(","+tokens[2]);
									idCount++;
								}
							}
						}
					}
				}
				else
					mNoteRead.setText("TagId(HEX):" + tagId + "\n" 
							+"Tag Info:"+tagInfo+"\n"
							+ "User:" + username+"\n");
			}
		}
		if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
		{
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			writeTag(getNoteAsNdef(), detectedTag);
		}
	}
	private NdefMessage getNoteAsNdef()
	{
		//
		String s1=null,s2=null,s3=null;
		if(etEpc.getText().toString().equals("")==true)
			s1 = "null";
		else
			s1 = etEpc.getText().toString();
		
		if(etBid.getText().toString().equals("")==true)
			s2 = "null";
		else
			s2 = etBid.getText().toString();
		
		if(etUid.getText().toString().equals("")==true)
			s3 = "null";
		else
			s3 = etUid.getText().toString();
		String temp = s1+","+s2+","+s3;
		byte[] textBytes = temp.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	NdefMessage[] getNdefMessages(Intent intent)
	{
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
		{
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null)
			{
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++)
				{
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else
			{
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else
		{
			// Log.d(TAG, "Unknown intent.");
			finish();
		}
		return msgs;
	}

	private void enableNdefExchangeMode()
	{
		mNfcAdapter.enableForegroundNdefPush(FunctionModuleActivity.this, getNoteAsNdef());
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
	}

	private void disableNdefExchangeMode()
	{
		mNfcAdapter.disableForegroundNdefPush(this);
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void enableTagWriteMode()
	{
		mWriteMode = true;
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
	}

	private void disableTagWriteMode()
	{
		mWriteMode = false;
		mNfcAdapter.disableForegroundDispatch(this);
	}
	
	private void enableTagReadMode()
	{
		mReadMode = true;
	}
	boolean writeTag(NdefMessage message, Tag tag)
	{
		int size = message.toByteArray().length;

		try
		{
			Ndef ndef = Ndef.get(tag);
			if (ndef != null)
			{
				ndef.connect();

				if (!ndef.isWritable())
				{
					System.out.println("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size)
				{
					
					return false;
				}

				ndef.writeNdefMessage(message);			
				Context context = getApplicationContext();
				CharSequence text = "****Write data successfully!****";
		        int duration = Toast.LENGTH_SHORT;
		        Toast toast = Toast.makeText(context, text, duration);
		        toast.show();	        
				return true;
			} else
			{
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null)
				{
					try
					{
						format.connect();
						format.format(message);
						return true;
					} catch (IOException e)
					{
						return false;
					}
				} else
				{
					return false;
				}
			}
		} catch (Exception e)
		{		
			Context context = getApplicationContext();
			CharSequence text = "！！！Write data failure！！！";
	        int duration = Toast.LENGTH_SHORT;
	        Toast toast = Toast.makeText(context, text, duration);
	        toast.show();		
		}
		return false;
	}
	
	class Check extends Thread{
		private CountDownLatch count;
		
		public Check(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		public void run(){
			//String TargetURL1 = "http://166.111.96.153:20251/bt/com/conn/android/searchBid";
			//String TargetURL2 = "http://166.111.96.153:20251/bt/com/conn/android/searchEpc";
			String TargetURL1 = "http://192.168.11.253:8080/bt/com/conn/android/searchBid";
			String TargetURL2 = "http://192.168.11.253:8080/bt/com/conn/android/searchEpc";
			HttpClient httpClient = new DefaultHttpClient();
			String TargetURL= null;
			try
			{
				if(epcTracing.equals("null")!=true)				
					TargetURL = TargetURL2;
				else
					TargetURL = TargetURL1;
				HttpPost request = new HttpPost(TargetURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
	//			params.add(new BasicNameValuePair("bid",bid ));
				if(epcTracing.equals("null")!=true)				
					params.add(new BasicNameValuePair("epc",epcTracing ));
				else
					params.add(new BasicNameValuePair("bid",bidTracing ));
	            
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				request.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(request);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					
					String retSrc = EntityUtils.toString(httpResponse.getEntity());
					if(retSrc.length() < 1)
					{
						retSrc = "{\"HEXLE\":\"null,null,null,null,null,null\"}";
					}
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
	
	public String bytesToHexString(byte[] src) 
	{  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString().toUpperCase();  
	}

	/*
	 * viewPager的响应函数
	 */
	class MyPageListener implements  OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int arg0) {
			int x=moveX*2+width;   //从第一个到第二个view，粗的下划线的偏移量
			/**
			 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
			 * 　     float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
				float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
				float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
				float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
			 */
			Log.v("index的值为:", index+"");
			Log.v("arg0的值为:", arg0+"");
			Animation animation=
					new TranslateAnimation(x*index, x*arg0, 0, 0);
			index=arg0;
			animation	.setFillAfter(true);   //设置动画停止在结束位置
			animation	.setDuration(300);     //设置动画时间
			image		.startAnimation(animation);  //启动动画
		}
	}
}
