package com.sarah.findmyphone
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.view.*
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_trackers.*
import kotlinx.android.synthetic.main.ticket_contactslist.view.*

class Trackers : AppCompatActivity() {
    val listOfContacts = ArrayList<UsersContact>()
    var adapter:ContactsAdapter?=null
    var userData:UserData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trackers)
        userData = UserData(applicationContext)
        adapter = ContactsAdapter(this@Trackers,listOfContacts)
        lvContactsList.adapter = adapter
        lvContactsList.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, id ->
            val userInformation = listOfContacts[position]
            UserData.myTrack.remove(userInformation.phoneNumber)
            refreshContacts()
            val mFirebaseDb = FirebaseDatabase.getInstance().reference
            val userData = UserData(applicationContext)
            mFirebaseDb.child("Users").child(userInformation.phoneNumber).child("Finder").child(userData.loadPhoneNumber()).removeValue()
            userData!!.saveContactInfo()
        }
        userData!!.loadContactInfo()
        refreshContacts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_tracker,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.add_contacts ->{
                checkPermission()
            }
            R.id.done -> {
                finish()
            }else ->{
            return super.onOptionsItemSelected(item)
        }
        }
        return true
    }
    val CONTACTS_CODE = 123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),CONTACTS_CODE)
                return
            }
        }
        pickContacts()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CONTACTS_CODE -> {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickContacts()
                }else{
                    Toast.makeText(this,"Failed to retrieve contacts",Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    val PICKER_CODE = 245
    private fun pickContacts() {
        val intent = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,PICKER_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            PICKER_CODE -> {
                if(resultCode == Activity.RESULT_OK){
                    val contacts = data!!.data
                    val content = contentResolver.query(contacts,null,null, null,null)
                    if(content.moveToFirst()){
                        val id = content.getString(content.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasphone = content.getString(content.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        if(hasphone.equals("1")){
                            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" +id,null,null)
                            phones.moveToFirst()
                            var phoneNumber = phones.getString(phones.getColumnIndex("data1"))
                            val name = content.getString(content.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            phoneNumber = UserData.formatPhoneNo(phoneNumber)
                            UserData.myTrack.put(phoneNumber,name)
                           refreshContacts()
                            //save to shared pref
                            userData!!.saveContactInfo()
                            //save to db
                            val userData = UserData(applicationContext)
                            val mFirebaseDb = FirebaseDatabase.getInstance().reference
                            mFirebaseDb.child("Users").child(phoneNumber).child("Finder").child(userData.loadPhoneNumber()).setValue(true)
                        }
                    }
                }
            }
            else->{
                super.onActivityResult(requestCode, resultCode, data)

            }
        }
    }

    private fun refreshContacts() {
        listOfContacts.clear()
        for((key,value) in UserData.myTrack){
            listOfContacts.add(UsersContact(value,key))
        }
        adapter!!.notifyDataSetChanged()

    }

    class ContactsAdapter(context: Context, val listOfContacts: ArrayList<UsersContact>) : BaseAdapter() {
        val context:Context?= context
        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val userContact = listOfContacts[position]
            val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val contactTicket = inflator.inflate(R.layout.ticket_contactslist,null)
            contactTicket.tvname.text = userContact.name
            contactTicket.tvphonenumber.text = userContact.phoneNumber
            return contactTicket
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
}
