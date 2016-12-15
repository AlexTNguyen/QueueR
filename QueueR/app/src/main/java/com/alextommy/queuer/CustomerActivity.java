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

public class CustomerActivity extends AppCompatActivity{

    private final CustomerAdapter adapter = new CustomerAdapter(this, new ArrayList<Customer>());
    private String database_id;
    private DatabaseReference mDatabase;
    private DatabaseReference restaurant;
    private String restaurant_name = "default";
    private String key = null;
    private View popupView;
    private PopupWindow popupWindow;
    private int position = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Intent intent = getIntent();
        database_id = intent.getStringExtra("id");
        key = intent.getStringExtra("key");

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
                    if (entry.key.equals(key)) {
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
        TextView r_name = (TextView) findViewById(R.id.restaurant_info);
        TextView c_pos = (TextView) findViewById(R.id.customer_info);
        r_name.setText(restaurant_name);
        c_pos.setText(String.valueOf(position));
    }

    public void quitCustomer(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.popup_totitle,
                (ViewGroup) findViewById(R.id.popup_totitle));
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void quitConfirm(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        popupWindow.dismiss();
        this.finish();
        startActivity(intent);
    }
}
