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
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Phone;

/**
 * Телефон.
 * @author Носов А.В.
 */
public class PhoneImpl {
    
    // Variables declaration
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    /** Работа с БД. */
    private DBHelper dBHelper;
    /** Доступ к данным БД. */
    private SQLiteDatabase database;
    // End of variables declaration
    
    public PhoneImpl(Context c) {
        this.context = c;
    }
    
    /**
     * Возвращает курсор всех телефонов в базе.
     * @return курсов всех телефонов
     */
    public Cursor getCursorAllPhone() {
        Cursor cursor = database.query(Phone.TABLE_NAME, 
                null, null, null, null, null, null);
        return cursor;
    }
    
    /**
     * Возвращает все телефоны в базе.
     * @return список телефонов
     */
    public ArrayList<Phone> getAllPhone() {
        ArrayList<Phone> phones = new ArrayList<Phone>();
        
        this.open();
        Cursor c = getCursorAllPhone();
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Phone.COLUMN_ID);
            int bIndex = c.getColumnIndex(Phone.COLUMN_ID_BANK);
            int daIndex = c.getColumnIndex(Phone.COLUMN_DISPLAY_ADDRESS);
            int oaIndex = c.getColumnIndex(Phone.COLUMN_ORIGINATING_ADDRESS);
            do {
                Phone phone = new Phone();
                phone.setId(c.getInt(idIndex));
                phone.setIdBank(c.getInt(bIndex));
                phone.setDisplayAddress(c.getString(daIndex));
                phone.setOriginatingAddress(c.getString(oaIndex));
                phones.add(phone);
            } while (c.moveToNext());
        }
        this.close();
        
        return phones;
    }
    
    /**
     * Возвращает курсор номера по его идентификатору.
     * @param id идентификатор телефона
     * @return курсор номера
     */
    public Cursor getCursorPhoneByID(int id) {
        String[] fields = new String[] { Phone.COLUMN_ID, 
                                         Phone.COLUMN_ID_BANK,
                                         Phone.COLUMN_DISPLAY_ADDRESS,
                                         Phone.COLUMN_ORIGINATING_ADDRESS
                                       };
        
        return database.query( Phone.TABLE_NAME, 
                               fields,
                               Phone.COLUMN_ID + "=?",
                               new String[] { String.valueOf(id) },
                               null, null, null, null);
    }
    
    /**
     * Возвращает телефон по его идентификатору.
     * @param id идентификатор телефона
     * @return телефон
     */
    public Phone getPhoneByID(int id) {
        Phone phone = new Phone();
        if (id < 0) return phone;
        
        this.open();
        Cursor c = getCursorPhoneByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Phone.COLUMN_ID);
            int bIndex = c.getColumnIndex(Phone.COLUMN_ID_BANK);
            int daIndex = c.getColumnIndex(Phone.COLUMN_DISPLAY_ADDRESS);
            int oaIndex = c.getColumnIndex(Phone.COLUMN_ORIGINATING_ADDRESS);

            phone.setId(c.getInt(idIndex));
            phone.setIdBank(c.getInt(bIndex));
            phone.setDisplayAddress(c.getString(daIndex));
            phone.setOriginatingAddress(c.getString(oaIndex));
        }
        this.close();
        
        return phone;
    }
    
    /**
     * Возвращает true, если телефон с этим ID существует.
     * @param id идентификатор
     * @return <b>true</b> - телефон есть в БД,
     *         <b>false</b> - телефон отсутствует.
     */
    public boolean isPhoneByID(int id) {
        String[] fields = new String[] { Phone.COLUMN_ID
                                       };
        
        Cursor c = database.query( Phone.TABLE_NAME, 
                                   fields,
                                   Phone.COLUMN_ID + "=?",
                                   new String[] { String.valueOf(id) },
                                   null, null, null, null);
        return (c != null);
    }
    
    /**
     * Добавляет телефон в базу.
     * @param phone телефон
     */
    public void addPhone(Phone phone) {
        if ( (phone.getOriginatingAddress() == null) ||
             (phone.getOriginatingAddress().equals("")) ) return;
        // TODO тут
//        ProfileImpl profileImpl = new ProfileImpl(context);
//        boolean b = profileImpl.isProfileByID(phone.getId_profile());
//        if (!b) return;
        
        ContentValues cv = new ContentValues();
        cv.put(Phone.COLUMN_ID_BANK, phone.getIdBank());
        cv.put(Phone.COLUMN_DISPLAY_ADDRESS, phone.getDisplayAddress());
        cv.put(Phone.COLUMN_ORIGINATING_ADDRESS, phone.getOriginatingAddress());
        database.insert(Phone.TABLE_NAME, null, cv);
    }
    // TODO тут
//    /**
//     * Возвращает список телефонов по идентификатору профиля.
//     * @param id_profile идентификатор профиля
//     * @return список телефонов
//     */
//    public Cursor getPhonesByIDProfile(int id_profile) {
//        String[] fields = new String[] { Phone.COLUMN_ID, 
//                                         Phone.COLUMN_ID_BANK,
//                                         Phone.COLUMN_DISPLAY_ADDRESS,
//                                         Phone.COLUMN_ORIGINATING_ADDRESS
//                                       };
//        
//        return database.query( Phone.TABLE_NAME, 
//                               fields,
//                               Phone.COLUMN_ID_PROFILE + "=?",
//                               new String[] { String.valueOf(id_profile) },
//                               null, null, null, null);
//    }
    
    /**
     * Возвращает список телефонов по оригинальному адресу.
     * @param originatingAddress оргинальный адрес
     * @return список телефонов
     */
    public Cursor getPhonesByOriginatingAddress(String originatingAddress) {
        String[] fields = new String[] { Phone.COLUMN_ID, 
                                         Phone.COLUMN_ID_BANK,
                                         Phone.COLUMN_DISPLAY_ADDRESS,
                                         Phone.COLUMN_ORIGINATING_ADDRESS
                                       };
        return database.query( Phone.TABLE_NAME, 
                               fields,
                               Phone.COLUMN_ORIGINATING_ADDRESS + "=?",
                               new String[] { String.valueOf(originatingAddress) },
                               null, null, null, null);
    }
    
    /**
     * Удалить номер по его идентификатору.
     * @param id идентификатор номера
     */
    public void deletePhoneByID(long id) {
        database.delete(Phone.TABLE_NAME, Phone.COLUMN_ID + " = " + id, null);
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
