package com.alextommy.queuer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class EntryList extends AppCompatActivity {

    private final CustomerAdapter adapter = new CustomerAdapter(this, Customer.makeData());
    private View popupView;
    private PopupWindow popupWindow;
    private int temp_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent.getStringExtra("add").equals("true")) {
            String name = intent.getStringExtra("name");
            String size = intent.getStringExtra("size");
            String time = intent.getStringExtra("time");
            adapter.insert(new Customer(name, size, time));
        }

        TextView columnHeader1 = (TextView) findViewById(R.id.column_header1);
        TextView columnHeader2 = (TextView) findViewById(R.id.column_header2);
        TextView columnHeader3 = (TextView) findViewById(R.id.column_header3);
        String name = "Name";
        String partySize = "Party Size";
        String waitTime = "Wait Time";
        columnHeader1.setText(name);
        columnHeader2.setText(partySize);
        columnHeader3.setText(waitTime);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onListClick);
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
        popupWindow = new PopupWindow(popupView, 600, 800, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        // Log.v("pls", "work")
    }

//    public CustomerAdapter getAdapter() {
//        return adapter;
//    }
//
//    public void setAdapter(CustomerAdapter newAdapter){
//        adapter = newAdapter;
//    }


    public void editEntry(View view) {
        // edit the list content
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        EditText time   = (EditText)popupView.findViewById(R.id.timeEntry);
        adapter.insert_at(new Customer(name.getText().toString(),
                size.getText().toString(),
                time.getText().toString()), temp_pos);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        popupWindow.dismiss();
    }

    private void editPopup(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_edit,
                (ViewGroup) findViewById(R.id.popup_edit));
        popupWindow = new PopupWindow(popupView, 600, 800, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private final AdapterView.OnItemClickListener onListClick=new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            editPopup(view);
            temp_pos = position;
            System.out.println("id is");
            System.out.println(position);
        }
    };

}
