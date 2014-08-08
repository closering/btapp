package com.bt.btapp;

import java.sql.Timestamp;

public class Monitor {
	private String username;//监护人用户名
	private double longitude;//经度
	private double latitude;//纬度
	private double radius;//半径
	private Timestamp time;
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return username;
	}
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
	public Double getLongitude(){
		return longitude;
	}
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	public double getLatitude(){
		return latitude;
	}
	public void setRradius(double radius){
		this.radius = radius;
	}
	public double getRadius(){
		return radius;
	}
	public void setTime(Timestamp time){
		this.time = time;
	}
	public Timestamp getTime(){
		return time;
	}
}
