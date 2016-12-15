package com.alextommy.queuer;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.UUID;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CustomerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private final CustomerAdapter adapter = new CustomerAdapter(this, new ArrayList<Customer>());
    private String database_id;
    private DatabaseReference mDatabase;
    private DatabaseReference restaurant;
    private String restaurant_name = "default";
    private Customer current = null;
    private ZXingScannerView mScannerView;
    private View popupView;
    private PopupWindow popupWindow;
    private boolean on = false;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 999;
    private View tempview;
    private int position = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
    }

    // popup for restaurant to manually enter customer info
    public void showPopup(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup,
                (ViewGroup) findViewById(R.id.popup));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void newScan(View view) {
        if (current == null) {
            scanQR(view);
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            popupView = layoutInflater.inflate(R.layout.popup_newscan,
                    (ViewGroup) findViewById(R.id.popup_newscan));
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    }

    // handle QR code scan request
    public void scanQR(View view){
        mScannerView = new ZXingScannerView(this); // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        on = true;

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mScannerView.startCamera(); // Start camera
        } else {
            tempview = view;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    scanQR(tempview);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(on) {
            mScannerView.stopCamera(); // Stop camera on pause
            setContentView(R.layout.activity_customer);
        }
        on = false;
    }

    // handle the result of QR code scan
    @Override
    public void handleResult(Result rawResult) {
        database_id = rawResult.getText();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(database_id).child("Entries");
        PopupCustomer();
    }

    // Popup window for customer to enter their information
    public void PopupCustomer() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_customer,
                (ViewGroup) findViewById(R.id.popup_customer));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    // handle the information that customer entered in the popup window
    public void newCustomer(View view) {
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry_customer);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry_customer);
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter a Name!", Toast.LENGTH_SHORT).show();
        } else if (size.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter party size!", Toast.LENGTH_SHORT).show();
        } else {
            Customer newEntry = new Customer(name.getText().toString(), Integer.parseInt(size.getText().toString()));
            DatabaseReference pushedRef = mDatabase.push();
            newEntry.setKey(pushedRef.getKey());
            pushedRef.setValue(newEntry);
            Toast.makeText(getApplicationContext(), "Successfully added to the restaurant's Queue!", Toast.LENGTH_LONG).show();
            popupWindow.dismiss();
            current = newEntry;
            // get database of scanned restaurant
            mDatabase = FirebaseDatabase.getInstance().getReference().child(database_id).child("Entries");
            restaurant = FirebaseDatabase.getInstance().getReference().child(database_id).child("Restaurant");

            restaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    restaurant_name = snapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            System.out.print("adding listener");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    adapter.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Customer entry = child.getValue(Customer.class);
                        adapter.insert(entry);
                    }
                    adapter.sort();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        Customer entry = (Customer) adapter.getItem(i);
                        if (entry.key.equals(current.key) && entry.name.equals(current.name)) {
                            position = i;
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // set restaurant name
            setContentView(R.layout.activity_customer);
            TextView r_name = (TextView) findViewById(R.id.restaurant_info);
            TextView c_pos = (TextView) findViewById(R.id.customer_info);
            r_name.setText(restaurant_name);
            c_pos.setText(String.valueOf(position));
        }
    }

    // make sure pressing the "back" button when in scanner view doesn't close the app
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (mScannerView != null) {
//                mScannerView.stopCamera();
//                setContentView(R.layout.activity_customer);
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
