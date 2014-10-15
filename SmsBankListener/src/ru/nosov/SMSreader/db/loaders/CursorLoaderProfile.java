/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import ru.nosov.SMSreader.db.impl.ProfileImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderProfile extends CursorLoader {

    // Variables declaration
    private ProfileImpl profileImpl;
    // End of variables declaration
    
    public CursorLoaderProfile(Context context, ProfileImpl impl) {
        super(context);
        this.profileImpl = impl;
    }
    
    @Override
    public Cursor loadInBackground() {
        return profileImpl.getAllProfiles();
    }
}
