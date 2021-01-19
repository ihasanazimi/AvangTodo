package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.utils.IntentActions
import ir.ha.avanghtodo.utils.UtilClass

class DialogAboutUs : DialogFragment() {

    lateinit var intentActions: IntentActions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        intentActions = IntentActions(context)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_about_us,null,false)
        val btnEmail = view.findViewById<TextView>(R.id.emailBtn)
        val btnLinkdin = view.findViewById<TextView>(R.id.linkdinBtn)
        val btnInstagram = view.findViewById<TextView>(R.id.instagramBtn)
        val closeBtn = view.findViewById<View>(R.id.close)

        //Set UnderLines
        UtilClass.setUnderLineForTextView(btnEmail,btnEmail.context.resources.getString(R.string.email_id))
        UtilClass.setUnderLineForTextView(btnLinkdin,btnLinkdin.context.resources.getString(R.string.linkdin_id))
        UtilClass.setUnderLineForTextView(btnInstagram,btnInstagram.context.resources.getString(R.string.instagram_id))


        btnEmail.setOnClickListener {
            intentActions.sendEmail(it.context.resources.getString(R.string.email_id))
        }


        btnLinkdin.setOnClickListener {
            intentActions.openLinkedInPage(it.context.resources.getString(R.string.linkdin_id))
        }


        btnInstagram.setOnClickListener {
            intentActions.openInstagram(it.context.resources.getString(R.string.instagram_id))
        }

        closeBtn.setOnClickListener { dismiss() }


        dialog.setView(view)
        return dialog.create()
    }


}