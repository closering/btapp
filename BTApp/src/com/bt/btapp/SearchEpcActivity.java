package com.bt.btapp;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONObject;

import com.bt.btapp.SubmitActivity.Submit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
  
public class SearchEpcActivity extends Activity
{
	/** Called when the activity is first created. */
	private String idList = null;
	private ExpandableListView expList = null;
	private String uid = null;
	private TextView tvEpcInfo;
	private String username = null;
	
	private String Response = null;
	static JSONObject result = null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchepc);
		expList = (ExpandableListView) findViewById(R.id.expList1);
		tvEpcInfo = (TextView)this.findViewById(R.id.tvEpcInfo);
		tvEpcInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		idList = bundle.getString("idList");
		username = bundle.getString("username");
		
		String []token1 = idList.split(";");
		String []token2 = token1[0].split(",");
		String []token3 = token1[1].split(",");
		String epcInfo = "epc: "+token2[0]+"\n" +"product name: "+token2[1] +"\n"
				+"company name: "+token2[2]+"\n"+"product time: "+token2[3]+"\n"
				+"expire time: "+token2[4]+"\n";
		
		tvEpcInfo.setText(epcInfo);
		
		String Epc = token2[0];
		//创建二个一级条目标题
		Map<String, String> title = new HashMap<String, String>();
		
		title.put("Epc", "Epc: "+Epc);
		
		//创建一级条目容器
		List<Map<String, String>> gruops = new ArrayList<Map<String,String>>();
		
		gruops.add(title);
		
		//创建二级条目内容
		 List<List<Map<String, String>>> ss = new ArrayList<List<Map<String, String>>>();  
		//内容一
		List<Map<String, String>> child = new ArrayList<Map<String, String>>();  
		for(int i=0;i<token3.length;i++)
		{
			
			Map<String, String> content = new HashMap<String, String>();
			content.put("Uid","Uid: "+token3[i]);
			child.add(content);
		
		}
		ss.add(child);
						
		//创建ExpandableList的Adapter容器
		//参数: 1.上下文    2.一级集合	3.一级样式文件 4. 一级条目键值		5.一级显示控件名
		// 		6. 二级集合	7. 二级样式	8.二级条目键值	9.二级显示控件名
		SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(
				this, gruops, R.drawable.groups, new String[] { "Epc" }, new int[]{R.id.textGroup}, 
				ss, R.drawable.childs, new String[] { "Uid" }, new int[]{R.id.textChild}
				);
		
		//加入列表
		expList.setAdapter(sela);
		
		 
		expList.setOnChildClickListener(childClickListener); 
		
	}
	
	// 子条目上的点击事件  
    OnChildClickListener childClickListener = new OnChildClickListener() 
    {  
  
        @Override  
        public boolean onChildClick(ExpandableListView parent, View v,  
                int groupPosition, int childPosition, long id) {  
 
            	String []tokens = ((TextView) v.findViewById(R.id.textChild))  
                    .getText().toString().split(" "); 
            	uid = tokens[1];
            	
            	CountDownLatch threadSignal = new CountDownLatch(1);
        		new SearchUid(threadSignal).start();
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

        		if(Response.equals(""))
        			Toast.makeText(SearchEpcActivity.this, "the uid code is not existed!", 
    						Toast.LENGTH_SHORT).show();
        		else
        		{
        			/*
        				Bundle bundle =new Bundle();
        				bundle.putCharSequence("Response", uid+","+Response);
        				bundle.putCharSequence("username",username);Intent _intent = 
        					new  Intent(SearchEpcActivity.this,LocationActivity.class);
        				_intent.putExtras(bundle);
        				startActivity(_intent);*/
        			//之所以写Response = uid+","+Response;是为了尽量少改动武洪桥同学原有的代码
        			Response = uid+","+Response;
        			ExtractBidInfo extract = new ExtractBidInfo(Response);
        			extract.handle();
        			String productName = extract.getProductName();
        			String logisticsInfo = extract.getLogisticsInfo();
        			String uidInfo = extract.getUidInfo();
        			Bundle bundle =new Bundle();
					bundle.putCharSequence("productName", productName);
					bundle.putCharSequence("logisticsInfo",logisticsInfo);
					bundle.putCharSequence("uidInfo",uidInfo);
					bundle.putCharSequence("username",username);
					bundle.putCharSequence("uid",uid);
        			Intent _intent = new  Intent(SearchEpcActivity.this,Tracing.class);
        			_intent.putExtras(bundle);
        			startActivity(_intent);
        		}
            return true;  
        }
    }; 
    
	class SearchUid extends Thread
	{
		private CountDownLatch count;
		
		public SearchUid(CountDownLatch m_count) {   
	        this.count = m_count;  
	    } 
		
		public void run(){
			String TargetURL = "http://192.168.11.253:8080/bt/com/conn/android/searchUid";
			//String TargetURL = "http://166.111.96.153:20251/bt/com/conn/android/searchUid";
			//String TargetURL = "http://192.168.2.251:8080/bt/com/conn/android/productQuery";
			HttpClient httpClient = new DefaultHttpClient();		
			try
			{
				HttpPost request = new HttpPost(TargetURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid",uid));
	            
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
