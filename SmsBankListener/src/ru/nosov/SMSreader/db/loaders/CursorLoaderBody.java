/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import ru.nosov.SMSreader.db.impl.RegexImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderBody extends CursorLoader {

    // Variables declaration
    private RegexImpl bodyImpl;
    // End of variables declaration
    
    public CursorLoaderBody(Context context, RegexImpl impl) {
        super(context);
        this.bodyImpl = impl;
    }
    
    @Override
    public Cursor loadInBackground() {
        return bodyImpl.getAllRegex();
    }
}
