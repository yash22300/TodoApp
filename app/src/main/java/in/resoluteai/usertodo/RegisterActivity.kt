package `in`.resoluteai.usertodo

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        var email : EditText = findViewById(R.id.register_email)
        var password : EditText = findViewById(R.id.register_pass)
        var confirmPassword : EditText = findViewById(R.id.register_conf_pass)

        var register : TextView = findViewById(R.id.register_btn)
        var back : TextView = findViewById(R.id.register_loginBtn)

        register.setOnClickListener {

            if(TextUtils.isEmpty(email.text.toString().trim()))
            {
                Toast.makeText(this,"Enter a valid email",Toast.LENGTH_SHORT).show()
            }
            else if(TextUtils.isEmpty(password.text.toString().trim()))
            {
                Toast.makeText(this,"Password is missing",Toast.LENGTH_SHORT).show()
            }
            else if(TextUtils.isEmpty(confirmPassword.text.toString().trim()))
            {
                Toast.makeText(this,"Confirm Password is missing",Toast.LENGTH_SHORT).show()
            }
            else if(!password.text.toString().trim().equals(confirmPassword.text.toString().trim()))
            {
                Toast.makeText(this,"Passwords don't match",Toast.LENGTH_SHORT).show()
            }
            else
            {

                var LoadingBar : ProgressDialog = ProgressDialog(this)
                LoadingBar.show()
                LoadingBar.setContentView(R.layout.progress_bar)
                LoadingBar.window?.setBackgroundDrawableResource(android.R.color.transparent)
                LoadingBar.setCanceledOnTouchOutside(true)

                auth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            LoadingBar.dismiss()
                            var intent = Intent(this,SetupActivity::class.java)
                            startActivity(intent)
                            finish()
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            LoadingBar.dismiss()
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Error Occurred : Try Again",
                                Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }
                    }
            }
        }

        back.setOnClickListener {

            this.onBackPressed()

        }
    }
}