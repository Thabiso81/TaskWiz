package com.thabiso81.taskwiz.globalMethods

import java.time.LocalDate
import java.util.Locale

class GlobalMethods {
    fun getDate(epochDate: Long): String{
        var outputDate = "Due on ${LocalDate.ofEpochDay(epochDate).dayOfMonth} ${LocalDate.ofEpochDay(epochDate).month}"

        var dueDateDayOfWeek = LocalDate.ofEpochDay(epochDate).dayOfWeek.toString()

        val dueDate = LocalDate.ofEpochDay(epochDate).dayOfMonth

        val todaysDate = LocalDate.now().dayOfMonth

        if (dueDate.minus(todaysDate) >= 0 && dueDate.minus(todaysDate) < 1 ) {
            outputDate = "Due Today"
        }

        if((dueDate.minus(todaysDate)) == 1 ) {
            outputDate = "Due Tomorrow"
        }

        if (dueDate.minus(todaysDate) > 1 && dueDate.minus(todaysDate) < 8 ){
            outputDate = "Due ${dueDateDayOfWeek.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}"
        }

        return outputDate
    }
}