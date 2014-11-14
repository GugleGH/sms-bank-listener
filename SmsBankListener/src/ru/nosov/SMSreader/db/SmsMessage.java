/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;

/**
 * Sms сообщение.
 * @author Носов А.В.
 */
public class SmsMessage {
    
    // Variables declaration
    public static final String LOG_TAG = LOG_NAME + "SmsMessage";
    
    /** Номер. */
    public static final String COLUMN_ORIGINATING_ADDRESS = "address";
    /** Тело сообщения. */
    public static final String COLUMN_BODY = "body";
    /** Дата. */
    public static final String COLUMN_DATE = "date";
    
    /** Номер.*/
    private String address;
    /** Тело сообщения. */
    private String body;
    /** Дата. */
    private Long date;
    // End of variables declaration

    /**
     * Создать sms сообщение.
     * @param address номер
     * @param body тело сообщения
     * @param date дата
     */
    public SmsMessage(String address, String body, Long date) {
        this.address = address;
        this.body = body;
        this.date = date;
    }
    
    /**
     * Возвращает адрес сообщения.
     * @return номер
     */
    public String getAddress() {
        return address;
    }

    /**
     * Устанавливает адрес сообщения.
     * @param address номер
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Возвращает тело сообщения.
     * @return тело сообщения
     */
    public String getBody() {
        return body;
    }

    /**
     * Устанавливает тело сообщения.
     * @param body тело сообщения
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Возвращает дату получения сообщения.
     * @return дата
     */
    public Long getDate() {
        return date;
    }

    /**
     * Устанавливает дату получения сообщения.
     * @param date дата
     */
    public void setDate(Long date) {
        this.date = date;
    }
}
