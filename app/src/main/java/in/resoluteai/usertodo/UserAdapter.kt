package `in`.resoluteai.usertodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList : ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_layout,
        parent,
        false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.name.text = currentItem.username +","
        holder.age.text = currentItem.age + " years"
        holder.dob.text = currentItem.dob


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val name : TextView = itemView.findViewById(R.id.user_list_name)
        val age : TextView = itemView.findViewById(R.id.user_list_age)
        val dob : TextView = itemView.findViewById(R.id.user_list_dob)



    }


}