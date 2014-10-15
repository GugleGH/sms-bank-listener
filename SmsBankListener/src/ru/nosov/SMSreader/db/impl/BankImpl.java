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
import ru.nosov.SMSreader.db.Bank;
import ru.nosov.SMSreader.db.DBHelper;

/**
 * Банк.
 * @author Носов А.В.
 */
public class BankImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public BankImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все банки в базе.
     * @return список банковский счет
     */
    public Cursor getAllBanks() {
        Cursor cursor = database.query(Bank.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает банк по его идентификатору.
     * @param id идентификатор банка
     * @return банк
     */
    public Cursor getBankByID(int id) {
        String[] fields = new String[] { Bank.COLUMN_ID, 
                                         Bank.COLUMN_NAME,
                                         Bank.COLUMN_DESCRIPTION
                                       };
        
        return database.query( Bank.TABLE_NAME, 
                               fields,
                               Bank.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает true, если банк с этим ID существует.
     * @param id идентификатор
     * @return <b>true</b> - банк есть в БД,
     *         <b>false</b> - банк отсутствует.
     */
    public boolean isBankByID(int id) {
        String[] fields = new String[] { Bank.COLUMN_ID
                                       };
        
        Cursor c = database.query( Bank.TABLE_NAME, 
                                   fields,
                                   Bank.COLUMN_ID + "=?",
                                   new String[] { String.valueOf(id) },
                                   null, null, null, null);
        return (c != null);
    }
    
    /**
     * Добавляет банк в базу.
     * @param bank банк
     */
    public void addBank(Bank bank) {
        if ( (bank== null) ||
             (bank.getName()== null) ||
             (bank.getName().equals("")) ||
             (bank.getDescription()== null) ||
             (bank.getDescription().equals("")) ) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Bank.COLUMN_NAME, bank.getName());
        cv.put(Bank.COLUMN_DESCRIPTION, bank.getDescription());
        database.insert(Bank.TABLE_NAME, null, cv);
    }
    
    /**
     * Удалить банк по его идентификатору.
     * @param id банк
     */
    public void deleteBankByID(long id) {
        database.delete(Bank.TABLE_NAME, Bank.COLUMN_ID + " = " + id, null);
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
