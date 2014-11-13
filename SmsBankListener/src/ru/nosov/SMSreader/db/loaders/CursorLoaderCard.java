/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import ru.nosov.SMSreader.db.impl.CardImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderCard extends CursorLoader {

    // Variables declaration
    private CardImpl cardImpl;
    // End of variables declaration
    
    public CursorLoaderCard(Context context, CardImpl impl) {
        super(context);
        this.cardImpl = impl;
    }
    
//    @Override
//    protected void onStartLoading() {
//        // That's how we start every AsyncTaskLoader...
//        // -  code snippet from  android.content.CursorLoader  (method  onStartLoading)
//        if (takeContentChanged()) {
//            forceLoad();
//        }
//    }
    
    @Override
    public Cursor loadInBackground() {
        return cardImpl.getCursorAllCard();
    }
}
