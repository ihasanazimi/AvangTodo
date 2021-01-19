package ir.ha.avanghtodo.detail

import ir.ha.avanghtodo.room.Task
import ir.ha.avanghtodo.utils.BasePresenter
import ir.ha.avanghtodo.utils.BaseView

interface DetailsC {



    interface View : BaseView {

        fun showTask(task: Task)

        fun setDeleteBtnVisibility(visible: Boolean)

        fun showError(message: String)

        fun returnResult(resultCode: Int, task: Task, extraName: String)

        fun changeImportanceState(importance: Int)

        fun addBtnVisibility(visible: Boolean)

        fun editBtnVisibility(visible: Boolean)

        fun showCategories(list: List<String>)

    }


    interface Presenter : BasePresenter<View> {

        fun deleteTask(task: Task)

        fun addTask(title: String, importance: Int , category: String)

        fun editTask(task: Task, title: String, importance: Int,category: String)

        fun selectImportance(importance: Int)

        fun removeCategory(category: String)


    }

}