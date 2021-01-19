package ir.ha.avanghtodo.utils

interface BasePresenter<T : BaseView> {

    fun onAttach(view : T)

    fun onDetach()
}