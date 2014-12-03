/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Settings;

/**
 * Настройки.
 * @author Носов А.В.
 */
public class SettingsImpl {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "SettingsImpl";
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public SettingsImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает курсор настроек в базе.
     * @return курсор настроек
     */
    public Cursor getCursorSettings() {
        Cursor cursor = database.query(Settings.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает настройки в базе.
     * @return список телефонов
     */
    public Settings getSettings() {
        Settings settings = new Settings();
        
        this.open();
        Cursor c = getCursorSettings();
        if (c.moveToFirst()) {
            int bInd = c.getColumnIndex(Settings.COLUMN_BILLING);
            int lbInd = c.getColumnIndex(Settings.COLUMN_LAST_BILLING);
            
            settings.setBilling(c.getInt(bInd) == 1);
            settings.setLastBilling(c.getString(lbInd));
        }
        this.close();
        
        return settings;
    }
    
    /**
     * Обновление настрек в базе.
     * @param settings настройки
     */
    public void updateSettings(Settings settings) {
        if (settings == null) return;
        this.open();
        try {
            ContentValues cv = new ContentValues();
            cv.put(Settings.COLUMN_BILLING, (settings.isBilling() ? 1 : 0) );
            if (settings.getLastBilling() == null) {
                Log.e(LOG_TAG, "Отлуствует дата билинга");
                return;
            }
            cv.put(Settings.COLUMN_LAST_BILLING, settings.getLastBilling());
            database.insert(Settings.TABLE_NAME, null, cv);
        } finally {
            this.close();
        }
    }
    
    /**
     * Обновление состояния билинга в базе.
     * @param b billing <b>true</b> - билинг пройден,
     * <b>false</b> - требуется билинг.
     */
    public void updateSettingsBilling(boolean b) {
        Settings s = getSettings();
        s.setBilling(b);
        updateSettings(s);
//        this.open();
//        ContentValues cv = new ContentValues();
//        cv.put(Settings.COLUMN_BILLING, (b ? 1 : 0) );
//        database.insert(Settings.TABLE_NAME, null, cv);
//        this.close();
    }
    
    /**
     * Открыть доступ к таблице.
     */
    public void open() {
        dBHelper = new DBHelper(context, null);
        database = dBHelper.getWritableDatabase();
    }
    
    /**
     * Закрыть доступ к таблице.
     */
    public void close() {
        if (dBHelper != null) dBHelper.close();
    }
}
