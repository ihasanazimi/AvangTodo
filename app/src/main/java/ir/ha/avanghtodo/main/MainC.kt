package ir.ha.avanghtodo.main

import ir.ha.avanghtodo.room.CompletedTask
import ir.ha.avanghtodo.room.Task
import ir.ha.avanghtodo.utils.BasePresenter
import ir.ha.avanghtodo.utils.BaseView

interface MainC {


    interface View : BaseView {

        fun showAllTasks(list: List<Task>)

        fun showAllCompletedTasks(list: List<CompletedTask>)

        fun updateTask(task: Task)

        fun clearList()

        fun deleteAll()

        fun deleteAllCompletedList()

        fun setEmptyStateVisibility(visible: Boolean)

        fun updateItemCompleteAlpha(task : Task , position : Int)

        fun setEnableDeleteBtn(enable: Boolean)

        fun showCompletedListViewGroup(visible: Boolean)

        fun closeListener(close: Boolean)

        fun alarmAndSortViewGroup(visible: Boolean)

    }


    interface Presenter : BasePresenter<View> {

        fun onDeleteAllBtnClick()

        fun onDeleteAllCompletedListBtnClick()

        fun onSearch(q: String): ArrayList<Task>

    }

}