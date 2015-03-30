/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import ru.nosov.SMSreader.db.impl.TransactionImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderTransaction extends CursorLoader {

    // Variables declaration
    private TransactionImpl transactionImpl;
    // End of variables declaration
    
    public CursorLoaderTransaction(Context context, TransactionImpl impl) {
        super(context);
        this.transactionImpl = impl;
    }
    
    @Override
    public Cursor loadInBackground() {
        return transactionImpl.getCursorAllTransaction();
    }
}
