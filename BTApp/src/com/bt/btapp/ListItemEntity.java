package com.bt.btapp;

public class ListItemEntity {
	private String date;//��ʽ ��  ��/��/��
	private String time;//��ʽ��   ʱ:��
	private String message;//���͵���Ϣ����
	
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
