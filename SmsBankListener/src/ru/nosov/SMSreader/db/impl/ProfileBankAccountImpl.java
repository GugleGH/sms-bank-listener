/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.ProfileBankAccount;

/**
 * Связывает профили и банковские счета.
 * @author Носов А.В.
 */
public class ProfileBankAccountImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public ProfileBankAccountImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все банки в базе.
     * @return список банковский счет
     */
    public Cursor getAllPBA() {
        Cursor cursor = database.query(ProfileBankAccount.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает связь по ID профиля.
     * @param idProfile идентификатор профиля
     * @return связь
     */
    public Cursor getPBAByIDProfile(int idProfile) {
        String[] fields = new String[] { ProfileBankAccount.COLUMN_ID,
                                         ProfileBankAccount.COLUMN_ID_PROFILE,
                                         ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT
                                       };
        
        return database.query(ProfileBankAccount.TABLE_NAME, 
                               fields,
                               ProfileBankAccount.COLUMN_ID_PROFILE + "=?",
                               new String[] { String.valueOf(idProfile) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает связь по ID счета.
     * @param idBankAccount идентификатор счета
     * @return связь
     */
    public Cursor getPBAByIDBankAccount(int idBankAccount) {
        String[] fields = new String[] { ProfileBankAccount.COLUMN_ID,
                                         ProfileBankAccount.COLUMN_ID_PROFILE,
                                         ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT
                                       };
        
        return database.query(ProfileBankAccount.TABLE_NAME, 
                               fields,
                               ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + "=?",
                               new String[] { String.valueOf(idBankAccount) },
                               null, null, null, null);
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
