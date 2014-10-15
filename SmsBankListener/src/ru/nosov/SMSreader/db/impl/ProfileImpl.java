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
import ru.nosov.SMSreader.db.Profile;

/**
 * Профиль.
 * @author Носов А.В.
 */
public class ProfileImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public ProfileImpl(Context c) {
        this.context = c;
    }
    
    public Cursor getAllProfiles() {
//        Cursor cursor = database.query(Profile.TABLE_NAME, 
//                null, null, null, null, null, null);
        return database.query(Profile.TABLE_NAME, 
                null, null, null, null, null, null);
    }
    
    /**
     * Возвращает профиль по ID.
     * @param id идентификатор
     * @return профиль
     */
    public Cursor getProfileByID(int id) {
        String[] fields = new String[] { Profile.COLUMN_ID, 
                                         Profile.COLUMN_VISIBLE_NAME
                                       };
        
        return database.query( Profile.TABLE_NAME, 
                               fields,
                               Profile.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает true, если профиль с этим ID существует.
     * @param id идентификатор
     * @return <b>true</b> - профиль есть в БД,
     *         <b>false</b> - профиль отсутствует.
     */
    public boolean isProfileByID(int id) {
        String[] fields = new String[] { Profile.COLUMN_ID
                                       };
        
        Cursor c = database.query( Profile.TABLE_NAME, 
                                   fields,
                                   Profile.COLUMN_ID + "=?",
                                   new String[] { String.valueOf(id) },
                                   null, null, null, null);
        return (c != null);
    }

    /**
     * Добавляет профиль.
     * @param visibleName отображаемое имя
     * @param smsName 
     */
    public void addProfile(String visibleName, String smsName) {
        ContentValues cv = new ContentValues();
        cv.put(Profile.COLUMN_VISIBLE_NAME, visibleName);
        database.insert(Profile.TABLE_NAME, null, cv);
    }
    
    public void deleteProfileByID(long id) {
        database.delete(Profile.TABLE_NAME, Profile.COLUMN_ID + " = " + id, null);
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
