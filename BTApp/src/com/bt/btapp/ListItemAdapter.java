package com.bt.btapp;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;  
    private List<ListItemEntity> items;  
    private int index = 0; 
    public ListItemAdapter(Context context, List<ListItemEntity> items) {  
        super();  
        this.inflater = LayoutInflater.from(context);  
        this.items = items;  
    }
    
    public void setIndex(int selected) {  
        index = selected;  
    } 
	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return items.size();  
	}

	@Override
	public Object getItem(int position) {
		// TODO �Զ����ɵķ������
		return items.get(position);  
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
		return position;  
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO �Զ����ɵķ������
		ViewHolder holder;  
        if (convertView == null) {  
            convertView = inflater.inflate(R.layout.message_list, null);  
            holder = new ViewHolder();  
            holder.message = (TextView) convertView  
                    .findViewById(R.id.message);
            holder.date = (TextView) convertView  
                    .findViewById(R.id.date);
            holder.time = (TextView) convertView  
                    .findViewById(R.id.time);
              
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        if (index == position) {  
          
            convertView.setBackgroundColor(Color.rgb(204, 255, 0));  
                             //�˴���������textviewΪѡ��״̬��������ʵ��Ч��         
             convertView.findViewById(R.id.message)  
             .setSelected(true);  
  
        } else { 
        	if(position%2 == 0){
        		convertView.setBackgroundColor(Color.rgb(204, 204, 255)); 
        	}
        	else{
        		convertView.setBackgroundColor(Color.rgb(255, 204, 204));  
        	}
            //ûѡ�еľͲ���������                             
                        convertView.findViewById(R.id.message)  
             .setSelected(false);  
        }  
        convertView.setTag(holder);  
        holder.message.setText(items.get(position).getMessage());  
        holder.date.setText(items.get(position).getDate());
        holder.time.setText(items.get(position).getTime());
        return convertView;
	}
	private class ViewHolder {  
        private TextView message; 
        private TextView date;
        private TextView time;
    } 

}
