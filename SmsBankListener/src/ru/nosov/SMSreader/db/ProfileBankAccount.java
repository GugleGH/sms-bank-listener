/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

/**
 * Связывает профили и банковские счета.
 * @author Носов А.В.
 */
public class ProfileBankAccount {
    
    // Variables declaration
    /** Имя таблицы. */
    public static final String TABLE_NAME = Profile.TABLE_NAME+"_"+BankAccount.TABLE_NAME;
    /** Идентификатор. */
    public static final String COLUMN_ID = "_id";
    /** Идентификатор профиля. */
    public static final String COLUMN_ID_PROFILE = "_id_"+Profile.TABLE_NAME;
    /** Идентификатор счета. */
    public static final String COLUMN_ID_BANK_ACCOUNT = "_id_"+BankAccount.TABLE_NAME;
    
    /** Идентификатор. */
    private int id;
    /** Идентификатор профиля. */
    private int idProfile;
    /** Идентификатор счета. */
    private int idBankAccount;
    // End of variables declaration
    
    public ProfileBankAccount() {
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
     * Возвращает идентификатор профиля.
     * @return идентификатор профиля
     */
    public int getIdProfile() {
        return idProfile;
    }

    /**
     * Устанавливает идентификатор профиля.
     * @param idProfile идентификатор профиля
     */
    public void setIdProfile(int idProfile) {
        this.idProfile = idProfile;
    }

    /**
     * Возвращает идентификатор банковского счета.
     * @return идентификатор счета
     */
    public int getIdBankAccount() {
        return idBankAccount;
    }

    /**
     * Устанавливает идентификатор банковского счета.
     * @param idBankAccount идентификатор счета
     */
    public void setIdBankAccount(int idBankAccount) {
        this.idBankAccount = idBankAccount;
    }
}
