package ir.ha.avanghtodo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.Task

class Adapter(var myTasks : ArrayList<Task> ,var eventListener: EventListener) : RecyclerView.Adapter<Adapter.Vh>() {

    lateinit var myParent: ViewGroup


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        myParent = parent
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.row_normal, parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(myTasks[position])
    }

    override fun getItemCount(): Int {
        return myTasks.size
    }




    fun addItem(task: Task) {
        myTasks.add(0, task)
        notifyItemInserted(0)
    }

    fun delete(task: Task, position: Int) {

        if (myTasks[position].uid == task.uid) {
            myTasks.removeAt(position)
            notifyItemRemoved(position)
        }

    }


    fun delete(task: Task) {

        for (i in 0..myTasks.size - 1) {

            if (myTasks[i].uid == task.uid) {
                myTasks.removeAt(i)
                notifyDataSetChanged()
                break
            }

        }

    }


    fun setList(tasks: List<Task>) {
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

    fun update(task: Task) {
        for (i in 0..myTasks.size - 1) {
            if (myTasks[i].uid == task.uid) {
                myTasks[i] = task
                notifyItemChanged(i)
            }
        }
    }


    inner class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private var titleTv: TextView = itemView.findViewById(R.id.taskName)
        private var importanceView: MaterialCardView = itemView.findViewById(R.id.view)
        private var categoryTv: TextView = itemView.findViewById(R.id.category)




        fun bind(task: Task) {

            titleTv.text = task.title
            titleTv.paintFlags = 0
            if (task.category.isNotEmpty()) {
                categoryTv.text = task.category
            }


            itemView.setOnClickListener {
                eventListener.onItemClickListener(myTasks[adapterPosition], adapterPosition)
                return@setOnClickListener
            }


            itemView.setOnLongClickListener {
                eventListener.onItemLongClickListener(myTasks[adapterPosition], adapterPosition)
                return@setOnLongClickListener true
            }


            when (task.importance) {
                Task.LOW -> {
                    importanceView.setCardBackgroundColor(myParent.resources.getColor(R.color.lowImportanceColor))
                    categoryTv.setTextColor(myParent.context.resources.getColor(R.color.lowImportanceColor))
                }
                Task.NORMAL -> {
                    importanceView.setCardBackgroundColor(myParent.resources.getColor(R.color.normalImportanceColor))
                    categoryTv.setTextColor(myParent.context.resources.getColor(R.color.normalImportanceColor))
                }
                Task.HIGH -> {
                    importanceView.setCardBackgroundColor(myParent.resources.getColor(R.color.highImportanceColor))
                    categoryTv.setTextColor(myParent.context.resources.getColor(R.color.highImportanceColor))
                }
            }

        }


    }


    class MyDiffUtil() : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title === newItem.title
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }


    interface EventListener {

        fun onItemClickListener(task: Task, position: Int)
        fun onItemLongClickListener(task: Task, position: Int)
    }



}