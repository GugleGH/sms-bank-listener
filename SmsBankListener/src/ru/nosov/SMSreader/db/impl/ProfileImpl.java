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
import java.util.ArrayList;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.ProfileBankAccount;

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
    
    /**
     * Возвращает курсор со списком профилей.
     * @return курсор профилей
     */
    public Cursor getCursorAllProfiles() {
        return database.query(Profile.TABLE_NAME, 
                null, null, null, null, null, null);
    }
    
    /**
     * Возвращает список профилей.
     * @return список профилей
     */
    public ArrayList<Profile> getAllProfiles() {
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        
        Cursor c = getCursorAllProfiles();
        this.open();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Profile.COLUMN_ID);
            int vnIndex = c.getColumnIndex(Profile.COLUMN_VISIBLE_NAME);

            do {
                Profile p = new Profile();
                p.setId(c.getInt(idIndex));
                p.setVisibleName(c.getString(vnIndex));
                profiles.add(p);
            } while (c.moveToNext());
        }
        this.close();
        return profiles;
    }
    
    /**
     * Возвращает курсор профиля по ID.
     * @param id идентификатор
     * @return курсор профиля
     */
    public Cursor getCursorProfileByID(int id) {
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
     * Возвращает профиль по ID.
     * @param id идентификатор
     * @return профиль
     */
    public Profile getProfileByID(int id) {
        Profile profile = new Profile();
        if (id < 0) return profile;
        
        Cursor c = getCursorProfileByID(id);
        this.open();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Profile.COLUMN_ID);
            int vnIndex = c.getColumnIndex(Profile.COLUMN_VISIBLE_NAME);
            
            profile.setId(c.getInt(idIndex));
            profile.setVisibleName(c.getString(vnIndex));
        }
        this.close();
        
        return profile;
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
