/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.utils;

import android.graphics.Color;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Утилиты.
 * @author Носов А.В.
 */
public class Util {
    
    // Variables declaration
    //public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss");
    // End of variables declaration
    
    public static int getColor4Graph(int hash) {
//        int hash = c.hashCode();
        int red = (hash >> 16) & 0xFF;
        int green = (hash >> 8) & 0xFF;
        int blue = (hash >> 0) & 0xFF;
        
        int next = 102;
        
        if ( (red + next) < 255) return Color.rgb((red + next), green, blue);
        if ( (green + next) < 255) return Color.rgb(red, (green + next), blue);
        if ( (blue + next) < 255) return Color.rgb(red, green, (blue + next));
        
        return Color.rgb(127, 127, 127);
    }
    
    public static int nextColor4Graph(int n) {
        switch (n) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.DKGRAY;
            case 4:
                return Color.GRAY;
            case 5:
                return Color.LTGRAY;
            case 6:
                return Color.MAGENTA;
            case 7:
                return Color.RED;
            case 8:
                return Color.TRANSPARENT;
            case 9:
                return Color.WHITE;
            case 10:
                return Color.YELLOW;
            default:
                return Color.GREEN;
        }
    }
    
    
    
    public static Calendar transformDate(String str) {
        return transformDate1(str);
    }
    
    private static Calendar transformDate1(String str) {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            c.setTime(df.parse(str));
            return c;
        } catch (ParseException ex) {
            return transformDate2(str);
        }
    }
    
    private static Calendar transformDate2(String str) {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
            c.setTime(df.parse(str));
            return c;
        } catch (ParseException ex) {
            return transformDate3(str);
        }
    }
    
    private static Calendar transformDate3(String str) {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
            c.setTime(df.parse(str));
            return c;
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /**
     * Форматирует дату в формат yyyy-MM-dd hh:mm:ss.
     * @param d дата
     * @return строка формата yyyy-MM-dd hh:mm:ss
     */
    public static String formatDateToSQL(Date d) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(d);
    }
    
    /**
     * Форматирует календарь в формат yyyy-MM-dd hh:mm:ss.
     * @param c календарь
     * @return строка формата yyyy-MM-dd hh:mm:ss
     */
    public static String formatCalendarToSQL(Calendar c) {
        return formatDateToSQL(c.getTime());
    }
    
    /**
     * Форматирует строку формата yyyy-MM-dd hh:mm:ss в календарь.
     * @param d строка формата yyyy-MM-dd hh:mm:ss
     * @return календарь
     */
    public static Calendar formatSQLToDate(String d) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(d));
            return c;
        } catch (ParseException ex) {
            return null;
        }
    }
    
    /**
     * Возвращает календарь по милисекундам.
     * @param timeInMillis милисекунды
     * @return календарь
     */
    public static Calendar getCalendarByTimeInMillis(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return c;
    }
    
    /**
     * Возвращает календарь с установленны первым днем месяца.
     * @param d дата
     * @return календарь с установленным первым днем
     */
    public static Calendar getFirstDayOfMonth(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c = getFirstDayOfMonth(c);
        return c;
    }
    
    /**
     * Возвращает календарь с установленны первым днем месяца.
     * @param c календарь
     * @return календарь с установленным первым днем
     */
    public static Calendar getFirstDayOfMonth(Calendar c) {
        c.set(Calendar.DATE, c.getMinimum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }
    
    /**
     * Возвращает календарь с установленным последним днем месяца.
     * @param d дата
     * @return календарь с установленным последним днем
     */
    public static Calendar getLastDayOfMonth(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c = getLastDayOfMonth(c);
        return c;
    }
    
    /**
     * Возвращает календарь с установленным последним днем месяца.
     * @param c календарь
     * @return календарь с установленным последним днем
     */
    public static Calendar getLastDayOfMonth(Calendar c) {
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }
    
    /**
     * Возвращает <i>true</i>, если совпали год/месяц в датах.
     * @param first первая дата
     * @param last вторая дата
     * @return <b>true</b> - месяц/год совпадают,
     * <b>false</b> - месяц/год НЕ совпадают.
     */
    public static boolean validateYYYYMM(Calendar first, Calendar last) {
        return ( (first.get(Calendar.YEAR) == last.get(Calendar.YEAR)) &&
             (first.get(Calendar.MONTH) == last.get(Calendar.MONTH)) );
    }
    
    /**
     * Возвращает <i>true</i>, если совпали год/месяц/день/время в датах.
     * @param first первая дата
     * @param last вторая дата
     * @return <b>true</b> - год/месяц/день/время совпадают,
     * <b>false</b> - год/месяц/день/время НЕ совпадают.
     */
    public static boolean validateYYYYMMDDTT(Calendar first, Calendar last) {
        String d1 = Util.formatCalendarToSQL(first);
        String d2 = Util.formatCalendarToSQL(last);
        return d1.equals(d2);
    }
}
