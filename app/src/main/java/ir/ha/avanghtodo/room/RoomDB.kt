package ir.ha.avanghtodo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class,CompletedTask::class , Category::class],version = 4,exportSchema = false)
abstract class RoomDB : RoomDatabase() {

    abstract fun dao(): DaoNormalTask
    abstract fun daoCompleted(): DaoCompletedTask
    abstract fun daoCat(): DaoTaskCategory

    companion object {

        @Volatile
        var database: RoomDB? = null

        fun getDataBase(context: Context): RoomDB {
            val tempInstance = database
            if (database != null) { return tempInstance as RoomDB
            }

            /**synchronized
             * این کلمه کلیدی باعث میشه وقتی 2 تا عملیات بصورت همزمان میخوان به نخ اصلی دست پیدا کنند.اجبار میکند که حتما یکی از انها دسترسی پیدا کن
             *  */

            synchronized(this) {

                val instance = Room.databaseBuilder(context.applicationContext, RoomDB::class.java, "database")
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build()

                database = instance
                return instance
            }
        }
    }

}