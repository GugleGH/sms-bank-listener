/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Calendar;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.types.TypeBank;
import ru.nosov.SMSreader.utils.Util;

/**
 * Доступ к БД.
 * @author Носов А.В.
 */
public class DBHelper extends SQLiteOpenHelper {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "DBHelper";
    /** Идентификатор загрузчика профилей. */
    public static final int LOADER_ID_PROFILE = 1;
    /** Идентификатор загрузчика номеров. */
    public static final int LOADER_ID_PHONE = 2;
    /** Идентификатор загрузчика регулярных выражений. */ 
    public static final int LOADER_ID_REGEX = 3;
    /** Идентификатор загрузчика карт. */
    public static final int LOADER_ID_CARD = 4;
    /** Идентификатор загрузчика транзакций. */
    public static final int LOADER_ID_TRANSACTION = 5;
    /** Идентификатор загрузчика названий банков. */
    public static final int LOADER_ID_BANK_NAME = 6;
    /** Идентификатор загрузчика банковских счетов. */
    public static final int LOADER_ID_BANK_ACCOUNT = 7;
    /** Имя БД. */
    public static final String DB_NAME = "dbSMSReader";
    /** Версия БД. */
    private static final int DB_VERSION = 3;
    
    /** Регулярное выражение пробела или начала строки. */
    private static String REGEX_SPASE = "(?:^|\\s)";
    /** Регулярное выражение любое колво любого символа. */
    //private static String REGEX_ANY = "(?:[-+0-9a-zA-Z.\\s\\*\\/\\:\\;]+|(?:$))";
    private static String REGEX_ANY = ".*";
    /** Регулярное выражение ДД.ММ.ГГ. */
    private static String REGEX_DDMMYY_POINT = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\.(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\.(?:\\d{2}))";
    /** Регулярное выражение ДД/ММ/ГГГГ. */
    private static String REGEX_DDMMYYYY_SLASH = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\/(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\/(?:\\d{4}))";
    //private static String REGEX_DDMMYYYY_BACKSLASH = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\.(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\.(?:\\d{2}))";
    /** Регулярное выражение ЧЧ:ММ:СС. */
    private static String REGEX_HHMMSS = "((?:(?:(?:[0,1][0-9])|(?:2[0-3]))|[0-9])\\:(?:(?:[0-5][0-9])|[0-9])\\:(?:(?:[0-5][0-9])|[0-9]))";
    /** Регулярное выражение номера карты формата *1234. */
    private static String REGEX_CARD_1 = "(\\*(?:\\d{4,4}))";
    /** Регулярное выражение номера карты формата 1234*1234. */
    private static String REGEX_CARD_2 = "((?:\\d{4,4})\\*(?:\\d{4,4}))";
    /** Регулярное выражение числа вида 1234.12 */
    private static String REGEX_MANY_TCHK = "((\\d+)|(?:\\d+(?:\\.\\d{0,2})))";
    /** Регулярное выражение числа вида 1234,12 */
    private static String REGEX_MANY_ZPT = "((\\d+)|(?:\\d+(?:\\,\\d{0,2})))";
    
    private static int idBankRaiffeisen = 1;
    private static int idBankTNB = 2;
    private static int idPhoneRaiffeisen = 1;
    private static int idPhoneTNB = 2;
    private static int idRegexRaiffeisen1 = 1;
    private static int idRegexRaiffeisen2 = 2;
    private static int idRegexRaiffeisen3 = 3;
    private static int idRegexTNB = 4;
    private static int idProfileAll = 1;
    private static int idProfileRaiffeisen = 2;
    private static int idProfileTNB = 3;
    private static int idBankAccountRaiffeisen = 1;
    private static int idBankAccountTNB = 2;
    private static int idPBA1 = 1;
    private static int idPBA2 = 2;
    private static int idPBA3 = 3;
    private static int idPBA4 = 4;
    private static int idCard9548 = 1;
    private static int idCard2643 = 2;
    private static int idCard4860_6650 = 3;
    private static int idPC1 = 1;
    private static int idPC2 = 2;
    private static int idPC3 = 3;
    // End of variables declaration
    
    public DBHelper(Context context, CursorFactory factory) {
        super(context, DB_NAME, factory, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // Данные поумолчанию
        createTableProfileBankAccount(db);
        createTablePhoneCard(db);
        createTableProfile(db);
        createTableBankAccount(db);
        createTableBankName(db);
        createTablePhone(db);
        createTableCard(db);
        createTableRegex(db);
        createTableRegexTransaction(db);
        createTableSettingsV2(db);
        Log.i(LOG_TAG, "Create All Table.");
        
        // Мои данные
        myProfile(db);
        myBankAccount(db);
        myProfileBankAccount(db);
        myCard(db);
        myPhoneCard(db);
        myTransaction(db);
        Log.i(LOG_TAG, "Create My Data.");
        
        // Тестовые данные
        testData(db);
        Log.i(LOG_TAG, "Create Test Data.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        while (oldVersion <= newVersion) {
            switch (oldVersion) {
                case 1:
                    upgradeDB1To2(db);
                    Log.i(LOG_TAG, "Обновление БД с версии 1 на версию 2 прошло успешно.");
                    break;
                case 2:
                    upgradeDB2To3(db);
                    Log.i(LOG_TAG, "Обновление БД с версии 2 на версию 3 прошло успешно.");
                    break;
                default:
            }
            oldVersion ++;
        }
    }
    
    private void createTableProfileBankAccount(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ProfileBankAccount.TABLE_NAME + " ( "
            + ProfileBankAccount.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + ProfileBankAccount.COLUMN_ID_PROFILE + " INTEGER NOT NULL, "
            + ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT + " INTEGER NOT NULL"+ " );");
    }
    
    private void createTablePhoneCard(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PhoneCard.TABLE_NAME + " ( "
            + PhoneCard.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + PhoneCard.COLUMN_ID_PHONE + " INTEGER NOT NULL, "
            + PhoneCard.COLUMN_ID_CARD + " INTEGER NOT NULL"+ " );");
    }
    
    private void createTableProfile(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Profile.TABLE_NAME + " ( "
            + Profile.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Profile.COLUMN_VISIBLE_NAME + " TEXT NOT NULL" + " );");
    }
    
    private void createTableBankAccount(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + BankAccount.TABLE_NAME + " ( "
            + BankAccount.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + BankAccount.COLUMN_NAME + " TEXT NOT NULL" + " );");
    }
    
    private void createTableBankName(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Bank.TABLE_NAME + " ( "
            + Bank.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Bank.COLUMN_NAME + " TEXT NOT NULL, "
            + Bank.COLUMN_DESCRIPTION + " TEXT NOT NULL" + " );");
        
        // Райффайзен
        ContentValues cv = new ContentValues();
        cv.put(Bank.COLUMN_ID, idBankRaiffeisen);
        cv.put(Bank.COLUMN_NAME, TypeBank.RAIFFEISEN.toString());
        cv.put(Bank.COLUMN_DESCRIPTION, TypeBank.RAIFFEISEN.getDescription());
        db.insert(Bank.TABLE_NAME, null, cv);
        
        // Транснациональный
        cv = new ContentValues();
        cv.put(Bank.COLUMN_ID, idBankTNB);
        cv.put(Bank.COLUMN_NAME, TypeBank.TNB.toString());
        cv.put(Bank.COLUMN_DESCRIPTION, TypeBank.TNB.getDescription());
        db.insert(Bank.TABLE_NAME, null, cv);
    }
    
    private void createTablePhone(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Phone.TABLE_NAME + " ( "
            + Phone.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Phone.COLUMN_ID_BANK + " INTEGER NOT NULL, "
            + Phone.COLUMN_DISPLAY_ADDRESS + " TEXT NOT NULL, "
            + Phone.COLUMN_ORIGINATING_ADDRESS + " TEXT NOT NULL" + " );");
        
        // Райффайзен
        ContentValues cv = new ContentValues();
        cv.put(Phone.COLUMN_ID, idPhoneRaiffeisen);
        cv.put(Phone.COLUMN_ID_BANK, idBankRaiffeisen);
        cv.put(Phone.COLUMN_DISPLAY_ADDRESS, "Raiffeisen");
        cv.put(Phone.COLUMN_ORIGINATING_ADDRESS, "Raiffeisen");
        db.insert(Phone.TABLE_NAME, null, cv);
        
        // Транснациональный
        cv = new ContentValues();
        cv.put(Phone.COLUMN_ID, idPhoneTNB);
        cv.put(Phone.COLUMN_ID_BANK, idBankTNB);
        cv.put(Phone.COLUMN_DISPLAY_ADDRESS, "Bank");
        cv.put(Phone.COLUMN_ORIGINATING_ADDRESS, "Bank");
        db.insert(Phone.TABLE_NAME, null, cv);
    }
    
    private void createTableCard(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Card.TABLE_NAME + " ( "
            + Card.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Card.COLUMN_ID_BANK_ACCOUNT + " INTEGER NOT NULL, "
            + Card.COLUMN_CARD_NUMBER + " TEXT NOT NULL" + " );");
    }
    
    private void createTableRegex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Regex.TABLE_NAME + " ( "
            + Regex.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Regex.COLUMN_ID_BANK + " integer NOT NULL, "
            + Regex.COLUMN_REGEX + " TEXT NOT NULL" + " );");
        
        // Райффайзен (оплата)
        String regex = REGEX_SPASE + "(?:Kart. " + REGEX_CARD_1 + ";)" + REGEX_ANY
                                     + "(?: Summa\\:" + REGEX_MANY_TCHK + " RUR)"
                                     + "(?: Data\\:" + REGEX_DDMMYYYY_SLASH + ";)"
                                     + "(?: Dostupny Ostatok\\: " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY;
        ContentValues cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexRaiffeisen1);
        cv.put(Regex.COLUMN_ID_BANK, idBankRaiffeisen);
        cv.put(Regex.COLUMN_REGEX, regex);
        db.insert(Regex.TABLE_NAME, null, cv);
        // Райффайзен (плановое списание - это напоминание)
        // Райффайзен (проведено по счету - снятие денег)
        regex = REGEX_ANY + "(?: " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY
                                     + "(?:kart. " + REGEX_CARD_1 + ")"
                                     + "(?: na " + REGEX_DDMMYYYY_SLASH + ":)"
                                     + "(?: " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY;
        
        cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexRaiffeisen2);
        cv.put(Regex.COLUMN_ID_BANK, idBankRaiffeisen);
        cv.put(Regex.COLUMN_REGEX, regex);
        db.insert(Regex.TABLE_NAME, null, cv);
        // Райффайзен (пополнение)
        regex = REGEX_ANY + REGEX_CARD_1 + REGEX_ANY
                                     + "(?: " + REGEX_DDMMYYYY_SLASH + " )"
                                     + REGEX_ANY
                                     + "(?:na " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY
                                     + "(?: Dostupny ostatok\\: " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY;
        cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexRaiffeisen3);
        cv.put(Regex.COLUMN_ID_BANK, idBankRaiffeisen);
        cv.put(Regex.COLUMN_REGEX, regex);
        db.insert(Regex.TABLE_NAME, null, cv);
        
        // Транснациональный
        regex = REGEX_SPASE + REGEX_DDMMYY_POINT + " " + REGEX_HHMMSS
                                      + "(?: KAPTA " + REGEX_CARD_2 + " )" + REGEX_ANY
                                      + "(?: " + REGEX_MANY_ZPT + " RUR)" + REGEX_ANY
                                      + "(?:DOSTUPNO " + REGEX_MANY_ZPT + " RUR)";
        cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexTNB);
        cv.put(Regex.COLUMN_ID_BANK, idBankTNB);
        cv.put(Regex.COLUMN_REGEX, regex);
        db.insert(Regex.TABLE_NAME, null, cv);
    }
    
    private void createTableRegexTransaction(SQLiteDatabase db) {
        // YYYY-MM-DD HH:MM:SS.SSS
        db.execSQL("CREATE TABLE " + Transaction.TABLE_NAME + " ( "
            + Transaction.COLUMN_ID + " INTEGER primary key autoincrement, " 
            + Transaction.COLUMN_ID_CARD + " INTEGER NOT NULL, "
            + Transaction.COLUMN_DATE + " TEXT NOT NULL, "
            + Transaction.COLUMN_AMOUNT + " REAL NOT NULL, "
            + Transaction.COLUMN_PAYMENT_AMOUNT + " REAL DEFAULT 0, "
            + Transaction.COLUMN_BALANCE + " REAL NOT NULL, " 
            + Transaction.COLUMN_DESCRIPTION + " TEXT" + " );");
    }
    
    private void createTableSettingsV2(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Settings.TABLE_NAME + " ( "
            + Settings.COLUMN_LAST_BILLING + " TEXT NOT NULL, "
            + Settings.COLUMN_BILLING + " INTEGER NOT NULL" + " );");
        
        Calendar c = Calendar.getInstance();
        
        ContentValues cv = new ContentValues();
        cv.put(Settings.COLUMN_BILLING, "false");
        cv.put(Settings.COLUMN_LAST_BILLING, Util.formatCalendarToSQL(c));
        db.insert(Settings.TABLE_NAME, null, cv);
    }
    
    /* ---------- ---------- ---------- ---------- ---------- */
    
    private void upgradeDB1To2(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createTableSettingsV2(db);
            db.execSQL("ALTER TABLE " + Transaction.TABLE_NAME 
                    + " ADD COLUMN " 
                    + Transaction.COLUMN_DESCRIPTION
                    + " TEXT" + ";");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    
    private void upgradeDB2To3(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("ALTER TABLE " + Transaction.TABLE_NAME 
                    + " ADD COLUMN " 
                    + Transaction.COLUMN_PAYMENT_AMOUNT
                    + " REAL DEFAULT 0" + ";");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    
    /* ---------- ---------- ---------- ---------- ---------- */
    
    private void myProfile(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(Profile.COLUMN_ID, idProfileAll);
        cv.put(Profile.COLUMN_VISIBLE_NAME, "Общий");
        db.insert(Profile.TABLE_NAME, null, cv);
        
        cv = new ContentValues();
        cv.put(Profile.COLUMN_ID, idProfileRaiffeisen);
        cv.put(Profile.COLUMN_VISIBLE_NAME, "Райффайзен");
        db.insert(Profile.TABLE_NAME, null, cv);
        
        cv = new ContentValues();
        cv.put(Profile.COLUMN_ID, idProfileTNB);
        cv.put(Profile.COLUMN_VISIBLE_NAME, "Транснациональный");
        db.insert(Profile.TABLE_NAME, null, cv);
    }
    
    private void myBankAccount(SQLiteDatabase db) {
        // Райффайзен
        ContentValues cv = new ContentValues();
        cv.put(BankAccount.COLUMN_ID, idBankAccountRaiffeisen);
        cv.put(BankAccount.COLUMN_NAME, "RUR raif");
        db.insert(BankAccount.TABLE_NAME, null, cv);

        // Транснациональный
        cv = new ContentValues();
        cv.put(BankAccount.COLUMN_ID, idBankAccountTNB);
        cv.put(BankAccount.COLUMN_NAME, "RUR зп");
        db.insert(BankAccount.TABLE_NAME, null, cv);
    }
    
    private void myProfileBankAccount(SQLiteDatabase db) {
        // Общий + RUR raif
        ContentValues cv = new ContentValues();
        cv.put(ProfileBankAccount.COLUMN_ID, idPBA1);
        cv.put(ProfileBankAccount.COLUMN_ID_PROFILE, idProfileAll);
        cv.put(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT, idBankAccountRaiffeisen);
        db.insert(ProfileBankAccount.TABLE_NAME, null, cv);
        // Общий + RUR зп
        cv = new ContentValues();
        cv.put(ProfileBankAccount.COLUMN_ID, idPBA2);
        cv.put(ProfileBankAccount.COLUMN_ID_PROFILE, idProfileAll);
        cv.put(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT, idBankAccountTNB);
        db.insert(ProfileBankAccount.TABLE_NAME, null, cv);
        // Райффайзен + RUR raif
        cv = new ContentValues();
        cv.put(ProfileBankAccount.COLUMN_ID, idPBA3);
        cv.put(ProfileBankAccount.COLUMN_ID_PROFILE, idProfileRaiffeisen);
        cv.put(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT, idBankAccountRaiffeisen);
        db.insert(ProfileBankAccount.TABLE_NAME, null, cv);
        // Транснациональный + RUR зп
        cv = new ContentValues();
        cv.put(ProfileBankAccount.COLUMN_ID, idPBA4);
        cv.put(ProfileBankAccount.COLUMN_ID_PROFILE, idProfileTNB);
        cv.put(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT, idBankAccountTNB);
        db.insert(ProfileBankAccount.TABLE_NAME, null, cv);
    }
    
    private void myCard(SQLiteDatabase db) {
        // Райффайзен
        ContentValues cv = new ContentValues();
        cv.put(Card.COLUMN_ID, idCard9548);
        cv.put(Card.COLUMN_ID_BANK_ACCOUNT, idBankAccountRaiffeisen);
        cv.put(Card.COLUMN_CARD_NUMBER, "*9548");
        db.insert(Card.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(Card.COLUMN_ID, idCard2643);
        cv.put(Card.COLUMN_ID_BANK_ACCOUNT, idBankAccountRaiffeisen);
        cv.put(Card.COLUMN_CARD_NUMBER, "*2643");
        db.insert(Card.TABLE_NAME, null, cv);
        
        // Транснациональный
        cv = new ContentValues();
        cv.put(Card.COLUMN_ID, idCard4860_6650);
        cv.put(Card.COLUMN_ID_BANK_ACCOUNT, idBankAccountTNB);
        cv.put(Card.COLUMN_CARD_NUMBER, "4860*6650");
        db.insert(Card.TABLE_NAME, null, cv);
        
    }
    
    private void myPhoneCard(SQLiteDatabase db) {
        // Райффайзен 4627*9548
        ContentValues cv = new ContentValues();
        cv.put(PhoneCard.COLUMN_ID, idPC1);
        cv.put(PhoneCard.COLUMN_ID_PHONE, idPhoneRaiffeisen);
        cv.put(PhoneCard.COLUMN_ID_CARD, idCard9548);
        db.insert(PhoneCard.TABLE_NAME, null, cv);
        // Райффайзен 4627*2643
        cv = new ContentValues();
        cv.put(PhoneCard.COLUMN_ID, idPC2);
        cv.put(PhoneCard.COLUMN_ID_PHONE, idPhoneRaiffeisen);
        cv.put(PhoneCard.COLUMN_ID_CARD, idCard2643);
        db.insert(PhoneCard.TABLE_NAME, null, cv);
        
        // Транснациональный 4860*6650
        cv = new ContentValues();
        cv.put(PhoneCard.COLUMN_ID, idPC3);
        cv.put(PhoneCard.COLUMN_ID_PHONE, idPhoneTNB);
        cv.put(PhoneCard.COLUMN_ID_CARD, idCard4860_6650);
        db.insert(PhoneCard.TABLE_NAME, null, cv);
    }
    
    private void myTransaction(SQLiteDatabase db) {
//        // Райффайзен
//        ContentValues cv = new ContentValues();
//        cv.put(Transaction.COLUMN_ID_CARD, idCard2643);
//        cv.put(Transaction.COLUMN_DATE, "2014-10-10 15:13:00");
//        cv.put(Transaction.COLUMN_AMOUNT, "396.70");
//        cv.put(Transaction.COLUMN_BALANCE, "135983.86");
//        db.insert(Transaction.TABLE_NAME, null, cv);
//        
//        // Транснациональный
//        cv = new ContentValues();
//        cv.put(Transaction.COLUMN_ID_CARD, idCard4860_6650);
//        cv.put(Transaction.COLUMN_DATE, "2014-10-01 14:36:41");
//        cv.put(Transaction.COLUMN_AMOUNT, "23577.91");
//        cv.put(Transaction.COLUMN_BALANCE, "127224.48");
//        db.insert(Transaction.TABLE_NAME, null, cv);
//        cv = new ContentValues();
//        cv.put(Transaction.COLUMN_ID_CARD, idCard4860_6650);
//        cv.put(Transaction.COLUMN_DATE, "2014-10-07 15:05:48");
//        cv.put(Transaction.COLUMN_AMOUNT, "80000.00");
//        cv.put(Transaction.COLUMN_BALANCE, "47224.48");
//        db.insert(Transaction.TABLE_NAME, null, cv);
    }
    
    /* ---------- ---------- ---------- ---------- ---------- */
    
    private void testData(SQLiteDatabase db) {
        int p = idProfileTNB+1;
        int ba = idBankAccountTNB + 1;
        int ph1 = idPhoneTNB + 1;
        int ph2 = idPhoneTNB + 2;
        int c1 = idCard4860_6650 + 1;
        int c2 = idCard4860_6650 + 2;
        ContentValues cv = new ContentValues();
        // Profile
        cv.put(Profile.COLUMN_ID, p);
        cv.put(Profile.COLUMN_VISIBLE_NAME, "Тестовый профиль");
        db.insert(Profile.TABLE_NAME, null, cv);
        // BankAccount
        cv = new ContentValues();
        cv.put(BankAccount.COLUMN_ID, ba);
        cv.put(BankAccount.COLUMN_NAME, "Тестовый счет");
        db.insert(BankAccount.TABLE_NAME, null, cv);
        // Profile + BankAccount
        cv = new ContentValues();
        cv.put(ProfileBankAccount.COLUMN_ID, idPBA4+1);
        cv.put(ProfileBankAccount.COLUMN_ID_PROFILE, p);
        cv.put(ProfileBankAccount.COLUMN_ID_BANK_ACCOUNT, ba);
        db.insert(ProfileBankAccount.TABLE_NAME, null, cv);
        // Phone 1
        cv = new ContentValues();
        cv.put(Phone.COLUMN_ID, ph1);
        cv.put(Phone.COLUMN_ID_BANK, idPhoneTNB);
        cv.put(Phone.COLUMN_DISPLAY_ADDRESS, "VirtTestTNB");
        cv.put(Phone.COLUMN_ORIGINATING_ADDRESS, "15555215556");
        db.insert(Phone.TABLE_NAME, null, cv);
        // Phone 2
        cv = new ContentValues();
        cv.put(Phone.COLUMN_ID, ph2);
        cv.put(Phone.COLUMN_ID_BANK, idPhoneRaiffeisen);
        cv.put(Phone.COLUMN_DISPLAY_ADDRESS, "VirtTestRaif");
        cv.put(Phone.COLUMN_ORIGINATING_ADDRESS, "15555215556");
        db.insert(Phone.TABLE_NAME, null, cv);
        // Card 1
        cv = new ContentValues();
        cv.put(Card.COLUMN_ID, c1);
        cv.put(Card.COLUMN_ID_BANK_ACCOUNT, ba);
        cv.put(Card.COLUMN_CARD_NUMBER, "0000+1111");
        db.insert(Card.TABLE_NAME, null, cv);
        // Card 2
        cv = new ContentValues();
        cv.put(Card.COLUMN_ID, c2);
        cv.put(Card.COLUMN_ID_BANK_ACCOUNT, ba);
        cv.put(Card.COLUMN_CARD_NUMBER, "+2222");
        db.insert(Card.TABLE_NAME, null, cv);
        // Phone + Card
        cv = new ContentValues();
        cv.put(PhoneCard.COLUMN_ID, idPC3 + 1);
        cv.put(PhoneCard.COLUMN_ID_PHONE, ph1);
        cv.put(PhoneCard.COLUMN_ID_CARD, c1);
        db.insert(PhoneCard.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(PhoneCard.COLUMN_ID, idPC3 + 2);
        cv.put(PhoneCard.COLUMN_ID_PHONE, ph2);
        cv.put(PhoneCard.COLUMN_ID_CARD, c2);
        db.insert(PhoneCard.TABLE_NAME, null, cv);
        // Regex
        String regexT1 = REGEX_SPASE + REGEX_DDMMYY_POINT + " " + REGEX_HHMMSS
                + "(?: KARTA ((?:\\d{4,4})\\+(?:\\d{4,4})) )"
                + REGEX_ANY + "(?: " + REGEX_MANY_TCHK + " RUR)" + REGEX_ANY
                + "(?: DOSTUPNO " + REGEX_MANY_TCHK + " RUR)";
        
        cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexTNB+1);
        cv.put(Regex.COLUMN_ID_BANK, idBankTNB);
        cv.put(Regex.COLUMN_REGEX, regexT1);
        db.insert(Regex.TABLE_NAME, null, cv);
        String regexT2 = REGEX_SPASE + "(?:Karta (\\+(?:\\d{4,4}));)" + REGEX_ANY
                                     + "(?: Summa\\:" + REGEX_MANY_TCHK + " RUR)"
                                     + "(?: Data\\:" + REGEX_DDMMYYYY_SLASH + ";)"
                                     + "(?: Dostupny Ostatok\\: " + REGEX_MANY_TCHK + " RUR)"
                                     + REGEX_ANY;
        
        cv = new ContentValues();
        cv.put(Regex.COLUMN_ID, idRegexTNB+2);
        cv.put(Regex.COLUMN_ID_BANK, idBankRaiffeisen);
        cv.put(Regex.COLUMN_REGEX, regexT2);
        db.insert(Regex.TABLE_NAME, null, cv);
        // Test Transaction
        cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, c1);
        cv.put(Transaction.COLUMN_DATE, "2014-10-01 12:00:01");
        cv.put(Transaction.COLUMN_AMOUNT, "50000.00");
        cv.put(Transaction.COLUMN_BALANCE, "50000.00");
        db.insert(Transaction.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, c1);
        cv.put(Transaction.COLUMN_DATE, "2014-10-03 15:15:10");
        cv.put(Transaction.COLUMN_AMOUNT, "1000.05");
        cv.put(Transaction.COLUMN_BALANCE, "50000.00");
        db.insert(Transaction.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, c1);
        cv.put(Transaction.COLUMN_DATE, "2014-10-08 23:15:10");
        cv.put(Transaction.COLUMN_AMOUNT, "6784.12");
        cv.put(Transaction.COLUMN_BALANCE, "42215.83");
        db.insert(Transaction.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, c1);
        cv.put(Transaction.COLUMN_DATE, "2014-10-21 10:03:16");
        cv.put(Transaction.COLUMN_AMOUNT, "5100.00");
        cv.put(Transaction.COLUMN_BALANCE, "47315.83");
        db.insert(Transaction.TABLE_NAME, null, cv);
        cv = new ContentValues();
        cv.put(Transaction.COLUMN_ID_CARD, c1);
        cv.put(Transaction.COLUMN_DATE, "2014-10-29 18:32:58");
        cv.put(Transaction.COLUMN_AMOUNT, "28123.54");
        cv.put(Transaction.COLUMN_BALANCE, "19192.29");
        db.insert(Transaction.TABLE_NAME, null, cv);
    }
}
