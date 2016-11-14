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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.content.Intent;
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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private View popupView;
    private PopupWindow popupWindow;
    private boolean on = false;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 999;
    private DatabaseReference mDatabase;
    private DatabaseReference restaurant;
    private String restaurant_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.title);
    }

//    public void showList(View view) {
//        Intent intent = new Intent(this, EntryList.class);
//        intent.putExtra("add", "false");
//        startActivity(intent);
//    }

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void addEntry(View view) {
        String name   = ((EditText)popupView.findViewById(R.id.nameEntry)).getText().toString();
        String size   = ((EditText)popupView.findViewById(R.id.sEntry)).getText().toString();
        Intent intent = new Intent(this, EntryList.class);
        intent.putExtra("add", "true");
        intent.putExtra("name", name);
        intent.putExtra("size", size);
        popupWindow.dismiss();
        startActivity(intent);
    }

    public void showPopup(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup,
                (ViewGroup) findViewById(R.id.popup));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        // Log.v("pls", "work")
    }

    public void scanQR(View view){
        mScannerView = new ZXingScannerView(this); // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        on = true;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mScannerView.startCamera(); // Start camera
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
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
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(on) {
            mScannerView.stopCamera(); // Stop camera on pause
            setContentView(R.layout.title);
        }
        on = false;
    }

    @Override
    public void handleResult(Result rawResult) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
        setContentView(R.layout.title);
        String id = rawResult.getText();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(id).child("Entries");
//        restaurant = FirebaseDatabase.getInstance().getReference().child(id).child("Restaurant");
//        restaurant.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    restaurant_name = child.getValue(String.class);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.v("IT", "BROKE");
//            }
//        });
        PopupCustomer();
    }

    public void PopupCustomer() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_customer,
                (ViewGroup) findViewById(R.id.popup_customer));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        // Log.v("pls", "work")
    }

    public void newCustomer(View view) {
        EditText name   = (EditText)popupView.findViewById(R.id.nameEntry_customer);
        EditText size   = (EditText)popupView.findViewById(R.id.sEntry_customer);
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter a Name!", Toast.LENGTH_SHORT).show();
        } else if (size.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter party size!", Toast.LENGTH_SHORT).show();
        } else {
            Customer newEntry = new Customer(name.getText().toString(), Integer.parseInt(size.getText().toString()));
            mDatabase.push().setValue(newEntry);
            Toast.makeText(getApplicationContext(), "Successfully added to the restaurant's Queue!", Toast.LENGTH_LONG).show();
            popupWindow.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mScannerView != null) {
                mScannerView.stopCamera();
                setContentView(R.layout.title);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}