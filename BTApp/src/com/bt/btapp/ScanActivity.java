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

import com.any.wmap.WMapUIDActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity
{
	private String Response = null;
	static JSONObject result = null;
	private String productEpc = null;
	
	private String tagInfo = null;//写入的tag信息
	private String tagId = null;//tag固有ID
	private String epc = "";
	private String bid = "";
	private String uid = "";
	
	private boolean mResumed = false;
	private boolean mWriteMode = false;
	private boolean mReadMode = false;
	
	private Button button_WMap;
	private String username;
	private String idList = null ;
	private int idCount = 0;
	private String firstId;
	private boolean isFirstId = true;
	private boolean findFirstBid = false;
	
	private String locationX = "123.00";
	private String locationY = "145.74";
	
	NfcAdapter mNfcAdapter;
	EditText etEpc;
	EditText etBid;
	EditText etUid;
	
	TextView mNoteRead;//显示扫描信息
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;
	RadioGroup raGroup;
	String opt = "no operation";

	Builder dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan);
		
		
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		username = bundle.getString("username");
		
		

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);//获得手机默认的NFC适配器
		findViewById(R.id.write_tag).setOnClickListener(mTagWriter);
		findViewById(R.id.read_tag).setOnClickListener(mTagRead);
		etEpc = ((EditText) findViewById(R.id.et_epc));
		etBid = ((EditText) findViewById(R.id.et_bid));
		etUid = ((EditText) findViewById(R.id.et_uid));

	   	mNoteRead = ((TextView) findViewById(R.id.noteRead));
	   	
	   	
	   	raGroup = (RadioGroup)findViewById(R.id.radioGroup);
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
	   	
	   	
	   //	idList = "10,11,"+username+","+"opt";

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

		if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
		{
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			writeTag(getNoteAsNdef(), detectedTag);
		}
	}

	private View.OnClickListener mTagWriter = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			disableNdefExchangeMode();
			enableTagWriteMode();
			dialog = new AlertDialog.Builder(ScanActivity.this);
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
	};

	private View.OnClickListener mTagRead = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			disableTagWriteMode();
			enableNdefExchangeMode();
			enableTagReadMode();

			dialog = new AlertDialog.Builder(ScanActivity.this);

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
	};

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
		mNfcAdapter.enableForegroundNdefPush(ScanActivity.this, getNoteAsNdef());
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
//				
				Context context = getApplicationContext();
				CharSequence text = "****Write data successfully!****";
		        int duration = Toast.LENGTH_SHORT;
		        Toast toast = Toast.makeText(context, text, duration);
		        toast.show();
//		        
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
//			
			Context context = getApplicationContext();
			CharSequence text = "！！！Write data failure！！！";
	        int duration = Toast.LENGTH_SHORT;
	        Toast toast = Toast.makeText(context, text, duration);
	        toast.show();
//			
		}
		return false;
	}
	public void onButtonSetupClick(View v){
		Intent _intent = 
				new  Intent(ScanActivity.this,ConfigurateActivity.class);
		startActivity(_intent);
	}
	

	public void onButtonFinishClick(View v)
	{
//		idList = "10,11,"+username+","+opt+ ","+idList;
		idList = locationX + ","+locationY +","+username +","
				+ opt+ "," +idList;
//		mNoteRead.setText(idList+"\n"+"扫到id:"+Integer.toString(idCount)+"个！");

		idCount =0;
		isFirstId =true;
		if(findFirstBid)
		{
			Intent _intent = 
				new  Intent(ScanActivity.this,SubmitActivity.class);
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
				Toast.makeText(this, "Scan error! you must scan a tag that contains" +
								"a Bid code.", Toast.LENGTH_SHORT).show();
				idList = null;
				mNoteRead.setText("waiting for read tag.");
			}
		
	}
	
	public void onButtonCheckClick(View v)
	{
		
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
		if(epc.equals("null")!=true&&!epc.equals(""))
		{
			if(Response==null)
			{
				Toast.makeText(ScanActivity.this, "the epc code is not existed!", 
						Toast.LENGTH_SHORT).show();
				init();
			}
			else if(Response.equals("-1"))
			{
				Toast.makeText(ScanActivity.this, "error!", Toast.LENGTH_SHORT).show();
				init();
			}
			else
				{
					Intent _intent = 				
							new  Intent(ScanActivity.this,SearchEpcActivity.class);
						Bundle bundle =new Bundle();
						bundle.putCharSequence("idList",Response);
						bundle.putCharSequence("username",username);
						_intent.putExtras(bundle);
						startActivity(_intent);
						init();
				}
		}
		else if(bid.equals("null")!=true&&!bid.equals(""))
		{
			if(Response.equals(""))
			{
				Toast.makeText(ScanActivity.this, "the bid code is not existed!", 
						Toast.LENGTH_SHORT).show();
				init();
			}
			else if(Response.equals("-1"))
			{
				Toast.makeText(ScanActivity.this, "error!", Toast.LENGTH_SHORT).show();
				init();
			}
			else
			{
				Intent _intent = 
					new  Intent(ScanActivity.this,SearchBidActivity.class);
				Bundle bundle =new Bundle();
				bundle.putCharSequence("idList", bid+","+Response);
				bundle.putCharSequence("username",username);
				_intent.putExtras(bundle);
				startActivity(_intent);
				init();
			}
		}
		else
		{
			Toast.makeText(ScanActivity.this, "error!", Toast.LENGTH_SHORT).show();
			init();
		}
		
	}
	
	class Check extends Thread{
		private CountDownLatch count;
		
		public Check(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			String TargetURL1 = "http://166.111.96.153:20251/bt/com/conn/android/searchBid";
			String TargetURL2 = "http://166.111.96.153:20251/bt/com/conn/android/searchEpc";
			//String TargetURL = "http://192.168.2.251:8080/bt/com/conn/android/productQuery";
			HttpClient httpClient = new DefaultHttpClient();
			String TargetURL= null;
			try
			{
				if(epc.equals("null")!=true)				
					TargetURL = TargetURL2;
				else
					TargetURL = TargetURL1;
				HttpPost request = new HttpPost(TargetURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
	//			params.add(new BasicNameValuePair("bid",bid ));
				if(epc.equals("null")!=true)				
					params.add(new BasicNameValuePair("epc",epc ));
				else
					params.add(new BasicNameValuePair("bid",bid ));
	            
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
	
	public void init()
	{
		this.findFirstBid = false;
		this.idList = null;
		this.isFirstId = true;
		this.idCount = 0;
		this.epc = "";
		this.bid = "";
		this.uid = "";
		this.mNoteRead.setText("waiting for read tag.");
	}
}


	
	

