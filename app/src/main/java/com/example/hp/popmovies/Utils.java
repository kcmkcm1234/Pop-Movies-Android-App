package com.example.hp.popmovies;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.MetricAffectingSpan;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp on 7/18/2016.
 */
public class Utils {

    static String getFormattedDate(String date){
        String[] dateParts = date.split("-");
        int year = Integer.valueOf(dateParts[0]);
        int month = Integer.valueOf(dateParts[1]);
        int day = Integer.valueOf(dateParts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        return getReadableDateFromCalendar(calendar);
    }

    static String getReadableDateFromCalendar(Calendar c){
        String monthString;
        int month = c.get(Calendar.MONTH);
        switch (month){
            case Calendar.JANUARY: monthString = "Jan";break;
            case Calendar.FEBRUARY: monthString = "Feb";break;
            case Calendar.MARCH: monthString = "Mar";break;
            case Calendar.APRIL: monthString = "Apr";break;
            case Calendar.MAY: monthString = "May";break;
            case Calendar.JUNE: monthString = "Jun";break;
            case Calendar.JULY: monthString = "Jul";break;
            case Calendar.AUGUST: monthString = "Aug";break;
            case Calendar.SEPTEMBER: monthString = "Sep";break;
            case Calendar.OCTOBER: monthString = "Oct";break;
            case Calendar.NOVEMBER: monthString = "Nov";break;
            default: monthString = "Dec";break;
        }
        int day = c.get(Calendar.DAY_OF_MONTH);
        String dayString;
        if(day<=9){
            dayString="0"+day;
        }else {
            dayString=""+day;
        }
        int year = c.get(Calendar.YEAR);

        return dayString+" "+monthString+" "+year;
    }

}
