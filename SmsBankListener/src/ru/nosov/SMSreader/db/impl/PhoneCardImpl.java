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
import ru.nosov.SMSreader.db.PhoneCard;

/**
 * Связывает телефоны и карты.
 * @author Носов А.В.
 */
public class PhoneCardImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public PhoneCardImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все банки в базе.
     * @return список банковский счет
     */
    public Cursor getAllPC() {
        Cursor cursor = database.query(PhoneCard.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает связь по ID телефона.
     * @param idPhone идентификатор телефона
     * @return связь
     */
    public Cursor getPCByIDPhone(int idPhone) {
        String[] fields = new String[] { PhoneCard.COLUMN_ID,
                                         //PhoneCard.COLUMN_ID_PHONE,
                                         PhoneCard.COLUMN_ID_CARD
                                       };
        
        return database.query(PhoneCard.TABLE_NAME, 
                               fields,
                               PhoneCard.COLUMN_ID_PHONE + "=?",
                               new String[] { String.valueOf(idPhone) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает связь по ID карыт.
     * @param idCard идентификатор карты
     * @return связь
     */
    public Cursor getPCByIDCard(int idCard) {
        String[] fields = new String[] { PhoneCard.COLUMN_ID,
                                         PhoneCard.COLUMN_ID_PHONE
                                         //PhoneCard.COLUMN_ID_CARD
                                       };
        
        return database.query(PhoneCard.TABLE_NAME, 
                               fields,
                               PhoneCard.COLUMN_ID_CARD + "=?",
                               new String[] { String.valueOf(idCard) },
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
