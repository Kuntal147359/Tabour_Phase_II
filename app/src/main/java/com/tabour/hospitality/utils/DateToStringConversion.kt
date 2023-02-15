package com.tabour.hospitality.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.tabour.hospitality.models.RVDates
import com.tabour.hospitality.models.RVTimes
import com.tabour.hospitality.models.ResvtnChsDt
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.datatype.DatatypeConstants.DAYS


class DateToStringConversion(dt: String) {

    var dt: String

    init {
        this.dt = dt
    }

    companion object {
        var DATE_FORMAT_1: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"

        fun converteddate(dt: String): String {

            val editeddate = dt

            val dashposition = editeddate.indexOf("-")
            val stryear = editeddate.substring(0, dashposition)
            var strmonth = editeddate.substring(dashposition + 1, 7)

            when (strmonth) {
                "01" -> strmonth = "Jan"
                "02" -> strmonth = "Feb"
                "03" -> strmonth = "Mar"
                "04" -> strmonth = "Apr"
                "05" -> strmonth = "May"
                "06" -> strmonth = "June"
                "07" -> strmonth = "July"
                "08" -> strmonth = "Aug"
                "09" -> strmonth = "Sep"
                "10" -> strmonth = "Oct"
                "11" -> strmonth = "Nov"
                "12" -> strmonth = "Dec"
            }

            val secondhiphn = editeddate.lastIndexOf("-")
            val length = editeddate.length
            val strday = editeddate.substring(secondhiphn + 1, length)

            //        String properdt = finalDay;

            //        String properdt = finalDay;
            return "$strday $strmonth $stryear"
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrDate(): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            return sdf.format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrTime(): String {
            val currentTime = Calendar.getInstance().time

//            val formatTime: DateFormat = SimpleDateFormat("K:mm a")
            val formatTime: DateFormat = SimpleDateFormat("hh:mm a")
            formatTime.timeZone = TimeZone.getDefault()
            val finaltime = formatTime.format(currentTime)

            return finaltime.toString()
        }

        fun getCurrDay(): String {
            val cal = Calendar.getInstance()
            val time = Date()
            val day = time.day

            var txt_day = ""
            when (day) {
                0 -> {
                    txt_day = "Sunday"
                }
                1 -> {
                    txt_day = "Monday"
                }
                2 -> {
                    txt_day = "Tuesday"
                }
                3 -> {
                    txt_day = "Wednesday"
                }
                4 -> {
                    txt_day = "Thursday"
                }
                5 -> {
                    txt_day = "Friday"
                }
                6 -> {
                    txt_day = "Saturday"
                }
            }

            return txt_day

        }

        fun getCurrTimeinhhmmss(): String {
            val currentTime = Calendar.getInstance().time
            val formatTime: DateFormat = SimpleDateFormat("hh:mm:ss")
            formatTime.timeZone = TimeZone.getDefault()
            val finaltime = formatTime.format(currentTime)

            return finaltime.toString()
        }

        fun getTimeinAmPM(time: String): String {
//            val time1 = SimpleDateFormat("hh:mm a").parse(time)
            val sdf = SimpleDateFormat("H:mm")
            val dateObj = sdf.parse(time)
            System.out.println(dateObj)
            println(SimpleDateFormat("K:mm").format(dateObj))

            return SimpleDateFormat("hh:mm a").format(dateObj)
        }

        fun compareTwoTimes(currTime: String, slottime: String): Boolean {

//            val string1 = "20:11:13"
            val time1 = SimpleDateFormat("hh:mm a").parse(currTime)

//            val string2 = "14:49:00"
            val time2 = SimpleDateFormat("hh:mm a").parse(slottime)

//          time1!!.after(time2)

            when {
                time1!!.before(time2) -> {
                    return true
                }
                time1.after(time2) -> {
                    return false
                }
            }

            return false
        }

        fun getCurrentMonth(): Pair<String, String> {
//            val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM")
            val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
            val currMonth = YearMonth.now()
            val currYear = YearMonth.now()
            return currMonth.format(monthFormatter) to currYear.format(yearFormatter)
        }

        fun getNextMonths(nextcount: Int): ArrayList<String>{
            val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            val currMonth = YearMonth.now()
            val currYear = YearMonth.now()

            val monthArr = ArrayList<String>()
            for(i in 1..nextcount)
            {
                val month = currMonth.plusMonths(i.toLong())
                monthArr.add(month.format(monthYearFormatter))
            }
            monthArr.add(0,currMonth.format(monthYearFormatter))

            return monthArr
        }

        fun getYearMonthFromDate(dt: String): Pair<String, String>
        {
            // 2023-01-10
            val combineDate = dt.split("-")
            val year = combineDate.get(0)
            var month = ""
            when (combineDate.get(1)) {
                "01" -> month = "January"
                "02" -> month = "February"
                "03" -> month = "March"
                "04" -> month = "April"
                "05" -> month = "May"
                "06" -> month = "June"
                "07" -> month = "July"
                "08" -> month = "August"
                "09" -> month = "September"
                "10" -> month = "October"
                "11" -> month = "November"
                "12" -> month = "December"
            }

            return month to year

        }


        fun getAllDaysInMonth(yearMonth: String): ArrayList<ResvtnChsDt>
        {
            val yrmnth = yearMonth.split(" ")
            val year = yrmnth.get(1)
            val month = getMonthInDigits(yrmnth.get(0))
            // Get the number of days in that month
            val sdf = SimpleDateFormat("EEE,dd")
            val sdf_param = SimpleDateFormat("YYY-MM-dd")
            val sdf_month = SimpleDateFormat("MMM")

            val cal = Calendar.getInstance()
            cal.clear()
            cal.set(year.toInt(), month, 1)
//            cal[year.toInt(), month - 1] = 1
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val daysArr = ArrayList<ResvtnChsDt>()
            for (i in 0 until daysInMonth) {
                Log.d("AllDaysInMonth","${daysInMonth}: ${sdf.format(cal.time)}")
                val dt_split = sdf.format(cal.time).split(",")
                val month = sdf_month.format(cal.time)

                daysArr.add(
                    ResvtnChsDt(dt_split.get(0), dt_split.get(1), month, sdf_param.format(cal.time))
                )
                cal.add(Calendar.DAY_OF_WEEK, 1)
            }

//            val sdf = SimpleDateFormat("EEE,dd")
//            val sdf_param = SimpleDateFormat("YYY-MM-dd")
//
//            val daysArr = ArrayList<ResvtnChsDt>()
////            daysArr.add(ResvtnChsDt("","",""))
//            for (i in 0..30) {
//                Log.d("dateTag", sdf.format(cal.time))
//
//                val dt_split = sdf.format(cal.time).split(",")
//
//                daysArr.add(
//                    ResvtnChsDt(dt_split.get(0), dt_split.get(1), sdf_param.format(cal.time))
//                )
//                cal.add(Calendar.DAY_OF_WEEK, 1)
//            }


            return daysArr
        }

        fun getAllWeekDays(): ArrayList<RVDates> {
            val cal = Calendar.getInstance()
//            cal.set(2020,10,7)
//            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
            val sdf = SimpleDateFormat("EEE, dd MMM")
            val sdf_param = SimpleDateFormat("YYY-MM-dd")

            val daysArr = ArrayList<RVDates>()
            daysArr.add(RVDates("", ""))
            for (i in 0..30) {
                Log.d("dateTag", sdf.format(cal.time))

//                daysArr.add(sdf.format(cal.time))
                daysArr.add(
                    RVDates(sdf.format(cal.time), sdf_param.format(cal.time))
                )
                cal.add(Calendar.DAY_OF_WEEK, 1)
            }

            daysArr.add(RVDates("", ""))
            daysArr.add(RVDates("", ""))
            daysArr.add(RVDates("", ""))
            daysArr.add(RVDates("", ""))

            return daysArr

        }

        fun getAllTimeIntervals(dsply_date: String, currdt: String): ArrayList<RVTimes> {
//            val df: DateFormat = SimpleDateFormat("hh:mm a")
            val df: DateFormat = SimpleDateFormat("hh:mm a")
            val df_param: DateFormat = SimpleDateFormat("HH:mm")
            val cal = Calendar.getInstance()
            val timeArr = ArrayList<RVTimes>()

            if(currdt.equals(dsply_date, ignoreCase = true))
            {
                val unroundedMinutes: Int = cal.get(Calendar.MINUTE)
                val mod = unroundedMinutes % 30
                cal.add(Calendar.MINUTE,  30 - mod)
                val currentTime = cal.time
                val startDate = cal[Calendar.DATE]

                timeArr.add(
                    RVTimes("", "")
                )

                while (cal[Calendar.DATE] === startDate) {
                    Log.d("alltime", df.format(cal.time))
                    timeArr.add(
                        RVTimes(df.format(cal.time), df_param.format(cal.time))
                    )
                    cal.add(Calendar.MINUTE, 30)
                }

                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )


            }
            else
            {
                cal[Calendar.HOUR_OF_DAY] = 0
                cal[Calendar.MINUTE] = 0
                cal[Calendar.SECOND] = 0
                val startDate = cal[Calendar.DATE]

                timeArr.add(
                    RVTimes("", "")
                )

                while (cal[Calendar.DATE] === startDate) {
                    Log.d("alltime", df.format(cal.time))
                    Log.d("alltime", cal[Calendar.DATE].toString())
                    timeArr.add(
                        RVTimes(df.format(cal.time), df_param.format(cal.time))
                    )
                    cal.add(Calendar.MINUTE, 30)
                }

                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )
                timeArr.add(
                    RVTimes("", "")
                )

            }







            return timeArr
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrTimeinAmPm(): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val currentTimedefore = calendar.time
            val unroundedMinutes: Int = calendar.get(Calendar.MINUTE)
            val mod = unroundedMinutes % 30
//            calendar.add(Calendar.MINUTE, if (mod < 8) -mod else 30 - mod)
            calendar.add(Calendar.MINUTE, 30 - mod)
            val currentTime = calendar.time

            val formatTime: DateFormat = SimpleDateFormat("hh:mm a")
            formatTime.timeZone = TimeZone.getDefault()
            val finaltime = formatTime.format(currentTime)

            val formatTime2: DateFormat = SimpleDateFormat("HH:mm")
            formatTime2.timeZone = TimeZone.getDefault()
            val finalTimeinHRS = formatTime2.format(currentTime)

//            "04:02 pm"

            Log.d("checkTime", finaltime)

            return finaltime.toString() to finalTimeinHRS.toString()
        }

        fun getAllWeekDaysForResvtn(): ArrayList<ResvtnChsDt> {
            val cal = Calendar.getInstance()
//            cal.set(2020,10,10)
//            cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
            val sdf = SimpleDateFormat("EEE,dd")
            val sdf_param = SimpleDateFormat("YYY-MM-dd")
            val sdf_month = SimpleDateFormat("MMM")

            val daysArr = ArrayList<ResvtnChsDt>()

            val today: LocalDate = LocalDate.now()
            val endOfMonth: LocalDate = today.withDayOfMonth(today.lengthOfMonth())
            Log.d("dateTag", "${today}, ${endOfMonth}, ${today.lengthOfMonth()}")
//            val daysBetween: Long = DAYS.between(today, endOfMonth)

//            daysArr.add(ResvtnChsDt("","",""))
            for (i in 0..30) {
//                Log.d("dateTag", sdf.format(cal.time))

                val dt_split = sdf.format(cal.time).split(",")
                val month = sdf_month.format(cal.time)

                daysArr.add(
                    ResvtnChsDt(dt_split.get(0), dt_split.get(1), month, sdf_param.format(cal.time))
                )
                cal.add(Calendar.DAY_OF_WEEK, 1)
            }

//            daysArr.add(ResvtnChsDt("","",""))
//            daysArr.add(ResvtnChsDt("","",""))

            return daysArr

        }

        fun checkOpenNow(currTime: String,opn_time: String, slottime: String): String {
//            val time1 = SimpleDateFormat("hh:mm:ss").parse(currTime)
//            val time2 = SimpleDateFormat("hh:mm:ss").parse(slottime)

            val time1 = SimpleDateFormat("hh:mm a").parse(currTime)
            val time3 = SimpleDateFormat("hh:mm:ss").parse(opn_time)
            val time2 = SimpleDateFormat("hh:mm:ss").parse(slottime)

//          time1!!.after(time2)

            when {
                time1!!.before(time2) && time1.after(time3) -> {
                    return "Open Now"
                }
                time1.before(time3) -> {
                    return "Closed"
                }
                time1.after(time2) -> {
                    return "Closed"
                }
            }

            return "Closed"
        }

        fun converteddatetimeforBooking(dt: String): String {
            val format1 =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

//            format1.timeZone = TimeZone.getTimeZone("GMT")

            var dt1: Date? = null
            try {
                dt1 = format1.parse(dt)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val format2: DateFormat = SimpleDateFormat("EEE")
            val finalDay = format2.format(dt1)

            val format3: DateFormat = SimpleDateFormat("dd")
            val finaldate = format3.format(dt1)

            val format6: DateFormat = SimpleDateFormat("YYYY")
            val finalyear = format6.format(dt1)

            val format4: DateFormat = SimpleDateFormat("hh:mm a")
            format4.timeZone = TimeZone.getDefault()
//        String finaltime = format4.format(dt1);
            //        String finaltime = format4.format(dt1);
            val finaltime = format4.format(dt1)

            val format5: DateFormat = SimpleDateFormat("MMM")
            val strmonth = format5.format(dt1)

//            when (strmonth) {
//                "01" -> strmonth = "Jan"
//                "02" -> strmonth = "Feb"
//                "03" -> strmonth = "Mar"
//                "04" -> strmonth = "Apr"
//                "05" -> strmonth = "May"
//                "06" -> strmonth = "June"
//                "07" -> strmonth = "July"
//                "08" -> strmonth = "Aug"
//                "09" -> strmonth = "Sep"
//                "10" -> strmonth = "Oct"
//                "11" -> strmonth = "Nov"
//                "12" -> strmonth = "Dec"
//            }

//            2022-11-04 15:11:08
//            18 January 2022, 11:30 AM

            return "$finaldate $strmonth $finalyear, $finaltime"
        }

        fun createDialogWithoutDateField(
            context: Context,
            currYear: String,
            currMonth: String
        ): DatePickerDialog {
            val monthindigit = getMonthInDigits(currMonth)
            val dpd = DatePickerDialog(context, null, currYear.toInt(), monthindigit, 1)
            try {
                val datePickerDialogFields = dpd.javaClass.declaredFields
                for (datePickerDialogField in datePickerDialogFields) {
                    if (datePickerDialogField.name == "mDatePicker") {
                        datePickerDialogField.isAccessible = true
                        val datePicker = datePickerDialogField[dpd] as DatePicker
                        val datePickerFields = datePickerDialogField.type.declaredFields
                        for (datePickerField in datePickerFields) {
                            Log.i("testdatePicker", datePickerField.name)
//                            if ("mDaySpinner" == datePickerField.name) {
//                                datePickerField.isAccessible = true
//                                val dayPicker = datePickerField[datePicker]
//                                (dayPicker as View).setVisibility(View.GONE)
//                            }

                            datePickerField.isAccessible = true
                            val dayPicker = datePickerField[datePicker]
                            (dayPicker as View).setVisibility(View.GONE)

                        }
                    }
                }
            } catch (ex: Exception) {
            }
            return dpd
        }

        private fun getMonthInDigits(strmonth: String): Int {

            var monthindigit = -1
            when (strmonth) {
                "January" -> monthindigit = 0
                "February" -> monthindigit = 1
                "March" -> monthindigit = 2
                "April" -> monthindigit = 3
                "May" -> monthindigit = 4
                "June" -> monthindigit = 5
                "July" -> monthindigit = 6
                "August" -> monthindigit = 7
                "September" -> monthindigit = 8
                "October" -> monthindigit = 9
                "November" -> monthindigit = 10
                "December" -> monthindigit = 11
            }

            return monthindigit


        }


    }

}