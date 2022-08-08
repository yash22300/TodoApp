package `in`.resoluteai.usertodo

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SetupActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    val database = Firebase.database
    val auth = Firebase.auth
    val userRef = database.getReference("Users")
    var day =0
    var month = 0
    var year = 0
    private lateinit var gender : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val userId = Firebase.auth.currentUser?.uid
        var name: EditText = findViewById(R.id.setup_name)
        var age: EditText = findViewById(R.id.setup_age)
        gender = findViewById(R.id.setup_Gender)
        var submit: TextView = findViewById(R.id.submit)

        gender.setOnClickListener {
                getDateTimeCalendar()

                DatePickerDialog(this,this,day, month,year,).show()
        }

        submit.setOnClickListener {
            if (TextUtils.isEmpty(name.text.toString().trim()))
            {
                Toast.makeText(this,"Enter a valid name",Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(age.text.toString().trim()))
            {
                Toast.makeText(this,"Enter a valid age",Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(gender.text.toString().trim()))
            {
                Toast.makeText(this,"Select a date",Toast.LENGTH_SHORT).show()
            }
            else
            {
                var LoadingBar = ProgressDialog(this)
                LoadingBar.show()
                LoadingBar.setContentView(R.layout.progress_bar)
                LoadingBar.window?.setBackgroundDrawableResource(android.R.color.transparent)
                LoadingBar.setCanceledOnTouchOutside(true)

                val user = User(name.text.toString().trim(),age.text.toString().trim(),
                    gender.text.toString().trim())

                if (userId != null) {
                    userRef.child(userId).setValue(user).addOnCompleteListener(this) {
                            task ->
                        if (task.isSuccessful)
                        {
                            LoadingBar.dismiss()
                            var intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this,"Error Occurred : Try Again",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }


    private fun getDateTimeCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        day = p3
        month = p2+1
        year = p1

        gender.text = day.toString() +" - "+month.toString()+" - "+year.toString()
    }


}