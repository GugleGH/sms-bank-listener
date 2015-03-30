/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.impl.ProfileImpl;

/**
 * Запросы к БД.
 * @author Носов А.В.
 */
public class CursorLoaderProfile extends CursorLoader {

    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "CursorLoaderProfile";
    private ProfileImpl profileImpl;
    // End of variables declaration
    
    public CursorLoaderProfile(Context context, ProfileImpl impl) {
        super(context);
        this.profileImpl = impl;
    }
    
    @Override
    public Cursor loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground");
        return (profileImpl == null) ? null : profileImpl.getCursorAllProfiles();
    }
}
