/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Номер телефона.
 * @author Носов А.В.
 */
public class Phone {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "phone";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор наименования банка. */
    public static final String COLUMN_ID_BANK = "_id_bank";
    /** Отображаемое имя. */
    public static final String COLUMN_DISPLAY_ADDRESS = "displayAddress";
    /** Номер. */
    public static final String COLUMN_ORIGINATING_ADDRESS = "originatingAddress";
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор банка. */
    private int idBank;
    /** Отображаемое имя. */
    private String displayAddress;
    /** Номер. */
    private String originatingAddress;
    // End of variables declaration
    
    public Phone () {
    }

    /**
     * Возвращает идентификатор
     * @return 
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
     * Возвращает идентификатор банка.
     * @return идентификатор банка
     */
    public int getIdBank() {
        return idBank;
    }

    /**
     * Устанавливает идентификатор банка.
     * @param idBank идентификатор банка
     */
    public void setIdBank(int idBank) {
        this.idBank = idBank;
    }

    /**
     * Возвращает номер.
     * @return номер
     */
    public String getOriginatingAddress() {
        return originatingAddress;
    }

    /**
     * Устанавливает номер.
     * @param originatingAddress номер
     */
    public void setOriginatingAddress(String originatingAddress) {
        this.originatingAddress = originatingAddress;
    }

    /**
     * Возвращает отображаемое имя сообщения.
     * @return оторажаемое имя
     */
    public String getDisplayAddress() {
        return displayAddress;
    }

    /**
     * Устанавливает отображаемое имя сообщения.
     * @param displayAddress оторажаемое имя
     */
    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }
}
