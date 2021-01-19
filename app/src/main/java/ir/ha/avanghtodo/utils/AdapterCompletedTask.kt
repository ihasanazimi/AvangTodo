package ir.ha.avanghtodo.utils


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.CompletedTask

class AdapterCompletedTask(var myTasks : ArrayList<CompletedTask>,var eventListenerCompletedTask: EventListenerCompletedTask) :  RecyclerView.Adapter<AdapterCompletedTask.Vh>() {

    lateinit var myParent: ViewGroup


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCompletedTask.Vh {
        myParent = parent
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.row_completed, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterCompletedTask.Vh, position: Int) {
        holder.bind(myTasks[position])
    }

    override fun getItemCount(): Int {
        return myTasks.size
    }



    fun addItem(task: CompletedTask) {
        myTasks.add(0, task)
        notifyItemInserted(0)
    }


    fun delete(task: CompletedTask, position: Int) {

        if (myTasks[position].uid == task.uid) {
            myTasks.removeAt(position)
            notifyItemRemoved(position)
        }

    }


    fun delete(task: CompletedTask) {

        for (i in 0..myTasks.size - 1) {

            if (myTasks[i].uid == task.uid) {
                myTasks.removeAt(i)
                notifyDataSetChanged()
                break
            }

        }

    }


    fun setList(tasks: List<CompletedTask>) {
        myTasks.clear()
        for (i in tasks) {
            myTasks.add(i)
        }
        notifyDataSetChanged()
    }


    fun clear() {
        myTasks.clear()
        notifyDataSetChanged()
    }

    fun update(task: CompletedTask) {
        for (i in 0..myTasks.size - 1) {
            if (myTasks[i].uid == task.uid) {
                myTasks[i] = task
                notifyItemChanged(i)
            }
        }
    }


    inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private var titleTv: TextView = itemView.findViewById(R.id.taskName)
        private var categoryTv: TextView = itemView.findViewById(R.id.category)


        fun bind(task: CompletedTask) {

            titleTv.text = task.title
            titleTv.paintFlags = titleTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            if (task.category.isNotEmpty()) {
                categoryTv.text = task.category
            }


            itemView.setOnClickListener {
                eventListenerCompletedTask.onItemCompletedClickListener(myTasks[adapterPosition], adapterPosition)
                return@setOnClickListener
            }

        }


    }




    interface EventListenerCompletedTask {

        fun onItemCompletedClickListener(task: CompletedTask, position: Int)
    }




}