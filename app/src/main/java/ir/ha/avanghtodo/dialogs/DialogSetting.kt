package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.room.Category
import ir.ha.avanghtodo.room.RoomDB
import ir.ha.avanghtodo.utils.Pref

class DialogSetting : DialogFragment() {

    lateinit var pref: Pref
    lateinit var db: RoomDB
    lateinit var myContext: Context
    lateinit var tvMsgAuthSummary: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        db = RoomDB.getDataBase(context)
        pref = Pref(context)
        this.myContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_setting, null, false)
        val switchAuth = view.findViewById<SwitchMaterial>(R.id.authCheckBox)
        val deleteAllCats = view.findViewById<View>(R.id.deleteCatsIv)
        tvMsgAuthSummary = view.findViewById(R.id.tv_summary)

        switchAuth.isChecked = pref.getAuthAllow()

        deleteAllCats.setOnClickListener { v ->

            val snackBar = Snackbar.make(
                view.findViewById(R.id.rootSetting),
                myContext.resources.getString(R.string.sure),
                Snackbar.LENGTH_LONG
            )

            snackBar.setAction(myContext.getString(R.string.yes_cats)) {
                snackBar.dismiss()
                db.daoCat().deleteAllCats()

                // For when Categories List Is Empty  -  inja ye dune Item ezade mikonim (none)
                createDefaultCat()


                Snackbar.make(
                    view.findViewById(R.id.rootSetting),
                    myContext.resources.getString(R.string.done),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            snackBar.show()
        }

        //Enable Row Auth
        if (pref.getAuthSupport()) {
            switchAuth.isEnabled = true
            tvMsgAuthSummary.text = resources.getString(R.string.setting_auth_desssssssssssssss)
            tvMsgAuthSummary.setTextColor(resources.getColor(R.color.grayDarker4))
        } else {
            switchAuth.isEnabled = false
            switchAuth.isChecked = false
            tvMsgAuthSummary.text = resources.getString(R.string.msgSup)
            tvMsgAuthSummary.setTextColor(resources.getColor(R.color.red))
        }


        if (Build.VERSION.SDK_INT < 23){
            switchAuth.isEnabled = false
            switchAuth.isChecked = false
            tvMsgAuthSummary.text = resources.getString(R.string.android_version_error)
            tvMsgAuthSummary.setTextColor(resources.getColor(R.color.red))
        }


        switchAuth.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.isChecked = isChecked
            pref.setAuthAllow(isChecked)
        }

        dialog.setView(view)
        return dialog.create()
    }

    private fun createDefaultCat() {
        if (RoomDB.getDataBase(myContext).daoCat().getAllCategories().isEmpty()) {
            val cat = Category()
            cat.categoty = resources.getString(R.string.none)
            val id = RoomDB.getDataBase(myContext).daoCat().insert(cat)
            cat.uid = id
        }
    }


}