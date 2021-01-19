package ir.ha.avanghtodo.room

import androidx.room.*

@Dao
interface DaoCompletedTask {

    @Insert
    fun insert(task : CompletedTask) : Long


    @Delete
    fun delete (task : CompletedTask) : Int


    @Update
    fun update (task : CompletedTask) : Int


    @Query("Select * from CompletedTask")
    fun getAllTask() : List<CompletedTask>


    @Query("delete from CompletedTask")
    fun deleteAll()


    @Query("select * from CompletedTask where title like '%' || :q || '%'")
    fun search(q : String) : List<CompletedTask>

    @Query("select * from CompletedTask where category =:category;")
    fun filterByCategory(category : String) : List<Task>


}