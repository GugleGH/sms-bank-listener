/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Банк.
 * @author Носов А.В.
 */
public class Bank {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "bankName";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор номера. */
    public static final String COLUMN_NAME = "name";
    /** Номер карты. */
    public static final String COLUMN_DESCRIPTION = "description";
    
    /** Идентификатор. */
    private int id;
    /** Имя банка. */
    private String name;
    /** Описание банка. */
    private String description;
    // End of variables declaration
    
    public Bank () {
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
     * Возвращает имя.
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя.
     * @param name имя
     */
    public void setName(String name) {
        this.name = name;
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
}
