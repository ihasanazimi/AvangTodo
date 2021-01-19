package ir.ha.avanghtodo.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoTaskCategory {



    @Insert
    fun insert(category: Category) : Long

    @Delete
    fun delete(category: Category) : Int

    @Query("Select * from Category")
    fun getAllCategories() : List<Category>

    @Query("Delete from Category")
    fun deleteAllCats()

}