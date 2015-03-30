/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

import java.util.Date;

/**
 * Операция.
 * @author Носов А.В.
 */
public class Transaction implements Comparable<Transaction> {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "transact";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор карты. */
    public static final String COLUMN_ID_CARD = "_id_card";
    /** Дата операции. */
    public static final String COLUMN_DATE = "date";
    /** Сумма операции. */
    public static final String COLUMN_AMOUNT = "amount";
    /** Сумма платежей. */
    public static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
    /** Остаток на карте. */
    public static final String COLUMN_BALANCE = "balance";
    /** Описание операции. */
    public static final String COLUMN_DESCRIPTION = "description";
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор карты. */
    private int idCard;
    /** Дата операции. */
    private Date dateTime;
    /** Дата операции  в формате yyyy-MM-dd hh:mm:ss. */
    private String dateSQL;
    /** Сумма операции. */
    private float amount;
    /** Сумма платежей. */
    private float payment_amount;
    /** Остаток на карте. */
    private float balace;
    /** Описание операции. */
    private String description;
    // End of variables declaration
    
    public Transaction () {
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

    /**
     * Возвращает дату операции.
     * @return дата операции
     */
    public Date getDateTime() {
        return dateTime;
    }
    
    /**
     * Устанавливает дату операции.
     * @param dateTime дата операции
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Возвращает дату операции в формате yyyy-MM-dd hh:mm:ss.
     * @return дата операции
     */
    public String getDateSQL() {
        return dateSQL;
    }

    /**
     * Устанавливает дату операции.
     * @param dateSQL датаоперации
     */
    public void setDateSQL(String dateSQL) {
        this.dateSQL = dateSQL;
    }
    
    /**
     * Возвращает сумму операции.
     * @return сумма операции
     */
    public float getAmount() {
        return amount;
    }
    
    /**
     * Устанавливает сумма операции.
     * @param amount сумма операции
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Возвращает сумму платежа.
     * @return сумма платежа
     */
    public float getPayment_amount() {
        return payment_amount;
    }

    /**
     * Устанавливает сумму платежа.
     * @param payment_amount сумма платежа
     */
    public void setPayment_amount(float payment_amount) {
        this.payment_amount = payment_amount;
    }

    /**
     * Возвращает остаток на карте.
     * @return остаток на карте
     */
    public float getBalace() {
        return balace;
    }

    /**
     * Устанавливает остаток на карте.
     * @param balace остаток
     */
    public void setBalace(float balace) {
        this.balace = balace;
    }

    /**
     * Возвращает описание.
     * @return описание
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Устанавливает описание.
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public int compareTo(Transaction t) {
        if (getDateTime() == null || t.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(t.getDateTime());
    }
}
