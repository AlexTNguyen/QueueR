package com.alextommy.queuer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;


public class EntryList extends AppCompatActivity {

    private final CustomerAdapter adapter = new CustomerAdapter(this, new ArrayList<Customer>());
    private View popupView;
    private PopupWindow popupWindow;
    private int temp_pos;
    private List<String> keys = new ArrayList<String>();
    private String currentKey;
    private Customer current;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Dewick").child("Entries");
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.listview);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.v("IM", "IN");
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.v("IM", "IN");
                    Customer entry = child.getValue(Customer.class);
                    adapter.insert(entry);
                    keys.add(child.getKey());
                }
                adapter.sort();
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("IT", "BROKE");
            }
        });

        if(intent.getStringExtra("add").equals("true")) {
            String name = intent.getStringExtra("name");
            String size = intent.getStringExtra("size");
            adapter.insert(new Customer(name, Integer.parseInt(size)));
        }

        TextView columnHeader1 = (TextView) findViewById(R.id.column_header1);
        TextView columnHeader2 = (TextView) findViewById(R.id.column_header2);
        TextView columnHeader3 = (TextView) findViewById(R.id.column_header3);
        String name = "Name";
        String partySize = "Size";
        String checkin = "Check-In";
        columnHeader1.setText(name);
        columnHeader2.setText(partySize);
        columnHeader3.setText(checkin);

        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(onListClick);
    }

    public void generateQR(View view) {
        Intent i = new Intent(this, GeneratorActivity.class);
        startActivity(i);
    }

    public void addEntry(View view) {
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        Customer newEntry = new Customer(name.getText().toString(), Integer.parseInt(size.getText().toString()));
        mDatabase.push().setValue(newEntry);
        adapter.insert(newEntry);
        ListView listView = (ListView) findViewById(R.id.listview);
        adapter.sort();
        listView.setAdapter(adapter);
        Log.v("hi", "hi");
        popupWindow.dismiss();
    }

    public void showPopup(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup,
                (ViewGroup) findViewById(R.id.popup));
        popupWindow = new PopupWindow(popupView, 600, 800, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }



    // edit the list content
    public void editEntry(View view) {
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        Spinner spinner = (Spinner)popupView.findViewById(R.id.spinner);
        int status = spinner.getSelectedItemPosition();
        Customer newCustomer = (Customer) adapter.getItem(temp_pos);
        newCustomer.setName(name.getText().toString());
        newCustomer.setSize(Integer.parseInt(size.getText().toString()));
        newCustomer.setStatus(status);
        adapter.insert_at(newCustomer, temp_pos);
        mDatabase.child(currentKey).setValue(newCustomer);
        ListView listView = (ListView) findViewById(R.id.listview);
        adapter.sort();
        listView.setAdapter(adapter);
        popupWindow.dismiss();
    }

    // pop up window after clicking on list
    private void editPopup(View view) {
        //name.setText();
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_edit,
                (ViewGroup) findViewById(R.id.popup_edit));
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        name.setText(current.getName());
        size.setText(Integer.toString(current.getSize()));
        Spinner spinner = (Spinner)popupView.findViewById(R.id.spinner);
        int statusID = current.getStatus();
        Log.v("status", Integer.toString(statusID));
        spinner.setSelection(statusID);
        popupWindow = new PopupWindow(popupView, 300, 400, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private final AdapterView.OnItemClickListener onListClick=new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            temp_pos = position;
            current = (Customer)adapter.getItem(position);
            currentKey = keys.get(position);
            editPopup(view);
        }
    };
}
