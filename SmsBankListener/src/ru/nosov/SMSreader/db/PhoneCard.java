/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Связывает телефоны и карты.
 * @author Носов А.В.
 */
public class PhoneCard {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = Phone.TABLE_NAME+"_"+Card.TABLE_NAME;
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор профиля. */
    public static final String COLUMN_ID_PHONE = "_id_"+Phone.TABLE_NAME;
    /** Идентификатор счета. */
    public static final String COLUMN_ID_CARD = "_id_"+Card.TABLE_NAME;
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор телефона. */
    private int idPhone;
    /** Идентификатор карты. */
    private int idCard;
    // End of variables declaration
    
    public PhoneCard() {
    }

    /**
     * Возвращает идетификатор.
     * @return идентификатор
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор.
     * @param id идентификатор
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор номера.
     * @return идентификатор номера
     */
    public int getIdPhone() {
        return idPhone;
    }

    /**
     * Устанавливает идентификатор номера.
     * @param idPhone идентификатор номера
     */
    public void setIdPhone(int idPhone) {
        this.idPhone = idPhone;
    }

    /**
     * Возвращает идентификатор карты.
     * @return идентификатор карты
     */
    public int getIdCard() {
        return idCard;
    }

    /**
     * Устанавливает идентификатор карты.
     * @param idCard идентификатор карты
     */
    public void setIdCard(int idCard) {
        this.idCard = idCard;
    }
}
