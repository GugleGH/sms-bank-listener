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
import ru.nosov.SMSreader.db.Regex;
import ru.nosov.SMSreader.db.DBHelper;

/**
 * Тело сообшения.
 * @author Носов А.В.
 */
public class RegexImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public RegexImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает все телефоны в базе.
     * @return список телефонов
     */
    public Cursor getAllRegex() {
        Cursor cursor = database.query(Regex.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает тело сообщения по его идентификатору.
     * @param id идентификатор сообщения
     * @return тело сообщения
     */
    public Cursor getRegexByID(int id) {
        String[] fields = new String[] { Regex.COLUMN_ID, 
                                         Regex.COLUMN_ID_BANK,
                                         Regex.COLUMN_REGEX
                                       };
        
        return database.query(Regex.TABLE_NAME, 
                               fields,
                               Regex.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Добавляет регулярное выражение в базу.
     * @param regex регулярное выражение
     */
    public void addRegex(Regex regex) {
        if ( (regex.getRegex()== null) ||
             (regex.getRegex().equals("")) ) return;
        
//        PhoneImpl phoneImpl = new PhoneImpl(context);
//        boolean b = phoneImpl.isPhoneByID(regex.getId_card());
//        if (!b) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Regex.COLUMN_ID_BANK, regex.getIdBank());
        cv.put(Regex.COLUMN_REGEX, regex.getRegex());
        database.insert(Regex.TABLE_NAME, null, cv);
    }
    
    /**
     * Возвращает список регулярных выражений по идентификатору банка.
     * @param id_bankName идентификатор банка
     * @return список регулярных выражений
     */
    public Cursor getRegexesByIDBankName(int id_bankName) {
        String[] fields = new String[] { Regex.COLUMN_ID, 
                                         Regex.COLUMN_ID_BANK,
                                         Regex.COLUMN_REGEX
                                       };
        
        return database.query(Regex.TABLE_NAME, 
                               fields,
                               Regex.COLUMN_ID_BANK + "=?",
                               new String[] { String.valueOf(id_bankName) },
                               null, null, null, null);
    }
    
    /**
     * Удалить тело сообщения по его идентификатору.
     * @param id идентификатор сообщения
     */
    public void deleteRegexByID(long id) {
        database.delete(Regex.TABLE_NAME, Regex.COLUMN_ID + " = " + id, null);
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
