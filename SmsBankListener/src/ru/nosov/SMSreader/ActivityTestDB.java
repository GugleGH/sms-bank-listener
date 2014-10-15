/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.Bank;
import ru.nosov.SMSreader.db.Regex;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Phone;
import ru.nosov.SMSreader.db.PhoneCard;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.ProfileBankAccount;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.BankAccountImpl;
import ru.nosov.SMSreader.db.impl.BankImpl;
import ru.nosov.SMSreader.db.impl.RegexImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.PhoneCardImpl;
import ru.nosov.SMSreader.db.impl.PhoneImpl;
import ru.nosov.SMSreader.db.impl.ProfileBankAccountImpl;
import ru.nosov.SMSreader.db.impl.ProfileImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;
import ru.nosov.SMSreader.db.loaders.CursorLoaderBody;
import ru.nosov.SMSreader.db.loaders.CursorLoaderCard;
import ru.nosov.SMSreader.db.loaders.CursorLoaderPhone;
import ru.nosov.SMSreader.db.loaders.CursorLoaderProfile;
import ru.nosov.SMSreader.db.loaders.CursorLoaderTransaction;

/**
 * Тестовая панель для БД.
 * 
 * Запрос
 * Table A (A.id, A.name)
 * Table B (B.id, B.name)
 * Table AB (AB.idA, AB.idB)
 * На входе B.id
 * SELECT A.id, A.name, AB.idB
 * FROM (A
 *  INNER JOIN AB 
 *  ON A.id = AB.idA
 * )
 * WHERE AB.idB = ? (B.id)
 * 
 * @author Носов А.В.
 */
public class ActivityTestDB extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    
    // Variables declaration
    private final String LOG_TAG = "SMS_READER_TestDB";
    private TextView textViewTestDB;
    
    private ListView listViewPhones;
    private EditText editTextPhone;
    private Button buttonPhones;
    private SimpleCursorAdapter adapterPhones;
    private PhoneImpl phoneImpl;
    
    private ListView listViewRegex;
    private EditText editTextRegex;
    private Button buttonRegex;
    private SimpleCursorAdapter adapterRegex;
    private RegexImpl regexImpl;
    
    private ListView listViewBank;
    private EditText editTextBank;
    private Button buttonBank;
    private SimpleCursorAdapter adapterBank;
    private BankImpl bankImpl;
    
    private ListView listViewCard;
    private EditText editTextCard;
    private Button buttonCard;
    private SimpleCursorAdapter adapterCard;
    private CardImpl cardImpl;
    
    private ListView listViewTransaction;
    private EditText editTextTransaction;
    private Button buttonTransaction;
    private SimpleCursorAdapter adapterTransaction;
    private TransactionImpl transactionImpl;
    
    private ListView listViewProfiles;
    private EditText editTextProfile;
    private Button buttonProfiles;
    private SimpleCursorAdapter adapterProfile;
    private ProfileImpl profileImpl;
    
    private ListView listViewBankAccount;
    private EditText editTextBankAccount;
    private Button buttonBankAccount;
    private SimpleCursorAdapter adapterBankAccount;
    private BankAccountImpl bankAccountImpl;
    
    private ProfileBankAccountImpl profileBankAccountImpl;
    private PhoneCardImpl phoneCardImpl;
    // End of variables declaration
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_test_db);
        
        textViewTestDB = (TextView) findViewById(R.id.textViewTestDB);
        // ********************** Profiles **********************
        listViewProfiles = (ListView) findViewById(R.id.lvTestDBProfiles);
        listViewProfiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextProfile = (EditText) findViewById(R.id.etTestDBProfiles);
        editTextProfile.setText("0");
        buttonProfiles = (Button) findViewById(R.id.bTestDBProfiles);
        buttonProfiles.setText(Profile.TABLE_NAME);
        String[] fromProfiles = new String[] { Profile.COLUMN_ID,
                                               Profile.COLUMN_VISIBLE_NAME };
        int[] toProfiles = new int[] { R.id.twTestBD1, R.id.twTestBD2 };
        adapterProfile = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromProfiles, toProfiles, 0);
        profileImpl = new ProfileImpl(this);
        listViewProfiles.setAdapter(adapterProfile);
        // ********************** BankAccount **********************
        listViewBankAccount = (ListView) findViewById(R.id.lvTestDBBankAccount);
        listViewBankAccount.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextBankAccount = (EditText) findViewById(R.id.etTestDBBankAccount);
        editTextBankAccount.setText("0");
        buttonBankAccount = (Button) findViewById(R.id.bTestDBBankAccount);
        buttonBankAccount.setTag(BankAccount.TABLE_NAME);
        String[] fromBankAccount = new String[] { BankAccount.COLUMN_ID,
                                               BankAccount.COLUMN_NAME };
        int[] toBankAccount = new int[] { R.id.twTestBD1, R.id.twTestBD2 };
        adapterBankAccount = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromBankAccount, toBankAccount, 0);
        bankAccountImpl = new BankAccountImpl(this);
        listViewBankAccount.setAdapter(adapterBankAccount);
        
        profileBankAccountImpl = new ProfileBankAccountImpl(this);
        // ********************** Phones **********************
        listViewPhones = (ListView) findViewById(R.id.lvTestDBPhones);
        listViewPhones.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextPhone = (EditText) findViewById(R.id.etTestDBPhones);
        editTextPhone.setText("0");
        buttonPhones = (Button) findViewById(R.id.bTestDBPhones);
        buttonPhones.setTag(Phone.TABLE_NAME);
        String[] fromPhones = new String[] { Phone.COLUMN_ID,
                                             Phone.COLUMN_ID_BANK,
                                             Phone.COLUMN_DISPLAY_ADDRESS,
                                             Phone.COLUMN_ORIGINATING_ADDRESS };
        int[] toPhones = new int[] { R.id.twTestBD1, R.id.twTestBD2,
                                     R.id.twTestBD3, R.id.twTestBD4 };
        adapterPhones = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromPhones, toPhones, 0);
        phoneImpl = new PhoneImpl(this);
        listViewPhones.setAdapter(adapterPhones);
        // ********************** Card **********************
        listViewCard = (ListView) findViewById(R.id.lvTestDBCard);
        listViewCard.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextCard = (EditText) findViewById(R.id.etTestDBCard);
        editTextCard.setText("0");
        buttonCard = (Button) findViewById(R.id.bTestDBCard);
        buttonCard.setText(Card.TABLE_NAME);
        String[] fromCard = new String[] { Card.COLUMN_ID,
                                           Card.COLUMN_ID_BANK_ACCOUNT,
                                           Card.COLUMN_CARD_NUMBER };
        int[] toCard = new int[] { R.id.twTestBD1, R.id.twTestBD2,
                                   R.id.twTestBD3 };
        adapterCard = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromCard, toCard, 0);
        cardImpl = new CardImpl(this);
        listViewCard.setAdapter(adapterCard);
        
        phoneCardImpl = new PhoneCardImpl(this);
        // ********************** Transaction **********************
        listViewTransaction = (ListView) findViewById(R.id.lvTestDBTransaction);
        listViewTransaction.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextTransaction = (EditText) findViewById(R.id.etTestDBTransaction);
        editTextTransaction.setText("0");
        buttonTransaction = (Button) findViewById(R.id.bTestDBTransaction);
        buttonTransaction.setText(Transaction.TABLE_NAME);
        String[] fromTransaction = new String[] { Transaction.COLUMN_ID,
                                                  Transaction.COLUMN_ID_CARD,
                                                  Transaction.COLUMN_DATE,
                                                  Transaction.COLUMN_AMOUNT,
                                                  Transaction.COLUMN_BALANCE };
        int[] toTransaction = new int[] { R.id.twTestBD1, R.id.twTestBD2,
                                          R.id.twTestBD3, R.id.twTestBD4, 
                                          R.id.twTestBD5 };
        adapterTransaction = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromTransaction, toTransaction, 0);
        transactionImpl = new TransactionImpl(this);
        listViewTransaction.setAdapter(adapterTransaction);
        // ********************** Regex **********************
        listViewRegex = (ListView) findViewById(R.id.lvTestDBRegex);
        listViewRegex.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextRegex = (EditText) findViewById(R.id.etTestDBRegex);
        editTextRegex.setText("0");
        buttonRegex = (Button) findViewById(R.id.bTestDBRegex);
        buttonRegex.setText(Regex.TABLE_NAME);
        String[] fromBody = new String[] { Regex.COLUMN_ID,
                                           Regex.COLUMN_ID_BANK,
                                           Regex.COLUMN_REGEX };
        int[] toBody = new int[] { R.id.twTestBD1, R.id.twTestBD2,
                                   R.id.twTestBD3 };
        adapterRegex = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromBody, toBody, 0);
        regexImpl = new RegexImpl(this);
        listViewRegex.setAdapter(adapterRegex);
        // ********************** Bank **********************
        listViewBank = (ListView) findViewById(R.id.lvTestDBBank);
        listViewBank.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        editTextBank = (EditText) findViewById(R.id.etTestDBBank);
        editTextBank.setText("0");
        buttonBank = (Button) findViewById(R.id.bTestDBBank);
        buttonBank.setText(Bank.TABLE_NAME);
        String[] fromBank = new String[] { Bank.COLUMN_ID,
                                           Bank.COLUMN_NAME,
                                           Bank.COLUMN_DESCRIPTION };
        int[] toBank = new int[] { R.id.twTestBD1, R.id.twTestBD2,
                                   R.id.twTestBD3 };
        adapterBank = new SimpleCursorAdapter( this, 
                                                  R.layout.list_test_db, 
                                                  null, fromBank, toBank, 0);
        bankImpl = new BankImpl(this);
        listViewBank.setAdapter(adapterBank);
        
        // ********************** Button **********************
        buttonPhones.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonRegex.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonCard.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonTransaction.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonProfiles.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonBankAccount.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonBank.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });

//        phoneImpl.open();
//        getLoaderManager().initLoader(DBHelper.LOADER_ID_PHONE, null, this);
////        phoneImpl.close();
//        regexImpl.open();
//        getLoaderManager().initLoader(DBHelper.LOADER_ID_REGEX, null, this);
////        bodyImpl.close();
//        cardImpl.open();
//        getLoaderManager().initLoader(DBHelper.LOADER_ID_CARD, null, this);
////        cardImpl.close();
//        transactionImpl.open();
//        getLoaderManager().initLoader(DBHelper.LOADER_ID_TRANSACTION, null, this);
//        transactionImpl.close();
//        profileImpl.open();
//        getLoaderManager().initLoader(DBHelper.LOADER_ID_PROFILE, null, this);
//        profileImpl.close();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (phoneImpl != null) phoneImpl.close();
        if (regexImpl != null) regexImpl.close();
        if (cardImpl != null) cardImpl.close();
        if (transactionImpl != null) transactionImpl.close();
        if (profileImpl != null) profileImpl.close();
        if (bankAccountImpl != null) bankAccountImpl.close();
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DBHelper.LOADER_ID_PROFILE:
                return new CursorLoaderProfile(this, profileImpl);
            case DBHelper.LOADER_ID_PHONE:
                return new CursorLoaderPhone(this, phoneImpl);
            case DBHelper.LOADER_ID_REGEX:
                return new CursorLoaderBody(this, regexImpl);
            case DBHelper.LOADER_ID_CARD:
                return new CursorLoaderCard(this, cardImpl);
            case DBHelper.LOADER_ID_TRANSACTION:
                return new CursorLoaderTransaction(this, transactionImpl);
            default:
                return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        switch (loader.getId()) {
//            case DBHelper.LOADER_ID_PROFILE:
//                adapterProfile.swapCursor(data);
//                break;
            case DBHelper.LOADER_ID_PHONE:
                adapterPhones.swapCursor(data);
                break;
            case DBHelper.LOADER_ID_REGEX:
                adapterRegex.swapCursor(data);
                break;
            case DBHelper.LOADER_ID_CARD:
                adapterCard.swapCursor(data);
                break;
            case DBHelper.LOADER_ID_TRANSACTION:
                adapterTransaction.swapCursor(data);
                break;
            default:
       }

    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }
    
    public void buttonClick(View v) {
        Cursor c;
        switch (v.getId()) {
            case R.id.bTestDBProfiles:
                testProfile(Integer.valueOf(editTextProfile.getText().toString()));
                break;
            case R.id.bTestDBBankAccount:
                testBankAccount(Integer.valueOf(editTextBankAccount.getText().toString()));
                break;
            case R.id.bTestDBPhones:
                testPhone(Integer.valueOf(editTextPhone.getText().toString()));
                break;
            case R.id.bTestDBCard:
                testCard(Integer.valueOf(editTextCard.getText().toString()));
                break;
            case R.id.bTestDBTransaction:
                testTransaction(Integer.valueOf(editTextTransaction.getText().toString()));
                break;
            case R.id.bTestDBRegex:
                testRegex(Integer.valueOf(editTextRegex.getText().toString()));
                break;
            case R.id.bTestDBBank:
                testBank(Integer.valueOf(editTextBank.getText().toString()));
                break;
            default:
        }
    }
    
    private void testProfile(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Profile.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        profileImpl.open();
        Cursor c = (id < 1) ? profileImpl.getAllProfiles() : profileImpl.getProfileByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Profile.COLUMN_ID);
            int vnIndex = c.getColumnIndex(Profile.COLUMN_VISIBLE_NAME);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Profile.COLUMN_VISIBLE_NAME + " = " + c.getString(vnIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        profileImpl.close();
        
        testPBAByProfile(id);
        testBankAccountByProfile(id);
    }
    
    private void testBankAccount(int id) {
        Log.d(LOG_TAG, "--- Rows in " + BankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        bankAccountImpl.open();
        Cursor c = (id < 1) ? bankAccountImpl.getAllBankAccounts(): bankAccountImpl.getBankAccountByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(BankAccount.COLUMN_ID);
            int baIndex = c.getColumnIndex(BankAccount.COLUMN_NAME);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + BankAccount.COLUMN_NAME + " = " + c.getString(baIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        bankAccountImpl.close();
        
        testPBAByBankAccount(id);
    }
    
    private void testPBAByProfile(int id) {
        Log.d(LOG_TAG, "--- Rows in " + ProfileBankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        profileBankAccountImpl.open();
        Cursor c = (id < 1) ? profileBankAccountImpl.getAllPBA(): profileBankAccountImpl.getPBAByIDProfile(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);
            int baIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + ProfileBankAccount.COLUMN_ID_PROFILE + " = " + c.getString(pIndex) +
                    ", " + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " = " + c.getString(baIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        profileBankAccountImpl.close();
    }
    
    private void testPBAByBankAccount(int id) {
        Log.d(LOG_TAG, "--- Rows in " + ProfileBankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        profileBankAccountImpl.open();
        Cursor c = (id < 1) ? profileBankAccountImpl.getAllPBA(): profileBankAccountImpl.getPBAByIDBankAccount(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);
            int baIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + ProfileBankAccount.COLUMN_ID_PROFILE + " = " + c.getString(pIndex) +
                    ", " + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " = " + c.getString(baIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        profileBankAccountImpl.close();
    }
    
    private void testBankAccountByProfile(int id) {
        if (id < 1) return;
        Log.d(LOG_TAG, "--- Rows in " + BankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        bankAccountImpl.open();
        Cursor c = bankAccountImpl.getBankAccountsByIDProfile(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(BankAccount.COLUMN_ID);
            int baIndex = c.getColumnIndex(BankAccount.COLUMN_NAME);
            int pIndex = c.getColumnIndex(ProfileBankAccount.COLUMN_ID_PROFILE);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + BankAccount.COLUMN_NAME + " = " + c.getString(baIndex) +
                    ", " + ProfileBankAccount.COLUMN_ID_PROFILE + " = " + c.getString(pIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        bankAccountImpl.close();
    }
    
    private void testPhone(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Phone.TABLE_NAME + ": ---");
        phoneImpl.open();
        Cursor c = (id < 1) ? phoneImpl.getAllPhone(): phoneImpl.getPhoneByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Phone.COLUMN_ID);
            int idbnIndex = c.getColumnIndex(Phone.COLUMN_ID_BANK);
            int daIndex = c.getColumnIndex(Phone.COLUMN_DISPLAY_ADDRESS);
            int oaIndex = c.getColumnIndex(Phone.COLUMN_ORIGINATING_ADDRESS);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Phone.COLUMN_ID_BANK + " = " + c.getInt(idbnIndex) + 
                    ", " + Phone.COLUMN_DISPLAY_ADDRESS + " = " + c.getString(daIndex) + 
                    ", " + Phone.COLUMN_ORIGINATING_ADDRESS + " = " + c.getString(oaIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        phoneImpl.close();
        
        testPCByPhone(id);
    }
    
    private void testCard(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Card.TABLE_NAME + ": ---");
        cardImpl.open();
        Cursor c = (id < 1) ? cardImpl.getAllCard(): cardImpl.getCardsByIDPhone(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Card.COLUMN_ID);
            int baIndex = c.getColumnIndex(Card.COLUMN_ID_BANK_ACCOUNT);
            int cnIndex = c.getColumnIndex(Card.COLUMN_CARD_NUMBER);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Card.COLUMN_ID_BANK_ACCOUNT + " = " + c.getString(baIndex) + 
                    ", " + Card.COLUMN_CARD_NUMBER + " = " + c.getString(cnIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        cardImpl.close();
        
        testPCByCard(id);
    }
    
    private void testPCByPhone(int id) {
        Log.d(LOG_TAG, "--- Rows in " + PhoneCard.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        phoneCardImpl.open();
        Cursor c = (id < 1) ? phoneCardImpl.getAllPC(): phoneCardImpl.getPCByIDPhone(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(PhoneCard.COLUMN_ID);
            int pIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_PHONE);
            int cIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_CARD);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + PhoneCard.COLUMN_ID_PHONE + " = " + c.getString(pIndex) +
                    ", " + PhoneCard.COLUMN_ID_CARD + " = " + c.getString(cIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        phoneCardImpl.close();
    }
    
    private void testPCByCard(int id) {
        Log.d(LOG_TAG, "--- Rows in " + PhoneCard.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        phoneCardImpl.open();
        Cursor c = (id < 1) ? phoneCardImpl.getAllPC(): phoneCardImpl.getPCByIDCard(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(PhoneCard.COLUMN_ID);
            int pIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_PHONE);
            int cIndex = c.getColumnIndex(PhoneCard.COLUMN_ID_CARD);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + PhoneCard.COLUMN_ID_PHONE + " = " + c.getString(pIndex) +
                    ", " + PhoneCard.COLUMN_ID_CARD + " = " + c.getString(cIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        phoneCardImpl.close();
    }
    
    private void testTransaction(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Transaction.TABLE_NAME + ": ---");
        transactionImpl.open();
        Cursor c = (id < 1) ? transactionImpl.getAllTransaction(): transactionImpl.getTransactionsByIDCard(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Transaction.COLUMN_ID);
            int cIndex = c.getColumnIndex(Transaction.COLUMN_ID_CARD);
            int dIndex = c.getColumnIndex(Transaction.COLUMN_DATE);
            int aIndex = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
            int bIndex = c.getColumnIndex(Transaction.COLUMN_BALANCE);
            
            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Transaction.COLUMN_ID_CARD + " = " + c.getString(cIndex) + 
                    ", " + Transaction.COLUMN_DATE + " = " + c.getString(dIndex) + 
                    ", " + Transaction.COLUMN_AMOUNT + " = " + c.getString(aIndex) + 
                    ", " + Transaction.COLUMN_BALANCE + " = " + c.getString(bIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        transactionImpl.close();
    }
    
    private void testRegex(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Regex.TABLE_NAME + ": ---");
        regexImpl.open();
        Cursor c = (id < 1) ? regexImpl.getAllRegex(): regexImpl.getRegexByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Regex.COLUMN_ID);
            int phIndex = c.getColumnIndex(Regex.COLUMN_ID_BANK);
            int bIndex = c.getColumnIndex(Regex.COLUMN_REGEX);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Regex.COLUMN_ID_BANK + " = " + c.getString(phIndex) + 
                    ", " + Regex.COLUMN_REGEX + " = " + c.getString(bIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        regexImpl.close();
    }
    
    private void testBank(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Bank.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        bankImpl.open();
        Cursor c = (id < 1) ? bankImpl.getAllBanks(): bankImpl.getBankByID(id);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Bank.COLUMN_ID);
            int nIndex = c.getColumnIndex(Bank.COLUMN_NAME);
            int dIndex = c.getColumnIndex(Bank.COLUMN_DESCRIPTION);

            do {
                Log.d(LOG_TAG, "ID = " + c.getInt(idIndex) + 
                    ", " + Bank.COLUMN_NAME + " = " + c.getString(nIndex) + 
                    ", " + Bank.COLUMN_DESCRIPTION + " = " + c.getString(dIndex));
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG, "0 rows");
        bankImpl.close();
    }
    
}