package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import ir.ha.avanghtodo.R

class HelpDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_help,null,false)
        builder.setView(view)

        val okBtn = view.findViewById<MaterialButton>(R.id.ok)
        okBtn.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }
}