package com.bt.btapp;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import android.content.Context;

public class JpushApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Set s = new HashSet();
        s.add("student");
        JPushInterface.setAliasAndTags(getApplicationContext(), "bucengyaoyuan", s);
        MyReceiver myReceiver = new MyReceiver();
    }
}
