
package com.bt.btapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ProductActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		TextView mProduct_name = (TextView)findViewById(R.id.textviewProduct_name);
		TextView mProduct_factory = (TextView)findViewById(R.id.textviewProduct_factory);
		TextView mProduct_factoryAddress = (TextView)findViewById(R.id.textviewProduct_factoryAddress);
		TextView mProduct_date = (TextView)findViewById(R.id.textviewProduct_date);
		TextView mProduct_expiration = (TextView)findViewById(R.id.textviewProduct_expiration);
		TextView mProduct_epc = (TextView)findViewById(R.id.textviewProduct_epc);
		
		Button mShowtrace = (Button)findViewById(R.id.buttonShowtrace);
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		String response = bundle.getString("productId");
		String []tokens = response.split(",");
		
		mProduct_name.setText(tokens[1]);
		mProduct_factory.setText(tokens[2]);
		mProduct_factoryAddress.setText(tokens[3]);
		mProduct_date.setText(tokens[4]);
		mProduct_expiration.setText(tokens[5]);
		mProduct_epc.setText(tokens[0]);
		
		
//		if(bundle.getString("productId").equals("bt"))
//		{
//			mProduct_name.setText("bt产品");
//			mProduct_factory.setText("btLab");
//			mProduct_factoryAddress.setText("清华大学经管学院");
//			mProduct_date.setText("2014.3.10");
//			mProduct_expiration.setText("100years");
//			mProduct_epc.setText("1234567890");
//		}
//		else
//		{
//			mProduct_name.setText("无该商品信息...");
//			mProduct_factory.setText("无该商品信息...");
//			mProduct_factoryAddress.setText("无该商品信息...");
//			mProduct_date.setText("无该商品信息...");
//			mProduct_expiration.setText("无该商品信息...");
//			mProduct_epc.setText("无该商品信息...");
//		}
		
		
	}

	

}