/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Настройкпи.
 * @author Носов А.В.
 */
public class Settings {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "settings";
    /** Билинг. */
    public static final String COLUMN_BILLING = "billing";
    /** Дата последнего билинга. */
    public static final String COLUMN_LAST_BILLING = "lastBilling";
    
    /** Билинг. */
    private boolean billing = false;
    /** Дата последнеого билинга. */
    private String lastBilling;
    // End of variables declaration
    
    public Settings() {
    }

    /**
     * Возвращает флаг, что данные в талице прошли билинг.
     * @return billing <b>true</b> - билинг пройден,
     * <b>false</b> - требуется билинг.
     */
    public boolean isBilling() {
        return billing;
    }

    /**
     * Устанавливает флаг, что данные в талице прошли билинг.
     * @param billing <b>true</b> - билинг пройден,
     * <b>false</b> - требуется билинг.
     */
    public void setBilling(boolean billing) {
        this.billing = billing;
    }

    /**
     * Возвращает дату последнего билинга в формате yyyy-MM-dd hh:mm:ss.
     * @return дата билинга
     */
    public String getLastBilling() {
        return lastBilling;
    }

    /**
     * Устанавливает дату послденего билинга.
     * @param lastBilling дата билинга
     */
    public void setLastBilling(String lastBilling) {
        this.lastBilling = lastBilling;
    }
}
