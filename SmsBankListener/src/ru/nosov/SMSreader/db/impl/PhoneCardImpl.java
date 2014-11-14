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
     * Возвращает курсов на список связей номеров и карт.
     * @return курсов на список
     */
    public Cursor getCursorAllPC() {
        Cursor cursor = database.query(PhoneCard.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает список связей номеров и карт.
     * @return список связей
     */
    public ArrayList<PhoneCard> getAllPC() {
        ArrayList<PhoneCard> phoneCards = new ArrayList<PhoneCard>();
        
        this.open();
        Cursor c = getCursorAllPC();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(PhoneCard.COLUMN_ID);
            int pIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_PHONE);
            int cIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_CARD);
            do {
                PhoneCard phoneCard = new PhoneCard();
                phoneCard.setId(c.getInt(idIndex));
                phoneCard.setIdPhone(c.getInt(pIndex));
                phoneCard.setIdCard(c.getInt(cIndex));
                phoneCards.add(phoneCard);
            } while (c.moveToNext());
        }
        this.close();
        
        return phoneCards;
    }
    
    /**
     * Возвращает список связей номеров и карт по ID номера
     * @param idPhone идентификатор телефона
     * @return список связей
     */
    public Cursor getCursorPCByIDPhone(int idPhone) {
        String[] fields = new String[] { PhoneCard.COLUMN_ID,
                                         PhoneCard.COLUMN_ID_PHONE,
                                         PhoneCard.COLUMN_ID_CARD
                                       };
        
        return database.query(PhoneCard.TABLE_NAME, 
                               fields,
                               PhoneCard.COLUMN_ID_PHONE + "=?",
                               new String[] { String.valueOf(idPhone) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает список связей номеров и карт по ID номера
     * @param idPhone идентификатор телефона
     * @return список связей
     */
    public ArrayList<PhoneCard> getPCByIDPhone(int idPhone) {
        ArrayList<PhoneCard> phoneCards = new ArrayList<PhoneCard>();
        if (idPhone < 0) return phoneCards;
        
        this.open();
        Cursor c = getCursorPCByIDPhone(idPhone);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(PhoneCard.COLUMN_ID);
            int pIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_PHONE);
            int cIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_CARD);
            do {
                PhoneCard phoneCard = new PhoneCard();
                phoneCard.setId(c.getInt(idIndex));
                phoneCard.setIdPhone(c.getInt(pIndex));
                phoneCard.setIdCard(c.getInt(cIndex));
                phoneCards.add(phoneCard);
            } while (c.moveToNext());
        }
        this.close();
        
        return phoneCards;
    }
    
    /**
     * Возвращает курсор списка связей номераа и карты по ID карыт.
     * @param idCard идентификатор карты
     * @return курсор списка связей
     */
    public Cursor getCursorPCByIDCard(int idCard) {
        String[] fields = new String[] { PhoneCard.COLUMN_ID,
                                         PhoneCard.COLUMN_ID_PHONE,
                                         PhoneCard.COLUMN_ID_CARD
                                       };
        
        return database.query(PhoneCard.TABLE_NAME, 
                               fields,
                               PhoneCard.COLUMN_ID_CARD + "=?",
                               new String[] { String.valueOf(idCard) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает список связей номера и карты по ID карыт.
     * @param idCard идентификатор карты
     * @return список связей
     */
    public ArrayList<PhoneCard> getPCByIDCard(int idCard) {
        ArrayList<PhoneCard> phoneCards = new ArrayList<PhoneCard>();
        if (idCard < 0) return phoneCards;
        
        this.open();
        Cursor c = getCursorPCByIDCard(idCard);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(PhoneCard.COLUMN_ID);
            int pIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_PHONE);
            int cIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_CARD);
            do {
                PhoneCard phoneCard = new PhoneCard();
                phoneCard.setId(c.getInt(idIndex));
                phoneCard.setIdPhone(c.getInt(pIndex));
                phoneCard.setIdCard(c.getInt(cIndex));
                phoneCards.add(phoneCard);
            } while (c.moveToNext());
        }
        this.close();
        
        return phoneCards;
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
