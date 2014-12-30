package ru.nosov.SMSreader;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import static ru.nosov.SMSreader.ActivityGraph.ID_PROFILE;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Phone;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.SmsMessage;
import ru.nosov.SMSreader.db.adapters.AdapterProfile4Main;
import ru.nosov.SMSreader.db.impl.PhoneImpl;
import ru.nosov.SMSreader.db.impl.ProfileImpl;
import ru.nosov.SMSreader.db.impl.SmsMessageImpl;
import ru.nosov.SMSreader.db.loaders.CursorLoaderProfile;
import ru.nosov.SMSreader.services.CleaningBDService;
import ru.nosov.SMSreader.services.SmsService;

/**
 * http://startandroid.ru/ru/uroki/vse-uroki-spiskom.html
 * http://www.tutorialspoint.com/android/android_user_interface_layouts.htm
 * 
 * SmsBankListener / qwerty
 * 
 * @author Носов А.В.
 */
public class ActivityMain extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    
    // Variables declaration
    public static final String LOG_NAME = "Sms_Bank_listener_";
    public static final String LOG_TAG = LOG_NAME + "ActivityMain";
    public static final String ADD_SMS = "addsms";
    /** Основная разметка. */
    private LinearLayout layoutLinear;
    /** Список профилей. */
    private ListView listViewProfiles;
    /** Подключение к БД. */
    private SimpleCursorAdapter adapterProfile;
    /** Доступ к профилям. */
    private ProfileImpl profileImpl;
    private Button buttonBilling;
    private Button buttonTestDB;
    private Button buttonDropBD;
    private Button buttonReadSms;
    private TextView textView;
    // End of variables declaration
    
    /** Called when the activity is first created.
     * @param savedInstanceState */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        layoutLinear = (LinearLayout) findViewById(R.id.layoutLinear);
        listViewProfiles = (ListView) findViewById(R.id.listViewMainProfiles);
        buttonBilling = (Button) findViewById(R.id.buttonBilling);
        buttonTestDB = (Button) findViewById(R.id.buttonTestBD);
        buttonDropBD = (Button) findViewById(R.id.buttonDropDB);
        buttonReadSms = (Button) findViewById(R.id.buttonReadSms);
        profileImpl = new ProfileImpl(this);
        
        listViewProfiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        String[] from = new String[] { Profile.COLUMN_VISIBLE_NAME };
        int[] to = new int[] { R.id.profileName };
//        adapterProfile = new SimpleCursorAdapter( this, 
//                                                  R.layout.list_profiles4main, 
//                                                  null, from, to, 0);
        adapterProfile = new AdapterProfile4Main(this, 
                                                  R.layout.list_profiles4main, 
                                                  null, from, to, 0);
        listViewProfiles.setAdapter(adapterProfile);
        
//        listViewProfiles.setOnItemClickListener(new OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(LOG_TAG, "--- 2 --- " + position + " " + id);
//            }
//            
//        });
        
        buttonBilling.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                myButtononClick(v);
            }
        });
        buttonTestDB.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                myButtononClick(v);
            }
        });
        buttonDropBD.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                myButtononClick(v);
            }
        });
        buttonReadSms.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                myButtononClick(v);
            }
        });
        textView = (TextView) findViewById(R.id.textView);
        
        profileImpl.open();
        getLoaderManager().initLoader(DBHelper.LOADER_ID_PROFILE, null, this);
        
        validateIntent();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileImpl != null) profileImpl.close();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                visibleProfilesSettingsActivity();
                return true;
//            case R.id.action_profiles_list:
//                visibleProfilesListActivity();
//                return true;
            case R.id.action_aboute:
                textView.setText("Select Aboute");
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DBHelper.LOADER_ID_PROFILE)
            return new CursorLoaderProfile(this, profileImpl);
        else return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (loader.getId()) {
            case DBHelper.LOADER_ID_PROFILE:
                adapterProfile.swapCursor(data);
                break;
            default:
       }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        
    }

    public void myButtononClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBilling:
//                visibleProfilesListActivity();
                startBillingService();
                break;
            case R.id.buttonTestBD:
                visibleTestBDActivity();
                break;
            case R.id.buttonDropDB:
//                DBHelper bdh = new DBHelper(this, null);
//                Log.d(LOG_TAG, "1 DropBD");
//                SQLiteDatabase db = bdh.getWritableDatabase();
//                Log.d(LOG_TAG, "2 DropBD");
                this.deleteDatabase(DBHelper.DB_NAME + ".db");
                Log.d(LOG_TAG, "3 DropBD");
//                db.delete(Transaction.TABLE_NAME, null, null);
//                Log.d(LOG_TAG, "4 DropBD");
//                db.close();
//                Log.d(LOG_TAG, "5 DropBD");
//                bdh.onCreate(db);
//                Log.d(LOG_TAG, "6 DropBD");
                break;
            case R.id.buttonReadSms:
                readSms();
                break;
            default:
        }
    }
    
    /**
     * Обработка нажатия кнопки в listview.
     * android:onClick="clickHandlerButtonGraph"
     * @param v View
     */
    public void clickHandlerButtonGraph(View v) {
        int positionButton = listViewProfiles.getPositionForView(v);
        Object obj = listViewProfiles.getItemAtPosition(positionButton);
        if (obj instanceof Cursor) {
            Cursor c = (Cursor) obj;
            if (c.moveToPosition(positionButton)) {
                int idIndex = c.getColumnIndex(Profile.COLUMN_ID);
                int id_profile = c.getInt(idIndex);
                Intent intent = new Intent(this, ActivityGraph.class);
                intent.putExtra(ID_PROFILE, id_profile);
                startActivity(intent);
            }
        }
    }
    
    /**
     * Проверяет intent.
     */
    private void validateIntent() {
        Intent intent = getIntent();
        if (intent == null) return;
        
        if (intent.getExtras() == null) return;
        
        String addsms = intent.getExtras().getString(ADD_SMS, null);
        if (addsms == null) return;
        
        textView.setText(addsms);
    }
    
    /**
     * Переход на страницу списка профилей.
     */
    private void visibleProfilesSettingsActivity() {
        //Intent intent = new Intent(this, ActivityProfilesSettings.class);
        Intent intent = new Intent(this, ActivityPSTv1.class);
        startActivity(intent);
    }
    
    private void startBillingService() {
        Intent cleaningBDService = new Intent(this, CleaningBDService.class);
        this.startService(cleaningBDService);
    }
    
    /**
     * Переход на страницу списка профилей.
     */
    private void visibleTestBDActivity() {
        Intent intent = new Intent(this, ActivityTestDB.class);
        startActivity(intent);
    }
    
    /**
     * Чтение sms базы.
     */
    private void readSms() {
        Log.d(LOG_TAG, "Start read sms");
        PhoneImpl phoneImpl = new PhoneImpl(this);
        ArrayList<Phone> phones = phoneImpl.getAllPhone();
        
        if ( (phones == null) || (phones.size() < 1) ) return;
        
        SmsMessageImpl smsMessageImpl = new SmsMessageImpl(this);
        ArrayList<SmsMessage> smsMessages = smsMessageImpl.getSmsMessageByPhones(phones);
        
        if ( (smsMessages == null) || (smsMessages.size() < 1) ) return;
        Log.d(LOG_TAG, "Sms count:" + smsMessages.size());
        
        int r9548 = 0;
        int r2643 = 0;
        
        for (SmsMessage smsMessage : smsMessages) {
            Intent smsIntent = new Intent(this, SmsService.class);
            
            if (smsMessage.getBody().startsWith("Karta *9548; Provedena tranzakcija:")) 
                r9548 = r9548+1;
            if (smsMessage.getBody().startsWith("Karta *2643; Provedena tranzakcija:"))
                r2643 = r2643+1;
            if (smsMessage.getBody().startsWith("Povedeno po schety"))
                r9548 = r9548+1;
            if (smsMessage.getBody().startsWith("Balans vashey karty *9548 popolnilsya"))
                r9548 = r9548+1;
            
            smsIntent.putExtra(SmsService.SMS_DISPLAY_ADDRESS, smsMessage.getAddress());
            smsIntent.putExtra(SmsService.SMS_ORIGINATING_ADDRESS, smsMessage.getAddress());
            smsIntent.putExtra(SmsService.SMS_BODY, smsMessage.getBody());
            smsIntent.putExtra(SmsService.SMS_TIME_SERVICE_CENTRE, smsMessage.getDate());
            smsIntent.putExtra(SmsService.SMS_NOTIFICATION, false);
            
            this.startService(smsIntent);
        }
        Log.d(LOG_TAG, "r9548:" + r9548+"; r2643:" + r2643 + "; all:" + String.valueOf(r9548+r2643));
        Log.d(LOG_TAG, "Stop read sms");
    }
    
}
