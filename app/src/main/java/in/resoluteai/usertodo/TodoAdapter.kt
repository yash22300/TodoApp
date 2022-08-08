package `in`.resoluteai.usertodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val taskList : ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_list_layout,
            parent,
            false)
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = taskList[position]

        holder.time.text = currentItem.time
        holder.title.text = currentItem.title

    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class TodoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.task_list_title)
        val time : TextView = itemView.findViewById(R.id.task_list_time)
    }
}