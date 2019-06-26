package com.sarah.findmyphone
import android.content.Context
import android.content.Intent
class UserData(context: Context) {
    val context:Context?=context
    val sharedPreferences = context.getSharedPreferences("userData",Context.MODE_PRIVATE)
    companion object {
        val myTrack:MutableMap<String,String> = HashMap()
        fun formatPhoneNo(phoneNumber:String):String{
            val number = phoneNumber.replace("[^0-9]".toRegex(),"")
            return number
        }
    }

    fun savePhone(phoneNumber:String){
        val editor = sharedPreferences.edit()
        editor.putString("phoneNumber",phoneNumber)
        editor.apply()
    }

    fun loadPhoneNumber():String{
        val phone = sharedPreferences.getString("phoneNumber","empty")
        return phone
    }
    fun isFirstTimeLoad(){
        val phone = sharedPreferences.getString("phoneNumber","empty")
        if(phone.equals("empty")){
            val intent = Intent(context,Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        }
    }
    fun saveContactInfo(){
        var listOfTrackers = ""
        for((key,value) in myTrack){
            if (listOfTrackers.length ==0 ){
                listOfTrackers = key + "%" + value
            }else{
                listOfTrackers += "%"+ key + "%" + value
            }
        }
        if(listOfTrackers.length == 0){
            listOfTrackers = "empty"
        }
        val editor = sharedPreferences.edit()
        editor.putString("listOfTrackers",listOfTrackers)
        editor.apply()
    }

    fun loadContactInfo(){
        myTrack.clear()
        val listOfTracks = sharedPreferences.getString("listOfTrackers","empty")
        if(!listOfTracks.equals("empty")){
            val items = listOfTracks.split("%").toTypedArray()
           var i = 0
            while(i<items.size){
                myTrack.put(items[i],items[i+1])
                print(myTrack)
                i += 2
            }
        }
    }
}