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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import ru.nosov.SMSreader.db.Card;
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
     * Возвращает курсор списка операций по идентификатору карты.
     * @param id_card идентификатор карты
     * @return курсор списка операций
     */
    public Cursor getCursorTransactionsByIDCard(int id_card) {
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
     * Возвращает список операций по идентификатору карты.
     * @param id_card идентификатор карты
     * @return список операций
     */
    public ArrayList<Transaction> getTransactionsByIDCard(int id_card) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        if (id_card < 0) return transactions;
        
        this.open();
        Cursor c = getCursorTransactionsByIDCard(id_card);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Transaction.COLUMN_ID);
            int dIndex = c.getColumnIndex(Transaction.COLUMN_DATE);
            int aIndex = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
            int bIndex = c.getColumnIndex(Transaction.COLUMN_BALANCE);

            do {
                try {
                    Transaction tr = new Transaction();
                    tr.setId(c.getInt(idIndex));
                    tr.setDateSQL(c.getString(dIndex));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    tr.setDateTime(dateFormat.parse(c.getString(dIndex)));
                    tr.setAmount(c.getFloat(aIndex));
                    tr.setBalace(c.getFloat(bIndex));
                    transactions.add(tr);
                } catch (ParseException ex) {
                    Log.e(LOG_TAG, c.getString(dIndex) + " --> " + ex.getMessage());
                }
            } while (c.moveToNext());
        }
        this.close();
        
        return transactions;
    }
    
    /**
     * Возвращает список операций по картам.
     * @param cards список карт
     * @return список операций
     */
    public ArrayList<Transaction> getTransactionsByIDCards(ArrayList<Card> cards) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        if ( (cards == null) || (cards.size() < 1) ) return transactions;
        
        this.open();
        for (Card card : cards) {
            Cursor c = getCursorTransactionsByIDCard(card.getId());
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex(Transaction.COLUMN_ID);
                int dIndex = c.getColumnIndex(Transaction.COLUMN_DATE);
                int aIndex = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
                int bIndex = c.getColumnIndex(Transaction.COLUMN_BALANCE);

                do {
                    try {
                        Transaction tr = new Transaction();
                        tr.setId(c.getInt(idIndex));
                        tr.setDateSQL(c.getString(dIndex));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        tr.setDateTime(dateFormat.parse(c.getString(dIndex)));
                        tr.setAmount(c.getFloat(aIndex));
                        tr.setBalace(c.getFloat(bIndex));
                        transactions.add(tr);
                    } catch (ParseException ex) {
                        Log.e(LOG_TAG, c.getString(dIndex) + " --> " + ex.getMessage());
                    }
                } while (c.moveToNext());
            }
        }
        this.close();
        
        return transactions;
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
