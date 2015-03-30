/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.exceptions;

/**
 * Исключения при работе с БД.
 * @author Носов А.В.
 */
public class ExceptionDB extends Exception {
    
    /**
     * Устанавливает сообщение исключения.
     * @param msg сообщение
     */
    public ExceptionDB (String msg) {
        super(msg);
    }
}
