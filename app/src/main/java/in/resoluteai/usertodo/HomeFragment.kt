package `in`.resoluteai.usertodo

import android.content.ContentValues.TAG
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var userRef : DatabaseReference
    private lateinit var list : RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var progressBar : ProgressBar
    private lateinit var name : TextView
    private lateinit var age : TextView
    private lateinit var dob : TextView
    private lateinit var btn : Switch
    val database = Firebase.database
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View ? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        var todo : ImageView = v.findViewById<ImageView>(R.id.home_user_todo)
        var add : RelativeLayout = v.findViewById<RelativeLayout>(R.id.add_todo_home)
        name = v.findViewById(R.id.home_name)
        age = v.findViewById(R.id.home_age)
        dob = v.findViewById(R.id.home_dob)

        progressBar= v.findViewById(R.id.user_list_progress)

        todo.setOnClickListener {
            var intent = Intent(activity,TodoActivity::class.java)
            startActivity(intent)
        }

        add.setOnClickListener {
            var intent = Intent(activity,NewTaskActivity::class.java)
            startActivity(intent)
        }

        btn = v.findViewById(R.id.switch_btn)
        btn.setOnCheckedChangeListener { compoundButton, b ->
            if(b)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        var isNightMode : Boolean = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        btn.isChecked = isNightMode

        list= v.findViewById(R.id.user_list)
        list.layoutManager = LinearLayoutManager(activity)
        list.setHasFixedSize(true)

        userArrayList = arrayListOf<User>()

        auth.currentUser?.let {
            FirebaseDatabase.getInstance().getReference("Users").child(it?.uid)
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists()) {
                            name.text = dataSnapshot.child("username").getValue().toString()
                            age.text = dataSnapshot.child("age").getValue().toString()+" years"
                            dob.text = dataSnapshot.child("dob").getValue().toString()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                        // ...
                    }
                })
        }

        getData()

        return v
    }

    private fun getData() {
        userRef = FirebaseDatabase.getInstance().getReference("Users")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    userArrayList.add(user!!)
                    progressBar.visibility = View.GONE
                }
                
                list.adapter = UserAdapter(userArrayList)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                
            }
        })

    }

}