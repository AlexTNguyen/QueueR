package com.alextommy.queuer;

import android.support.v7.app.AppCompatActivity;


import java.util.List;
import com.alextommy.queuer.R.id;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView columnHeader1 = (TextView) findViewById(R.id.column_header1);
        TextView columnHeader2 = (TextView) findViewById(R.id.column_header2);
        TextView columnHeader3 = (TextView) findViewById(R.id.column_header3);

        columnHeader1.setText("Name");
        columnHeader2.setText("Party Size");
        columnHeader3.setText("Wait Time");

        ListView view = (ListView) findViewById(R.id.listview);
        List<Customer> list = Customer.makeData();
        adapter = new CustomerAdapter(this, list);

        view.setAdapter(adapter);
    }

    public void addEntry(View view) {
        adapter.insert(new Customer("Alex", Integer.toString(2), "7:00 PM"));
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}
