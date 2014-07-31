package com.bt.btapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class Connect
{
	private CountDownLatch count;
	static JSONObject result = null;	
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	String TargetURL = null;
	//private HashMap Map = new HashMap() ;
	
	public Connect(Map map,String URL) 
	{   		
        
        this.TargetURL = URL;
        Iterator iterator = map.entrySet().iterator(); 
       
        while (iterator.hasNext()) 
        {    
         Map.Entry<String,String> entry = (Map.Entry<String,String>) iterator.next() ;
         params.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }           
            
    } 
	
	public void conn()
	{
		//String TargetURL = "http://101.5.146.244/test5.php";
		//String TargetURL = "http://192.168.2.251:8080/bt/com/conn/android/ServletTest";
		//String TargetURL = "http://192.168.11.247:8080/bt/com/conn/android/productQuery";
		HttpClient httpClient = new DefaultHttpClient();
		
		try
		{
			HttpPost request = new HttpPost(TargetURL);            
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
	
	public JSONObject getResult()
	{
		return result;
	}
	
	public static void main(String[] args)
	{
		Map<String,String> map = new HashMap<String,String>();
		map.put("bid", "1234");
		map.put("opt", "1");
		map.put("uid", "12345678,jfka,daeiu,eoigt,dioe");
		Connect connect = new Connect(map,"http://192.168.11.247:8080/bt/com/conn/android/productQuery");
		connect.conn();
		JSONObject jObject = connect.getResult();
		String s=null;
		try {
			s = jObject.getString("HEXLE");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(s);
		
	}
	
}
