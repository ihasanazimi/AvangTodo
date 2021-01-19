package ir.ha.avanghtodo.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.databinding.ActivityMainBinding
import ir.ha.avanghtodo.detail.DetailActivity
import ir.ha.avanghtodo.dialogs.*
import ir.ha.avanghtodo.room.Category
import ir.ha.avanghtodo.room.CompletedTask
import ir.ha.avanghtodo.room.RoomDB
import ir.ha.avanghtodo.room.Task
import ir.ha.avanghtodo.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),
    MainC.View,
    Adapter.EventListener,
    AdapterCompletedTask.EventListenerCompletedTask,
    DialogSort.SortEventListeners,
    DialogCompletedSearch.EventDialogSearchCompleteTask,
    DialogFilter.DialogFilterEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rv: RecyclerView
    private lateinit var rvCompleted: RecyclerView
    private lateinit var presenter: MainC.Presenter
    private lateinit var adapterTask: Adapter
    private lateinit var adapterCompletedTask: AdapterCompletedTask
    private lateinit var deleteAll: View
    private lateinit var deleteAllCompletedTask: View
    private lateinit var addTask: View
    private lateinit var emptyStateBox: View
    private lateinit var viewGroupAlarmAndSort: View
    private lateinit var searchBox: SearchView
    private lateinit var db: RoomDB
    private lateinit var ivMore: ImageView
    private lateinit var ivFilter: ImageView
    private lateinit var ivSort: ImageView
    private lateinit var showDate: TextView
    private lateinit var completedViewGroup: View
    private lateinit var dialogSort: DialogSort

    val Time_Between_Two_Back : Long = 2000
    var TimeBackPressed = 0L

    companion object {
        const val REQUEST_CODE = 123
        const val RESULT_CODE_ADD_TASK = 1001
        const val RESULT_CODE_UPDATE_TASK = 1002
        const val RESULT_CODE_DELETE_TASK = 1003
        const val ADD_TASK_KEY = "ADD_TASK_KEY"
        const val UPDATE_TASK_KEY = "UPDATE_TASK_KEY"
        const val DELETE_TASK_KEY = "DELETE_TASK_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()


        //Listeners
        /** Add Task */
        addTask.setOnClickListener {
            startActivityForResult(Intent(this, DetailActivity::class.java), REQUEST_CODE)
        }

        /**Help Dialog*/
        ivMore.setOnClickListener {
            val menu = PopupMenu(this, ivMore)
            menu.inflate(R.menu.help)
            menu.setOnMenuItemClickListener {

                when (it?.itemId) {

                    R.id.help -> {
                        val dialog = HelpDialog()
                        dialog.isCancelable = true
                        dialog.show(supportFragmentManager, null)
                    }

                    R.id.setting -> {
                        val dialog = DialogSetting()
                        dialog.isCancelable = true
                        dialog.show(supportFragmentManager, null)
                    }

                    R.id.aboutUs -> {
                        val dialogAboutUs = DialogAboutUs()
                        dialogAboutUs.show(supportFragmentManager, null)

                    }
                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }

                return@setOnMenuItemClickListener false
            }
            menu.show()
        }

        /**Search Listener in Task and found Target..*/
        searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.onSearch(newText.toString().trim())
                return false
            }

        })


        searchBox.setOnCloseListener {
            if (db.daoCompleted().getAllTask().isNotEmpty()) {
                closeListener(true)
                setEmptyStateVisibility(false)
            } else closeListener(false)
            return@setOnCloseListener false
        }

        /** (BottomSheetDialog Delete All Items)
         * Delete Item in InterFaces ----interfaces---> No Item Click | Yes item Click*/
        deleteAll.setOnClickListener {
            val bottomSheetDeleteAllMainList = BottomSheetDialog(object :
                BottomSheetDialog.EventListener {
                override fun deleteListener() {
                    presenter.onDeleteAllBtnClick()
                    alarmAndSortViewGroup(false)
                }

            })
            bottomSheetDeleteAllMainList.isCancelable = false
            bottomSheetDeleteAllMainList.show(supportFragmentManager, null)

        }


        deleteAllCompletedTask.setOnClickListener {
            val bottomSheetDeleteAllCompletedList = BottomSheetDialog(object :
                BottomSheetDialog.EventListener {
                override fun deleteListener() {
                    deleteAllCompletedList()
                }

            })
            bottomSheetDeleteAllCompletedList.isCancelable = false
            bottomSheetDeleteAllCompletedList.show(supportFragmentManager, null)

        }


        ivSort.setOnClickListener {
            dialogSort = DialogSort(this)
            dialogSort.show(supportFragmentManager, null)
        }


        //Search Completed List dialog
        findViewById<View>(R.id.searchBtnCompletedList).setOnClickListener {
            val dialog = DialogCompletedSearch(this)
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, null)
        }


        ivFilter.setOnClickListener {
            val dialogFilter = DialogFilter(this)
            dialogFilter.show(supportFragmentManager, null)
        }


        /**Attach VIEW  (MVP) */
        presenter.onAttach(this@MainActivity)


        /**Close keyboard*/
        hideKeyboard(this)


    }


    private fun initialize() {
        presenter = PresenterMain()
        presenter = PresenterMain(
            RoomDB.getDataBase(this).dao(),
            RoomDB.getDataBase(this).daoCompleted()
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        db = RoomDB.getDataBase(this)
        rv = binding.rv
        rvCompleted = binding.rvCompleted
        deleteAll = binding.deleteAllBtn
        deleteAllCompletedTask = binding.deleteAllBtnCompletedList
        emptyStateBox = binding.emptyStateBox
        addTask = binding.addTask
        searchBox = binding.searchET
        ivMore = binding.ivMore
        ivFilter = binding.ivFilter
        ivSort = binding.ivSort
        showDate = binding.showDate
        completedViewGroup = binding.completedListViewGroup
        viewGroupAlarmAndSort = binding.alarmAndSortViewGroup


        // For when Categories List Is Empty  -  inja ye dune Item ezade mikonim (none)
        if (RoomDB.getDataBase(this).daoCat().getAllCategories().isEmpty()) {
            val cat = Category()
            cat.categoty = resources.getString(R.string.none)
            val id = RoomDB.getDataBase(this).daoCat().insert(cat)
            cat.uid = id
        }


        //Show Date In TextView
        if (Locale.getDefault().language.equals("en")) {
            showDate.text = DateUtil.getGregorianDate()
        } else {
            showDate.text = DateUtil.getCurrentShamsidate().toString()
        }

        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvCompleted.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapterTask = Adapter(arrayListOf(), this)
        adapterCompletedTask = AdapterCompletedTask(arrayListOf(), this)

        rv.adapter = adapterTask
        rvCompleted.adapter = adapterCompletedTask

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (data == null) {
            /**Close keyboard*/
            hideKeyboard(this)
            searchBox.isFocusable = false
            return

        } else {


            if (requestCode == REQUEST_CODE) {


                /** Delete Expertion */
                if (resultCode == RESULT_CODE_DELETE_TASK) {

                    val task = data.getParcelableExtra<Task>(DELETE_TASK_KEY)
                    if (task != null) adapterTask.delete(task)

                    if (adapterTask.myTasks.isEmpty()) {
                        setEmptyStateVisibility(true)
                        setEnableDeleteBtn(false)
                        alarmAndSortViewGroup(false)
                    } else {
                        setEmptyStateVisibility(false)
                        setEnableDeleteBtn(true)
                        alarmAndSortViewGroup(true)
                    }

                    /**Close keyboard*/
                    hideKeyboard(this)
                    return
                }

                /** ADD Expertion */
                if (resultCode == RESULT_CODE_ADD_TASK) {

                    val task = data.getParcelableExtra<Task>(ADD_TASK_KEY)

                    if (task != null) {
                        rv.smoothScrollToPosition(0)
                        setEmptyStateVisibility(false)
                        alarmAndSortViewGroup(true)
                        setEnableDeleteBtn(true)
                        adapterTask.addItem(task)

                        /**Close keyboard*/
                        hideKeyboard(this)
                    }
                    return
                }

                /** UPDATE Expertion */
                if (resultCode == RESULT_CODE_UPDATE_TASK) {

                    val task = data.getParcelableExtra<Task>(UPDATE_TASK_KEY)
                    if (task != null) {
                        db.dao().update(task)
                        setEmptyStateVisibility(false)
                        setEnableDeleteBtn(true)
                        adapterTask.update(task)

                        /**Close keyboard*/
                        hideKeyboard(this)
                    }

                    return
                }

            }
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


    override fun showAllTasks(list: List<Task>) {
        if (list.isNotEmpty()) {

            adapterTask.setList(list)
            setEmptyStateVisibility(false)

        } else {
            setEmptyStateVisibility(true)
        }
    }


    override fun showAllCompletedTasks(list: List<CompletedTask>) {
        if (list.isNotEmpty()) {
            adapterCompletedTask.setList(list)
            showCompletedListViewGroup(true)
        } else {
            if (adapterTask.itemCount == 0) {
                setEmptyStateVisibility(true)
                Snackbar.make(
                    findViewById(R.id.root),
                    R.string.msg_deletedCompletedList,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            showCompletedListViewGroup(false)
        }
    }


    override fun updateTask(task: Task) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(UPDATE_TASK_KEY, task)
        startActivityForResult(intent, REQUEST_CODE)
    }


    override fun clearList() {
        adapterTask.clear()
    }


    override fun deleteAll() {
        adapterTask.clear()
        Snackbar.make(
            findViewById(R.id.root),
            getString(R.string.success_msg),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun deleteAllCompletedList() {
        adapterCompletedTask.clear()
        Snackbar.make(
            findViewById(R.id.root),
            getString(R.string.success_msg),
            Snackbar.LENGTH_LONG
        ).show()
        showCompletedListViewGroup(false)
        db.daoCompleted().deleteAll()
        if (db.dao().getAllTask().isEmpty()) {
            setEmptyStateVisibility(true)
        }
    }


    override fun setEmptyStateVisibility(visible: Boolean) {
        if (visible) {
            emptyStateBox.visibility = View.VISIBLE
        } else emptyStateBox.visibility = View.GONE
    }


    override fun updateItemCompleteAlpha(task: Task, position: Int) {
    }


    override fun setEnableDeleteBtn(enable: Boolean) {
        deleteAll.isEnabled = enable
    }


    override fun showCompletedListViewGroup(visible: Boolean) {
        if (visible) {
            completedViewGroup.visibility = View.VISIBLE
        } else {
            completedViewGroup.visibility = View.GONE
        }
    }

    override fun closeListener(close: Boolean) {
        if (close) {
            completedViewGroup.visibility = View.VISIBLE
        } else {
            completedViewGroup.visibility = View.GONE
        }
    }

    override fun alarmAndSortViewGroup(visible: Boolean) {
        if (visible) {
            viewGroupAlarmAndSort.visibility = View.VISIBLE
        } else {
            viewGroupAlarmAndSort.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }


    override fun onBackPressed() {

        if (TimeBackPressed+Time_Between_Two_Back > System.currentTimeMillis()){
            super.onBackPressed()
            return
        }else{
            Snackbar.make(findViewById(R.id.root), R.string.do_you_want_to_exit_the_application, Snackbar.LENGTH_LONG).show()
        }
        TimeBackPressed =System.currentTimeMillis();
    }


    //-------------------- EVENT LISTENER ------------------------//
    override fun onItemClickListener(task: Task, position: Int) {

        /**set complete TRUE and Remove it from DB and Normal Task (from RecyclerView) */
        adapterTask.delete(task, position)
        db.dao().delete(task)


        /**Create Complete Task Model by Complete = FALSE
         * and insert this on Completed List in Completed-RecyclerView */
        task.completed = !task.completed
        val completedTask = CompletedTask()
        completedTask.uid = task.uid
        completedTask.title = task.title
        completedTask.importance = task.importance
        completedTask.completed = task.completed
        completedTask.category = task.category
        completedTask.voice = task.voice

        //insert Into completed DB
        db.daoCompleted().insert(completedTask)
        //show list visibility
        showCompletedListViewGroup(true)
        //add to list (into adapter)
        adapterCompletedTask.addItem(completedTask)

        if (adapterTask.itemCount != 0) {
            setEnableDeleteBtn(true)
        } else {
            setEnableDeleteBtn(false)
            alarmAndSortViewGroup(false)
        }
    }


    override fun onItemLongClickListener(task: Task, position: Int) {
        updateTask(task)
    }


    override fun onItemCompletedClickListener(task: CompletedTask, position: Int) {

        /**set complete TRUE and Remove it from DB and Normal Task (from RecyclerView) */
        adapterCompletedTask.delete(task, position)
        db.daoCompleted().delete(task)


        /**Create Normal Task Model by Complete = TRUE
         *
         * and insert this on Normal Task-List in Normal-RecyclerView */
        task.completed = !task.completed
        val t = Task()
        t.uid = task.uid
        t.title = task.title
        t.importance = task.importance!!
        t.completed = task.completed
        t.category = task.category
        t.voice = task.voice

        //insert Into completed DB
        db.dao().insert(t)
        //hide EmptyState visibility
        setEmptyStateVisibility(false)
        //insert to list (into adapter)
        adapterTask.addItem(t)

        if (adapterTask.itemCount != 0) {
            setEnableDeleteBtn(true)
            alarmAndSortViewGroup(true)
        } else {
            setEnableDeleteBtn(false)
        }


        if (adapterCompletedTask.itemCount > 0) {
            showCompletedListViewGroup(true)
        } else {
            showCompletedListViewGroup(false)
        }

    }


    /**Close keyboard Function*/
    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun ASC() {
        val list3 = db.dao().sortByImportance(3)
        val list2 = db.dao().sortByImportance(2)
        val list1 = db.dao().sortByImportance(1)
        val mainList = list1 + list2 + list3
        showAllTasks(mainList)
        Snackbar.make(findViewById(R.id.root), R.string.done, Snackbar.LENGTH_SHORT).show()
    }


    override fun DESC() {
        val list3 = db.dao().sortByImportance(3)
        val list2 = db.dao().sortByImportance(2)
        val list1 = db.dao().sortByImportance(1)
        val mainList = list3 + list2 + list1
        showAllTasks(mainList)
        Snackbar.make(findViewById(R.id.root), R.string.done, Snackbar.LENGTH_SHORT).show()
    }

    override fun normalList() {
        showAllTasks(db.dao().getAllTask())
        Snackbar.make(findViewById(R.id.root), R.string.done, Snackbar.LENGTH_SHORT).show()
    }


    override fun recycleTask(completedTask: CompletedTask) {
        /**set complete TRUE and Remove it from DB and Normal Task (from RecyclerView) */
        adapterCompletedTask.delete(completedTask)
        db.daoCompleted().delete(completedTask)


        /**Create Normal Task Model by Complete = TRUE
         *
         * and insert this on Normal Task-List in Normal-RecyclerView */
        completedTask.completed = !completedTask.completed
        val t = Task()
        t.uid = completedTask.uid
        t.title = completedTask.title
        t.importance = completedTask.importance!!
        t.completed = completedTask.completed
        t.category = completedTask.category
        t.voice = completedTask.voice

        //insert Into completed DB
        db.dao().insert(t)
        //hide EmptyState visibility
        setEmptyStateVisibility(false)
        //insert to list (into adapter)
        adapterTask.addItem(t)

        if (adapterTask.itemCount != 0) {
            setEnableDeleteBtn(true)
            alarmAndSortViewGroup(true)
        } else {
            setEnableDeleteBtn(false)
        }


        if (adapterCompletedTask.itemCount > 0) {
            showCompletedListViewGroup(true)
        } else {
            showCompletedListViewGroup(false)
        }
    }


    /** Return new list from Search-Complete-Dialog AND Update Complete List*/
    override fun returnNewList(list: List<CompletedTask>) {
        adapterCompletedTask.clear()
        showAllCompletedTasks(list)
    }

    override fun filteredList(list: List<Task>) {
        adapterTask.setList(list)
    }

    override fun badgeViewUpdateFilter(used: Boolean) {
        val badge = findViewById<View>(R.id.badgeFilter)

        if (used) {
            badge.visibility = View.VISIBLE
        }else {
            badge.visibility = View.INVISIBLE
        }

    }

    override fun badgeViewUpdateSort(used: Boolean) {
        val badge = findViewById<View>(R.id.badgeSort)

        if (used) {
            badge.visibility = View.VISIBLE
        }else {
            badge.visibility = View.INVISIBLE
        }

    }


}