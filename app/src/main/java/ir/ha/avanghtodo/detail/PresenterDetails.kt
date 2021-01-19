package ir.ha.avanghtodo.detail

import ir.ha.avanghtodo.main.MainActivity
import ir.ha.avanghtodo.room.DaoNormalTask
import ir.ha.avanghtodo.room.DaoTaskCategory
import ir.ha.avanghtodo.room.Task

class PresenterDetails() : DetailsC.Presenter {

    var view : DetailsC.View ? = null
    private lateinit var daoNormalTask: DaoNormalTask
    private lateinit var daoCategory : DaoTaskCategory


    constructor(daoNormalTask: DaoNormalTask , daoTaskCategory: DaoTaskCategory) : this() {
        this.daoNormalTask = daoNormalTask
        this.daoCategory = daoTaskCategory
    }


    override fun deleteTask(task: Task) {

        daoNormalTask.delete(task)
        view?.returnResult(MainActivity.RESULT_CODE_DELETE_TASK, task, MainActivity.DELETE_TASK_KEY)

    }

    override fun addTask(title: String, importance: Int , category: String) {

        if (title.isNotEmpty()) {

            val task = Task()
            task.title = title
            task.importance = importance
            task.completed = false
            task.category = category

            val id = daoNormalTask.insert(task)
            task.uid = id

            view?.returnResult(MainActivity.RESULT_CODE_ADD_TASK, task, MainActivity.ADD_TASK_KEY)

        } else view?.showError("Please enter your desired task...")

    }

    override fun editTask(task: Task, title: String, importance: Int , category: String) {

        if (title.isNotEmpty()) {

            val t = Task()
            t.uid = task.uid
            t.title = title
            t.importance = importance
            t.completed = task.completed
            t.category = category
            t.voice = task.voice

            daoNormalTask.update(t)
            view?.editBtnVisibility(true)

            view?.returnResult(MainActivity.RESULT_CODE_UPDATE_TASK, t, MainActivity.UPDATE_TASK_KEY)

        } else view?.showError("Please enter your desired task...")

    }


    override fun selectImportance(importance: Int) {
        view?.changeImportanceState(importance)
    }

    override fun removeCategory(category: String) {

        val list = daoCategory.getAllCategories()
        val stringCats = arrayListOf<String>()

        for (i in 0..list.size-1){

            if (category == list[i].categoty){
                //Delete Cat
                daoCategory.delete(list[i])

                val newList = daoCategory.getAllCategories()
                for (i in newList){
                    stringCats.add(i.categoty)
                }

                view?.showCategories(stringCats)

                break
            }
        }

    }

    override fun onAttach(view: DetailsC.View) {
        this.view = view

        val cats = daoCategory.getAllCategories()
        val catsString = arrayListOf<String>()
        if (cats.isNotEmpty()) {
            for (i in cats) {
                catsString.add(i.categoty!!)
            }
            view.showCategories(catsString)
        }

    }

    override fun onDetach() {
        view = null
    }

}