package com.alextommy.queuer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private String database_id;
    private DatabaseReference mDatabase;
    private ZXingScannerView mScannerView;
    private View popupView;
    private PopupWindow popupWindow;
    private boolean on = false;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 999;
    private View tempview;
    private Button qr;
    private Button customer;
    private String customer_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.title);
    }

    // go to login page
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void customerView(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        intent.putExtra("id", database_id);
        intent.putExtra("key", customer_key);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
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
            // add new customer to database
            Customer newEntry = new Customer(name.getText().toString(), Integer.parseInt(size.getText().toString()));
            DatabaseReference pushedRef = mDatabase.push();
            newEntry.setKey(pushedRef.getKey());
            customer_key = newEntry.key;
            pushedRef.setValue(newEntry);
            Toast.makeText(getApplicationContext(), "Successfully added to the restaurant's Queue!", Toast.LENGTH_LONG).show();
            popupWindow.dismiss();
            setContentView(R.layout.title);
            qr = (Button) findViewById(R.id.customer);
            customer = (Button) findViewById(R.id.customer_view);
            qr.setVisibility(View.GONE);
            customer.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("id", database_id);
            intent.putExtra("key", newEntry.key);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        // make sure pressing the "back" button when in scanner view doesn't go back to title page
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mScannerView != null) {
                if (mScannerView.isShown()) {
                    mScannerView.stopCamera();
                    setContentView(R.layout.title);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}