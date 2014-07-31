package com.bt.btapp;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class SubmitActivity extends Activity 
{
	private String Response = null;
	private String idList;
	static JSONObject result = null;
	private String uidList;
	private String bidList;
	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit);
		tv = (TextView)findViewById(R.id.tv);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		idList = bundle.getString("idList");
//		tv.setText(response);
		
		String []temp = idList.split(",");
		if (temp[4].equals("null")==true)
		{
			String opt;
			if(temp[3].equals("1") == true)
				opt = "Stock In";
			else if(temp[3].equals("0") == true)
				opt = "Stock Out";
			else
				opt = "No Operation";
			tv.setText("Location:"+temp[0]+","+temp[1]+"\n"
					+"User:"+temp[2]+"\n"
					+"Operation:"+opt+"\n\n"
					+"Scaned  "+Integer.toString(0)+"  Tags!"+"\n");
		}
		else if(temp.length==5)
		{
			String opt;
			if(temp[3].equals("1") == true)
				opt = "Stock In";
			else if(temp[3].equals("0") == true)
				opt = "Stock Out";
			else
				opt = "No Operation";
			tv.setText("Location:"+temp[0]+","+temp[1]+"\n"
					+"User:"+temp[2]+"\n"
					+"Operation:"+opt+"\n\n"
					+"Bid:"+temp[4]+"\n\n"
					+"Scaned  "+Integer.toString(1)+"  Tags!"+"\n");
		}
		else
		{
			String []temp1 = idList.split(",",6);
			String opt;
			uidList = temp1[5];
			String []uid = uidList.split(",");
			String sId="";
			for(int i = 0;i<uid.length;i++)
			{
				sId +=(uid[i]+"\n"); 
			}
			bidList = temp1[4];
			if(temp1[3].equals("1") == true)
				opt = "Stock In";
			else if(temp1[3].equals("0") == true)
				opt = "Stock Out";
			else
				opt = "No Operation";
			tv.setText("Location:"+temp1[0]+","+temp1[1]+"\n"
					+"User:"+temp1[2]+"\n"
					+"Operation:"+opt+"\n\n"
					+"Bid:"+temp1[4]+"\n\n"
					+"Uid:"+"\n"+sId+"\n"
					+"Scaned  "+Integer.toString(temp.length - 4)+"  Tags!"+"\n");
		}
		
	}
	
	
	public void onButtonSubmitClick(View v)
	{		
		
		CountDownLatch threadSignal = new CountDownLatch(1);
		new Submit(threadSignal).start();
		try {
			threadSignal.await();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//等待所有子线程执行完
		
		//Toast.makeText(this, retSrc, Toast.LENGTH_SHORT).show();
		try{
			Response = result.getString("HEXLE");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(Response.equals("-1")==true)
		{
			tv.setText("sorry,submit failure,you must scan two more tags.");
		}
		else
		{
			String []parser = Response.split(",");
			String []parseId = uidList.split(",");
			String s1="";
			String s2="";
			int match =0;
			int notmatch =0;
			for(int i=0;i<parser.length;i++)
			{
				if(parser[i].equals("1")==true)
					{
						s1+=("Uid  "+parseId[i]+"  match"+"\n");
						match ++;
					}
					
				else
					{
						s2+=("Uid  "+parseId[i]+"  do not match"+"\n");
						notmatch++;
					}
					
			}
			tv.setText("Bid:"+bidList+"\n\n"+s1+s2+"\n"
					+Integer.toString(match)+"  tags match"+"\n"
					+Integer.toString(notmatch)+"  tags do not match"+"\n");
			
		}
		
	}
	
	
	class Submit extends Thread
{
	private CountDownLatch count;
	
	public Submit(CountDownLatch m_count) {   
        this.count = m_count;  
    } 
	
	public void run(){
		String TargetURL = "http://192.168.11.253:8080/bt/com/conn/android/bidUidScan";
		//String TargetURL = "http://166.111.96.153:20251/bt/com/conn/android/bidUidScan";
		//String TargetURL = "http://192.168.2.251:8080/bt/com/conn/android/productQuery";
		HttpClient httpClient = new DefaultHttpClient();		
		try
		{
			HttpPost request = new HttpPost(TargetURL);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("scan",idList));
			idList = null;
            
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

	

}
