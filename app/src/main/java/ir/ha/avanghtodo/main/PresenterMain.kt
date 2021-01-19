package ir.ha.avanghtodo.main

import ir.ha.avanghtodo.room.DaoCompletedTask
import ir.ha.avanghtodo.room.DaoNormalTask
import ir.ha.avanghtodo.room.Task

class PresenterMain() : MainC.Presenter {


    lateinit var daoNormalTask: DaoNormalTask
    lateinit var daoCompletedTask: DaoCompletedTask
    var view: MainC.View? = null


    constructor(daoNormalTask: DaoNormalTask, daoCompletedTask: DaoCompletedTask) : this() {
        this.daoNormalTask = daoNormalTask
        this.daoCompletedTask = daoCompletedTask
    }

    //-----------------------------------------------
    override fun onDeleteAllBtnClick() {

        daoNormalTask.deleteAll()
        view?.deleteAll()
        view?.setEnableDeleteBtn(false)

        val mainList = daoNormalTask.getAllTask()
        val completeList = daoCompletedTask.getAllTask()

        if (mainList.isEmpty() && completeList.isEmpty()) {
            view?.setEmptyStateVisibility(true)
        } else {
            view?.setEmptyStateVisibility(false)
            view?.alarmAndSortViewGroup(false)
        }

    }

    override fun onDeleteAllCompletedListBtnClick() {
        view?.deleteAllCompletedList()
        view?.showCompletedListViewGroup(false)
        daoCompletedTask.deleteAll()


        val mainList = daoNormalTask.getAllTask()
        val completeList = daoCompletedTask.getAllTask()

        if (mainList.isEmpty() && completeList.isEmpty()) {
            view?.setEmptyStateVisibility(true)
        } else {
            view?.setEmptyStateVisibility(false)
        }
    }


    override fun onSearch(q: String): ArrayList<Task> {

        if (q.isEmpty()) {
            view?.showCompletedListViewGroup(true)
        }else {
            view?.showCompletedListViewGroup(false)
        }

        val dbList = daoNormalTask.search(q).reversed()

        val searchList = arrayListOf<Task>()
        val empty = arrayListOf<Task>()

        for (i in dbList) {
            searchList.add(i)
        }


        val mainList = daoNormalTask.getAllTask().reversed()
        val completeList = daoCompletedTask.getAllTask().reversed()

        if (mainList.isEmpty() && completeList.isEmpty()) {
            view?.setEmptyStateVisibility(true)
        } else if (mainList.isNotEmpty()){view?.showCompletedListViewGroup(false)}


        return if (searchList.isNotEmpty()) {
            view?.setEmptyStateVisibility(false)
            view?.showAllTasks(searchList)
            searchList
        } else {
            view?.setEmptyStateVisibility(true)
            view?.showAllTasks(empty)
            view?.clearList()
            view?.showCompletedListViewGroup(false)
            empty
        }
    }


    override fun onAttach(view: MainC.View) {
        this.view = view

        val mainList = daoNormalTask.getAllTask().reversed()
        val completeList = daoCompletedTask.getAllTask().reversed()



        if (mainList.isEmpty() && completeList.isEmpty()) {
            view.setEmptyStateVisibility(true)
            view.alarmAndSortViewGroup(false)
        } else {
            view.setEmptyStateVisibility(false)
        }




        if (mainList.isNotEmpty()) {
            view.showAllTasks(mainList)
            view.setEnableDeleteBtn(true)
        } else {
            view.setEnableDeleteBtn(false)
            view.alarmAndSortViewGroup(false)
        }




        if (completeList.isNotEmpty()) {
            view.showAllCompletedTasks(completeList)
            view.showCompletedListViewGroup(true)
        } else {
            view.showCompletedListViewGroup(false)
        }



    }


    override fun onDetach() {
        view = null
    }

}