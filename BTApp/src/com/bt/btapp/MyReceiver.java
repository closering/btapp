package com.bt.btapp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		JpushApplication JpushApp = (JpushApplication)context.getApplicationContext();
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            System.out.println("收到了自定义消息。消息内容是：" + message);
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            handleMessage(context, message);
            System.out.println("总共有"+JpushApp.getMonitorList().size()+"个监护人");
            
            
            
            
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了通知");
       
            ListItemEntity item = new ListItemEntity();
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);//获取年份
            int month=cal.get(Calendar.MONTH)+1;//获取月份
            int day=cal.get(Calendar.DATE);//获取日
            int hour=cal.get(Calendar.HOUR_OF_DAY);//小时
            int minute=cal.get(Calendar.MINUTE);//分
            if(minute<10){
            	item.setTime(""+hour+":0"+minute);
            }else{
            	item.setTime(""+hour+":"+minute);
            }           
            item.setDate(""+year+"/"+month+"/"+day);
            item.setMessage(bundle.getString(JPushInterface.EXTRA_ALERT));
            JpushApp.addItem(item);
            
            JpushApp.adapter.notifyDataSetChanged();
            
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            System.out.println("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            JpushApp.setViewNum(3);//点击打开通知后打开的视图                                
            Intent i = new Intent(context, FunctionModuleActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            System.out.println("Unhandled intent - " + intent.getAction());
        }
	}
	/*
	 * 处理自定义消息函数,消息为json格式，{"operation":"","monitorName":"","longitude":"","latitude":"","radius":"","setDate":""}
	 */
	public void handleMessage(Context context, String message){
		try {
			JpushApplication JpushApp = (JpushApplication)context.getApplicationContext();
			List<Monitor> monitorList = JpushApp.getMonitorList();
			//如果有monitor信息，则执行以下程序，否则抛出异常
			JSONObject result = new JSONObject(message);
			String operation = result.getString("operation");
			String monitorName = result.getString("monitorName");
			Double longitude = Double.parseDouble(result.getString("longitude"));
			Double latitude = Double.parseDouble(result.getString("latitude"));
			Double radius = Double.parseDouble(result.getString("radius"));
			Timestamp time = Timestamp.valueOf(result.getString("setDate"));
			Monitor mo = new Monitor();
			mo.setUsername(monitorName);
			mo.setLatitude(latitude);
			mo.setLongitude(longitude);
			mo.setRradius(radius);
			mo.setTime(time);
			if(operation.equals("add")){
				for(int i=0; i<monitorList.size(); i++){
					if(monitorList.get(i).getUsername().equals(monitorName) && monitorList.get(i).getTime().before(time)){
						monitorList.remove(i);
						monitorList.add(mo);
						JpushApp.setMonitorList(monitorList);
						return ;
					}
				}
				JpushApp.addMonitor(mo);
			}
			else if(operation.equals("edit")){
				for(int i=0; i<monitorList.size(); i++){
					if(monitorList.get(i).getUsername().equals(monitorName) && monitorList.get(i).getTime().before(time)){
						monitorList.remove(i);
						monitorList.add(mo);
						JpushApp.setMonitorList(monitorList);
						return ;
					}
				}
			}
			else if(operation.equals("delete")){
				for(int i=0; i<monitorList.size(); i++){
					if(monitorList.get(i).getUsername().equals(monitorName) && monitorList.get(i).getTime().before(time)){
						monitorList.remove(i);
						JpushApp.setMonitorList(monitorList);
						return ;
					}
				}
			}	
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			System.out.println("自定义推送消息格式不对");
			e.printStackTrace();
		}
	}
}
