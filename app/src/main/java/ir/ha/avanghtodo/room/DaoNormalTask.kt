package ir.ha.avanghtodo.room

import androidx.room.*

@Dao
interface DaoNormalTask {

    @Insert
    fun insert(task : Task) : Long


    @Delete
    fun delete (task : Task) : Int


    @Update
    fun update (task : Task) : Int


    @Query("Select * from Task")
    fun getAllTask() : List<Task>


    @Query("delete from Task")
    fun deleteAll()


    @Query("select * from task where title like '%' || :q || '%'")
    fun search(q : String) : List<Task>

    @Query("select * from task where importance =:importance;")
    fun sortByImportance(importance : Int) : List<Task>

    @Query("select * from task where category =:category;")
    fun filterByCategory(category : String) : List<Task>

}