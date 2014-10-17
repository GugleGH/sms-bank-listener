/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.utils;

import android.graphics.Color;

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
}
