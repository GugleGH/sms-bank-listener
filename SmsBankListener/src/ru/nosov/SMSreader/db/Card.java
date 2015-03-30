/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Карта.
 * @author Носов А.В.
 */
public class Card {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "card";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор банковского счета. */
    public static final String COLUMN_ID_BANK_ACCOUNT = "_id_bankAccount";
    /** Номер карты. */
    public static final String COLUMN_CARD_NUMBER = "cardNumber";
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор номера. */
    private int idBankAccount;
    /** Номер карты. */
    private String cardNumber;
    // End of variables declaration
    
    public Card () {
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
     * Возвращает идентификатор счета.
     * @return идентификатор счета
     */
    public int getIdBankAccount() {
        return idBankAccount;
    }

    /**
     * Устанавливает идентификатор счета.
     * @param idBankAccount идентификатор счета
     */
    public void setIdBankAccount(int idBankAccount) {
        this.idBankAccount = idBankAccount;
    }

    /**
     * Возвращает номер карты.
     * @return номер карты
     */
    public String getCardNumber() {
        return cardNumber;
    }
    
    /**
     * Устанавливает номер карты.
     * @param cardNumber номер карты
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
