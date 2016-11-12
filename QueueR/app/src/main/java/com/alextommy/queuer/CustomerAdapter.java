package com.alextommy.queuer;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Color;

public class CustomerAdapter extends BaseAdapter {
    private final Activity activity;
    private final List<Customer> list;


    public CustomerAdapter(Activity activity, List<Customer> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    public void insert (Customer customer) {
        list.add(customer);
    }
    public void insert_at (Customer customer, int id) {
        list.remove(id);
        list.add(id, customer);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listrow, parent, false);
        }
        TextView col1 = (TextView) convertView.findViewById(R.id.column1);
        TextView col2 = (TextView) convertView.findViewById(R.id.column2);
        TextView col3 = (TextView) convertView.findViewById(R.id.column3);

        if(list.get(position).getStatus() == 0) {
            col1.setBackgroundColor(Color.parseColor("#ffcccc"));
            col2.setBackgroundColor(Color.parseColor("#ffcccc"));
            col3.setBackgroundColor(Color.parseColor("#ffcccc"));
        }


        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String date = format.format(list.get(position).getDate());
        col1.setText(list.get(position).getName());
        col2.setText(Integer.toString(list.get(position).getSize()));
        col3.setText(date);

        return convertView;
    }
}