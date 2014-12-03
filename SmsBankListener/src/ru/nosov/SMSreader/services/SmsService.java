/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import ru.nosov.SMSreader.types.TypeBank;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.nosov.SMSreader.ActivityMain;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.Regex;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.Phone;
import ru.nosov.SMSreader.db.Settings;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.RegexImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.PhoneImpl;
import ru.nosov.SMSreader.db.impl.SettingsImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;
import ru.nosov.SMSreader.receiver.SmsBody;
import ru.nosov.SMSreader.utils.Util;

/**
 * Обработка сообщения.
 * @author Носов А.В.
 */
public class SmsService extends Service {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "SmsService";
    
    public static final String SMS_DISPLAY_ADDRESS = "displayAddress";
    public static final String SMS_ORIGINATING_ADDRESS = "originatingAddress";
    public static final String SMS_BODY = "body";
    public static final String SMS_TIME_SERVICE_CENTRE = "time";
    public static final String SMS_NOTIFICATION = "notification";
    
    /** Показать уведомление. */
    private boolean notification = true;
    
    /** Уведомления. */
    private NotificationManager nm;
    
    
    /** Регулярное выражение ДД.ММ.ГГ. */
    private static String REGEX_DDMMYY_POINT = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\.(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\.(?:\\d{2}))";
    /** Регулярное выражение ДД.ММ.ГГГГ. */
    private static String REGEX_DDMMYYYY_POINT = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\.(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\.(?:\\d{4}))";
    /** Регулярное выражение ДД/ММ/ГГГГ. */
    private static String REGEX_DDMMYYYY_SLASH = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\/(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\/(?:\\d{4}))";
    //private static String REGEX_DDMMYYYY_BACKSLASH = "((?:(?:[0-2][0-9])|(?:3[0-1])|[1-9])\\.(?:(?:0[1-9])|(?:1[0-2])|(?:[1-9]))\\.(?:\\d{2}))";
    /** Регулярное выражение ЧЧ:ММ:СС. */
    private static String REGEX_HHMMSS = "((?:(?:(?:[0,1][0-9])|(?:2[0-3]))|[0-9])\\:(?:(?:[0-5][0-9])|[0-9])\\:(?:(?:[0-5][0-9])|[0-9]))";
    /** Регулярное выражение номера карты формата *1234. */
    private static String REGEX_CARD_1 = "(\\*(?:\\d{4,4}))";
    /** Регулярное выражение номера карты формата 1234*1234. */
    private static String REGEX_CARD_2 = "((?:\\d{4,4})\\*(?:\\d{4,4}))";
    /** Регулярное выражение номера карты формата *1234. */
    private static String REGEX_CARD_T1 = "(\\+(?:\\d{4,4}))";
    /** Регулярное выражение номера карты формата 1234*1234. */
    private static String REGEX_CARD_T2 = "((?:\\d{4,4})\\+(?:\\d{4,4}))";
    /** Регулярное выражение числа вида 1234.12 */
    private static String REGEX_MANY_TCHK = "((\\d+)|(?:\\d+(?:\\.\\d{0,2})))";
    /** Регулярное выражение числа вида 1234,12 */
    private static String REGEX_MANY_ZPT = "((\\d+)|(?:\\d+(?:\\,\\d{0,2})))";
//    private Context context;
//    private ExecutorService executor;
    // End of variables declaration
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
//        executor = Executors.newFixedThreadPool(3);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "----- Start My Service");
        String sms_from = intent.getExtras().getString(SmsService.SMS_DISPLAY_ADDRESS, null);
        String sms_number = intent.getExtras().getString(SmsService.SMS_ORIGINATING_ADDRESS, null);
        String originatingAddress = getOriginatingAddress(sms_from, sms_number);
        String sms_body = intent.getExtras().getString(SmsService.SMS_BODY, null);
        Long time = intent.getExtras().getLong(SmsService.SMS_TIME_SERVICE_CENTRE, 0);
        notification = intent.getExtras().getBoolean(SmsService.SMS_NOTIFICATION, true);
        
        if ( (originatingAddress == null) ||
                (originatingAddress.equals("")) ||
                (sms_body == null) ||
                (sms_body.equals("")) ) {
            
            this.stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        
        Log.d(LOG_TAG, sms_body);
        
        ArrayList<Phone> phones = getPhonesByAddress(sms_number);
        if (phones != null) {
            for (Phone phone : phones) {
//                Log.d(LOG_TAG, "Phone ID=" + phone.getId() + 
//                        "; OA=" + phone.getOriginatingAddress() + 
//                        "; B=" + phone.getIdBank());
                
                /* Это использовалось по общим регуляркам из БД.
                ArrayList<Regex> regexs = getRegexByBank(phone.getIdBank());
//                if (regexs != null)
//                    Log.d(LOG_TAG, "Regex size=" + regexs.size());
                
                SmsBody smsBody = getBodyByRegex(regexs, sms_body);
                */
                
                SmsBody smsBody = createSmsBody(sms_body);
                if (smsBody == null) continue;
                else Log.d(LOG_TAG, "smsBody: C="+smsBody.getCard()+"; B="+smsBody.getBalance());
                
                smsBody.setDateTime(Util.getCalendarByTimeInMillis(time).getTime());
                smsBody = transformDateByBank(smsBody);
                if (smsBody == null) {
                    Log.d(LOG_TAG, "Не смог привести время");
                    notificationFailData("Не смог привести время", sms_body);
                }
                
                ArrayList<Card> cards = getCardsBySMS(phone, smsBody);
//                if (cards != null) 
//                    Log.d(LOG_TAG, "Card size=" + cards.size());
                
                saveTransaction(cards, smsBody);
            }
        }
        
        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "----- Stop My Service");
    }
    
    private String getOriginatingAddress(String sms_from, String sms_number) {
        if ( (sms_number != null) && (!sms_number.equals("")) ) return sms_number;
        if ( (sms_from != null) && (!sms_from.equals("")) ) return sms_from;
        return null;
    }
    
    /**
     * Возвращает список телефонов соответствующих адресу сообщения.
     * @param originatingAddress адрес сообщения
     * @return список телефонов
     */
    private ArrayList<Phone> getPhonesByAddress(String originatingAddress) {
        if (originatingAddress == null) return null;
        if (originatingAddress.equals("")) return null;
        PhoneImpl phoneImpl = new PhoneImpl(this);
        phoneImpl.open();
        ArrayList<Phone> phones = new ArrayList<Phone>();
        Cursor cPhone = phoneImpl.getPhonesByOriginatingAddress(originatingAddress);
        if (cPhone.moveToFirst()) {
            do {
                int idIndex = cPhone.getColumnIndex(Phone.COLUMN_ID);
                int idbnIndex = cPhone.getColumnIndex(Phone.COLUMN_ID_BANK);
                int daIndex = cPhone.getColumnIndex(Phone.COLUMN_DISPLAY_ADDRESS);
                int oaIndex = cPhone.getColumnIndex(Phone.COLUMN_ORIGINATING_ADDRESS);
                
                Phone p = new Phone();
                p.setId(cPhone.getInt(idIndex));
                p.setIdBank(cPhone.getInt(idbnIndex));
                p.setDisplayAddress(cPhone.getString(daIndex));
                p.setOriginatingAddress(cPhone.getString(oaIndex));
                phones.add(p);
                //getRegexByBank(cPhone.getString(idbIndex));
            } while (cPhone.moveToNext());
        }
        
        phoneImpl.close();
        return phones;
    }
    
    /**
     * Выозвращает список регулярных выражений принадлежащих этому банку.
     * @param idBank идентификатор банка
     * @return список регулярных выражений
     */
    private ArrayList<Regex> getRegexByBank(int idBank) {
//        if (_id_bankName == null) return null;
//        if (_id_bankName.equals("")) return null;
        if (idBank < 0) return null;
        
        RegexImpl regexImpl = new RegexImpl(this);
        regexImpl.open();
        ArrayList<Regex> regexs = new ArrayList<Regex>();
        Cursor cRegex = regexImpl.getRegexesByIDBankName(idBank);
        if (cRegex.moveToFirst()) {
            do {
                int idIndex = cRegex.getColumnIndex(Regex.COLUMN_ID);
                int idbIndex = cRegex.getColumnIndex(Regex.COLUMN_ID_BANK);
                int idrIndex = cRegex.getColumnIndex(Regex.COLUMN_REGEX);
                
                Regex r = new Regex();
                r.setId(cRegex.getInt(idIndex));
                r.setIdBank(cRegex.getInt(idbIndex));
                r.setRegex(cRegex.getString(idrIndex));
                regexs.add(r);
            } while (cRegex.moveToNext());
        }
        
        regexImpl.close();
        
        return regexs;
    }
    
    /**
     * Проверяет тело сообщения на регулярные выражения.
     * Первая положительная проверка вернет распасеное сообщение или null.
     * @param regexs список регулярных выражений
     * @param body тело сообщения
     * @return сообщение
     */
    private SmsBody getBodyByRegex(ArrayList<Regex> regexs, String body) {
        if (regexs == null) return null;
        for (Regex regex : regexs) {
//            Log.d(LOG_TAG, "Regex body:" + regex.getRegex());
            Pattern pattern = Pattern.compile(regex.getRegex());
            if (pattern.matcher(body).matches()) {
                Matcher matcher = pattern.matcher(body);
                
                TypeBank tb = TypeBank.getTypeModuleByID(regex.getIdBank());
                switch (tb) {
                    case RAIFFEISEN:
//                        Log.d(LOG_TAG, "RAIFFEISEN=" + regex.getRegex());
                        return createSmsBodyByRAIFFEISEN(matcher);
                    case TNB:
//                        Log.d(LOG_TAG, "TNB=" + regex.getRegex());
                        return createSmsBodyByTNB(matcher);
//                        if (sms == null) break;
//                        else return sms;
                    default:
                        return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Создает сообщение из тела банка ТНБ.
     * @param matcher регулярное выражение
     * @return сообщение
     */
    private SmsBody createSmsBodyByRAIFFEISEN(Matcher matcher) {
        matcher.find();
//        for (int i=0; i<matcher.groupCount(); i++)
//            Log.d(LOG_TAG, "Regex:" + i + " = " + matcher.group(i));
        
        SmsBody smsBody = new SmsBody();
        try {
            String g1 = matcher.group(1);
            if (g1 != null) {
                if ( g1.startsWith("*") || g1.startsWith("+") ) smsBody.setCard(g1);
                else smsBody.setAmount(Float.valueOf(g1));
            }
            String g2 = matcher.group(2);
            if (g2 != null){
                if (g2.startsWith("*")) smsBody.setCard(g2);
                else if (g2.indexOf("/") > 0) smsBody.setDate(g2);
                else smsBody.setAmount(Float.valueOf(g2));
            }
            String g3 = matcher.group(3);
            if (g3 != null){
                if (g3.startsWith("*")) smsBody.setCard(g3);
                else smsBody.setAmount(Float.valueOf(g3));
            }
            String g4 = matcher.group(4);
            if (g4 != null){
                smsBody.setDate(matcher.group(4));
            }
            String g5 = matcher.group(5);
            if (g5 != null){
                float balance = Float.valueOf(matcher.group(5));
                smsBody.setBalance(balance);
            }
            smsBody.setTime("00:00:00");
            
//            Log.d(LOG_TAG, "SmsBODY: C=" + matcher.group(1)
//                    + "; A" + matcher.group(2)
//                    + "; A" + matcher.group(3)
//                    + "; D=" + matcher.group(4)
//                    + "; B=" + matcher.group(5));
//                                                    // оплата           - проведено        - полполнение
//            smsBody.setCard(matcher.group(1));      // Номер карты      - Сумма транзакции - Номер карты
//            smsBody.setAmount(matcher.group(2));    // Сумма транзакции - Сумма транзакции - DD/MM/YYYY
//            //String amount = matcher.group(3);     // Сумма транзакции - Номер карты      - Сумма транзакции
//            smsBody.setDate(matcher.group(4));      // DD/MM/YYYY       - DD/MM/YYYY       - null
//            smsBody.setBalance(matcher.group(5));   // Доступно
            return smsBody;
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Не смог привести числа у RAIFFEISEN.");
            return null;
        }
    }
    
    /**
     * Создает сообщение из тела банка ТНБ.
     * @param matcher регулярное выражение
     * @return сообщение
     */
    private SmsBody createSmsBodyByTNB(Matcher matcher) {
        matcher.find();
        SmsBody smsBody = new SmsBody();
        try {
            float amount = Float.valueOf(matcher.group(4).replace(",", "."));
            float balance = Float.valueOf(matcher.group(6).replace(",", "."));
            smsBody.setDate(matcher.group(1));
            smsBody.setTime(matcher.group(2));
            smsBody.setCard(matcher.group(3));
            smsBody.setAmount(amount);
            smsBody.setBalance(balance);
//            Log.d(LOG_TAG, "SmsBODY: D=" + matcher.group(1)
//                    + " " + matcher.group(2)
//                    + "; C=" + matcher.group(3)
//                    + "; A=" + matcher.group(4)
//                    + "; B=" + matcher.group(6));
//            smsBody.setDate(matcher.group(1));      // DD.MM.YY
//            smsBody.setTime(matcher.group(2));      // hh.mm.ss
//            smsBody.setCard(matcher.group(3));      // Номер карты
//            smsBody.setAmount(matcher.group(4));    // Сумма транзакции
//            //String amount = matcher.group(5);     // Сумма транзакции
//            smsBody.setBalance(matcher.group(6));   // Доступно
            return smsBody;
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Не смог привести числа у TNB.");
            return null;
        }
    }
    
    /**
     * Возвращает список карточек подходящих под номер карты в сообщении.
     * @param phone номер
     * @param smsBody тело
     * @return список карточек
     */
    private ArrayList<Card> getCardsBySMS(Phone phone, SmsBody smsBody) {
        if ( (smsBody == null) || (phone == null) ||
             (smsBody.getCard() == null) ) return null;
        
        CardImpl cardImpl = new CardImpl(this);
        ArrayList<Card> cards = cardImpl.getCardsByIDPhone(phone.getId());
        for (int i=0; i<cards.size(); i++) {
            Card card = cards.get(i);
            if (!card.getCardNumber().equals(smsBody.getCard()))
                cards.remove(i);
        }
        return cards;
    }
    
    /**
     * Сохранение транзакции в БД.
     * @param cards карта
     * @param smsBody тело сообщения
     */
    private void saveTransaction(ArrayList<Card> cards, SmsBody smsBody) {
        if (cards == null) return;
        if (smsBody == null) return;
        
        Transaction t = null;
        TransactionImpl transactionImpl = new TransactionImpl(this);
        transactionImpl.open();
        for (Card card : cards) {
            t = new Transaction();
            t.setIdCard(card.getId());
            t.setDateSQL(Util.formatDateToSQL(smsBody.getDateTime()));
            t.setDateTime(smsBody.getDateTime());
            t.setAmount(smsBody.getAmount());
            t.setBalace(smsBody.getBalance());
            boolean b = transactionImpl.addTransaction(t);
            String msg = Transaction.TABLE_NAME + " add " + card.getCardNumber() +
                    "; C=" + t.getIdCard() + "; D=" + t.getDateSQL() + 
                    "; A=" + t.getAmount() + "; B="+ t.getBalace() + 
                    "; " + String.valueOf(b);
            Log.i(LOG_TAG, msg);
            notificationAddData("Добавленно в базу", msg);
        }
        transactionImpl.close();
        if (t == null) return;
        validateBilling(t);
    }
    
    /**
     * Проверка флага, что требуется билинг данных.
     * @param t транзакция
     */
    private void validateBilling(Transaction t) {
        Calendar first = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.setTime(t.getDateTime());
        if (Util.validateMMYYYY(first, last)) return;
        
        SettingsImpl impl = new SettingsImpl(this);
        Settings s = impl.getSettings();
        if (!s.isBilling()) return;
        
        last = Util.formatSQLToDate(s.getLastBilling());
        if (Util.validateMMYYYY(first, last)) return;
        
        impl.open();
        impl.updateSettingsBilling(true);
        impl.close();
    }
    
    /**
     * Переводит дату в формат БД.
     * @param smsBody тело сообщения
     * @param typeBank тип банка
     * @return  тело сообщения
     */
    private SmsBody transformDateByBank(SmsBody smsBody) {
        if (smsBody == null) return null;
        
        String str = (smsBody.getTime() == null) ? 
                smsBody.getDate() :
                smsBody.getDate()+ " " + smsBody.getTime();
        
        smsBody.setDateTime(Util.transformDate(str).getTime());
        
        if (smsBody.getDateTime() == null) return null;
        else return smsBody;
//        TypeBank tb = TypeBank.getTypeModuleByID(typeBank);
//        switch (tb) {
//            case RAIFFEISEN:
//                String[] dateRF = smsBody.getDate().split("\\/");
//                Log.d(LOG_TAG, "Date:"+smsBody.getDate()+"; L:"+dateRF.length);
//                if (dateRF.length != 3) return null;
//                
//                Calendar cRF = Calendar.getInstance();
////                Log.d(LOG_TAG, "cRF1:"+cRF.toString());
//                cRF.set(Calendar.YEAR, Integer.valueOf(dateRF[2]));
//                cRF.set(Calendar.MONTH, Integer.valueOf(dateRF[1])-1);
//                cRF.set(Calendar.DATE, Integer.valueOf(dateRF[0]));
////                Calendar cRF = new GregorianCalendar(
////                                                Integer.valueOf(dateRF[2]), 
////                                                Integer.valueOf(dateRF[1]), 
////                                                Integer.valueOf(dateRF[0]));
//                smsBody.setDateTime(cRF.getTime());
//                String[] timeRF = smsBody.getTime().split("\\:");
//                if (timeRF.length != 3) return smsBody;
////                Log.d(LOG_TAG, "cRF:"+cRF.toString());
//                cRF.set(Calendar.HOUR, Integer.valueOf(timeRF[0]));
//                cRF.set(Calendar.MINUTE, Integer.valueOf(timeRF[1]));
//                cRF.set(Calendar.SECOND, Integer.valueOf(timeRF[2]));
////                cRF = new GregorianCalendar(
////                                                Integer.valueOf(dateRF[2]), 
////                                                Integer.valueOf(dateRF[1]), 
////                                                Integer.valueOf(dateRF[0]),
////                                                Integer.valueOf(timeRF[0]), 
////                                                Integer.valueOf(timeRF[1]), 
////                                                Integer.valueOf(timeRF[2]));
//                smsBody.setDateTime(cRF.getTime());
//                return smsBody;
//            case TNB:
//                String[] dateTNB = smsBody.getDate().split("\\.");
//                if (dateTNB.length != 3) return null;
//                String yyyy = (dateTNB[2].length() == 2) ? "20"+dateTNB[2] : "2"+dateTNB[2];
//                Calendar cTNB = Calendar.getInstance();
//                cTNB.set(Calendar.YEAR, Integer.valueOf(yyyy));
//                cTNB.set(Calendar.MONTH, Integer.valueOf(dateTNB[1])-1);
//                cTNB.set(Calendar.DATE, Integer.valueOf(dateTNB[0])-1);
////                Calendar cTNB = new GregorianCalendar(
////                                                Integer.valueOf(yyyy), 
////                                                Integer.valueOf(dateTNB[1]), 
////                                                Integer.valueOf(dateTNB[0]));
//                smsBody.setDateTime(cTNB.getTime());
//                String[] timeTNB = smsBody.getTime().split("\\:");
//                if (timeTNB.length != 3) return smsBody;
//                cTNB.set(Calendar.HOUR, Integer.valueOf(timeTNB[0]));
//                cTNB.set(Calendar.MINUTE, Integer.valueOf(timeTNB[1]));
//                cTNB.set(Calendar.SECOND, Integer.valueOf(timeTNB[2]));
////                cTNB = new GregorianCalendar(
////                                                Integer.valueOf(yyyy), 
////                                                Integer.valueOf(dateTNB[1]), 
////                                                Integer.valueOf(dateTNB[0]), 
////                                                Integer.valueOf(timeTNB[0]), 
////                                                Integer.valueOf(timeTNB[1]), 
////                                                Integer.valueOf(timeTNB[2]));
////                Log.i(LOG_TAG, "1 "+yyyy+"-"+dateTNB[1]+"-"+dateTNB[0]+" "
////                        +timeTNB[0]+":"+timeTNB[1]+":"+timeTNB[2]);
////                Log.i(LOG_TAG, "2 "+dateFormat.format(cTNB.getTime()));
//                
//                smsBody.setDateTime(cTNB.getTime());
//                return smsBody;
//            default:
//                return null;
//        }
    }
    
    /**
     * Уведомление с положительным результатом.
     * @param msgShort короткое сообщение
     * @param msgFull полное сообщение
     */
    private void notificationAddData(String msgShort, String msgFull) {
        sendNotification(ru.nosov.SMSreader.R.drawable.mail_plus, msgShort, msgFull);
    }
    
    /**
     * Уведомление с отрицательным результатом.
     * @param msgShort короткое сообщение
     * @param msgFull полное сообщение
     */
    private void notificationFailData(String msgShort, String msgFull) {
        sendNotification(ru.nosov.SMSreader.R.drawable.mail_minus, msgShort, msgFull);
    }
    
    /**
     * Отправляет уведомление.
     * @param icon иконка
     * @param msgShort короткое сообщение
     * @param msgFull полное сообщение
     */
    private void sendNotification(int icon, String msgShort, String msgFull) {
        if (!notification) return;
        Intent notificationIntent = new Intent(this, ActivityMain.class);
        notificationIntent.putExtra(ActivityMain.ADD_SMS, msgFull);
        
        Notification.Builder nb = new Notification.Builder(this)
            .setSmallIcon(icon)
            .setAutoCancel(true) //уведомление закроется по клику на него
            .setTicker(msgShort) //текст, который отобразится вверху статус-бара при создании уведомления
            .setContentText(msgFull) // Основной текст уведомления
            .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
            .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
            .setContentTitle("SmsListener") //заголовок уведомления
            .setDefaults(Notification.DEFAULT_VIBRATE); // звук, вибро и диодный индикатор
        
//        Notification notification = nb.getNotification(); //генерируем уведомление
        Notification nn = nb.build();
        
        nm.notify(0, nn); // отображаем его пользователю.
//        notifications.put(0, notification); //теперь мы можем обращаться к нему по id
    }
    
    /**
     * Создает смс тело для транзакци.
     * @param sms входящее сообщение
     * @return смс тело
     */
    private SmsBody createSmsBody(String sms) {
        
        String card = getCard(sms);
        String amount = getAmount(sms);
        String balance = getBalance(sms);
        String data = getData(sms);
        
        if (card == null)
            notificationFailData("Карта не найдена.", sms);
        if (amount == null)
            notificationFailData("Сумма не найдена", sms);
        if (balance == null)
            notificationFailData("Остаток не найден", sms);
        if (data != null)
            notificationFailData("Дата не найдена", sms);
        
        if ( (card == null) || (amount == null) ||
             (balance == null) || (data == null) ) return null;
        
        try {
            SmsBody body = new SmsBody();
            body.setAmount(Float.valueOf(amount.replace(",", ".")));
            body.setBalance(Float.valueOf(balance.replace(",", ".")));
            body.setCard(card);
            body.setDate(data);
            
            return body;
        } catch (NumberFormatException ex) {
            notificationFailData("Не смог привести числа.", sms);
            return null;
        }
    }
    
    /**
     * Возвращает описание транзакции из входящего сообщения.
     * @param sms входящее сообщение
     * @return описание транзакции
     */
    private String getDescription(String sms) {
        String regex = "(?>Pokupka: " + "(.*)" + "; )";
        String description = findRegex(regex, sms);
        if (description != null) return description;
        
        regex = "( " + "(.*)" + " / )";
        description = findRegex(regex, sms);
        if (description != null) return description;
        
        return null;
    }
    
    /**
     * Возвращает дату из входящего сообщения.
     * @param sms входящее сообщение
     * @return дата
     */
    private String getData(String sms) {
        String regex = "(?:Data: {0,}" + REGEX_DDMMYYYY_SLASH + ";)";
        String data = findRegex(regex, sms);
        if (data != null) return data;
        
        regex = "(?:Data: {0,}" + REGEX_DDMMYYYY_POINT + ";)";
        data = findRegex(regex, sms);
        if (data != null) return data;
        
        regex = "(?:na " + REGEX_DDMMYYYY_SLASH + ":)";
        data = findRegex(regex, sms);
        if (data != null) return data;
        
        regex = "(?:^" + "("+REGEX_DDMMYY_POINT + " " + REGEX_HHMMSS+")" + " )";
        data = findRegex(regex, sms);
        if (data != null) return data;
        
        regex = "(?:popolnilsya {0,}" + REGEX_DDMMYYYY_SLASH + " )";
        data = findRegex(regex, sms);
        if (data != null) return data;
        
        return null;
    }
    
    /**
     * Возвращает сумму баланса из входящего сообщения.
     * @param sms входящее сообщение
     * @return сумма баланса
     */
    private String getBalance(String sms) {
        String regex = "(?:Dostupny Ostatok: " + REGEX_MANY_TCHK + " RUR)";
        String balance = findRegex(regex, sms);
        if (balance != null) return balance;
        
        regex = "(?:DOSTUPNO " + REGEX_MANY_ZPT + " RUR)";
        balance = findRegex(regex, sms);
        if (balance != null) return balance;
        
        regex = "(?:\\: " + REGEX_MANY_TCHK + " RUR)";
        balance = findRegex(regex, sms);
        if (balance != null) return balance;
        
        return null;
    }
    
    /**
     * Возвращает сумму транзакции из входящего сообщения.
     * @param sms входящее сообщение
     * @return сумма транзакции
     */
    private String getAmount(String sms) {
        String regex = "(?:; " + REGEX_MANY_TCHK + " RUR)";
        String amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:NALICHNYE {1,}" + REGEX_MANY_ZPT + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:POPOLNENIE \\+ {1,}" + REGEX_MANY_ZPT + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:- {1,}" + REGEX_MANY_TCHK + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:Summa: {0,}" + REGEX_MANY_TCHK + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:na {0,}" + REGEX_MANY_TCHK + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        regex = "(?:tranzakcija: {0,}" + REGEX_MANY_TCHK + " RUR)";
        amoun = findRegex(regex, sms);
        if (amoun != null) return amoun;
        
        return null;
    }
    
    /**
     * Возвращает номер карты из входящего сообщения.
     * @param sms входящее сообщение
     * @return номер карты
     */
    private String getCard(String sms) {
        String regex = "(?:Ka.t. " + REGEX_CARD_2 + " )";
        String card = findRegex(regex, sms);
        if (card != null) return card;
        
        regex = "(?:Ka.t. " + REGEX_CARD_1 + ";)";
        card = findRegex(regex, sms);
        if (card != null) return card;
        
        regex = "(?:Ka.t. " + REGEX_CARD_1 + " )";
        card = findRegex(regex, sms);
        if (card != null) return card;
        
        regex = "(?:Ka.t. " + REGEX_CARD_T1 + " )";
        card = findRegex(regex, sms);
        if (card != null) return card;
        
        regex = "(?:Ka.t. " + REGEX_CARD_T2 + " )";
        card = findRegex(regex, sms);
        if (card != null) return card;
        
        return null;
    }
    
    /**
     * Поиск текста по регулярному выражению.
     * @param regex регулярное выражение
     * @param msg строка
     * @return текст или null
     */
    private String findRegex(String regex, String msg) {
        try {
            Pattern prf2 = Pattern.compile(regex.toLowerCase());
            Matcher matcher = prf2.matcher(msg.toLowerCase());
            if (matcher.find())
                return matcher.group(1);
            return null;
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }
    
}
