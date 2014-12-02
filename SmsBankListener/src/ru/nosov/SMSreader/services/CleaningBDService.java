/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.Settings;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.BankAccountImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.SettingsImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;
import ru.nosov.SMSreader.utils.Util;

/**
 * Обработка сообщения.
 * @author Носов А.В.
 */
public class CleaningBDService extends Service {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "CleaningBDService";
        
//    private ExecutorService executor;
    // End of variables declaration
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
//        executor = Executors.newFixedThreadPool(1);
    }
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(LOG_TAG, "----- Start SmsBillingService");
        BankAccountImpl bankAccountImpl = new BankAccountImpl(this);
        // Может пока транзакциями? 39 урок
        ArrayList<BankAccount> bankAccounts = bankAccountImpl.getAllBankAccounts();
        if ( (bankAccounts == null) || (bankAccounts.isEmpty()) ) return stopService(false);
        
//        Log.d(LOG_TAG, "Кол-во счетов:" + bankAccounts.size());

        for (BankAccount bankAccount : bankAccounts) {
            CardImpl cardImpl = new CardImpl(this);
            ArrayList<Card> cards = cardImpl.getCardsByIDBankAccount(bankAccount.getId());
            
            if ( (cards == null) || (cards.isEmpty()) ) continue;
            
//            Log.d(LOG_TAG, "------------------------------");
//            Log.d(LOG_TAG, "Счет: " + bankAccount.getName() + 
//                           "; Кол-во карт: " + cards.size());
            
            TransactionImpl transactionImpl = new TransactionImpl(this);
            ArrayList<Transaction> transactions = transactionImpl.getTransactionsByIDCards(cards);
            Collections.sort(transactions);
            
            ArrayList<Transaction> delList = new ArrayList<Transaction>();
            for (Transaction transaction : transactions) {
                // Если delList пустой, то это первая транзакция этого месяца
                if (delList.isEmpty()) {
                    delList.add(transaction);
                } else {
                    Calendar start = Util.getFirstDayOfMonth(delList.get(0).getDateTime());
                    Calendar next = Util.getFirstDayOfMonth(transaction.getDateTime());

//                    Log.d(LOG_TAG, "Проверка дат: " 
//                            + Util.formatCalendarToSQL(start) + " | " 
//                            + Util.formatCalendarToSQL(next) + " / " 
//                            + start.getTime().equals(next.getTime()));

                    if (start.getTime().equals(next.getTime())) {
                        delList.add(transaction);
                    } else {
                        startCleaning(delList);
                        // Новый месяц
                        delList = new ArrayList<Transaction>();
                        delList.add(transaction);
//                        Log.d(LOG_TAG, "Новый месяц:" + transaction.getDateSQL());
                    }
                }
            }
            startCleaning(delList);
        }
        
        return stopService(true);
    }
    
    /**
     * Запуск очистки БД.
     * @param transactions список транзакций текущего месяца
     */
    private void startCleaning(ArrayList<Transaction> transactions) {
        if ( (transactions != null) && (transactions.size() < 1) ) return;
        if (validateCurrentDate(transactions.get(0))) return;
        
        if ( (transactions.size() == 1) && 
             validateLastDayOfMonth(transactions.get(0)) ) return;
        
        TransactionImpl transactionImpl = new TransactionImpl(this);
        transactionImpl.open();
        for (Transaction t : transactions) {
            transactionImpl.deleteTransactionByID(t.getId());
//            Log.d(LOG_TAG, "- Удалили "+transactions.get(i).getId()+" "+
//                    transactions.get(i).getDateSQL());
        }
        Transaction t = transactions.get(transactions.size()-1);
//        Log.d(LOG_TAG, "Последний:" + t.getDateSQL() + "  | " + Util.formatDateToSQL(t.getDateTime()));
        Calendar c = Util.getLastDayOfMonth(t.getDateTime());
        t.setDateSQL(Util.formatCalendarToSQL(c));
        float amount = amountMonth(transactions.get(0), transactions.get(transactions.size()-1));
        t.setAmount(amount);
        Log.d(LOG_TAG, "Новая дата:" + t.getDateSQL() + "; CID:" + t.getIdCard()
                       + " ; Затраты:" + String.valueOf(amount));
        transactionImpl.addTransaction(t);
        
        transactionImpl.close();
    }
    
    /**
     * Сверяет год/месяц в транзакции с текущей датой.
     * @param t транзакция
     * @return <b>true</b> - год/месяц совпали,
     * <b>false</b> - год/месяц НЕ совпали.
     */
    private boolean validateCurrentDate(Transaction t) {
        // Если год/месяц совпали то это текущая дата - выходим.
        Calendar start = Util.getFirstDayOfMonth(Calendar.getInstance().getTime());
        Calendar next = Util.getFirstDayOfMonth(t.getDateTime());
//        Log.d(LOG_TAG, "Текущая дата/Первая транзакция: " 
//                + Util.formatCalendarToSQL(start) + " | " 
//                + Util.formatCalendarToSQL(next) + " / "
//                + next.getTime().equals(start.getTime()));
        return Util.formatCalendarToSQL(start).equals(Util.formatCalendarToSQL(next));
    }
    
    /**
     * Сверяет год/месяц/день/время в транзакции с последним днем месяца.
     * @param t транзакция
     * @return <b>true</b> - год/месяц/день/время совпали,
     * <b>false</b> - год/месяц/день/время НЕ совпали.
     */
    private boolean validateLastDayOfMonth(Transaction t) {
        Calendar start = Util.getLastDayOfMonth(t.getDateTime());
        Calendar next = Calendar.getInstance();
        next.setTime(t.getDateTime());
        Log.d(LOG_TAG, "Последний день/Дата внутри: " 
                + Util.formatCalendarToSQL(start) + " | " 
                + Util.formatCalendarToSQL(next) + " / "
                + next.getTime().equals(start.getTime()));
        return Util.formatCalendarToSQL(start).equals(Util.formatCalendarToSQL(next));
    }
    
    /**
     * Расчет общих затрат в месяце.
     * @param start первая транзакция
     * @param end последняя транзакция
     * @return сумма затрат
     */
    private float amountMonth(Transaction start, Transaction end) {
        float as = start.getBalace();
        float ae = end.getBalace();
        
        return as - ae;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(LOG_TAG, "----- Stop SmsBillingService");
    }
    
    /**
     * Остановка сервиса.
     * @param b билинг прошел успешно
     * @return 
     */
    private int stopService(boolean b) {
        if (b) {
            SettingsImpl si = new SettingsImpl(this);
            Settings s = new Settings();
            s.setBilling(b);
            si.updateSettings(s);
        }
        this.stopSelf();
        return START_NOT_STICKY;
    }
}
