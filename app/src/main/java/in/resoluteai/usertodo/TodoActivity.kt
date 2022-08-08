package `in`.resoluteai.usertodo

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class TodoActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    val auth = Firebase.auth
    private var taskRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Todo")
    private lateinit var list : RecyclerView
    private lateinit var taskArrayList: ArrayList<Todo>
    private lateinit var progressBar : ProgressBar
    private lateinit var currentDate : String
    private lateinit var noData : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        noData = findViewById(R.id.no_data_found)
        progressBar= findViewById(R.id.task_list_progress)
        list= findViewById(R.id.task_list)
        list.layoutManager = LinearLayoutManager(this)
        list.setHasFixedSize(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        var sdf : SimpleDateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        currentDate = sdf.format(Calendar.getInstance().time).toString()

        taskRef.child(auth.currentUser?.uid!!).child(currentDate).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                {
                    noData.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    list.visibility = View.VISIBLE
                    showTask()
                }
                else
                {
                    list.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    noData.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })

        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                Log.d("date",DateUtils.getDayNumber(date)+DateUtils.getMonthNumber(date)+DateUtils.getYear(date))

                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
                return if(currentDate.equals(DateUtils.getDayNumber(date)+DateUtils.getMonthNumber(date)+DateUtils.getYear(date)))
                {
                    R.layout.selected_calendar_item
                }
                    else if (!currentDate.equals(DateUtils.getDayNumber(date)+DateUtils.getMonthNumber(date)+DateUtils.getYear(date)))
                {
                        R.layout.calendar_item
                }
                    else if (isSelected)

                    when (cal[Calendar.DAY_OF_WEEK]) {
                        //Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        //Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        //FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item

                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        //Calendar.MONDAY -> R.layout.first_special_calendar_item
                        //Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        //Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                Log.d("date1",date.toString())
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                var tv_data_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_date_calendar_item)
                tv_data_calendar_item.text = DateUtils.getDayNumber(date)
                //holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                var tv_day_calendar_item : TextView = holder.itemView.findViewById(R.id.tv_day_calendar_item)
                tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

            }
        }

        var tvDate : TextView = findViewById<TextView>(R.id.tvDate)
        var tvDay : TextView = findViewById<TextView>(R.id.tvDay)
        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)
                super.whenSelectionChanged(isSelected, position, date)
            }


        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                currentDate = DateUtils.getDayNumber(date)+DateUtils.getMonthNumber(date)+DateUtils.getYear(date)
                list.visibility = View.GONE
                noData.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                taskRef.child(auth.currentUser?.uid!!).child(currentDate).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            taskArrayList = arrayListOf<Todo>()
                            noData.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                            list.visibility = View.VISIBLE
                            showTask()
                        }
                        else
                        {
                            list.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            noData.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                        // ...
                    }
                })
                // in this example sunday and saturday can't be selected, others can
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> true
                    Calendar.SUNDAY -> true
                    else -> true
                }
            }
        }

        var main_single_row_calendar : SingleRowCalendar = findViewById(R.id.main_single_row_calendar)
        main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            //setDates(getFutureDatesOfCurrentMonth())
            init()
        }
        // here we init our calendar, also you can set more properties if you haven't specified in XML layout

        taskArrayList = arrayListOf<Todo>()

    }

    private fun showTask() {
        val userId = Firebase.auth.currentUser?.uid

        val taskRefQuery = userId?.let { FirebaseDatabase.getInstance().getReference("Todo").child(it).child(currentDate).orderByChild("count") }!!

        taskRefQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(Todo::class.java)
                    taskArrayList.add(task!!)
                    progressBar.visibility = View.GONE
                }

                list.adapter = TodoAdapter(taskArrayList)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    /*private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    private fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }*/

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    override fun onStart() {
        super.onStart()
        var tvDate : TextView = findViewById<TextView>(R.id.tvDate)
        var tvDay : TextView = findViewById<TextView>(R.id.tvDay)

        var sdf : SimpleDateFormat = SimpleDateFormat("MMMM', 'dd", Locale.getDefault())
        tvDate.text = sdf.format(Calendar.getInstance().time).toString()

        //var sdf1 : SimpleDateFormat = SimpleDateFormat("DDDD", Locale.getDefault())

        var i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()
        if (i.equals("1"))
        {
            tvDay.text = " Sunday"
        }
        else if (i.equals("2"))
        {
            tvDay.text = " Monday"
        }
        else if (i.equals("3"))
        {
            tvDay.text = " Tuesday"
        }
        else if (i.equals("4"))
        {
            tvDay.text = " Wednesday"
        }
        else if (i.equals("5"))
        {
            tvDay.text = " Thursday"
        }
        else if (i.equals("6"))
        {
            tvDay.text = " Friday"
        }
        else if (i.equals("7"))
        {
            tvDay.text = " Saturday"
        }

        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                Log.d("date",date.toString())
                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
                return if (position == 0)

                    when (cal[Calendar.DAY_OF_WEEK]) {
                        //Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        //Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        //FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item
                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        //Calendar.MONDAY -> R.layout.first_special_calendar_item
                        //Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        //Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {

            }
        }

    }

}