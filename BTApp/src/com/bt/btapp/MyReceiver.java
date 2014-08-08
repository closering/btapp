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
            System.out.println("�յ����Զ�����Ϣ����Ϣ�����ǣ�" + message);
            // �Զ�����Ϣ����չʾ��֪ͨ������ȫҪ������д����ȥ����
            handleMessage(context, message);
            System.out.println("�ܹ���"+JpushApp.getMonitorList().size()+"���໤��");
            
            
            
            
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("�յ���֪ͨ");
       
            ListItemEntity item = new ListItemEntity();
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);//��ȡ���
            int month=cal.get(Calendar.MONTH)+1;//��ȡ�·�
            int day=cal.get(Calendar.DATE);//��ȡ��
            int hour=cal.get(Calendar.HOUR_OF_DAY);//Сʱ
            int minute=cal.get(Calendar.MINUTE);//��
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
            System.out.println("�û��������֪ͨ");
            // ����������Լ�д����ȥ�����û���������Ϊ
            JpushApp.setViewNum(3);//�����֪ͨ��򿪵���ͼ                                
            Intent i = new Intent(context, FunctionModuleActivity.class);  //�Զ���򿪵Ľ���
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            System.out.println("Unhandled intent - " + intent.getAction());
        }
	}
	/*
	 * �����Զ�����Ϣ����,��ϢΪjson��ʽ��{"operation":"","monitorName":"","longitude":"","latitude":"","radius":"","setDate":""}
	 */
	public void handleMessage(Context context, String message){
		try {
			JpushApplication JpushApp = (JpushApplication)context.getApplicationContext();
			List<Monitor> monitorList = JpushApp.getMonitorList();
			//�����monitor��Ϣ����ִ�����³��򣬷����׳��쳣
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
			// TODO �Զ����ɵ� catch ��
			System.out.println("�Զ���������Ϣ��ʽ����");
			e.printStackTrace();
		}
	}
}
