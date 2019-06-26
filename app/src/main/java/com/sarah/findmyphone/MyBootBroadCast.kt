package com.sarah.findmyphone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.widget.Toast


class MyBootBroadCast:BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
       if(intent!!.action.equals("android.intent.action.BOOT_COMPLETED")){
            val intent = Intent(context,MyServices::class.java)
            context!!.startService(intent)
        }
    }

}