package ir.ha.avanghtodo.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import ir.ha.avanghtodo.R
import ir.ha.avanghtodo.utils.Pref

class DialogSort(var event : SortEventListeners) : DialogFragment() {

    lateinit var pref : Pref


    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = Pref(context)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_sort_list_by_importance_level,null,false)
        dialog.setView(view)

        val ASC = view.findViewById<View>(R.id.ASC)
        val DESC = view.findViewById<View>(R.id.DESC)
        val noSort = view.findViewById<View>(R.id.no_sort)
        val high = view.findViewById<ImageView>(R.id.high)
        val low = view.findViewById<ImageView>(R.id.low)

        when(pref.getSortItem()){
            0->{low.setImageResource(R.drawable.ic_tick)}
            1->{high.setImageResource(R.drawable.ic_tick)}
            else->{
                low.setImageResource(0)
                high.setImageResource(0)
            }
        }

        ASC.setOnClickListener { dismiss();event.ASC() ; event.badgeViewUpdateSort(true) ; pref.setSortItem(Pref.LOW) }
        DESC.setOnClickListener { dismiss();event.DESC() ; event.badgeViewUpdateSort(true) ; pref.setSortItem(Pref.IMPORTANT) }
        noSort.setOnClickListener { dismiss() ; event.normalList(); event.badgeViewUpdateSort(false) ; pref.setSortItem(Pref.NORMAL) }

        return dialog.create()
    }


    interface SortEventListeners{
        fun ASC()
        fun DESC()
        fun normalList()
        fun badgeViewUpdateSort(used : Boolean)
    }
}