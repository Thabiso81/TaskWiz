package com.thabiso81.taskwiz.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate

@TypeConverters
class TaskTypeConverter {
    @TypeConverter
    fun fromAnytoString(attribute: Any?) : String{
        if(attribute == null){
            return ""
        }
            return attribute as String
    }

    @TypeConverter
    fun fromStringToAny(attribute: String?): Any{

        if (attribute == null){
            return ""
        }
        return attribute
    }

    @TypeConverter
    fun fromLocalDatetoLong(attribute: LocalDate?) : Long{
        if(attribute == null){
            return 0
        }
        return attribute.toEpochDay()
    }

    @TypeConverter
    fun fromLongtoLocalDate(attribute: Long?) : LocalDate{
        if(attribute == null){
            return LocalDate.now()
        }
        return LocalDate.ofEpochDay(attribute)
    }
}