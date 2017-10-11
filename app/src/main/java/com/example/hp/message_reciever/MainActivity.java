package com.example.hp.message_reciever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
/*
     AdapterView.OnItemClickListener is a callback method to be invoked when an item in this
      AdapterView has been clicked.
    */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //Declaring variables
    private static MainActivity inst;
    ArrayList<String> messagesList = new ArrayList<>();
    ListView listView;
    ArrayAdapter arrayAdapter;

    public static MainActivity instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing objects by ID
        listView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        // Checking for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("SmsReceiver", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},123);
            listView.setEnabled(false);
        } else {
            Log.d("SmsReceiver", "Permission is granted");
        }
    }
    //Creating Method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("SmsReceiver", "Permission has been granted");
                refreshInbox();
                listView.setEnabled(true);
            } else {
                Log.d("SmsReceiver", "Permission has been denied or request cancelled");
            }
        }
    }
    //Creating Method
    public void refreshInbox() {
        //Creating object of contentResolver
        ContentResolver contentResolver = getContentResolver();
        //Applying query
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        assert smsInboxCursor != null;
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        // It will move the current message received by user at top
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        arrayAdapter.clear(); // Clear the adapter
        do {
            String str = "senderNum:  " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }
    //Creating Method
    public void updateList(final String smsMessage) {
        //Inserting in arrayAdapter
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }
    // Creating onClick method
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        //Applying try and catch
        try {
            //Taking String and spliting it
            String[] smsMessages = messagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            //initializing message
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }
            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();//Toast Message
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}