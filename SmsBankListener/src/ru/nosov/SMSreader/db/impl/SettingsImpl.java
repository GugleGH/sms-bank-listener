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
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Settings;

/**
 * Настройки.
 * @author Носов А.В.
 */
public class SettingsImpl {
    
    // Variables declaration
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
            int bInd = c.getColumnIndex(Settings.COLUMN_BILLNING);
            
            settings.setBilling(c.getInt(bInd) == 1);
        }
        this.close();
        
        return settings;
    }
    
    /**
     * Обновление настрек в базе.
     * @param settings настройки
     */
    public void updateSettings(Settings settings) {
        if ( settings == null) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Settings.COLUMN_BILLNING, (settings.isBilling() ? 1 : 0) );
        database.insert(Settings.TABLE_NAME, null, cv);
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
