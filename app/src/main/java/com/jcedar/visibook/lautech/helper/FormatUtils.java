package com.jcedar.visibook.lautech.helper;

import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Inbuilt on 6/2/2015.
 */
public class FormatUtils {

    private static final String TAG = FormatUtils.class.getSimpleName();
    // currency unicode values
    public static final String NAIRA = "\u20A6";
    public static final String DOLLAR = "\u20A6";

    public static String makeMonthYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        String strVal = sdf.format(date);
        return strVal;
    }

    public static String makeMonthYearWithComma(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
        String strVal = sdf.format(date);
        return strVal;
    }

    public static String formatDate(Calendar dateTime){
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        return   sdf.format(dateTime.getTime());
    }
    public static String millisToMachineDate(long instant) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTime dt = new DateTime(instant);
        return dt.toString(formatter);
    }

    public static String getCurrentDate() {
        DateTime currentDate = new DateTime(DateTimeZone.getDefault());
        String strDate = currentDate.toString();
        return strDate;
    }
    public static long dateToMillis(String dateTime) {
        if (TextUtils.isEmpty(dateTime)) return 0;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTime dt = formatter.parseDateTime(dateTime);
        return dt.getMillis();
    }
    public static long getDateInMillis(String dateString) {
        Date date = null;
        try {
            date = AppSettings.serverDateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing " + date + " to date");
            e.printStackTrace();
            return -1;
        }
    }

    public static String formatMoney(double value) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(value);
    }


    public static String formatMoney(String value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        return formatter.format(bigDecimal);
    }

    public static String convertDate(String dateValue, String oldFormat, String newFormat){
        DateTimeFormatter oldFormatter = DateTimeFormat.forPattern(oldFormat);
        DateTime date = oldFormatter.parseDateTime(dateValue);
        DateTimeFormatter newFormatter = DateTimeFormat.forPattern(newFormat);
        return date.toString(newFormatter);
    }

    public static long yodaDateToMillis(String dateTime) {
        Log.d(TAG, dateTime);
        if (TextUtils.isEmpty(dateTime)) return 0;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        DateTime dt = formatter.parseDateTime(dateTime);
        return dt.getMillis();
    }

    public static String makeFriendlyDate(String instant) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy");
        DateTime dt = new DateTime(instant);
        return dt.toString(formatter);
    }

    public static String ellipsize(String input) {
        int maxLength = 25;
        String ellip = "...";
        if (input == null || input.length() <= maxLength
                || input.length() < ellip.length()) {
            return input;
        }
        return input.substring(0, maxLength - ellip.length()).concat(ellip);
    }

    public static String toTwoDecimalPlaces(String credit_point){
       DecimalFormat df = new DecimalFormat("#.##");
        double point = Double.parseDouble(credit_point);
        /* String out = df.format(point);
        System.out.println("Value: " +  out);

        return out;*/
        return String.format("%.2f", point);
    }

    public static String makeHumanFriendlyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String strVal = sdf.format(date);
        return strVal;
    }

    public static String makeYearMonthDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String strVal = sdf.format(date);
        return strVal;
    }
    public static String makeTrimmedYear(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        String strVal = sdf.format(date);
        return strVal;
    }

    public static String removeEscapeXters(String string){
        String ss="";
        if(string != null ){
            ss = string.replaceAll("\\n", "");
            ss = ss.replaceAll("\\t", "");
            ss = ss.replaceAll("\\r", "");
        }
        return ss;
    }
}
