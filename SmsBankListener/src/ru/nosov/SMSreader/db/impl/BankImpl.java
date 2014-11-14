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
     * Возвращает курсор списка всех банков в базе.
     * @return курсор списка всех банков
     */
    public Cursor getCursorAllBanks() {
        Cursor cursor = database.query(Bank.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает список всех банков в базе.
     * @return список всех банков
     */
    public ArrayList<Bank> getAllBanks() {
        ArrayList<Bank> banks = new ArrayList<Bank>();
        
        this.open();
        Cursor c = getCursorAllBanks();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Bank.COLUMN_ID);
            int nIndex = c.getColumnIndex(Bank.COLUMN_NAME);
            int dIndex = c.getColumnIndex(Bank.COLUMN_DESCRIPTION);
            
            do {
                Bank bank = new Bank();
                bank.setId(c.getInt(idIndex));
                bank.setName(c.getString(nIndex));
                bank.setDescription(c.getString(dIndex));
                banks.add(bank);
            } while (c.moveToNext());
        }
        this.close();
        
        return banks;
    }
    
    /**
     * Возвращает курсор на банк по его идентификатору.
     * @param id идентификатор банка
     * @return курсор на банк
     */
    public Cursor getCursorBankByID(int id) {
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
     * Возвращает банк по его идентификатору.
     * @param id идентификатор банка
     * @return банк
     */
    public Bank getBankByID(int id) {
        Bank bank = new Bank();
        
        this.open();
        Cursor c = getCursorBankByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Bank.COLUMN_ID);
            int nIndex = c.getColumnIndex(Bank.COLUMN_NAME);
            int dIndex = c.getColumnIndex(Bank.COLUMN_DESCRIPTION);
            
            bank.setId(c.getInt(idIndex));
            bank.setName(c.getString(nIndex));
            bank.setDescription(c.getString(dIndex));
        }
        this.close();
        
        return bank;
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
