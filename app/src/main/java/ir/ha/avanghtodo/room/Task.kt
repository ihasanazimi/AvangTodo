package ir.ha.avanghtodo.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(

        @PrimaryKey(autoGenerate = true)
        var uid: Long = 0,

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "category")
        var category: String = "",

        @ColumnInfo(name = "VoicePath")
        var voice : String = "",

        @ColumnInfo(name = "completed")
        var completed: Boolean = false,

        @ColumnInfo(name = "importance")
        var importance: Int = NORMAL


) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(uid)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(voice)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeInt(importance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {



        const val HIGH: Int = 3
        const val NORMAL: Int = 2
        const val LOW: Int = 1




        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
