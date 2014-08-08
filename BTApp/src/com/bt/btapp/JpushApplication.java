package com.bt.btapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class JpushApplication extends Application {
	private String username = null;//当前登录用户的用户名
	private int viewNum = 0;//进入FunctionModuleActivity页面时显示第viewNum个视图
	private List<ListItemEntity> messageList;//推送消息 消息源
	public ListItemAdapter adapter;//推送消息列表适配器
	
	private List<Monitor> monitorList = new ArrayList<Monitor>();//初始化下monitorList.size()为0
	private Map<Monitor, Boolean> monitorListSend = new HashMap<Monitor, Boolean>();//每个监护人是否已经发送过出界消息
	
	private MyService bindService;
	private boolean flag;
	@Override
    public void onCreate() {
        super.onCreate();
        messageList = new ArrayList<ListItemEntity>();
        adapter = new ListItemAdapter(getApplicationContext(), messageList);
    }
	public void JpushInit(){
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Set s = new HashSet();
        s.add("student");
        JPushInterface.setAliasAndTags(getApplicationContext(), username, s);//设置别名和标签
        MyReceiver myReceiver = new MyReceiver();  
        
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MyService.class);
        startService(intent);
	}
	
	
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return username;
	}
	public void setViewNum(int viewNum){
		this.viewNum = viewNum;
	}
	public int getViewNum(){
		return viewNum;
	}
	
	public void addItem(ListItemEntity item){
		messageList.add(item);
	}
	public List<ListItemEntity> getMessageList(){
		return messageList;
	}
	public void deleteMessage(int pos){
		messageList.remove(pos);
	}
	public void addMonitor(Monitor m){
		monitorList.add(m);
		monitorListSend.put(m, false);
	}
	public List getMonitorList(){
		return monitorList;
	}
	public void setMonitorList(List<Monitor> monitorList){
		this.monitorList = monitorList;
	}
	public Map getMonitorListSend(){
		return monitorListSend;
	}
	public void setMonitorListSend(Monitor m, boolean b){
		monitorListSend.put(m, b);
	}
}
