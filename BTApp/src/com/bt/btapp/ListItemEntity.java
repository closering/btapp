package com.bt.btapp;

public class ListItemEntity {
	private String date;//格式 ：  年/月/日
	private String time;//格式：   时:分
	private String message;//推送的消息内容
	
	public ListItemEntity() { 
    } 
 
    public ListItemEntity(String date, String time, String message) { 
        this.date = date; 
        this.time = time; 
        this.message = message; 
    } 
    public String getDate(){
    	return date;
    }
    public void setDate(String date){
    	this.date = date;
    }
    
    public String getTime(){
    	return time;
    }
    public void setTime(String time){
    	this.time = time;
    }
    
    public String getMessage(){
    	return message;
    }
    public void setMessage(String message){
    	this.message = message;
    }
}
