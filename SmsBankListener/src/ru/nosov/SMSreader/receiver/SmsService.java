/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.receiver;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.Regex;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.Phone;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.RegexImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.PhoneImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;

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
    
    private ExecutorService executor;
    // End of variables declaration
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(3);
    }
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(LOG_TAG, "----- Start My Service");
        String sms_from = intent.getExtras().getString(SmsService.SMS_DISPLAY_ADDRESS, null);
        String sms_number = intent.getExtras().getString(SmsService.SMS_ORIGINATING_ADDRESS, null);
        String originatingAddress = getOriginatingAddress(sms_from, sms_number);
        String sms_body = intent.getExtras().getString(SmsService.SMS_BODY, null);
        long time = intent.getExtras().getLong(SmsService.SMS_TIME_SERVICE_CENTRE, 0);
        
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
                ArrayList<Regex> regexs = getRegexByBank(phone.getIdBank());
//                if (regexs != null)
//                    Log.d(LOG_TAG, "Regex size=" + regexs.size());
                
                SmsBody smsBody = getBodyByRegex(regexs, sms_body);
//                if (smsBody != null)
//                    Log.d(LOG_TAG, "smsBody: C="+smsBody.getCard()+"; B="+smsBody.getBalance());
                
                smsBody = transformDateByBank(smsBody, phone.getIdBank(), time);
//                if (smsBody == null)
//                    Log.d(LOG_TAG, "Не смог привести время");
                
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
//        Log.d(LOG_TAG, "----- Stop My Service");
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
        SmsBody sms;
        for (Regex regex : regexs) {
//            Log.d(LOG_TAG, "Regex body:" + regex.getRegex());
            Pattern pattern = Pattern.compile(regex.getRegex());
            if (pattern.matcher(body).matches()) {
                Matcher matcher = pattern.matcher(body);
//                matcher.find();
                
                TypeBank tb = TypeBank.getTypeModuleByID(regex.getIdBank());
                switch (tb) {
                    case RAIFFEISEN:
//                        Log.d(LOG_TAG, "RAIFFEISEN=" + regex.getRegex());
                        sms = createSmsBodyByRAIFFEISEN(matcher);
                        if (sms == null) break;
                        else return sms;
                    case TNB:
//                        Log.d(LOG_TAG, "TNB=" + regex.getRegex());
                        sms = createSmsBodyByTNB(matcher);
                        if (sms == null) break;
                        else return sms;
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
        SmsBody smsBody = new SmsBody();
        try {
            String g1 = matcher.group(1);
            if (g1 != null) {
                if (g1.startsWith("*")) smsBody.setCard(g1);
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
        if (smsBody == null) return null;
        if (phone == null) return null;
        
        CardImpl cardImpl = new CardImpl(this);
        cardImpl.open();
        
        ArrayList<Card> cards = new ArrayList<Card>();
        Cursor cCard = cardImpl.getCursorCardsByIDPhone(phone.getId());
        if (cCard != null) {
            if (cCard.moveToFirst()) {
                int idIndex = cCard.getColumnIndex(Card.COLUMN_ID);
                int baIndex = cCard.getColumnIndex(Card.COLUMN_ID_BANK_ACCOUNT);
                int cnIndex = cCard.getColumnIndex(Card.COLUMN_CARD_NUMBER);
                
                do {
                    String cardNumber = cCard.getString(cnIndex);
//                    Log.d(LOG_TAG, "cardNumber/smsCard " + cardNumber +
//                                    "/" + smsBody.getCard());
                    if (cardNumber.equals(smsBody.getCard())){
                        Card c = new Card();
                        c.setId(cCard.getInt(idIndex));
                        c.setIdBankAccount(cCard.getInt(baIndex));
                        c.setCardNumber(cardNumber);
                        cards.add(c);
//                        Log.d(LOG_TAG, Card.TABLE_NAME + " - " + c.getCardNumber());
                    }
                } while (cCard.moveToNext());
            }
        }
        cardImpl.close();
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
        
        TransactionImpl transactionImpl = new TransactionImpl(this);
        transactionImpl.open();
        
        for (Card card : cards) {
            Transaction t = new Transaction();
            t.setIdCard(card.getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            t.setDateSQL(dateFormat.format(smsBody.getDateTime()));
            t.setAmount(smsBody.getAmount());
            t.setBalace(smsBody.getBalance());
            transactionImpl.addTransaction(t);
            Log.i(LOG_TAG, Transaction.TABLE_NAME + " add: "
                    + "C=" + t.getIdCard() + "; D=" + t.getDateSQL() + 
                    "; A=" + t.getAmount() + "; B="+ t.getBalace() + ";");
        }
        
        transactionImpl.close();
    }
    
    /**
     * Переводит дату в формат БД.
     * @param smsBody тело сообщения
     * @param typeBank тип банка
     * @return  тело сообщения
     */
    private SmsBody transformDateByBank(SmsBody smsBody, int typeBank, long time) {
        if (smsBody == null) return null;
        TypeBank tb = TypeBank.getTypeModuleByID(typeBank);
        switch (tb) {
            case RAIFFEISEN:
                String[] dateRF = smsBody.getDate().split("\\/");
                if (dateRF.length != 3) return null;
                if (time != 0) smsBody.setTime(new SimpleDateFormat("hh:mm:ss").format(time)); 
                Calendar cRF = Calendar.getInstance();
                cRF.set(Calendar.YEAR, Integer.valueOf(dateRF[2]));
                cRF.set(Calendar.MONTH, Integer.valueOf(dateRF[1])-1);
                cRF.set(Calendar.DATE, Integer.valueOf(dateRF[0]));
//                Calendar cRF = new GregorianCalendar(
//                                                Integer.valueOf(dateRF[2]), 
//                                                Integer.valueOf(dateRF[1]), 
//                                                Integer.valueOf(dateRF[0]));
                smsBody.setDateTime(cRF.getTime());
                String[] timeRF = smsBody.getTime().split("\\:");
                if (timeRF.length != 3) return smsBody;
                cRF.set(Calendar.HOUR, Integer.valueOf(timeRF[0]));
                cRF.set(Calendar.MINUTE, Integer.valueOf(timeRF[1]));
                cRF.set(Calendar.SECOND, Integer.valueOf(timeRF[2]));
//                cRF = new GregorianCalendar(
//                                                Integer.valueOf(dateRF[2]), 
//                                                Integer.valueOf(dateRF[1]), 
//                                                Integer.valueOf(dateRF[0]),
//                                                Integer.valueOf(timeRF[0]), 
//                                                Integer.valueOf(timeRF[1]), 
//                                                Integer.valueOf(timeRF[2]));
                smsBody.setDateTime(cRF.getTime());
                
                return smsBody;
            case TNB:
                String[] dateTNB = smsBody.getDate().split("\\.");
                if (dateTNB.length != 3) return null;
                String yyyy = (dateTNB[2].length() == 2) ? "20"+dateTNB[2] : "2"+dateTNB[2];
                Calendar cTNB = Calendar.getInstance();
                cTNB.set(Calendar.YEAR, Integer.valueOf(yyyy));
                cTNB.set(Calendar.MONTH, Integer.valueOf(dateTNB[1])-1);
                cTNB.set(Calendar.DATE, Integer.valueOf(dateTNB[0])-1);
//                Calendar cTNB = new GregorianCalendar(
//                                                Integer.valueOf(yyyy), 
//                                                Integer.valueOf(dateTNB[1]), 
//                                                Integer.valueOf(dateTNB[0]));
                smsBody.setDateTime(cTNB.getTime());
                String[] timeTNB = smsBody.getTime().split("\\:");
                if (timeTNB.length != 3) return smsBody;
                cTNB.set(Calendar.HOUR, Integer.valueOf(timeTNB[0]));
                cTNB.set(Calendar.MINUTE, Integer.valueOf(timeTNB[1]));
                cTNB.set(Calendar.SECOND, Integer.valueOf(timeTNB[2]));
//                cTNB = new GregorianCalendar(
//                                                Integer.valueOf(yyyy), 
//                                                Integer.valueOf(dateTNB[1]), 
//                                                Integer.valueOf(dateTNB[0]), 
//                                                Integer.valueOf(timeTNB[0]), 
//                                                Integer.valueOf(timeTNB[1]), 
//                                                Integer.valueOf(timeTNB[2]));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                Log.i(LOG_TAG, "1 "+yyyy+"-"+dateTNB[1]+"-"+dateTNB[0]+" "
//                        +timeTNB[0]+":"+timeTNB[1]+":"+timeTNB[2]);
//                Log.i(LOG_TAG, "2 "+dateFormat.format(cTNB.getTime()));
                
                smsBody.setDateTime(cTNB.getTime());
                return smsBody;
            default:
                return null;
        }
    }
}
