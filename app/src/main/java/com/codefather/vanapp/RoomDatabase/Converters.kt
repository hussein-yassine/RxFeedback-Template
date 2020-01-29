package com.codefather.vanapp.RoomDatabase.RoomDatabase

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

class Converters {

    @TypeConverter
    fun stringToIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {
        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun intListToString(list: List<Int>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToArrayList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {
        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun arraylistToString(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}