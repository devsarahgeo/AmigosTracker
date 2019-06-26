package com.sarah.findmyphone
import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rilixtech.Country
import com.rilixtech.CountryCodePicker
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*
class Login : AppCompatActivity() {
    lateinit var countrycode:CountryCodePicker
    var countryCodeAndroid = "91"
    var mAuth:FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        countrycode = findViewById(R.id.ccp)
        countrycode.setOnCountryChangeListener(object : CountryCodePicker.OnCountryChangeListener {
            override fun onCountrySelected(p0: Country?) {
                Toast.makeText(this@Login, "Updated " + countrycode, Toast.LENGTH_SHORT).show()
            }
        })
        countrycode.registerPhoneNumberTextView(etPhoneNumber)
        mAuth = FirebaseAuth.getInstance()
        signInAnonomously()
    }

    private fun signInAnonomously() {
        mAuth!!.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Authentication Success.",
                                Toast.LENGTH_SHORT).show()
                        val user = mAuth!!.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
    }

    @SuppressLint("SimpleDateFormat")
    fun buRegisterEvent(view:View){
        val userData = UserData(this)
        var number = countrycode.fullNumberWithPlus
        number=UserData.formatPhoneNo(number)
        userData.savePhone(number)
        val df = SimpleDateFormat("yyyy/MM/dd HH:MM:ss")
        val date = Date()
        //save to database
        val mFirebaseDb = FirebaseDatabase.getInstance().reference
        mFirebaseDb.child("Users").child(number).child("Request").setValue(df.format(date).toString())
        mFirebaseDb.child("Users").child(number).child("Finder").setValue(df.format(date).toString())
        finish()
    }
}
