package com.example.hp.message_reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SMS_Reciever extends BroadcastReceiver {
    //Declaring variables
    public static final String SMS_BUNDLE = "pdus";
    //onReceive() is called when the BroadcastReceiver is receiving an Intent broadcast.
    public void onReceive(Context context, Intent intent) {
        //creating bundle and getting the information
        Bundle intentExtras = intent.getExtras();
        //If statement
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            //initializing message
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]); //Creating object
                String smsBody = smsMessage.getMessageBody(); // Getting message body
                String address = smsMessage.getOriginatingAddress();// Getting message address

                smsMessageStr += "SenderNum:" + address + "\n";
                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_LONG).show();//Toast message
            MainActivity inst = MainActivity.instance(); // Update the list
            inst.updateList(smsMessageStr);
        }
    }

}