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
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Transaction;

/**
 * Операция.
 * @author Носов А.В.
 */
public class TransactionImpl {
    
    // Variables declaration
    private final String LOG_TAG = "SMS_READER_TransactionImpl";
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public TransactionImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все операции в базе.
     * @return список операций
     */
    public Cursor getAllTransaction() {
        Cursor cursor = database.query(Transaction.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает операцию по его идентификатору.
     * @param id идентификатор операции
     * @return операция
     */
    public Cursor getTransactionByID(int id) {
        String[] fields = new String[] { Transaction.COLUMN_ID, 
                                         Transaction.COLUMN_ID_CARD,
                                         Transaction.COLUMN_DATE,
                                         Transaction.COLUMN_AMOUNT,
                                         Transaction.COLUMN_BALANCE
                                       };
        
        return database.query( Transaction.TABLE_NAME, 
                               fields,
                               Transaction.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Добавляет операцию в базу.
     * @param transaction операция
     */
    public void addTransaction(Transaction transaction) {
        if ( (transaction.getDateSQL()== null) ||
             (transaction.getDateSQL().equals("")) ) return;
        
        CardImpl cardImpl = new CardImpl(context);
        cardImpl.open();
        boolean b = cardImpl.isCardByID(transaction.getIdCard());
        cardImpl.close();
        if (!b) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, transaction.getIdCard());
        cv.put(Transaction.COLUMN_DATE, transaction.getDateSQL());
        cv.put(Transaction.COLUMN_AMOUNT, transaction.getAmount());
        cv.put(Transaction.COLUMN_BALANCE, transaction.getBalace());
        database.insert(Transaction.TABLE_NAME, null, cv);
    }
    
    /**
     * Возвращает список операций по идентификатору карты.
     * @param id_card идентификатор карты
     * @return список операций
     */
    public Cursor getTransactionsByIDCard(int id_card) {
        String[] fields = new String[] { Transaction.COLUMN_ID, 
                                         Transaction.COLUMN_ID_CARD,
                                         Transaction.COLUMN_DATE,
                                         Transaction.COLUMN_AMOUNT,
                                         Transaction.COLUMN_BALANCE
                                       };
        
        return database.query( Transaction.TABLE_NAME, 
                               fields,
                               Transaction.COLUMN_ID_CARD + "=?",
                               new String[] { String.valueOf(id_card) },
                               null, null, null, null);
    }
    
    /**
     * Удалить операцию по его идентификатору.
     * @param id идентификатор операции
     */
    public void deleteTransactionByID(long id) {
        database.delete(Transaction.TABLE_NAME, Transaction.COLUMN_ID + " = " + id, null);
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
