/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Банковский счет.
 * @author Носов А.В.
 */
public class BankAccount {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "bankAccount";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Банковский счет. */
    public static final String COLUMN_NAME = "name";
    
    /** Идентификатор. */
    private int id;
    /** Название счета. */
    private String name;
    // End of variables declaration
    
    public BankAccount () {
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
     * Возвращает название счета.
     * @return название счета
     */
    public String getName() {
        return name;
    }
    
    /**
     * Устанавливает название счета.
     * @param name название счета
     */
    public void setName(String name) {
        this.name = name;
    }
}
