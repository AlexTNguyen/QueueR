package com.alextommy.queuer;

import android.*;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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

public class CustomerActivity extends AppCompatActivity {

    private final CustomerAdapter adapter = new CustomerAdapter(this, new ArrayList<Customer>());
    private String database_id;
    private DatabaseReference mDatabase;
    private DatabaseReference restaurant;
    private String restaurant_name = "N/A";
    private String customer_key = null;
    private View popupView;
    private PopupWindow popupWindow;
    private int position = 0;
    private Customer current;
    private  NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        final TextView r_name = (TextView) findViewById(R.id.restaurant_info);
        final TextView c_pos = (TextView) findViewById(R.id.customer_info);
        final TextView c_name = (TextView) findViewById(R.id.name_info);
        Intent intent = getIntent();
        database_id = intent.getStringExtra("id");
        customer_key = intent.getStringExtra("key");
        mNotificationManager = (NotificationManager) this.getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);

        SharedPreferences.Editor editor = getSharedPreferences("customerActivity", MODE_PRIVATE).edit();
        editor.putBoolean("open", true);
        editor.apply();

        // get database of scanned restaurant
        mDatabase = FirebaseDatabase.getInstance().getReference().child(database_id).child("Entries");
        restaurant = FirebaseDatabase.getInstance().getReference().child(database_id).child("Restaurant");

        restaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restaurant_name = snapshot.getValue(String.class);
                // set restaurant name
                String name = "Restaurant: " + restaurant_name;
                r_name.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.clear();
                position = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Customer entry = child.getValue(Customer.class);
                    adapter.insert(entry);
                }
                adapter.sort();
                for (int i = 0; i < adapter.getCount(); i++) {
                    Customer entry = (Customer) adapter.getItem(i);
                    if (entry.status == 0) {
                        position++;
                    }
                    if (entry.key.equals(customer_key)) {
                        current = entry;
                        break;
                    }
                }
                // set customer info
                String name = "Hi " + current.name + "!";
                String customer = "Your are #" + String.valueOf(position) + " in the queue";
                c_name.setText(name);
                c_pos.setText(customer);
                if (position == 1) {
                    make_notification(name, "You're next!");
                }
                if (current.status != 0) {
                    Toast.makeText(getApplicationContext(), "Hi " + current.name + ", you are off the queue!", Toast.LENGTH_LONG).show();
                    CustomerActivity.this.finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void make_notification(String title, String text){
//        Intent resultIntent = new Intent(this, CustomerActivity.class);
//        // Creating a artifical activity stack for the notification activity
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(CustomerActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//
//        // Pending intent to the notification manager
//        PendingIntent resultPending = stackBuilder
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        // Building the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.logo)) // notification icon
                .setSmallIcon(R.drawable.title)
                .setContentTitle(title) // main title of the notification
                .setContentText(text) // notification text
                .setContentIntent(pendingIntent) // notification intent
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)); // notification sound
        // mId allows you to update the notification later on.
        mNotificationManager.notify(10, mBuilder.build());
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
        popupWindow.dismiss();
        mDatabase.child(customer_key).removeValue();
        SharedPreferences.Editor editor = getSharedPreferences("customerActivity", MODE_PRIVATE).edit();
        editor.putBoolean("open", false);
        editor.apply();
        this.finish();
    }
}
