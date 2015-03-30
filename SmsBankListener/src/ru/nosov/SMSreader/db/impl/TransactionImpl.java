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
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.utils.Util;

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
     * Возвращает курсор всех операции в базе.
     * @return курсор всех операции
     */
    public Cursor getCursorAllTransaction() {
        Cursor cursor = database.query(Transaction.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает все операции в базе.
     * @return список операций
     */
    public ArrayList<Transaction> getAllTransaction() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        
        this.open();
        Cursor c = getCursorAllTransaction();
        if (c.moveToFirst()) {
            int cID = c.getColumnIndex(Transaction.COLUMN_ID);
            int cIDc = c.getColumnIndex(Transaction.COLUMN_ID_CARD);
            int cD = c.getColumnIndex(Transaction.COLUMN_DATE);
            int cA = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
            int cPA = c.getColumnIndex(Transaction.COLUMN_PAYMENT_AMOUNT);
            int cB = c.getColumnIndex(Transaction.COLUMN_BALANCE);
            int cDP = c.getColumnIndex(Transaction.COLUMN_DESCRIPTION);
            do {
                Transaction t = new Transaction();
                t.setId(c.getInt(cID));
                t.setIdCard(c.getInt(cIDc));
                t.setDateSQL(c.getString(cD));
                t.setAmount(c.getInt(cA));
                t.setPayment_amount(c.getInt(cPA));
                t.setBalace(c.getInt(cB));
                t.setDescription(c.getString(cDP));
                transactions.add(t);
            } while (c.moveToNext());
        }
        this.close();
        
        return transactions;
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
                                         Transaction.COLUMN_PAYMENT_AMOUNT,
                                         Transaction.COLUMN_BALANCE,
                                         Transaction.COLUMN_DESCRIPTION
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
     * @return <b>true</b> - операция добавленна, <br>
     * <b>false</b> - операция не добавленна
     */
    public boolean addTransaction(Transaction transaction) {
        if ( (transaction == null) ||
             (transaction.getDateSQL() == null) ||
             (transaction.getDateSQL().equals("")) ) return false;
        
        CardImpl cardImpl = new CardImpl(context);
        cardImpl.open();
        boolean b = cardImpl.isCardByID(transaction.getIdCard());
        cardImpl.close();
        if (!b) return false;
        
//        this.open();
        ContentValues cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, transaction.getIdCard());
        cv.put(Transaction.COLUMN_DATE, transaction.getDateSQL());
        cv.put(Transaction.COLUMN_AMOUNT, transaction.getAmount());
        cv.put(Transaction.COLUMN_PAYMENT_AMOUNT, transaction.getPayment_amount());
        cv.put(Transaction.COLUMN_BALANCE, transaction.getBalace());
        cv.put(Transaction.COLUMN_DESCRIPTION, transaction.getDescription());
        database.insert(Transaction.TABLE_NAME, null, cv);
//        this.close();
        return true;
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
                                         Transaction.COLUMN_PAYMENT_AMOUNT,
                                         Transaction.COLUMN_BALANCE,
                                         Transaction.COLUMN_DESCRIPTION
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
            int cID = c.getColumnIndex(Transaction.COLUMN_ID);
            int cD  = c.getColumnIndex(Transaction.COLUMN_DATE);
            int cA  = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
            int cPA = c.getColumnIndex(Transaction.COLUMN_PAYMENT_AMOUNT);
            int cB  = c.getColumnIndex(Transaction.COLUMN_BALANCE);
            int cDP = c.getColumnIndex(Transaction.COLUMN_DESCRIPTION);

            do {
                Transaction tr = new Transaction();
                tr.setId(c.getInt(cID));
                tr.setIdCard(id_card);
                tr.setDateSQL(c.getString(cD));
                tr.setDateTime(Util.formatSQLToDate(c.getString(cD)).getTime());
                tr.setAmount(c.getFloat(cA));
                tr.setPayment_amount(c.getFloat(cPA));
                tr.setBalace(c.getFloat(cB));
                tr.setDescription(c.getString(cDP));
                transactions.add(tr);
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
            ArrayList<Transaction> byCard = getTransactionsByIDCard(card.getId());
            if (byCard != null)
                for (Transaction t : byCard)
                    transactions.add(t);
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
