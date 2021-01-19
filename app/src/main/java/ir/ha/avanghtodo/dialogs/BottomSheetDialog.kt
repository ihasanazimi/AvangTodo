package ir.ha.avanghtodo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.ha.avanghtodo.R

class BottomSheetDialog(var eventDeleteListener : EventListener) : BottomSheetDialogFragment() {


    private lateinit var yesBtn : View
    private lateinit var noBtn : View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet,container,false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        yesBtn = view.findViewById(R.id.yesBtn)
        noBtn = view.findViewById(R.id.noBtn)

        yesBtn.setOnClickListener{
            dismiss()
            eventDeleteListener.deleteListener()
        }
        noBtn.setOnClickListener{dismiss()}

    }




    interface EventListener {
        fun deleteListener()
    }
}