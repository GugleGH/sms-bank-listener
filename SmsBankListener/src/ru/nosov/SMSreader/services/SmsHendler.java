/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.services;

import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;

/**
 * Отдельный поток по обработке сообщения.
 * @author Носов А.В.
 */
public class SmsHendler implements Runnable {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "SmsHendler";
    // End of variables declaration

    public SmsHendler() {
    }
    
    public void run() {
    }
    
}
