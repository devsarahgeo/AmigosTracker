package com.sarah.findmyphone

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MyServices: Service() {
    lateinit var userData:UserData
    lateinit var mFirebasedb: DatabaseReference

    override fun onBind(intent: Intent?): IBinder {
        return null!!
    }

    override fun onCreate() {
        super.onCreate()
        mFirebasedb = FirebaseDatabase.getInstance().reference
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userData = UserData(this)
        val myPhoneNo = userData.loadPhoneNumber()
        val Location = myLocationListener()
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,3f,Location)
        mFirebasedb.child("Users").child(myPhoneNo).child("Request").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if(MyServices.myLocation ==null) return
                val df = SimpleDateFormat("yyyy/MM/dd HH:MM:ss")
                val date = Date()
                mFirebasedb.child("Users").child(myPhoneNo).child("Location").child("lat").setValue(MyServices.myLocation!!.latitude)
                mFirebasedb.child("Users").child(myPhoneNo).child("Location").child("long").setValue(MyServices.myLocation!!.longitude)
                mFirebasedb.child("Users").child(myPhoneNo).child("Location").child("lastseen").setValue(df.format(date))
            }

        })
        return START_STICKY
    }
    companion object {
        var myLocation: Location?=null
    }
    inner class myLocationListener() : LocationListener {
        init {
            myLocation = Location("My Location")
            myLocation!!.latitude=0.0
            myLocation!!.longitude=0.0
        }
        override fun onLocationChanged(location: Location?) {
            myLocation = location
            myLocation!!.latitude
            myLocation!!.longitude

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }
}