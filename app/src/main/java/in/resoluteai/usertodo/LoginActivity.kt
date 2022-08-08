package `in`.resoluteai.usertodo

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        var email : EditText = findViewById(R.id.login_email)
        var password : EditText = findViewById(R.id.login_pass)
        var login : TextView = findViewById(R.id.login_btn)

        login.setOnClickListener {

            if(TextUtils.isEmpty(email.text.toString().trim()))
            {
                Toast.makeText(this,"Enter a valid email",Toast.LENGTH_SHORT).show()
            }
            else if(TextUtils.isEmpty(password.text.toString().trim()))
            {
                Toast.makeText(this,"Password is missing",Toast.LENGTH_SHORT).show()
            }
            else
            {

                var LoadingBar : ProgressDialog = ProgressDialog(this)
                LoadingBar.show()
                LoadingBar.setContentView(R.layout.progress_bar)
                LoadingBar.window?.setBackgroundDrawableResource(android.R.color.transparent)
                LoadingBar.setCanceledOnTouchOutside(true)

                auth.signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            LoadingBar.dismiss()
                            var intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            LoadingBar.dismiss()
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Error Occurred : Try Again",
                                Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }
                    }
            }
        }

        var register : TextView = findViewById(R.id.send_register_btn)
        register.setOnClickListener {
            var intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            sendUserToMainActivity()
        }
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }
}

