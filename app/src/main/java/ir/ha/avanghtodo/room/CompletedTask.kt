package ir.ha.avanghtodo.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompletedTask(

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
        var importance: Int? = 2

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readByte() != 0.toByte(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(uid)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(voice)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeValue(importance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompletedTask> {
        override fun createFromParcel(parcel: Parcel): CompletedTask {
            return CompletedTask(parcel)
        }

        override fun newArray(size: Int): Array<CompletedTask?> {
            return arrayOfNulls(size)
        }
    }
}


