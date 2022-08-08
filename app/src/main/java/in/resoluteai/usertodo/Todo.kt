package `in`.resoluteai.usertodo

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Todo(val title: String? = null, val time: String? = null, val count:Long?=null) {

}