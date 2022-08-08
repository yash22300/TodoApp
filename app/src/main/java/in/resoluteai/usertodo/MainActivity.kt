package `in`.resoluteai.usertodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val database = Firebase.database
    val auth = Firebase.auth
    val userRef = database.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment : Fragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container,fragment)
            .commit()


    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = auth.currentUser
        if (user == null) {
            sendUserToLoginActivity()
        } else {
            CheckUserExistence()
        }
    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this, RegisterActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()
    }

    private fun CheckUserExistence() {
        val current_user_id: String? = auth.currentUser?.uid
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!current_user_id?.let { dataSnapshot.hasChild(it) }!!) {
                    SendUserToSetupActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun SendUserToSetupActivity() {
        val setupIntent = Intent(this, SetupActivity::class.java)
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(setupIntent)
        finish()
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }
}