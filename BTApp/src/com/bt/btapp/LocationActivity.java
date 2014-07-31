package com.bt.btapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.any.wmap.WMapUIDActivity;
import com.timeline.LogisticsActivity;

public class LocationActivity extends Activity 
{
	private TableLayout tableLayout;  
	private TextView tvUidInfo;
	private String idInfo = null;
	private String uid = null;
	private Button button_WMap;
	private Button button_logistics;
	private String username = null;
	private String productName = null;
	private String logisticsInfo = null;
	private boolean hasLogisticsInfo = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
				
		tableLayout = (TableLayout) this.findViewById(R.id.tableLayout);
		tvUidInfo = (TextView)this.findViewById(R.id.tvUidInfo);
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		idInfo = bundle.getString("Response");
		username = bundle.getString("username");
		tvUidInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
//		tvUidInfo.setText(idInfo);
//		String productName = null;
		
		String []token1 = idInfo.split(";");

		String []token2 = token1[0].split(",");
		if(token1.length==2)
		{
			hasLogisticsInfo = true;
			logisticsInfo = token1[1];
			String []token3 = token1[1].split(",");
			String uidInfo = "epc: "+token2[1]+"\n" +"product time: "+token2[2] +"\n"
				+"recall: "+token2[3]+"\n"+"product name: "+token2[4]+"\n"
				+"expire time: "+token2[5]+"\n"+"company name: "+token2[6]+"\n"
				+"company address: "+token2[7]+"\n";
		productName = token2[4];
		uid = token2[0];
		tvUidInfo.setText(uidInfo);
			
		for(int i = 0;i<token3.length;i+=4)
		{
			TableRow tr = new TableRow(this);
			TextView tv1 = new TextView(this);
	        TextView tv2 = new TextView(this);
	        TextView tv3 = new TextView(this);
	        TextView tv4 = new TextView(this);
	        
	        tv1.setText(productName);
	        tv2.setText(token3[i]);
	        tv3.setText(token3[i+1]);
	        tv4.setText(token3[i+2]+","+token3[i+3]);
	        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
	        tv2.setGravity(Gravity.CENTER_HORIZONTAL);
	        tv3.setGravity(Gravity.CENTER_HORIZONTAL);
	        tv4.setGravity(Gravity.CENTER_HORIZONTAL);
	        tr.addView(tv1);  
	        tr.addView(tv2);
	        tr.addView(tv3);  
	        tr.addView(tv4); 
	        
	        tableLayout.addView(tr);
		}
		}
		else
		{
			String uidInfo = "epc: "+token2[1]+"\n" +"product time: "+token2[2] +"\n"
					+"recall: "+token2[3]+"\n"+"product name: "+token2[4]+"\n"
					+"expire time: "+token2[5]+"\n"+"company name: "+token2[6]+"\n"
					+"company address: "+token2[7]+"\n";
			productName = token2[4];
			uid = token2[0];
			tvUidInfo.setText(uidInfo);
		}
		button_WMap = (Button) findViewById(R.id.checkuidmap);
		button_WMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocationActivity.this,WMapUIDActivity.class);
				Bundle bundle =new Bundle();
				bundle.putCharSequence("uid", "uid,"+uid);
				bundle.putCharSequence("username",username);
				intent.putExtras(bundle);
				startActivity(intent);


			}
		});
		
		
		button_logistics = (Button) findViewById(R.id.checklogistics);
		button_logistics.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(hasLogisticsInfo)
				{
					Intent intent = new Intent(LocationActivity.this,LogisticsActivity.class);
					Bundle bundle =new Bundle();
					bundle.putCharSequence("productName", productName);
					bundle.putCharSequence("logisticsInfo",logisticsInfo);
					intent.putExtras(bundle);
					startActivity(intent);
//					hasLogisticsInfo = false;
				}
				else
				{
					Toast.makeText(LocationActivity.this, "no logistics info!", Toast.LENGTH_SHORT).show();
					hasLogisticsInfo = false;
				}
			}
		});

        
		
	}
	

	

}
