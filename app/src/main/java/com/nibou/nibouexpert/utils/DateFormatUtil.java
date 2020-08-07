package com.nibou.nibouexpert.utils;

import android.content.Context;

import com.nibou.nibouexpert.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateFormatUtil {

    public static String getLocalFormatDateString(String dateString, String newformat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat newFormat = new SimpleDateFormat(newformat);
        try {
            return newFormat.format(simpleDateFormat.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLocalFormatMillies(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return String.valueOf(simpleDateFormat.parse(dateString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getRequiredDateFormat(String milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }

    public static Date getRequiredDate(String milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return calendar.getTime();
    }


    public static String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String getServerMilliSeconds(String serverDateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return String.valueOf(simpleDateFormat.parse(serverDateString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getServerDate(String serverDateString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(getServerMilliSeconds(serverDateString)));
        return calendar.getTime();
    }

    public static String changeDateFormat(String milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }

    public static Date removeTimeFromDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((milliSeconds));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static String getLocalMillies() {
        return String.valueOf(System.currentTimeMillis());
    }


    public static String getLastMessageTime(Context context, String millis) {
        Date currentDate = removeTimeFromDate(System.currentTimeMillis());
        Date serverDate = removeTimeFromDate(Long.parseLong(millis));
        long diffDays = (((currentDate.getTime() / 1000) - (serverDate.getTime() / 1000)) / (24 * 60 * 60));
        if (diffDays == 0) {
            return changeDateFormat(millis, "HH:mm");
        } else if (diffDays == 1) {
            return context.getString(R.string.yesterday);
        }
//        else if (diffDays > 1 && diffDays < 7) {
//            return changeDateFormat(millis, "EEEE, HH:mm");
//        }
        else {
            return changeDateFormat(millis, "EEEE, dd MMM");
        }
    }


    // after chat merge bottom method will remove

    public static String generateTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static String generateNewFormat(String millies, String newformat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat(newformat);
        try {
            return newFormat.format(simpleDateFormat.parse(simpleDateFormat.format(Long.valueOf(millies))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFirstDateOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return generateNewFormat(String.valueOf(c.getTimeInMillis()), "dd.MM.yyyy");
    }

    public static Date getDateFromMillies(String millies) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(simpleDateFormat.format(Long.valueOf(millies)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDay(Context context, int dayNumber) {
        switch (dayNumber) {
            case 1:
                return context.getString(R.string.monday);
            case 2:
                return context.getString(R.string.tuesday);
            case 3:
                return context.getString(R.string.wednesday);
            case 4:
                return context.getString(R.string.thursday);
            case 5:
                return context.getString(R.string.friday);
            case 6:
                return context.getString(R.string.saturday);
            case 7:
                return context.getString(R.string.sunday);
        }
        return "";
    }
}
