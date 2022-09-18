package `in`.resoluteai.usertodo

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class NewTaskActivity : AppCompatActivity(),TimePickerDialog.OnTimeSetListener {

    val database = Firebase.database
    val auth = Firebase.auth
    val userRef = database.getReference("Todo")
    private lateinit var time : TextView
    var hour = 0
    var minute = 0
    var count : Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)


        time = findViewById(R.id.new_task_time)
        var title : EditText = findViewById(R.id.new_task_title)

        time.setOnClickListener {
            getDateTimeCalendar()

            TimePickerDialog(this,R. style.DialogTheme,this,hour, minute,false ).show()
        }


        var sdf : SimpleDateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        var currentDate = sdf.format(Calendar.getInstance().time).toString()

        userRef.child(auth.currentUser?.uid!!).child(currentDate).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                {
                    count = dataSnapshot.childrenCount
                }
                else
                {
                    count =0
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })


        var cancel : TextView = findViewById(R.id.task_cancel)
        var assign : TextView = findViewById(R.id.task_assign)
        assign.setOnClickListener {
            if (TextUtils.isEmpty(time.text.toString().trim()))
            {
                Toast.makeText(this,"Select a time",Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(title.text.toString().trim()))
            {
                Toast.makeText(this,"Title is missing",Toast.LENGTH_SHORT).show()
            }
            else
            {

                var sdf1 : SimpleDateFormat = SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault())
                var currentDateAndTime = sdf1.format(Calendar.getInstance().time).toString()

                var LoadingBar = ProgressDialog(this)
                LoadingBar.show()
                LoadingBar.setContentView(R.layout.progress_bar)
                LoadingBar.window?.setBackgroundDrawableResource(android.R.color.transparent)
                LoadingBar.setCanceledOnTouchOutside(true)

                val todo = Todo(title.text.toString().trim(),time.text.toString().trim(),count+1)

                if (auth.currentUser?.uid != null) {
                    userRef.child(auth.currentUser?.uid!!).child(currentDate).child(currentDateAndTime).setValue(todo).addOnCompleteListener(this) {
                            task ->
                        if (task.isSuccessful)
                        {
                            LoadingBar.dismiss()
                            cancel.text = "Back"
                        }
                        else
                        {
                            Toast.makeText(this,"Error Occurred : Try Again",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        cancel.setOnClickListener {
            this.onBackPressed()
        }


    }

    private fun getDateTimeCalendar() {
        val cal:Calendar = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        hour = p1
        minute = p2

        time.text = hour.toString() + " : " +minute.toString()
    }
}