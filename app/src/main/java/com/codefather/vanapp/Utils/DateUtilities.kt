package com.codefather.vanapp.Utils

import android.content.Context
import android.util.Log
import com.codefather.vanapp.R
import org.joda.time.*
import java.text.SimpleDateFormat
import org.joda.time.format.DateTimeFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * Created by Hussein Yassine on 05, March, 2019.
 *
 */

object DateUtilities {

    fun today(): Date = getDateFromDateTime(Date(/*117, 11, 12*/))

    fun getCurrentYear():String{
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR).toString()
    }

    fun getWeekStart():Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return calendar.time
    }

    fun getWeekEnd():Date {
        val calendar = getCalendarForDate(getWeekStart())
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        return calendar.time
    }

    fun getCalendarForDate(date: Date = today()): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    fun getCalendar(date: Date = today()): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    fun calendarByAdding(field: Int, amount: Int, calendar: Calendar = getCalendar(today())): Calendar {
        calendar.add(field, amount)
        return calendar
    }

    fun calendarBySetting(field: Int, amount: Int, calendar: Calendar = getCalendar(today())): Calendar {
        calendar.set(field, amount)
        return calendar
    }

    fun dateByAdding(field: Int, amount: Int, date: Date = today()): Date {
        return calendarByAdding(field, amount, getCalendar(date)).time
    }

    fun dateByAddingDays(amount: Int, date: Date = today()): Date {
        return dateByAdding(Calendar.DATE, amount, date)
    }

    fun dateByAddingMonths(amount: Int, date: Date = today()): Date {
        return dateByAdding(Calendar.MONTH, amount, date)
    }

    fun dateByAddingYears(amount: Int, date: Date = today()): Date {
        return dateByAdding(Calendar.YEAR, amount, date)
    }

    fun getDateFromDateTime(date: Date): Date {
        return getCalendar(date).time
    }

    fun getCurrentDateFromTimeString(inputTime: String?) : Long{
        if (inputTime != null){
            val date = LocalDate.now().toLocalDateTime(LocalTime.parse(inputTime))
            return date.toDate().time
        }else{
            val date = LocalDate.now()
            return date.toDate().time
        }
    }

    fun geSimpleDateFormatForDisplay(context: Context, date: Date): String {
        val diff = getDateFromDateTime(date).time - today().time
        val diffDays = (diff / (24 * 60 * 60 * 1000)).toInt()
        return when (diffDays) {
            0 -> context.resources.getString(R.string.today)
            1 -> context.resources.getString(R.string.tomorrow)
            else -> getMonthDayFormatForDisplay(date)
        }
    }

    fun getHoursFromString(inputDate: String?): Int{
        val dateToBeParsed = inputDate ?: getDateWithFormat(today(), Constants.DateFormats.HOUR_DAY)
        val sfd = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_PRIMARY, Locale.ENGLISH)
        val date = sfd.parse(dateToBeParsed)
        val output = SimpleDateFormat("HH", Locale.ENGLISH).format(date).toInt()
        Log.wtf("Hours output", "is " + output)
        return output
    }

    fun getMinsFromString(inputDate: String?): Int{
        val dateToBeParsed = inputDate ?: getDateWithFormat(today(), Constants.DateFormats.MINUTES_DAY)
        val sfd = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_PRIMARY, Locale.ENGLISH)
        val date = sfd.parse(dateToBeParsed)
        val output = SimpleDateFormat("mm", Locale.ENGLISH).format(date).toInt()
        Log.wtf("Mins output", "is " + output)
        return output
    }

    fun getDateFromString(dateString: String?, pattern:String): Date {
        return if (dateString == null)
             today()
        else{

            val dtf = DateTimeFormat.forPattern(pattern)
            val dateTime = dtf.parseDateTime(dateString)
            dateTime.toDate()
        }
    }

    fun getMonthDayFormatForDisplay(date: Date): String =
        SimpleDateFormat(Constants.DateFormats.MONTH_DAY_FORMAT_DISPLAY, Locale.getDefault()).format(date)

    fun getMonthDayYearFormatForDisplay(date: Date): String =
        SimpleDateFormat(Constants.DateFormats.MONTH_DAY_YEAR_FORMAT_DISPLAY, Locale.getDefault()).format(date)

    fun getDateTimeFormatForDisplay(date: Date): String =
        SimpleDateFormat(Constants.DateFormats.DATE_TIME_FORMAT_DISPLAY, Locale.getDefault()).format(date)

    fun getDateWithFormat(date: Date?, format: String): String {
        return if(date == null)
            ""
        else
            SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    fun checkInRange(date: Date, first: Date, last: Date): Boolean {
        return date >= first && date <= last
    }

    fun getTimeNowInMillis(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //return 20 * 60 * 60 * 1000
        val today = calendar.timeInMillis
        return now - today
    }

    fun getDateFromTimeInMillisOnly(millis: Long, matchToDate: Date?): Date {
        val startDate = Date(millis)
        val startDateCalendar = Calendar.getInstance()
        startDateCalendar.time = startDate
        startDateCalendar.timeZone = TimeZone.getTimeZone("UTC")
        if (matchToDate != null) {
            val cal = getCalendar(matchToDate)
            startDateCalendar.set(Calendar.YEAR, cal.get(Calendar.YEAR))
            startDateCalendar.set(Calendar.MONTH, cal.get(Calendar.MONTH))
            startDateCalendar.set(Calendar.DATE, cal.get(Calendar.DATE))
        }
        return startDateCalendar.time
    }

    fun getFormattedTime(date: Date): String {
        val sdf = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_DISPLAY, Locale.getDefault())
        return sdf.format(date)
    }

    fun getFormattedDateTime(millis: Long): String {
        val sdf = SimpleDateFormat(Constants.DateFormats.DATE_TIME_FORMAT_SECONDARY, Locale.ENGLISH)
        return sdf.format(Date(millis))
    }

    fun getFormattedTime(millis: Long): String {
        val sdf = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_DISPLAY, Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun getFormattedTimeUTC(millis: Long): String {
        val sdf = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT_DISPLAY, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(millis))
    }

    fun compareDatesByYear(firstDate: Date, secondDate: Date): Boolean {

        val firstCalendar = Calendar.getInstance()
        val secondCalendar = Calendar.getInstance()

        firstCalendar.time = firstDate
        secondCalendar.time = secondDate

        val yearsDiff = firstCalendar.get(Calendar.YEAR) - secondCalendar.get(Calendar.YEAR)

        return yearsDiff in 0..1
    }

    fun compareDatesByMonthYear(firstDate: Date, secondDate: Date): Boolean {

        val firstCalendar = Calendar.getInstance()
        val secondCalendar = Calendar.getInstance()

        firstCalendar.time = firstDate
        secondCalendar.time = secondDate

        val yearsDiff = firstCalendar.get(Calendar.YEAR) - secondCalendar.get(Calendar.YEAR)
        val monthsDiff = firstCalendar.get(Calendar.MONTH) - secondCalendar.get(Calendar.MONTH)

        return yearsDiff == 0 && monthsDiff in 0..1
    }

    fun compareDatesByMonthYearDay(firstDate: Date, secondDate: Date, weekCompare: Boolean): Boolean {

        val firstCalendar = Calendar.getInstance()
        val secondCalendar = Calendar.getInstance()

        firstCalendar.time = firstDate
        secondCalendar.time = secondDate

        val yearsDiff = firstCalendar.get(Calendar.YEAR) - secondCalendar.get(Calendar.YEAR)
        val monthsDiff = firstCalendar.get(Calendar.MONTH) - secondCalendar.get(Calendar.MONTH)
        val daysDiff = firstCalendar.get(Calendar.DAY_OF_MONTH) - secondCalendar.get(Calendar.DAY_OF_MONTH)

        val comparisonResult = if (weekCompare){
            yearsDiff == 0 && monthsDiff == 0 && daysDiff < 7
        }else{
            yearsDiff == 0 && monthsDiff == 0 && daysDiff == 0
        }
        return comparisonResult
    }

    fun getTimeSpanMonths(first: Date, second: Date): Int {
        val firstLocalDate = LocalDate.fromDateFields(first)
        val secondLocalDate = LocalDate.fromDateFields(second)
        val period = Period(firstLocalDate, secondLocalDate, PeriodType.days())
        return period.months
    }

    fun getTimeSpanDays(first: Date, second: Date): Int {
        val diffInMillisec = second.getTime() - first.getTime()
        return TimeUnit.MILLISECONDS.toDays(diffInMillisec).toInt()
    }

    fun getTimeSpanHours(first: Date, second: Date): Int {
        val firstLocalDate = LocalDate.fromDateFields(first)
        val secondLocalDate = LocalDate.fromDateFields(second)
        val period = Period(firstLocalDate, secondLocalDate, PeriodType.hours())
        return period.hours
    }

}