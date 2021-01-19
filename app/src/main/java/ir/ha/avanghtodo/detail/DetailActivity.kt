package ir.ha.avanghtodo.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.databinding.ActivityDetailsBinding
import ir.ha.avanghtodo.dialogs.DialogAddCategory
import ir.ha.avanghtodo.dialogs.DialogDelete
import ir.ha.avanghtodo.main.MainActivity
import ir.ha.avanghtodo.room.Category
import ir.ha.avanghtodo.room.RoomDB
import ir.ha.avanghtodo.room.Task
import kotlinx.android.synthetic.main.activity_details.*


class DetailActivity : AppCompatActivity(), DetailsC.View, View.OnClickListener,
    DialogDelete.EventListener,
    DialogAddCategory.DialogAddCategoryEventListeners {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var presenter: DetailsC.Presenter
    private lateinit var addTask: View
    private lateinit var editTask: View
    private lateinit var inputBox: EditText
    private lateinit var importanceLow: LinearLayout
    private lateinit var importanceNormal: LinearLayout
    private lateinit var importanceHigh: LinearLayout
    private lateinit var deleteBtn: ImageView
    private lateinit var ivLow: ImageView
    private lateinit var ivNormal: ImageView
    private lateinit var ivHigh: ImageView
    lateinit var deleteDialog: DialogDelete
    lateinit var updateTaskForDialog: Task
    private lateinit var spinner: AppCompatSpinner
    private lateinit var ivAddCat: ImageView
    private lateinit var ivRemoveCat: ImageView
    lateinit var spinnerAdapter : ArrayAdapter<String>

    private var importanceVariable: Int = Task.NORMAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        changeImportanceState(importanceVariable)

        /** MVP ATTACH */
        presenter.onAttach(this)

        val updateTask: Task? = intent.getParcelableExtra(MainActivity.UPDATE_TASK_KEY)
        if (updateTask != null) {
            updateTaskForDialog = updateTask
            importanceVariable = updateTask.importance
            showTask(updateTask)


            //Update Task Functionality
            editTask.setOnClickListener {
                presenter.editTask(updateTask, inputBox.text.toString(), importanceVariable,spinner.selectedItem.toString())
            }

            //Delete Task Functionality
            deleteBtn.setOnClickListener {
                deleteDialog = DialogDelete()
                deleteDialog.isCancelable = true
                deleteDialog.show(supportFragmentManager, null)
            }

        }


        //Add Task Functionality
        addTask.setOnClickListener {
            presenter.addTask(inputBox.text.toString(), importanceVariable , spinner.selectedItem.toString())
        }


        //Add Category Dialog
        ivAddCat.setOnClickListener {
            val dialogAddCategory = DialogAddCategory(this)
            dialogAddCategory.show(supportFragmentManager, null)
        }

        ivRemoveCat.setOnClickListener {
            val selectCurrentSpinnerItem = spinner.selectedItem.toString()
            if (selectCurrentSpinnerItem != resources.getString(R.string.none)) {
                presenter.removeCategory(selectCurrentSpinnerItem)
            }else Snackbar.make(findViewById(R.id.rootDetailActivity),resources.getString(R.string.def_cat_error),Snackbar.LENGTH_SHORT).show()
        }



        lowView.setOnClickListener(this)
        normalView.setOnClickListener(this)
        highView.setOnClickListener(this)



    }

    private fun initialize() {

        presenter = PresenterDetails()
        presenter = PresenterDetails(
            RoomDB.getDataBase(this).dao(),
            RoomDB.getDataBase(this).daoCat()
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)
        setContentView(binding.root)
        addTask = binding.addBtn
        editTask = binding.editBtn
        inputBox = binding.taskET
        deleteBtn = binding.deleteBtn
        ivLow = binding.low
        ivNormal = binding.normal
        ivHigh = binding.high
        importanceLow = binding.lowView
        importanceNormal = binding.normalView
        importanceHigh = binding.highView
        spinner = binding.spinner
        ivAddCat = binding.addCat
        ivRemoveCat = binding.removeCat

        val toolbar: Toolbar = binding.toolbarDetailsActivity

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

    }

    override fun showTask(task: Task) {

        inputBox.setText(task.title)
        spinner.setSelection(spinnerAdapter.getPosition(task.category))

        when (task.importance) {

            Task.LOW -> {
                ivLow.setImageResource(R.drawable.ic_tick)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle_2)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle)
            }
            Task.NORMAL -> {
                ivNormal.setImageResource(R.drawable.ic_tick)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle_2)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle)
            }
            Task.HIGH -> {
                ivHigh.setImageResource(R.drawable.ic_tick)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle_2)
            }

        }

        setDeleteBtnVisibility(true)
        editBtnVisibility(true)
        addBtnVisibility(false)
    }

    override fun setDeleteBtnVisibility(visible: Boolean) {
        if (visible) {
            deleteBtn.visibility = View.VISIBLE
        } else {
            deleteBtn.visibility = View.INVISIBLE
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun returnResult(resultCode: Int, task: Task, extraName: String) {

        val intent = Intent()
        intent.putExtra(extraName, task)
        setResult(resultCode, intent)
        finish()


    }

    override fun changeImportanceState(importance: Int) {

        when (importance) {

            Task.LOW -> {
                ivLow.setImageResource(R.drawable.ic_tick)
                ivNormal.setImageResource(0)
                ivHigh.setImageResource(0)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle_2)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle)
            }

            Task.NORMAL -> {
                ivLow.setImageResource(0)
                ivNormal.setImageResource(R.drawable.ic_tick)
                ivHigh.setImageResource(0)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle_2)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle)
            }

            Task.HIGH -> {
                ivLow.setImageResource(0)
                ivNormal.setImageResource(0)
                ivHigh.setImageResource(R.drawable.ic_tick)
                importanceLow.setBackgroundResource(R.drawable.strok_rectangle)
                importanceNormal.setBackgroundResource(R.drawable.strok_rectangle)
                importanceHigh.setBackgroundResource(R.drawable.strok_rectangle_2)
            }

        }

    }

    override fun addBtnVisibility(visible: Boolean) {
        if (visible) {
            addTask.visibility = View.VISIBLE
        } else addTask.visibility = View.GONE
    }

    override fun editBtnVisibility(visible: Boolean) {
        if (visible) {
            editTask.visibility = View.VISIBLE
        } else editTask.visibility = View.GONE
    }

    override fun showCategories(list: List<String>) {
        spinnerAdapter = ArrayAdapter(this@DetailActivity, android.R.layout.simple_spinner_dropdown_item, list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return true

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.lowView -> {
                importanceVariable = Task.LOW
            }
            R.id.normalView -> {
                importanceVariable = Task.NORMAL
            }
            R.id.highView -> {
                importanceVariable = Task.HIGH
            }
        }

        changeImportanceState(importanceVariable)

    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDetach()

    }


    /**Delete Dialog Event Listeners */
//---------------------------------------------------------------------
    override fun noItemClick() {
        deleteDialog.dismiss()
    }

    override fun yesItemClick() {
        presenter.deleteTask(updateTaskForDialog)
        deleteDialog.dismiss()
        finish()
    }
//---------------------------------------------------------------------


    override fun addCategory(category: Category) {

        if (category != null) {
            val categoryList = RoomDB.getDataBase(this).daoCat().getAllCategories()
            val categoryListString = arrayListOf<String>()
            for (element in categoryList){
                categoryListString.add(element.categoty!!)
            }
            showCategories(categoryListString)
            spinner.setSelection(spinnerAdapter.getPosition(category.categoty))
        }

    }

}
