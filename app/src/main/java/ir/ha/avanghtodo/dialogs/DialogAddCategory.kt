package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.Category
import ir.ha.avanghtodo.room.RoomDB

class DialogAddCategory(var dialogAddCatEventListener: DialogAddCategoryEventListeners) :
    DialogFragment() {


    lateinit var db: RoomDB

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = RoomDB.getDataBase(context)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null, false)
        val btnAdd = view.findViewById(R.id.dialogCategory_Add) as View
        val input = view.findViewById(R.id.dialogCategory_input) as TextInputEditText

        btnAdd.setOnClickListener {

            val allCats = db.daoCat().getAllCategories()

            if (input.text.toString().trim().isNotEmpty()) {

                val inputText = input.text.toString().trim()

                for (i in 0..allCats.size -1){
                    if (inputText == allCats[i].categoty){
                        input.error = resources.getString(R.string.duplicatin_error)
                        return@setOnClickListener
                    }
                }

                val cat = Category()
                cat.categoty = inputText
                val id = db.daoCat().insert(cat)
                cat.uid = id

                dialogAddCatEventListener.addCategory(cat)
                dismiss()

            } else {
                input.error = resources.getString(R.string.empty_error_for_add_category_input)
            }
        }


        dialog.setView(view)

        return dialog.create()
    }


    interface DialogAddCategoryEventListeners {

        fun addCategory(category : Category)

    }
}