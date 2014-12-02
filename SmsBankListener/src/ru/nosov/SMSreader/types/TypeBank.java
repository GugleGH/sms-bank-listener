/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.types;

import java.io.Serializable;

/**
 * Тип банка.
 * @author Носов А.В.
 */
public enum TypeBank implements Serializable {
    
    // Variables declaration
    UNKNOWN(-1, "Неизвестно"),
    /** Райффейзен. */
    RAIFFEISEN(1, "Райффейзен"),
    /** Транснациональный. */
    TNB(2, "Транснациональный");
    
    /** Описание модуля. */
    private final String description;
    /** Идентификатор модуля. */
    private final int id;
    // End of variables declaration
    
    TypeBank(int id, String description) {
        this.description = description;
        this.id = id;
    }
    
    /**
     * Возвращает описание модуля.
     * @return описание модуля
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Возвращает идентификатор модуля.
     * @return идентификатор модуля
     */
    public int getID() {
        return id;
    }
    
    /**
     * Возвращает тип модуля по идентификатору.
     * VERSION - поумолчанию.
     * @param i идентификатор
     * @return тип статуса
     */
    public static TypeBank getTypeModuleByID(int i) {
        try {
            return TypeBank.values()[i];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return TypeBank.UNKNOWN;
        }
    }
}
