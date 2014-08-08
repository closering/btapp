package com.timeline;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.bt.btapp.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
/**
 * 时间轴
 *
 */
public class LogisticsActivity extends Activity {
	private static final String TAG = "MainActivity";
	private List<OneStatusEntity> oneList;
	private ExpandableListView expandlistView;
	private StatusExpandAdapter statusAdapter;
	private Context context;
	
	private String productName = null;
	private String logisticsInfo = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logistics);
		context = this;
		expandlistView = (ExpandableListView) findViewById(R.id.expandlist);
		
		
		Intent intent=getIntent();
		Bundle bundle= intent.getExtras();
		productName = bundle.getString("productName");
		logisticsInfo = bundle.getString("logisticsInfo");
		
		
		putInitData();
		
		statusAdapter = new StatusExpandAdapter(context, oneList);
		expandlistView.setAdapter(statusAdapter);
		expandlistView.setGroupIndicator(null); // 去掉默认带的箭头

		// 遍历所有group,将所有项设置成默认展开
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

	private void putInitData() 
	{
/*		String[] strArray = new String[]{"PRODUCT:洗面奶"};
		String[] str1 = new String[]{"STAFF:甄老师", "STAFF:伍洪桥", "STAFF:赵老师", "STAFF:王迪恩", "STAFF:冯志超", "STAFF:申松跃", "STAFF:邰东哲", "STAFF:赵然"};
		
		String[] timeStr1 = new String[]{"2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22", "2013-11-02 13:16:22"};

		String[] locationStr1 = new String[]{"清华大学","北京大学","北京科技大学","北京交通大学","北京联合大学","北京体育大学","北京林业大学","北京化工大学"};
*/
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
					two.setCompleteTime("暂无");
					two.setIsfinished(false);
				}else{
					two.setCompleteTime(time[j]+" 完成");
					two.setIsfinished(true);
				}
				
				twoList.add(two);
			}
			one.setTwoList(twoList);
			oneList.add(one);
		}
		Log.i(TAG, "二级状态："+oneList.get(0).getTwoList().get(0).getStatusName());
		
	}
	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}