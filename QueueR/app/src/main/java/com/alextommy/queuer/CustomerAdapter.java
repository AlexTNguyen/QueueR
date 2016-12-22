package com.alextommy.queuer;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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

    public void sort () {
        sortByDate();
        Collections.sort(list, new Comparator<Customer>() {
            @Override
            public int compare(Customer one, Customer two) {
                if (one.getStatus() > two.getStatus())
                    return 1;
                if (one.getStatus() < two.getStatus())
                    return -1;
                return 0;
            }
        });
    }

    private void sortByDate () {
        Collections.sort(list, new Comparator<Customer>() {
            @Override
            public int compare(Customer one, Customer two) {
                return one.getDate().compareTo(two.getDate());
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
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
        else if(list.get(position).getStatus() == 1) {
            col1.setBackgroundColor(Color.parseColor("#ccffcc"));
            col2.setBackgroundColor(Color.parseColor("#ccffcc"));
            col3.setBackgroundColor(Color.parseColor("#ccffcc"));
        }
        else if(list.get(position).getStatus() == 2) {
            col1.setBackgroundColor(Color.parseColor("#bdbdbd"));
            col2.setBackgroundColor(Color.parseColor("#bdbdbd"));
            col3.setBackgroundColor(Color.parseColor("#bdbdbd"));
        }

        SimpleDateFormat format = new SimpleDateFormat("hh:mm a, MMM dd");
        String date = format.format(list.get(position).getDate());
        col1.setText(list.get(position).getName());
        col2.setText(Integer.toString(list.get(position).getSize()));
        col3.setText(date);

        return convertView;
    }
}