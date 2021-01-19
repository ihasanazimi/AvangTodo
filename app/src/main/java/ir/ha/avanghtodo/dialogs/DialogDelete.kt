package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import ir.ha.avanghtodo.R

class DialogDelete : DialogFragment() {


    private lateinit var yesBtn : View
    private lateinit var noBtn : View
    private lateinit var listenr : EventListener


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenr = context as EventListener
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_delete,null,false)
        dialog.setView(view)


        yesBtn = view.findViewById(R.id.yesBtn)
        noBtn = view.findViewById(R.id.noBtn)

        yesBtn.setOnClickListener{listenr.yesItemClick()}
        noBtn.setOnClickListener{listenr.noItemClick()}


        return dialog.create()
    }


    interface EventListener {
        fun noItemClick()
        fun yesItemClick()
    }
}