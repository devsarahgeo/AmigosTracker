package com.sarah.findmyphone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.*
import android.widget.Toast
import com.google.firebase.database.*
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent



class MainActivity : AppCompatActivity() {
    lateinit var mFirebasedb:DatabaseReference
    val listOfContacts = ArrayList<UsersContact>()
//    var adapter: ContactsAdapter?=null
    lateinit var userData:UserData
    companion object {
        var simId = ""
    }

    @SuppressLint("MissingPermission", "SimpleDateFormat", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebasedb = FirebaseDatabase.getInstance().reference
        userData = UserData(applicationContext)
        userData.isFirstTimeLoad()
//        adapter = ContactsAdapter(this@MainActivity, listOfContacts)
//        lvContactsList.adapter = adapter
        val tm: TelephonyManager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        simId = tm.simSerialNumber
        if(simId!=null){
            Toast.makeText(applicationContext, "SIM card ID: $simId",Toast.LENGTH_LONG).show()
        }

//        lvContactsList.onItemClickListener = AdapterView.OnItemClickListener{
//            parent, view, position, id ->
//            val userInformation = listOfContacts[position]
//            val df = SimpleDateFormat("yyyy/MM/dd HH:MM:ss")
//            val date = Date()
//            mFirebasedb.child("Users").child(userInformation.phoneNumber).child("Request").setValue(df.format(date))
//            val intent  =Intent(this@MainActivity,MapsActivity::class.java)
//            intent.putExtra("phoneNo",userInformation.phoneNumber)
//            startActivity(intent)
//        }
    }
    fun clickGuardians(view:View){
        val intent  =Intent(this@MainActivity,FriendstrackingList::class.java)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        if(userData.loadPhoneNumber() == "empty"){
            return
        }
//        refreshUsers()
//        if(MyServices.isServiceRunning) return
//        checkContactPermission()
//        checkLocationPermission()
    }
//    private fun refreshUsers() {
//        mFirebasedb.child("Users").child(userData.loadPhoneNumber()).child("Finder").addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(dataSnapshot: DataSnapshot?) {
//                try{
//                    val td:HashMap<String,Any> = dataSnapshot!!.value as HashMap<String,Any>
//                    print("td:$td")
//                    listOfContacts.clear()
//                    if(td==null){
//                        listOfContacts.add(UsersContact("NO_USERS","nothing"))
//                        adapter!!.notifyDataSetChanged()
//                        return
//                    }
//                    for(key in td.keys){
//                        val name = listOfContact[key]
//                        listOfContacts.add(UsersContact(name.toString(),key))
//                    }
//                    adapter!!.notifyDataSetChanged()
//                }catch (ex:Exception){
//                    listOfContacts.clear()
//                    listOfContacts.add(UsersContact("NO_USERS","nothing"))
//                    adapter!!.notifyDataSetChanged()
//                    return
//                }
//            }
//            override fun onCancelled(p0: DatabaseError?) {
//            }
//
//        })
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.main_menu,menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when(item!!.itemId){
//            R.id.help ->{
//            }
//            R.id.addTracker -> {
//                val intent = Intent(this,Trackers::class.java)
//                startActivity(intent)
//            }else ->{
//                 return super.onOptionsItemSelected(item)
//            }
//        }
//        return true
//    }
//    class ContactsAdapter(context: Context, val listOfContacts: ArrayList<UsersContact>) : BaseAdapter() {
//        val context: Context?= context
//        @SuppressLint("ViewHolder")
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            val userContact = listOfContacts[position]
//            if(userContact.name.equals("NO_USERS")){
//                val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                val emptyTicket = inflator.inflate(R.layout.ticket_nousers,null)
//                return emptyTicket
//            }else{
//                val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                val contactTicket = inflator.inflate(R.layout.ticket_contactslist,null)
//                contactTicket.tvname.text = userContact.name
//                contactTicket.tvphonenumber.text = userContact.phoneNumber
//                return contactTicket
//            }
//        }
//
//        override fun getItem(position: Int): Any {
//            return listOfContacts[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getCount(): Int {
//            return listOfContacts.size
//        }
//
//    }
//    val CONTACTS_CODE = 123
//    fun checkContactPermission(){
//        if(Build.VERSION.SDK_INT>=23){
//            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
//                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),CONTACTS_CODE)
//                return
//            }
//        }
//        loadContacts()
//    }
//    val LOCATION_CODE = 123
//    fun checkLocationPermission(){
//        if(Build.VERSION.SDK_INT>=23){
//            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_CODE)
//                return
//            }
//        }
//        getUsersLocation()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode){
//            CONTACTS_CODE -> {
//                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    loadContacts()
//                }else{
//                    Toast.makeText(this,"Failed to retrieve contacts", Toast.LENGTH_LONG).show()
//                }
//            }
//            LOCATION_CODE -> {
//                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    getUsersLocation()
//                }else{
//                    Toast.makeText(this,"Failed to get location", Toast.LENGTH_LONG).show()
//                }
//            }
//            else -> {
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            }
//        }
//    }
//    var listOfContact = HashMap<String,String>()
//    private fun loadContacts() {
//        try{
//            listOfContact.clear()
//            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
//            cursor.moveToFirst()
//            do{
//                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                val phoneno = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                listOfContact.put(UserData.formatPhoneNo(phoneno),name)
//
//            }while (cursor.moveToNext())
//        }catch (ex:Exception){}
//
//    }
//    fun getUsersLocation(){
//        if(!MyServices.isServiceRunning){
//            val intent = Intent(baseContext,MyServices::class.java)
//            startService(intent)
//        }
//    }
}
