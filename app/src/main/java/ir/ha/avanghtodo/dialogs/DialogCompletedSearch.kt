package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.CompletedTask
import ir.ha.avanghtodo.room.RoomDB
import ir.ha.avanghtodo.utils.AdapterCompletedTaskSearch
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.io.IOException


class DialogCompletedSearch (var eventListener : EventDialogSearchCompleteTask) : DialogFragment(),
    AdapterCompletedTaskSearch.EventListenerCompletedTask {


    lateinit var dialog: AlertDialog.Builder
    lateinit var searchView: TextInputEditText
    lateinit var recyclerView: RecyclerView
    lateinit var closeBTN: View
    lateinit var adapter: AdapterCompletedTaskSearch
    lateinit var db: RoomDB
//    private var tasksList = arrayListOf<CompletedTask>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Dialog Initialize
        dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_completed_search, null, false)

        // dialog views
        searchView = view.findViewById(R.id.searchET)
        recyclerView = view.findViewById(R.id.rv)
        closeBTN = view.findViewById(R.id.close)
        db = RoomDB.getDataBase(searchView.context)

        // RecyclerView Config / Get All List from DB
        val list = db.daoCompleted().getAllTask().reversed()
        val adapterList = arrayListOf<CompletedTask>()
        if (list.isNotEmpty()) {
            for (i in list) {
                adapterList.add(i)
            }

            adapter = AdapterCompletedTaskSearch(adapterList,this)
            recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
            ItemTouchHelper(touchHelperCallBack).attachToRecyclerView(recyclerView)
            // this Option is like IOS LIST UX (SLOW)
            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)


        }


        //Listeners
        closeBTN.setOnClickListener{
            dismiss()
            val allTask = db.daoCompleted().getAllTask()
            eventListener.returnNewList(allTask)
        }


        searchView.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty()){

                    //GET ALL Complete Task
                    val allTask = db.daoCompleted().getAllTask().reversed()

                    //Clear Task AND Show All DataBase Task
                    adapter.myTasks.clear()

                    val l = arrayListOf<CompletedTask>()
                    for (i in allTask){
                        l.add(i)
                    }

                    //Set list
                    adapter.myTasks = l
                    adapter.notifyDataSetChanged()

                }else {

                    val foundList = db.daoCompleted().search(s.toString().trim()).reversed()
                    adapter.setList(foundList)
                }

            }

        })


        dialog.setView(view)

        return dialog.create()
    }


        private val touchHelperCallBack : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.END or ItemTouchHelper.START){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            try {
                val position = viewHolder.adapterPosition
                db.daoCompleted().delete(adapter.myTasks[position])
                adapter.myTasks.removeAt(position)
                adapter.notifyItemRemoved(position)

                if (adapter.myTasks.isEmpty()){
                    dismiss()
                    val allTask = db.daoCompleted().getAllTask().reversed()
                    eventListener.returnNewList(allTask)
                }

            }catch (e : IOException){e.printStackTrace()}
        }

    }







    /**Dialog Events --------------------------------------------------------------------------------------*/
    interface EventDialogSearchCompleteTask {
        fun recycleTask(completedTask: CompletedTask)
        fun returnNewList(list: List<CompletedTask>)
    }



    /** Adapter Events --------------------------------------------------------------------------------------*/
    override fun onclick(task: CompletedTask, position: Int) {
        db.daoCompleted().delete(task)
        adapter.delete(task)
        adapter.myTasks.remove(task)
        eventListener.recycleTask(task)
        if (adapter.myTasks.isEmpty()){
            dismiss()
            val allTask = db.daoCompleted().getAllTask().reversed()
            eventListener.returnNewList(allTask)
        }
    }


    override fun onReturnResultList(newList: List<CompletedTask>, position: Int) {
        eventListener.returnNewList(newList)
    }


}