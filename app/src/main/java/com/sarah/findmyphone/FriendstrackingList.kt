package com.sarah.findmyphone

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_friendstracking_list.*
import kotlinx.android.synthetic.main.ticket_contactslist.view.*
import java.text.SimpleDateFormat
import java.util.*

class FriendstrackingList : AppCompatActivity() {
    lateinit var mFirebasedb: DatabaseReference
    lateinit var location:Location
    val listOfContacts = ArrayList<UsersContact>()
    var adapter: ContactsAdapter?=null
    lateinit var userData:UserData
    private lateinit var locationRequest: LocationRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendstracking_list)
        userData = UserData(applicationContext)

        mFirebasedb = FirebaseDatabase.getInstance().reference
        adapter = ContactsAdapter(this@FriendstrackingList, listOfContacts)
        lvContactsList.adapter = adapter
        lvContactsList.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, id ->
            val userInformation = listOfContacts[position]
            val df = SimpleDateFormat("yyyy/MM/dd HH:MM:ss")
            val date = Date()
            mFirebasedb.child("Users").child(userInformation.phoneNumber).child("Request").setValue(df.format(date))
            val intent  = Intent(this@FriendstrackingList,MapsActivity::class.java)
            intent.putExtra("phoneNo",userInformation.phoneNumber)
            startActivity(intent)
        }
    }
    private fun buildLocationRequest() {
        locationRequest= LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=5000
        locationRequest.fastestInterval=3000
        locationRequest.smallestDisplacement=10f
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.addTracker -> {
                val intent = Intent(this,Trackers::class.java)
                startActivity(intent)
            }else ->{
            return super.onOptionsItemSelected(item)
        }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        val userData= UserData(this)
        if (userData.loadPhoneNumber()=="empty"){
            return
        }
        refreshUsers()
        val intent = Intent(baseContext,MyServices::class.java)
        startService(intent)
        checkContactPermission()
        checkLocationPermission()
    }
    private fun refreshUsers() {
        mFirebasedb.child("Users").child(userData.loadPhoneNumber()).child("Finder").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                try{
                    val td:HashMap<String,Any> = dataSnapshot!!.value as HashMap<String,Any>
                    print("td:$td")
                    listOfContacts.clear()
                    if(td==null){
                        listOfContacts.add(UsersContact("NO_USERS","nothing"))
                        adapter!!.notifyDataSetChanged()
                        return
                    }
                    for(key in td.keys){
                        val name = listOfContact[key]
                        listOfContacts.add(UsersContact(name.toString(),key))
                    }
                    adapter!!.notifyDataSetChanged()
                }catch (ex:Exception){
                    listOfContacts.clear()
                    listOfContacts.add(UsersContact("NO_USERS","nothing"))
                    adapter!!.notifyDataSetChanged()
                    return
                }
            }
            override fun onCancelled(p0: DatabaseError?) {
            }

        })
    }
    class ContactsAdapter(context: Context, val listOfContacts: ArrayList<UsersContact>) : BaseAdapter() {
        val context: Context?= context
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val userContact = listOfContacts[position]
            if(userContact.name.equals("NO_USERS")){
                val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val emptyTicket = inflator.inflate(R.layout.ticket_nousers,null)
                return emptyTicket
            }else{
                val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val contactTicket = inflator.inflate(R.layout.ticket_contactslist,null)
                contactTicket.tvname.text = userContact.name
                contactTicket.tvphonenumber.text = userContact.phoneNumber
                return contactTicket
            }
        }

        override fun getItem(position: Int): Any {
            return listOfContacts[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listOfContacts.size
        }

    }
    val CONTACTS_CODE = 123
    fun checkContactPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),CONTACTS_CODE)
                return
            }
        }
        loadContacts()
    }
    val LOCATION_CODE = 123
    fun checkLocationPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_CODE)
                return
            }
        }
        getUsersLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CONTACTS_CODE -> {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    loadContacts()
                }else{
                    Toast.makeText(this,"Failed to retrieve contacts", Toast.LENGTH_LONG).show()
                }
            }
            LOCATION_CODE -> {
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    getUsersLocation()
                }else{
                    Toast.makeText(this,"Failed to get location", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    var listOfContact = HashMap<String,String>()
    private fun loadContacts() {
        try{
            listOfContact.clear()
            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
            cursor.moveToFirst()
            do{
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneno = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                listOfContact.put(UserData.formatPhoneNo(phoneno),name)

            }while (cursor.moveToNext())
        }catch (ex:Exception){}

    }
    fun getUsersLocation(){
        val intent = Intent(baseContext,MyServices::class.java)
        startService(intent)

    }
}
