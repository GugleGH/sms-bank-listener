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
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.ProfileBankAccount;

/**
 * Банковский счет.
 * @author Носов А.В.
 */
public class BankAccountImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public BankAccountImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все банковские счета в базе.
     * @return список банковский счет
     */
    public Cursor getAllBankAccounts() {
        Cursor cursor = database.query(BankAccount.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает банковский счет по его идентификатору.
     * @param id идентификатор счета
     * @return банковский счет
     */
    public Cursor getBankAccountByID(int id) {
        String[] fields = new String[] { BankAccount.COLUMN_ID, 
                                         BankAccount.COLUMN_NAME
                                       };
        
        return database.query( BankAccount.TABLE_NAME, 
                               fields,
                               BankAccount.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает true, если банковский счет с этим ID существует.
     * @param id идентификатор
     * @return <b>true</b> - банковский счет есть в БД,
     *         <b>false</b> - банковский счет отсутствует.
     */
    public boolean isBankAccountByID(int id) {
        String[] fields = new String[] { BankAccount.COLUMN_ID
                                       };
        
        Cursor c = database.query( BankAccount.TABLE_NAME, 
                                   fields,
                                   BankAccount.COLUMN_ID + "=?",
                                   new String[] { String.valueOf(id) },
                                   null, null, null, null);
        return (c != null);
    }
    
    /**
     * Добавляет банковский счет в базу.
     * @param bankAccount банковский счет
     */
    public void addBankAccount(BankAccount bankAccount) {
        if ( (bankAccount== null) ||
             (bankAccount.getName()== null) ||
             (bankAccount.getName().equals("")) ) return;
        
        ContentValues cv = new ContentValues();
        cv.put(BankAccount.COLUMN_NAME, bankAccount.getName());
        database.insert(BankAccount.TABLE_NAME, null, cv);
    }
    
    /**
     * Возвращает список банковских счетов по идентификатору id_profile.
     * @param id_profile идентификатор профиля
     * @return список карт
     */
    public Cursor getBankAccountsByIDProfile(int id_profile) {
        String ba = BankAccount.TABLE_NAME;
        String pba = ProfileBankAccount.TABLE_NAME;
        String[] fields = new String[] { ba + "." + BankAccount.COLUMN_ID,
                                         ba + "." + BankAccount.COLUMN_NAME,
                                         pba + "." + ProfileBankAccount.COLUMN_ID_PROFILE
                                       };
//        String sqlQuery = "SELECT *" + 
//                " FROM ( " + ba +
//                    " INNER JOIN " + pba +
//                    " ON " + ba + "." + BankAccount.COLUMN_ID + " = " + 
//                            pba + "." + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " ) " +
//                " WHERE " + pba + "." + ProfileBankAccount.COLUMN_ID_PROFILE + "=" + " ?";
        
//        String sqlQueryT = "SELECT bankAccount._id, bankAccount.name, profiles_bankAccount._id_profiles " + 
//                " FROM ( bankAccount" +
//                    " INNER JOIN profiles_bankAccount " +
//                    " ON bankAccount._id = profiles_bankAccount._id_bankAccount )" +
//                " WHERE profiles_bankAccount._id_profiles =" + " ?";
        
        String table = ba +
                    " INNER JOIN " + pba + " ON " + 
                ba + "." + BankAccount.COLUMN_ID + " = " + 
                pba + "." + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT;
        
        return database.query( table, 
                               fields,
                               ProfileBankAccount.COLUMN_ID_PROFILE + "=?",
                               new String[] { String.valueOf(id_profile) },
                               null, null, null, null);
    }
    
    /**
     * Удалить банковский счет по его идентификатору.
     * @param id банковский счет
     */
    public void deleteBankAccountByID(long id) {
        database.delete(BankAccount.TABLE_NAME, BankAccount.COLUMN_ID + " = " + id, null);
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