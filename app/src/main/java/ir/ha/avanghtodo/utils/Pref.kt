package ir.ha.avanghtodo.utils

import android.content.Context
import android.content.SharedPreferences
import ir.ha.avanghtodo.R

class Pref() {

    companion object {

        //FILTER
        const val FILTER_FILE_NAME = "FILTER_FILE_NAME"  //FILE NAME
        const val FILTER_LAST_ITEM_SELECTED_KEY = "FILTER_LAST_ITEM_SELECTED_KEY"


        //SORT
        const val SORT_PREF_FILE_NAME = "SORT_PREF_FILE_NAME" //FILE NAME
        const val SORT_LAST_ITEM_SELECTED_KEY = "SORT_LAST_ITEM_SELECTED_KEY"
        const val IMPORTANT = 1
        const val LOW = 0
        const val NORMAL = -1


        //AUTH
        const val AUTH_FILE_NAME = "AUTH_PREF_FILE" //FILE NAME
        const val AUTH_STATE_KEY = "AUTH_STATE_KEY"
        const val AUTH_SUPPORT_KEY = "AUTH_SUPPORT_KEY"


    }

    private lateinit var context: Context
    private lateinit var filterSharedPref: SharedPreferences
    private lateinit var authSharedPref: SharedPreferences
    private lateinit var sortingSharedPref: SharedPreferences

    constructor(context: Context) : this() {
        this.context = context
        filterSharedPref = context.getSharedPreferences(FILTER_FILE_NAME, Context.MODE_PRIVATE)
        authSharedPref = context.getSharedPreferences(AUTH_FILE_NAME, Context.MODE_PRIVATE)
        sortingSharedPref = context.getSharedPreferences(SORT_PREF_FILE_NAME,Context.MODE_PRIVATE)
    }


    // FOR Filter
    fun setLastVar(value: String) {
        val editor = filterSharedPref.edit()
        editor.putString(FILTER_LAST_ITEM_SELECTED_KEY, value)
        editor.apply()
    }

    fun getLastVar(): String {
        val lastVar = filterSharedPref.getString(
            FILTER_LAST_ITEM_SELECTED_KEY,
            context.resources.getString(R.string.none)
        )
        return lastVar.toString()
    }



    // FOR AUTH
    fun setAuthAllow(auth: Boolean) {
        val editor = authSharedPref.edit()
        editor.putBoolean(AUTH_STATE_KEY, auth)
        editor.apply()
    }

    fun getAuthAllow(): Boolean {
        return authSharedPref.getBoolean(AUTH_STATE_KEY, false)
    }

    fun setAuthSupport(support: Boolean){
        val editor = authSharedPref.edit()
        editor.putBoolean(AUTH_SUPPORT_KEY,support)
        editor.apply()
    }

    fun getAuthSupport(): Boolean {
        return authSharedPref.getBoolean(AUTH_SUPPORT_KEY, true)
    }



    // FOR Sort
    fun setSortItem(sortEnum: Int){
        val editor = sortingSharedPref.edit()
        editor.putInt(SORT_LAST_ITEM_SELECTED_KEY,sortEnum)
        editor.apply()
    }


    fun getSortItem() : Int{
        return sortingSharedPref.getInt(SORT_LAST_ITEM_SELECTED_KEY, NORMAL)
    }

}