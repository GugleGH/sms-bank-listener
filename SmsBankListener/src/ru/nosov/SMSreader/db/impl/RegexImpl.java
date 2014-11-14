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
import ru.nosov.SMSreader.db.Regex;
import ru.nosov.SMSreader.db.DBHelper;

/**
 * Регулярное выражение.
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
     * Возвращает курсор на список всех регулярных выражений в базе.
     * @return курсор на список
     */
    public Cursor getCursorAllRegex() {
        Cursor cursor = database.query(Regex.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает список всех регулярных выражений в базе.
     * @return список всех регулярных выражений
     */
    public ArrayList<Regex> getAllRegex() {
        ArrayList<Regex> regexs = new ArrayList<Regex>();
        
        this.open();
        Cursor c = getCursorAllRegex();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Regex.COLUMN_ID);
            int phIndex = c.getColumnIndex(Regex.COLUMN_ID_BANK);
            int bIndex = c.getColumnIndex(Regex.COLUMN_REGEX);
            
            do {
                Regex t = new Regex();
                t.setId(c.getInt(idIndex));
                t.setIdBank(c.getInt(phIndex));
                t.setRegex(c.getString(bIndex));
                regexs.add(t);
            } while (c.moveToNext());
        }
        this.close();
        
        return regexs;
    }
    
    /**
     * Возвращает курсор на регулярное выражение для тела сообщения по его идентификатору.
     * @param id идентификатор сообщения
     * @return курсор на регулярное выражение
     */
    public Cursor getCursorRegexByID(int id) {
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
     * Возвращает курсор на регулярное выражение для тела сообщения по его идентификатору.
     * @param id идентификатор сообщения
     * @return курсор на регулярное выражение
     */
    public Regex getRegexByID(int id) {
        Regex regex = new Regex();
        
        this.open();
        Cursor c = getCursorRegexByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Regex.COLUMN_ID);
            int phIndex = c.getColumnIndex(Regex.COLUMN_ID_BANK);
            int bIndex = c.getColumnIndex(Regex.COLUMN_REGEX);
            
            regex.setId(c.getInt(idIndex));
            regex.setIdBank(c.getInt(phIndex));
            regex.setRegex(c.getString(bIndex));
        }
        this.close();
        
        return regex;
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
