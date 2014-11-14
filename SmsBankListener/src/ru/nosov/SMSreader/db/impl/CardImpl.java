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
import ru.nosov.SMSreader.db.PhoneCard;

/**
 * Карта.
 * @author Носов А.В.
 */
public class CardImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public CardImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает курсор всех карт в базе.
     * @return курсор карт
     */
    public Cursor getCursorAllCard() {
        Cursor cursor = database.query(Card.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает список карт в базе.
     * @return список карт
     */
    public ArrayList<Card> getAllCard() {
        ArrayList<Card> cards = new ArrayList<Card>();
        
        this.open();
        Cursor c = getCursorAllCard();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Card.COLUMN_ID);
            int cnIndex = c.getColumnIndex(Card.COLUMN_CARD_NUMBER);
            do {
                Card card = new Card();
                card.setId(c.getInt(idIndex));
                card.setCardNumber(c.getString(cnIndex));
                cards.add(card);
            } while (c.moveToNext());
        }
        this.close();
        
        return cards;
    }
    
    /**
     * Возвращает карту по его идентификатору.
     * @param id идентификатор карты
     * @return карта
     */
    public Cursor getCardByID(int id) {
        String[] fields = new String[] { Card.COLUMN_ID, 
                                         Card.COLUMN_ID_BANK_ACCOUNT,
                                         Card.COLUMN_CARD_NUMBER
                                       };
        
        return database.query( Card.TABLE_NAME, 
                               fields,
                               Card.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает true, если карта с этим ID существует.
     * @param id идентификатор
     * @return <b>true</b> - карта есть в БД,
     *         <b>false</b> - карта отсутствует.
     */
    public boolean isCardByID(int id) {
        String[] fields = new String[] { Card.COLUMN_ID
                                       };
        
        Cursor c = database.query( Card.TABLE_NAME, 
                                   fields,
                                   Card.COLUMN_ID + "=?",
                                   new String[] { String.valueOf(id) },
                                   null, null, null, null);
        return (c != null);
    }
    
    /**
     * Добавляет карту в базу.
     * @param card карта
     */
    public void addCard(Card card) {
        if ( (card.getCardNumber()== null) ||
             (card.getCardNumber().equals("")) ) return;
        
        PhoneImpl phoneImpl = new PhoneImpl(context);
        boolean b = phoneImpl.isPhoneByID(card.getIdBankAccount());
        if (!b) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Card.COLUMN_CARD_NUMBER, card.getCardNumber());
        database.insert(Card.TABLE_NAME, null, cv);
    }
    
    /**
     * Возвращает курсор списка карт по идентификатору телефона.
     * @param id_phone идентификатор телефона
     * @return курсор списка карт
     */
    public Cursor getCursorCardsByIDPhone(int id_phone) {
        String c = Card.TABLE_NAME;
        String phc = PhoneCard.TABLE_NAME;
        String[] fields = new String[] { c + "." + Card.COLUMN_ID,
                                         c + "." + Card.COLUMN_ID_BANK_ACCOUNT,
                                         c + "." + Card.COLUMN_CARD_NUMBER,
                                         phc + "." + PhoneCard.COLUMN_ID_PHONE
                                       };
        String table = c +
                    " INNER JOIN " + phc + " ON " + 
                c + "." + Card.COLUMN_ID + " = " + 
                phc + "." + PhoneCard.COLUMN_ID_CARD;
        
        return database.query( table, 
                               fields,
                               PhoneCard.COLUMN_ID_PHONE + "=?",
                               new String[] { String.valueOf(id_phone) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает список карт по идентификатору телефона.
     * @param id_phone идентификатор телефона
     * @return список карт
     */
    public ArrayList<Card> getCardsByIDPhone(int id_phone) {
        ArrayList<Card> cards = new ArrayList<Card>();
        if (id_phone < 0) return cards;
        
        this.open();
        Cursor c = getCursorCardsByIDPhone(id_phone);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Card.COLUMN_ID);
            int cnIndex = c.getColumnIndex(Card.COLUMN_CARD_NUMBER);
            do {
                Card card = new Card();
                card.setId(c.getInt(idIndex));
                card.setCardNumber(c.getString(cnIndex));
                cards.add(card);
            } while (c.moveToNext());
        }
        this.close();
        
        return cards;
    }
    
    /**
     * Возвращает курсор списка карт по идентификатору банковского счета.
     * @param id_bankaccount идентификатор счета
     * @return курсор списка карт
     */
    public Cursor getCursorCardsByIDBankAccount(int id_bankaccount) {
        String[] fields = new String[] { Card.COLUMN_ID, 
                                         Card.COLUMN_ID_BANK_ACCOUNT,
                                         Card.COLUMN_CARD_NUMBER
                                       };
        
        return database.query( Card.TABLE_NAME, 
                               fields,
                               Card.COLUMN_ID_BANK_ACCOUNT + "=?",
                               new String[] { String.valueOf(id_bankaccount) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает список карт по идентификатору банковского счета.
     * @param id_bankaccount идентификатор счета
     * @return список карт
     */
    public ArrayList<Card> getCardsByIDBankAccount(int id_bankaccount) {
        ArrayList<Card> cards = new ArrayList<Card>();
        if (id_bankaccount < 0) return cards;
        
        this.open();
        Cursor c = getCursorCardsByIDBankAccount(id_bankaccount);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Card.COLUMN_ID);
            int cnIndex = c.getColumnIndex(Card.COLUMN_CARD_NUMBER);
            do {
                Card card = new Card();
                card.setId(c.getInt(idIndex));
                card.setCardNumber(c.getString(cnIndex));
                cards.add(card);
            } while (c.moveToNext());
        }
        this.close();
        return cards;
    }
    
    // TODO тут
//    
//    /**
//     * Возвращает список карт по идентификатору счета.
//     * @param id_card идентификатор счета
//     * @return список карт
//     */
//    public Cursor getCardsByIDCard(int id_card) {
//        String[] fields = new String[] { Card.COLUMN_ID, 
//                                         Card.COLUMN_ID_BANK_ACCOUNT,
//                                         Card.COLUMN_CARD_NUMBER
//                                       };
//        
//        return database.query( Card.TABLE_NAME, 
//                               fields,
//                               Card.COLUMN_ID_PHONE + "=?",
//                               new String[] { String.valueOf(id_card) },
//                               null, null, null, null);
//    }
    
    /**
     * Удалить карту по его идентификатору.
     * @param id идентификатор карты
     */
    public void deleteCardByID(long id) {
        database.delete(Card.TABLE_NAME, Card.COLUMN_ID + " = " + id, null);
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
