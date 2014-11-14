/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
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
     * Возвращает курсор на список связей профилей и банковских счетов.
     * @return курсор на список
     */
    public Cursor getCursorAllPBA() {
        Cursor cursor = database.query(ProfileBankAccount.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает список связей профилей и банковских счетов.
     * @return список связей
     */
    public ArrayList<ProfileBankAccount> getAllPBA() {
        ArrayList<ProfileBankAccount> profileBankAccounts = new ArrayList<ProfileBankAccount>();
        
        this.open();
        Cursor c = getCursorAllPBA();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);
            int baIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT);
            
            do {
                ProfileBankAccount pba = new ProfileBankAccount();
                pba.setId(c.getInt(idIndex));
                pba.setIdProfile(c.getInt(pIndex));
                pba.setIdBankAccount(c.getInt(baIndex));
                profileBankAccounts.add(pba);
            } while (c.moveToNext());
        }
        this.close();
        
        return profileBankAccounts;
    }
    
    /**
     * Возвращает связь по ID профиля.
     * @param idProfile идентификатор профиля
     * @return связь
     */
    public Cursor getCursorPBAByIDProfile(int idProfile) {
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
     * Возвращает курсор связей по ID профиля.
     * @param idProfile идентификатор профиля
     * @return курсор связей
     */
    public ArrayList<ProfileBankAccount> getPBAByIDProfile(int idProfile) {
        ArrayList<ProfileBankAccount> profileBankAccounts = new ArrayList<ProfileBankAccount>();
        if (idProfile < 0) return profileBankAccounts;
        
        this.open();
        Cursor c = getCursorPBAByIDProfile(idProfile);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);
            int baIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT);
            
            do {
                ProfileBankAccount pba = new ProfileBankAccount();
                pba.setId(c.getInt(idIndex));
                pba.setIdProfile(c.getInt(pIndex));
                pba.setIdBankAccount(c.getInt(baIndex));
                profileBankAccounts.add(pba);
            } while (c.moveToNext());
        }
        this.close();
        
        return profileBankAccounts;
    }
    
    /**
     * Возвращает курсор связей по ID счета.
     * @param idBankAccount идентификатор счета
     * @return курсор связей
     */
    public Cursor getCursorPBAByIDBankAccount(int idBankAccount) {
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
     * Возвращает связь по ID счета.
     * @param idBankAccount идентификатор счета
     * @return связь
     */
    public ArrayList<ProfileBankAccount> getPBAByIDBankAccount(int idBankAccount) {
        ArrayList<ProfileBankAccount> profileBankAccounts = new ArrayList<ProfileBankAccount>();
        if (idBankAccount < 0) return profileBankAccounts;
        
        this.open();
        Cursor c = getCursorPBAByIDBankAccount(idBankAccount);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);
            int baIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT);
            
            do {
                ProfileBankAccount pba = new ProfileBankAccount();
                pba.setId(c.getInt(idIndex));
                pba.setIdProfile(c.getInt(pIndex));
                pba.setIdBankAccount(c.getInt(baIndex));
                profileBankAccounts.add(pba);
            } while (c.moveToNext());
        }
        this.close();
        
        return profileBankAccounts;
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
