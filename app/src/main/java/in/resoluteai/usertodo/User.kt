package `in`.resoluteai.usertodo

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val username: String? = null, val age: String? = null, val dob:String?=null) {

}