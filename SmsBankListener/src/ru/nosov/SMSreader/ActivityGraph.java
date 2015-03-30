/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;
import ru.nosov.SMSreader.db.BankAccount;
import ru.nosov.SMSreader.db.Card;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.Transaction;
import ru.nosov.SMSreader.db.impl.BankAccountImpl;
import ru.nosov.SMSreader.db.impl.CardImpl;
import ru.nosov.SMSreader.db.impl.ProfileImpl;
import ru.nosov.SMSreader.db.impl.TransactionImpl;
import ru.nosov.SMSreader.utils.Util;

/**
 * График.
 * @author Носов А.В.
 */
public class ActivityGraph extends Activity {
    
    // Variables declaration
    private final String LOG_TAG = LOG_NAME + "ActivityGraph";
    public static final String ID_PROFILE = "idProfile";
    
    /** Основная разметка. */
    private LinearLayout layoutLinearGraph;
    /** Идентификатор профиля. */
    private int idProfile;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private XYSeries series;
    private XYSeriesRenderer renderer;
//    private int nextColor = Color.GREEN;
    // End of variables declaration
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        Log.i(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        idProfile = intent.getIntExtra(ID_PROFILE, -1);
        
        layoutLinearGraph = (LinearLayout) findViewById(R.id.layoutLinearGraph);
        
//        String name = getProfileName();
        
        ArrayList<BankAccount> bas = getBankAccountsByProfile();
//        Log.d(LOG_TAG, "Профиль ID=" + idProfile + "; BA=" + bas.size());
//        Log.d(LOG_TAG, "Профиль ID=" + idProfile + "; " + name + "; BA=" + bas.size());
        
        layoutLinearGraph.addView(createAchartEngine(bas));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.graph_menu_main:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Возвращает имя профиля.
     * @return имя профиля
     */
    private String getProfileName() {
        if (idProfile < 0) return null;
        String name = null;
        ProfileImpl profileImpl = new ProfileImpl(this);
        profileImpl.open();
        Cursor c = profileImpl.getCursorProfileByID(idProfile);
        if (c.moveToFirst()) {
//            int idIndex = c.getColumnIndex(Profile.COLUMN_ID);
            int vnIndex = c.getColumnIndex(Profile.COLUMN_VISIBLE_NAME);

            do {
                name = c.getString(vnIndex);
            } while (c.moveToNext());
        }

        profileImpl.close();
        
        return name;
    }
    
    /**
     * Возвращает список счетов профиля.
     * @return список счетов
     */
    private ArrayList<BankAccount> getBankAccountsByProfile() {
        if (idProfile < 0) return null;
        
        BankAccountImpl bankAccountImpl = new BankAccountImpl(this);
        
        return bankAccountImpl.getBankAccountsByIDProfile(idProfile);
    }
    
    /**
     * Возвращает список карт банковского счета.
     * @param account банковский счет
     * @return список карт
     */
    private ArrayList<Card> getCardsByIDBankAccount(BankAccount account) {
        if (account == null) return null;
        
        CardImpl cardImpl = new CardImpl(this);
        ArrayList<Card> cards = cardImpl.getCardsByIDBankAccount(account.getId());
//        Log.d(LOG_TAG, "Card count:" + cards.size());
        return cards;
//        return cardImpl.getCardsByIDBankAccount(account.getId());
    }
    
    /**
     * Возвращает список транзакций для карт.
     * @param cards карты
     * @return список транзакций
     */
    private ArrayList<Transaction> getTransactionsByIDCard(ArrayList<Card> cards) {
        if ( (cards == null) || (cards.size() < 1) ) return null;
        
        TransactionImpl transactionsImpl = new TransactionImpl(this);
        ArrayList<Transaction> transactions = transactionsImpl.getTransactionsByIDCards(cards);
//        Log.d(LOG_TAG, "Transaction count:" + transactions.size());
        Collections.sort(transactions);
        
        return transactions;
    }
    
    /**
     * Построение графика для профиля.
     * @param bas список аккаунтов у профиля
     * @return график
     */
    private GraphicalView createAchartEngine(ArrayList<BankAccount> bas) {
        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();
        
        mRenderer.setAntialiasing(true);
        mRenderer.setYLabelsAlign(Paint.Align.CENTER);
//        mRenderer.setDisplayValues(true);
//            mRenderer.setShowAxes(false);
//        mRenderer.setShowCustomTextGrid(true);
        mRenderer.setShowGrid(true);
//            mRenderer.setShowGridX(false);
//            mRenderer.setShowGridY(false);
        mRenderer.setShowLabels(true);
//            mRenderer.setShowLegend(false);
//        mRenderer.setZoomRate(0.5f);
        mRenderer.setYAxisMin(0);
//        mRenderer.setXLabels(0);
//        mRenderer.setYLabels(0);
        
//        mRenderer.setAxisTitleTextSize(mRenderer.getAxisTitleTextSize()/2);
//        mRenderer.setLabelsTextSize(mRenderer.getLabelsTextSize()/2);
        
        mRenderer.setZoomButtonsVisible(true);
        
//        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\nhh:mm:ss");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int nextColor = 0;
        for (BankAccount ba : bas) {
//            series = new XYSeries("");
            series = new TimeSeries("");
            renderer = new XYSeriesRenderer();
//            float size = renderer.getAnnotationsTextSize();
//            renderer.setAnnotationsTextSize(size/2);
            renderer.setAnnotationsTextAlign(Paint.Align.RIGHT);
            renderer.setColor(Util.nextColor4Graph(nextColor++));
            
            ArrayList<Transaction> ts = getTransactionsByIDCard(getCardsByIDBankAccount(ba));
            if (ts.size() < 1) continue;
            
            float payment = 0;
            
            for (Transaction t : ts) {
                double d = t.getDateTime().getTime();
//                double b = t.getBalace();
                
                String annot;
                if (validateLastDayOfMonth(t))
                    annot = t.getAmount() + "\n(" + t.getBalace() + ")";
                else 
                    annot = dateFormat.format(new Date((long)d)) + 
                        "\n(" + t.getAmount() + ")";
                
                if (validateLastMoth(t)) {
                    payment = payment + t.getPayment_amount();
//                    Log.d(LOG_TAG, "Payment:" + String.valueOf(t.getPayment_amount()));
                }
                
                series.add(d, t.getBalace());
                series.addAnnotation(annot, d, t.getBalace());
            }
            String desc = ba.getName() 
                    + " / " + String.valueOf(payment)
                    + " / " + ts.get(ts.size()-1).getBalace();
            series.setTitle(desc);
            
            renderer.setPointStyle(PointStyle.CIRCLE);
            renderer.setFillPoints(true);
            
            mDataset.addSeries(series);
            mRenderer.addSeriesRenderer(renderer);
            
//            Log.d(LOG_TAG, "Color=" + nextColor);
//            nextColor = Util.getColor4Graph(nextColor);
        }
        
//        GraphicalView graphicalView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
        GraphicalView graphicalView = ChartFactory.getTimeChartView(this, 
                                mDataset, mRenderer, "yyyy-MM-dd");
        return graphicalView;
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
        return Util.validateYYYYMMDDTT(start, next);
    }
    
    /**
     * Возвращает <i>true</i> если год и месяц в транзакции совпадают с 
     * системным годом и месяцем.
     * @param t транзакция
     * @return <b>true</b> - год/месяц совпали,
     * <b>false</b> - год/месяц/ НЕ совпали.
     */
    private boolean validateLastMoth(Transaction t) {
        Calendar dateT = Util.getLastDayOfMonth(t.getDateTime());
        Calendar dateS = Calendar.getInstance();
        return Util.validateYYYYMM(dateT, dateS);
    }
}
