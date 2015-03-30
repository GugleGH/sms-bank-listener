/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Профиль.
 * @author Носов А.В.
 */
public class Profile {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = "profiles";
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Отображаемое имя. */
    public static final String COLUMN_VISIBLE_NAME = "visibleName";
    
    /** Идентификатор. */
    private int id;
    /** Отображаемое имя. */
    private String visibleName;
    // End of variables declaration
    
    public Profile() {
    }

    /**
     * Возвращает идетификатор.
     * @return идентификатор
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
     * Возвразает отображаемое имя.
     * @return оторажаемое имя
     */
    public String getVisibleName() {
        return visibleName;
    }

    /**
     * Устнавливает отображаемое имя.
     * @param visibleName отображаемое имя
     */
    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }
}
