/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Регулярное выражение для тела сообщения.
 * @author Носов А.В.
 */
public class Regex {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "regex";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор наименования банка. */
    public static final String COLUMN_ID_BANK = "_id_bank";
    /** Тело сообщения. */
    public static final String COLUMN_REGEX = "regex";
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор наименования банка. */
    private int idBank;
    /** Регулярное выражение. */
    private String regex;
    // End of variables declaration
    
    public Regex () {
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
     * Устанавливает регулярное выражение.
     * @return регулярное выражение
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Устанавливает регулярное выражение.
     * @param regex регулярное выражение
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }
}
