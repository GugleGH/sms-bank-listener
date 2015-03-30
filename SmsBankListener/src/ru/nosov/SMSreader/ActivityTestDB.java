/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
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
import ru.nosov.SMSreader.services.SmsService;

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
    private final String LOG_TAG = LOG_NAME + "TestDB";
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
    
    private Button buttonAdd;
    private Button buttonReadSms;
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
        buttonAdd = (Button) findViewById(R.id.bAddTestData);
        buttonReadSms = (Button) findViewById(R.id.bReadSms);
        buttonAdd.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonReadSms.setOnClickListener( new View.OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
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
            case R.id.bAddTestData:
                addData();
                break;
            case R.id.bReadSms:
                readSms("Bank");
                readSms("15555215556");
                break;
            default:
        }
    }
    
    private void testProfile(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Profile.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        if (id < 1) profiles = profileImpl.getAllProfiles();
        else profiles.add(profileImpl.getProfileByID(id));
        
        for (Profile p : profiles) {
                Log.d(LOG_TAG, "ID = " + p.getId() + 
                    ", " + Profile.COLUMN_VISIBLE_NAME + " = " + p.getVisibleName());
        }
        
        if (profiles.isEmpty()) Log.d(LOG_TAG, "0 rows");
        
        testPBAByProfile(id);
        testBankAccountByProfile(id);
    }
    
    private void testBankAccount(int id) {
        Log.d(LOG_TAG, "--- Rows in " + BankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        if (id < 1) bankAccounts = bankAccountImpl.getAllBankAccounts();
        else bankAccounts.add(bankAccountImpl.getBankAccountByID(id));
        
        for (BankAccount ba : bankAccounts) {
                Log.d(LOG_TAG, "ID = " + ba.getId() + 
                    ", " + BankAccount.COLUMN_NAME + " = " + ba.getName());
        }
        
        if (bankAccounts.isEmpty()) Log.d(LOG_TAG, "0 rows");
        
        testPBAByBankAccount(id);
    }
    
    private void testPBAByProfile(int id) {
        Log.d(LOG_TAG, "--- Rows in " + ProfileBankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<ProfileBankAccount> profileBankAccounts = null;
        if (id < 1) profileBankAccounts = profileBankAccountImpl.getAllPBA();
        else profileBankAccounts = profileBankAccountImpl.getPBAByIDProfile(id);
        
        for (ProfileBankAccount bankAccount : profileBankAccounts) {
                Log.d(LOG_TAG, "ID = " + bankAccount.getId() + 
                    ", " + ProfileBankAccount.COLUMN_ID_PROFILE + " = " + bankAccount.getIdProfile() +
                    ", " + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " = " + bankAccount.getIdBankAccount());
        }
        
        if (profileBankAccounts.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testPBAByBankAccount(int id) {
        Log.d(LOG_TAG, "--- Rows in " + ProfileBankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<ProfileBankAccount> profileBankAccounts = null;
        if (id < 1) profileBankAccounts = profileBankAccountImpl.getAllPBA();
        else profileBankAccounts = profileBankAccountImpl.getPBAByIDBankAccount(id);
        
        for (ProfileBankAccount bankAccount : profileBankAccounts) {
                Log.d(LOG_TAG, "ID = " + bankAccount.getId() + 
                    ", " + ProfileBankAccount.COLUMN_ID_PROFILE + " = " + bankAccount.getIdProfile() +
                    ", " + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " = " + bankAccount.getIdBankAccount());
        }
        
        if (profileBankAccounts.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testBankAccountByProfile(int id) {
        if (id < 1) return;
        Log.d(LOG_TAG, "--- Rows in " + BankAccount.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        if (id < 1) bankAccounts = bankAccountImpl.getBankAccountsByIDProfile(id);
        
        for (BankAccount ba : bankAccounts) {
                Log.d(LOG_TAG, "ID = " + ba.getId() + 
                    ", " + BankAccount.COLUMN_NAME + " = " + ba.getName());
        }
        
        if (bankAccounts.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testPhone(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Phone.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<Phone> phones = new ArrayList<Phone>();
        if (id < 1) phones = phoneImpl.getAllPhone();
        else phones.add(phoneImpl.getPhoneByID(id));
        
        for (Phone ph : phones) {
                Log.d(LOG_TAG, "ID = " + ph.getId()+ 
                    ", " + Phone.COLUMN_ID_BANK + " = " + ph.getIdBank()+ 
                    ", " + Phone.COLUMN_DISPLAY_ADDRESS + " = " + ph.getDisplayAddress()+ 
                    ", " + Phone.COLUMN_ORIGINATING_ADDRESS + " = " + ph.getOriginatingAddress());
        }
        
        if (phones.isEmpty()) Log.d(LOG_TAG, "0 rows");
        
        testPCByPhone(id);
    }
    
    private void testCard(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Card.TABLE_NAME + ": ---");
        
        ArrayList<Card> cards;
        if (id < 1) cards = cardImpl.getAllCard();
        else cards = cardImpl.getCardsByIDPhone(id);
        
        for (Card c : cards) {
                Log.d(LOG_TAG, "ID = " + c.getId() + 
                    ", " + Card.COLUMN_ID_BANK_ACCOUNT + " = " + c.getIdBankAccount() + 
                    ", " + Card.COLUMN_CARD_NUMBER + " = " + c.getCardNumber());
        }
        
        if (cards.isEmpty()) Log.d(LOG_TAG, "0 rows");
        
        testPCByCard(id);
    }
    
    private void testPCByPhone(int id) {
        Log.d(LOG_TAG, "--- Rows in " + PhoneCard.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<PhoneCard> phoneCards;
        if (id < 1) phoneCards = phoneCardImpl.getAllPC();
        else phoneCards = phoneCardImpl.getPCByIDPhone(id);
        
        for (PhoneCard pc : phoneCards) {
                Log.d(LOG_TAG, "ID = " + pc.getId() + 
                    ", " + PhoneCard.COLUMN_ID_PHONE + " = " + pc.getIdPhone() +
                    ", " + PhoneCard.COLUMN_ID_CARD + " = " + pc.getIdCard());
        }
        
        if (id < 0) return;
        
        ArrayList<Card> cards = cardImpl.getCardsByIDPhone(id);
        for (Card c : cards) {
            Log.d(LOG_TAG, "ID = " + c.getId() +
                ", " + Card.COLUMN_ID_BANK_ACCOUNT + " = " + c.getIdBankAccount() +
                ", " + Card.COLUMN_CARD_NUMBER + " = " + c.getCardNumber());
        }
    }
    
    private void testPCByCard(int id) {
        Log.d(LOG_TAG, "--- Rows in " + PhoneCard.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<PhoneCard> phoneCards;
        if (id < 0) phoneCards = phoneCardImpl.getAllPC();
        else phoneCards = phoneCardImpl.getPCByIDCard(id);
        
        for (PhoneCard phoneCard : phoneCards) {
                Log.d(LOG_TAG, "ID = " + phoneCard.getId() + 
                    ", " + PhoneCard.COLUMN_ID_PHONE + " = " + phoneCard.getIdPhone() + 
                    ", " + PhoneCard.COLUMN_ID_CARD + " = " + phoneCard.getIdCard());
        }
        
        if (phoneCards.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testTransaction(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Transaction.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<Transaction> transactions;
        if (id < 1) transactions = transactionImpl.getAllTransaction();
        else transactions = transactionImpl.getTransactionsByIDCard(id);
        
        for (Transaction t : transactions) {
                Log.d(LOG_TAG, "ID = " + t.getId()+ 
                    ", " + Transaction.COLUMN_ID_CARD + " = " + t.getIdCard()+ 
                    ", " + Transaction.COLUMN_DATE + " = " + t.getDateSQL()+ 
                    ", " + Transaction.COLUMN_AMOUNT + " = " + t.getAmount()+ 
                    ", " + Transaction.COLUMN_BALANCE + " = " + t.getBalace());
        }
        
        if (transactions.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testRegex(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Regex.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<Regex> regexs = new ArrayList<Regex>();
        if (id < 1) regexs = regexImpl.getAllRegex();
        else regexs.add(regexImpl.getRegexByID(id));
        
        for (Regex r : regexs) {
            Log.d(LOG_TAG, "ID = " + r.getId()+ 
                ", " + Regex.COLUMN_ID_BANK + " = " + r.getIdBank()+ 
                ", " + Regex.COLUMN_REGEX + " = " + r.getRegex());
        }
        
        if (regexs.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void testBank(int id) {
        Log.d(LOG_TAG, "--- Rows in " + Bank.TABLE_NAME + ": ---");
        Log.d(LOG_TAG, "--- ID=" + id + " ---");
        
        ArrayList<Bank> banks = new ArrayList<Bank>();
        if (id < 1) banks = bankImpl.getAllBanks();
        else banks.add(bankImpl.getBankByID(id));
        
        for (Bank b : banks) {
            Log.d(LOG_TAG, "ID = " + b.getId()+ 
                ", " + Bank.COLUMN_NAME + " = " + b.getName()+ 
                ", " + Bank.COLUMN_DESCRIPTION + " = " + b.getDescription());
        }
        
        if (banks.isEmpty()) Log.d(LOG_TAG, "0 rows");
    }
    
    private void addData() {
        String[] sms_from = {
/*1*/       "Raiffeisen",
/*2*/       "Raiffeisen",
/*3*/       "Raiffeisen",
/*4*/       "Raiffeisen",
/*5*/       "Raiffeisen",
/*6*/       "Raiffeisen",
/*7*/       "Raiffeisen",
/*8*/       "Raiffeisen",
/*9*/       "Raiffeisen",
/*10*/      "Bank",
/*11*/      "Bank",
/*12*/      "Bank",
/*13*/      "Bank",
/*14*/      "Bank",
/*15*/      "Bank",
/*16*/      "Bank",
/*17*/      "Bank",
/*18*/      "Bank",
/*19*/      "Raiffeisen",
/*20*/      "Raiffeisen",
/*21*/      "Raiffeisen"
        };
        String[] sms_number = {
/*1*/       "Raiffeisen",
/*2*/       "Raiffeisen",
/*3*/       "Raiffeisen",
/*4*/       "Raiffeisen",
/*5*/       "Raiffeisen",
/*6*/       "Raiffeisen",
/*7*/       "Raiffeisen",
/*8*/       "Raiffeisen",
/*9*/       "Raiffeisen",
/*10*/      "Bank",
/*11*/      "Bank",
/*12*/      "Bank",
/*13*/      "Bank",
/*14*/      "Bank",
/*15*/      "Bank",
/*16*/      "Bank",
/*17*/      "Bank",
/*18*/      "Bank",
/*19*/      "Raiffeisen",
/*20*/      "Raiffeisen",
/*21*/      "Raiffeisen"
        };
        String[] sms_body = {
/*1*/       "Karta *2643; Provedena tranzakcija:RU/BALASHIKHA/DIKSI; Summa:396.70 RUR Data:05/09/2014; Dostupny Ostatok: 135983.86 RUR. Raiffeisenbank", 
/*2*/       "Karta *9548; Provedena tranzakcija:LU/ITUNES.COM/ITUNES.COM/BILL; Summa:169.00 RUR Data:11/09/2014; Dostupny Ostatok: 98304.54 RUR. Raiffeisenbank", 
/*3-*/       "Planovoe spisanie:35630.32 RUR. Balans scheta karty *9548 na 13/09/2014: 98304.54 RUR. Raiffeisenbank",
/*4*/       "Povedeno po schety 20/10/2014: - 35630.32 RUR. Balans scheta karty *9548 na 21/09/2014: 96933.15 RUR. Raiffeisenbank",
/*5*/       "Karta *2643; Provedena tranzakcija:RU/BALASHIKHA/DIKSI; Summa:396.70 RUR Data:10/10/2014; Dostupny Ostatok: 135983.86 RUR. Raiffeisenbank", 
/*6*/       "Karta *9548; Provedena tranzakcija:LU/ITUNES.COM/ITUNES.COM/BILL; Summa:169.00 RUR Data:15/10/2014; Dostupny Ostatok: 98304.54 RUR. Raiffeisenbank", 
/*7-*/       "Planovoe spisanie:35630.32 RUR. Balans scheta karty *9548 na 17/10/2014: 98304.54 RUR. Raiffeisenbank",
/*8*/       "Karta *2643; Provedena tranzakcija:RU/BALASHIKHA/PODRUZHKA 166; Summa:1371.39 RUR Data:18/10/2014; Dostupny Ostatok: 96933.15 RUR. Raiffeisenbank", 
/*9*/       "Povedeno po schety 20/10/2014: - 35630.32 RUR. Balans scheta karty *9548 na 20/10/2014: 96311.07 RUR. Raiffeisenbank",
/*10*/      "01.08.14 12:39:14 KAPTA 4860*6650 POPOLNENIE +   14871,39 RUR   / DOSTUPNO 127186,1 RUR",
/*11*/      "09.08.14 15:10:07 KAPTA 4860*6650 NALICHNYE   80000 RUR ZENIT ATM 45 MOSCOW RUS / DOSTUPNO 47186,1 RUR",
/*12*/      "13.08.14 15:10:07 KAPTA 4860*6650 NALICHNYE   80000 RUR ZENIT ATM 45 MOSCOW RUS / DOSTUPNO 30186,1 RUR",
/*13*/      "20.08.14 12:26:00 KAPTA 4860*6650 POPOLNENIE +   32184 RUR   / DOSTUPNO 79370,1 RUR",
/*14*/      "01.09.14 13:07:38 KAPTA 4860*6650 POPOLNENIE +   104276,47 RUR   / DOSTUPNO 183646,57 RUR",
/*15*/      "05.09.14 15:02:56 KAPTA 4860*6650 NALICHNYE   80000 RUR ZENIT ATM 192 MOSCOW RUS / DOSTUPNO 103646,57 RUR",
/*16*/      "01.10.14 14:36:41 KAPTA 4860*6650 POPOLNENIE +   23577,91 RUR   / DOSTUPNO 127224,48 RUR",
/*17*/      "19.10.14 15:05:48 KAPTA 4860*6650 NALICHNYE   80000 RUR ZENIT ATM 192 MOSCOW RUS / DOSTUPNO 47224,48 RUR",
/*18*/      "07.11.14 15:05:48 KAPTA 4860*6650 NALICHNYE   80000 RUR ZENIT ATM 192 MOSCOW RUS / DOSTUPNO 42224,48 RUR",
/*19*/      "Karta *2643; Provedena tranzakcija:RU/BALASHIKHA/DIKSI; Summa:622.08 RUR Data:16/10/2014; Dostupny Ostatok: 96933.15 RUR. Raiffeisenbank",
/*20*/      "Balans vashey karty *9548 popolnilsya 12/11/2014 na 80000.00 RUR. Dostupny ostatok: 135535.76 RUR. Raiffeisenbank",
/*21*/      "Karta *2643; Pokupka: RU/BALASHIKHA/DIKSI; 537.47 RUR; Data: 23/11/2014; Dostupny Ostatok: 94636.23 RUR. Raiffeisenbank"
        };
        String[] time = {
/*1*/       "2014/09/05 15:12:56",
/*2*/       "2014/09/09 07:49:39",
/*3-*/       "2014/09/13 09:06:32",
/*4*/       "2014/09/21 14:38:24",
/*5*/       "2014/10/10 15:12:56",
/*6*/       "2014/10/15 07:49:39",
/*7-*/       "2014/10/17 09:06:32",
/*8*/       "2014/10/18 14:38:24",
/*9*/       "2014/10/20 09:08:07",
/*10*/      "2014/08/01 12:40:52",
/*11*/      "2014/08/09 03:10:31",
/*12*/      "2014/08/13 03:10:31",
/*13*/      "2014/08/20 12:28:55",
/*14*/      "2014/09/01 01:38:54",
/*15*/      "2014/09/05 03:03:27",
/*16*/      "2014/10/01 03:21:41",
/*17*/      "2014/10/19 03:06:15",
/*18*/      "2014/11/07 03:06:15",
/*19*/      "2014/10/16 16:09:32",
/*20*/      "2014/11/12 21:14:35",
/*21*/      "2014/11/23 13:04:10"
        };
        
        for (int i=0; i<sms_from.length; i++) {
            try {
                Intent smsIntent = new Intent(this, SmsService.class);

                smsIntent.putExtra(SmsService.SMS_DISPLAY_ADDRESS, sms_from[i]);
                smsIntent.putExtra(SmsService.SMS_ORIGINATING_ADDRESS, sms_number[i]);
                smsIntent.putExtra(SmsService.SMS_BODY, sms_body[i]);
                Date d = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(time[i]);
                long t = d.getTime();
                smsIntent.putExtra(SmsService.SMS_TIME_SERVICE_CENTRE, t);

                this.startService(smsIntent);
            } catch (ParseException ex) {
            }
        }
    }
    
    private void readSms(String address) {
        Log.d(LOG_TAG, "Start read sms");
        Uri uri = Uri.parse("content://sms/inbox");
        String[] fields = new String[] { "_id", 
                                         "address", 
                                         "person", 
                                         "body", 
                                         "date"
                                       };
        
        Cursor c = getContentResolver().query(  uri, 
                                                fields, 
                                                "address=?",
                                                new String[] { address },
                                                "date desc");
        Log.d(LOG_TAG, "Cursor count:" + c.getCount());
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(fields[0]);
            int aIndex = c.getColumnIndex(fields[1]);
            int pIndex = c.getColumnIndex(fields[2]);
            int bIndex = c.getColumnIndex(fields[3]);
            int dIndex = c.getColumnIndex(fields[4]);
            
            do {
                Log.d(LOG_TAG, "id:"+c.getInt(idIndex)
                        +"; address:"+c.getString(aIndex)
                        +"; person:"+c.getString(pIndex)
                        +"; body:"+c.getString(bIndex)
                        +"; date:"+c.getString(dIndex));
            } while (c.moveToNext());
        }
        Log.d(LOG_TAG, "Stop read sms");
    }
}
