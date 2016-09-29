package com.alextommy.queuer;

import android.support.v7.app.AppCompatActivity;


import java.util.List;
import com.alextommy.queuer.R.id;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private CustomerAdapter adapter;
    private View popupView;
    private PopupWindow popupWindow;

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
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        EditText time   = (EditText)popupView.findViewById(R.id.timeEntry);
        adapter.insert(new Customer(name.getText().toString(),
                                    size.getText().toString(),
                                    time.getText().toString()));
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        popupWindow.dismiss();
    }

    public void showPopup(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup,
                (ViewGroup) findViewById(R.id.popup));
        popupWindow = new PopupWindow(popupView, 300, 400, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        Log.v("pls", "workkkk");
    }
}