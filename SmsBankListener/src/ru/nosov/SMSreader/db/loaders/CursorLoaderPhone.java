/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import ru.nosov.SMSreader.db.impl.PhoneImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderPhone extends CursorLoader {

    // Variables declaration
    private PhoneImpl phoneImpl;
    // End of variables declaration
    
    public CursorLoaderPhone(Context context, PhoneImpl impl) {
        super(context);
        this.phoneImpl = impl;
    }
    
    @Override
    public Cursor loadInBackground() {
        return phoneImpl.getAllPhone();
    }
}
