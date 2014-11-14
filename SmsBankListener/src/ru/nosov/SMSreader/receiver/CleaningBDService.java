/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.BankAccountImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;

/**
 * Обработка сообщения.
 * @author Носов А.В.
 */
public class CleaningBDService extends Service {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "SmsBillingService";
        
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
        Log.d(LOG_TAG, "----- Start SmsBillingService");
        BankAccountImpl bankAccountImpl = new BankAccountImpl(this);
        
        ArrayList<BankAccount> bankAccounts = bankAccountImpl.getAllBankAccounts();
        if ( (bankAccounts == null) || (bankAccounts.isEmpty()) ) return stopService();
        
        Calendar cs = Calendar.getInstance();
        cs.set(Calendar.DATE, 1);
        cs.set(Calendar.HOUR, 0);
        cs.set(Calendar.MINUTE, 0);
        cs.set(Calendar.SECOND, 0);
//        Date ds = cs.getTime();
//        Log.d(LOG_TAG, "Кол-во счетов:" + bankAccounts.size());

        for (BankAccount bankAccount : bankAccounts) {
            CardImpl cardImpl = new CardImpl(this);
            ArrayList<Card> cards = cardImpl.getCardsByIDBankAccount(bankAccount.getId());
            
            if ( (cards == null) || (cards.isEmpty()) ) continue;
            
//            Log.d(LOG_TAG, "Счет: " + bankAccount.getName() + 
//                           "; Кол-во карт: " + cards.size());
            
            TransactionImpl transactionImpl = new TransactionImpl(this);
            ArrayList<Transaction> transactions = transactionImpl.getTransactionsByIDCards(cards);
            Collections.sort(transactions);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            Log.d(LOG_TAG, "Текущая дата/Первая транзакция: " 
//                    + dateFormat.format(cs.getTime()) + "/" 
//                    + transactions.get(0).getDateSQL() + "/"
//                    + cs.getTime().equals(getCalendarByTransaction(transactions.get(0)).getTime()));
            
            ArrayList<Transaction> delList = new ArrayList<Transaction>();
            for (Transaction transaction : transactions) {
                if (cs.getTime().equals(getCalendarByTransaction(transaction).getTime())) {
//                    startCleaning(delList);
//                    return stopService();
                    break;
                }
                // Если delList пустой, то это первая транзакция этого месяца
                if (delList.isEmpty()) {
                    delList.add(transaction);
                } else {
                    Calendar start = getCalendarByTransaction(delList.get(0));
                    Calendar next = getCalendarByTransaction(transaction);

                    dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    Log.d(LOG_TAG, "Проверка дат: " 
//                            + dateFormat.format(start.getTime())
//                            + "/" 
//                            + dateFormat.format(next.getTime()) 
//                            + "/" + start.getTime().equals(next.getTime()));

                    // Если год и месяц первой транзакции в delList совпадает
                    // с той, что в БД то положить в delList
//                        if ( (start.get(Calendar.YEAR) == next.get(Calendar.YEAR)) &&
//                             (start.get(Calendar.MONTH) == next.get(Calendar.MONTH)) ) {
                    if (start.getTime().equals(next.getTime())) {
                        delList.add(transaction);
                    } else {
                        startCleaning(delList);
                        // Новый месяц
                        delList = new ArrayList<Transaction>();
                        delList.add(transaction);
//                        Log.d(LOG_TAG, transaction.getDateSQL());
                    }
                }
            }
            startCleaning(delList);
        }
        
        return stopService();
    }
    
    private Calendar getCalendarByTransaction(Transaction t) {
        Calendar c = Calendar.getInstance();
        c.setTime(t.getDateTime());
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c;
    }
    
    /**
     * Запуск очистки БД.
     * @param transactions список транзакций текущего месяца
     */
    private void startCleaning(ArrayList<Transaction> transactions) {
        if ( (transactions != null) && (transactions.size() < 1) ) return;
//        Log.d(LOG_TAG, "Чистим");
        TransactionImpl transactionImpl = new TransactionImpl(this);
        transactionImpl.open();
        for (int i=0; i<transactions.size()-1; i++) {
            transactionImpl.deleteTransactionByID(transactions.get(i).getId());
//            Log.d(LOG_TAG, "- Удалили "+transactions.get(i).getId()+" "+
//                    transactions.get(i).getDateSQL());
        }
//        Log.d(LOG_TAG, "Остаток:" + transactions.get(transactions.size()-1).getDateSQL());
        transactionImpl.close();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "----- Stop SmsBillingService");
    }
    
    /**
     * Остановка сервиса.
     * @return 
     */
    private int stopService() {
        this.stopSelf();
        return START_NOT_STICKY;
    }
}
