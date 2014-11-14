/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.impl;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.Phone;
import ru.nosov.SMSreader.db.SmsMessage;

/**
 * Карта.
 * @author Носов А.В.
 */
public class SmsMessageImpl {
    
    // Variables declaration
    public static final String LOG_TAG = LOG_NAME + "SmsImpl";
    /** Доступ к базовым функциям ОС. */
    private final Context context;
    // End of variables declaration
    
    public SmsMessageImpl(Context c) {
        this.context = c;
    }
    
    public ArrayList<SmsMessage> getSmsMessageByPhones(ArrayList<Phone> phones) {
//        Log.d(LOG_TAG, "Start read sms");
        ArrayList<SmsMessage> sms = new ArrayList<SmsMessage>();
        
        if ( (phones == null) || (phones.size() < 1) ) return sms;
        
        Uri uri = Uri.parse("content://sms/inbox");
        String[] fields = new String[] { SmsMessage.COLUMN_ORIGINATING_ADDRESS, 
                                         SmsMessage.COLUMN_BODY, 
                                         SmsMessage.COLUMN_DATE
                                       };
        
        for (Phone phone: phones) {
            Cursor c = context.getContentResolver().query(  uri, 
                                                    fields, 
                                                    "address=?",
                                                    new String[] { phone.getOriginatingAddress() },
                                                    "date desc");
            //Log.d(LOG_TAG, "Cursor count:" + c.getCount());
            if (c.moveToFirst()) {
                int oaIndex = c.getColumnIndex(SmsMessage.COLUMN_ORIGINATING_ADDRESS);
                int bIndex = c.getColumnIndex(SmsMessage.COLUMN_BODY);
                int dIndex = c.getColumnIndex(SmsMessage.COLUMN_DATE);

                do {
                    SmsMessage sm = new SmsMessage(
                            c.getString(oaIndex),
                            c.getString(bIndex),
                            c.getLong(dIndex)
                    );
                    sms.add(sm);
                } while (c.moveToNext());
            }
        }
//        Log.d(LOG_TAG, "Stop read sms");
        return sms;
    }
}
