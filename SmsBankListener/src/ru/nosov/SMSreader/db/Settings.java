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
    public static final String COLUMN_BILLNING = "billing";
    
    /** Билинг. */
    private boolean billing = false;
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
}
