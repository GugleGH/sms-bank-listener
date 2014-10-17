/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.R;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.impl.BankAccountImpl;

/**
 * Адаптер для отображения списка профилей на главной странице.
 * @author Носов А.В.
 */
public class AdapterProfile4Main extends SimpleCursorAdapter {

    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "ProfileCursorAdapter";
    private int layout;
    private static LayoutInflater inflater = null;
    // End of variables declaration
    
    public AdapterProfile4Main(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.layout = layout;
    }
    
    @Override
    public View newView(Context _context, Cursor _cursor, ViewGroup parent) {
        inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, parent, false);
        return view;
    }
    
    @Override
    public void bindView(View view, Context _context, Cursor _cursor) {
        View v = view;
        if (view == null)
            v = inflater.inflate(R.layout.list_profiles4main, null);
        
        TextView profile = (TextView) v.findViewById(R.id.profileName);
        TextView bancAccount = (TextView) v.findViewById(R.id.bankAccounts);
        ImageView image = (ImageView)  v.findViewById(R.id.list_image);
        
        if (_cursor == null)  return;
        
        int idIndex = _cursor.getColumnIndex(Profile.COLUMN_ID);
        int vnIndex = _cursor.getColumnIndex(Profile.COLUMN_VISIBLE_NAME);
        
        BankAccountImpl ba = new BankAccountImpl(_context);
        ba.open();
        String str = null;
        Cursor cBA = ba.getBankAccountsByIDProfile(_cursor.getInt(idIndex));
        if (cBA.moveToFirst()) {
            int baIndex = cBA.getColumnIndex(BankAccount.COLUMN_NAME);
            do {
                str = (str ==null) ? cBA.getString(baIndex) : str + "\n" + cBA.getString(baIndex);
            } while (cBA.moveToNext());
        }
        ba.close();
        profile.setText(_cursor.getString(vnIndex));
        bancAccount.setText(str);
//        Log.d(LOG_TAG, Profile.COLUMN_VISIBLE_NAME + " = " + _cursor.getString(vnIndex)
//                       + " " + str);
        
    }
    
//    @Override
//    public int getCount() {
//        return (cursor == null) ? 0 : cursor.getCount();
//    }
//    
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//    
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
}
