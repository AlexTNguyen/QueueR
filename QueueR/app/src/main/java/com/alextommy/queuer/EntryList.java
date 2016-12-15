package com.alextommy.queuer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.roughike.bottombar.*;

import java.util.ArrayList;
import java.util.List;


public class EntryList extends AppCompatActivity {

    private final CustomerAdapter adapter = new CustomerAdapter(this, new ArrayList<Customer>());
    private View popupView;
    private PopupWindow popupWindow;
    private int temp_pos;
    private String currentKey;
    private Customer current;
    private FirebaseAuth auth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean initial = true;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Entries");
    private ListView listView;
    public static AppCompatActivity entrylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();
        Intent intent = getIntent();
        listView = (ListView) findViewById(R.id.listview);
        auth = FirebaseAuth.getInstance();
        entrylist = this;

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            int selected = 0;
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if(selected != 0) {
                    if (tabId == R.id.tab_qr) {
                        generateQR();
                    } else if (tabId == R.id.tab_add) {
                        showPopup();
                    } else if (tabId == R.id.tab_profile) {
                        profile_page();
                    }
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_qr) {
                    generateQR();
                }
                else if(tabId == R.id.tab_add) {
                    showPopup();
                }
                else if(tabId == R.id.tab_profile){
                    profile_page();
                }
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Customer entry = child.getValue(Customer.class);
                    adapter.insert(entry);
                }
                adapter.sort();
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listView.setOnItemClickListener(onListClick);
    }

    public void generateQR() {
        Intent i = new Intent(this, GeneratorActivity.class);
        startActivity(i);
    }

    public void profile_page() {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void addEntry(View view) {
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry);
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter a Name!", Toast.LENGTH_SHORT).show();
        } else if (size.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter party size!", Toast.LENGTH_SHORT).show();
        } else {
            Customer newEntry = new Customer(name.getText().toString(), Integer.parseInt(size.getText().toString()));
            ListView listView = (ListView) findViewById(R.id.listview);
            DatabaseReference pushedRef = mDatabase.push();
            newEntry.setKey(pushedRef.getKey());
            pushedRef.setValue(newEntry);
            popupWindow.dismiss();
        }
    }

    public void showPopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup,
                (ViewGroup) findViewById(R.id.popup));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
        mDatabase.child(currentKey).setValue(newCustomer);
        popupWindow.dismiss();
    }

    public void deleteEntry(View view) {
        mDatabase.child(currentKey).removeValue();
        popupWindow.dismiss();
    }

    // pop up window after clicking on list
    private void editPopup() {
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
        spinner.setSelection(statusID);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private final AdapterView.OnItemClickListener onListClick=new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            temp_pos = position;
            current = (Customer)adapter.getItem(position);
            currentKey = current.getKey();
            editPopup();
        }
    };
}
