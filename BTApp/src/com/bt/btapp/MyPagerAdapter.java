package com.bt.btapp;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {
	List<View> viewList;
	public MyPagerAdapter(List<View> viewList){
		this.viewList=viewList;
	}
	@Override
	public int getCount() {
		return viewList.size();
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);   //返回当前要显示的view
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;  //不这样写会显示不了 view
	}

}
