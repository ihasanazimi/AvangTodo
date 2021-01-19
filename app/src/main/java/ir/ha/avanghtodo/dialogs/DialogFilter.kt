package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.RoomDB
import ir.ha.avanghtodo.room.Task
import ir.ha.avanghtodo.utils.Pref

class DialogFilter(var dialogFilterEvent: DialogFilterEventListener) : DialogFragment() {


    lateinit var db: RoomDB
    lateinit var pref : Pref
    var list = arrayListOf<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = RoomDB.getDataBase(context)
        val myCats = db.daoCat().getAllCategories()
        for (i in myCats) {
            list.add(i.categoty)
        }
        pref = Pref(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_filtr, null, false)
        val btnDone = view.findViewById(R.id.dialogFilter_Done) as View
        val spinner = view.findViewById(R.id.spinner) as AppCompatSpinner
        val clearFilterCheckBox = view.findViewById(R.id.checkBox) as AppCompatCheckBox

        val spinnerAdapter = ArrayAdapter<String>(btnDone.context, android.R.layout.simple_spinner_dropdown_item, list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(spinnerAdapter.getPosition(pref.getLastVar()))


        //Listener
        btnDone.setOnClickListener {

            if (!clearFilterCheckBox.isChecked) {
                val filteredTasks = db.dao().filterByCategory(spinner.selectedItem.toString())
                pref.setLastVar(spinner.selectedItem.toString())
                dialogFilterEvent.filteredList(filteredTasks)

                dialogFilterEvent.badgeViewUpdateFilter(true)

                dismiss()
            }else {
                val filteredTasks = db.dao().getAllTask()
                pref.setLastVar(btnDone.context.resources.getString(R.string.none))
                dialogFilterEvent.filteredList(filteredTasks)

                dialogFilterEvent.badgeViewUpdateFilter(false)

                dismiss()
            }
        }

        clearFilterCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            spinner.isEnabled = !isChecked
        }



        dialog.setView(view)
        return dialog.create()
    }


    interface DialogFilterEventListener {
        fun filteredList(list: List<Task>)
        fun badgeViewUpdateFilter(used : Boolean)
    }
}