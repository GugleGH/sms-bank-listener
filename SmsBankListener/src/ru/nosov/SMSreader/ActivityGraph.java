/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        idProfile = intent.getIntExtra("idProfile", -1);
        
        layoutLinearGraph = (LinearLayout) findViewById(R.id.layoutLinearGraph);
        
//        String name = getProfileName();
        
        ArrayList<BankAccount> bas = getBankAccountsByProfile();
        
//        Log.d(LOG_TAG, "Профиль ID=" + idProfile + "; " + name + "; BA=" + bas.size());
        
        //LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        //layoutLinearGraph.addView(createGraphView(bas));
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
        Cursor c = profileImpl.getProfileByID(idProfile);
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
        
        ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        
        BankAccountImpl bankAccountImpl = new BankAccountImpl(this);
        bankAccountImpl.open();
        Cursor c = bankAccountImpl.getBankAccountsByIDProfile(idProfile);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(BankAccount.COLUMN_ID);
            int baIndex = c.getColumnIndex(BankAccount.COLUMN_NAME);
            
            do {
                BankAccount ba = new BankAccount();
                ba.setId(c.getInt(idIndex));
                ba.setName(c.getString(baIndex));
                bankAccounts.add(ba);
            } while (c.moveToNext());
        }

        bankAccountImpl.close();
        
        return bankAccounts;
    }
    
    /**
     * Возвращает список карт банковского счета.
     * @param account банковский счет
     * @return список карт
     */
    private ArrayList<Card> getCardsByIDBankAccount(BankAccount account) {
        if (account == null) return null;
        
        ArrayList<Card> cards = new ArrayList<Card>();
        
        CardImpl cardImpl = new CardImpl(this);
        cardImpl.open();
        Cursor c = cardImpl.getCardsByIDBankAccount(account.getId());
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(Card.COLUMN_ID);
            int cnIndex = c.getColumnIndex(Card.COLUMN_CARD_NUMBER);
            // TODO тут
            do {
                Card card = new Card();
                card.setId(c.getInt(idIndex));
//                card.setIdBankAccount(c.getInt(idpIndex));
//                card.setId_bankAccount(account.getId());
                card.setCardNumber(c.getString(cnIndex));
                cards.add(card);
            } while (c.moveToNext());
        }

        cardImpl.close();
        
        return cards;
    }
    
    /**
     * Возвращает список транзакций для карт.
     * @param cards карты
     * @return список транзакций
     */
    private ArrayList<Transaction> getTransactionsByIDCard(ArrayList<Card> cards) {
        if ( (cards == null) || (cards.size() < 1) ) return null;
        
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        
        TransactionImpl transactionsImpl = new TransactionImpl(this);
        transactionsImpl.open();
        
        for (Card card : cards) {
            Cursor c = transactionsImpl.getTransactionsByIDCard(card.getId());
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex(Transaction.COLUMN_ID);
                int dIndex = c.getColumnIndex(Transaction.COLUMN_DATE);
                int aIndex = c.getColumnIndex(Transaction.COLUMN_AMOUNT);
                int bIndex = c.getColumnIndex(Transaction.COLUMN_BALANCE);

                do {
                    try {
                        Transaction tr = new Transaction();
                        tr.setId(c.getInt(idIndex));
                        tr.setDateSQL(c.getString(dIndex));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        tr.setDateTime(dateFormat.parse(c.getString(dIndex)));
                        tr.setAmount(c.getFloat(aIndex));
                        tr.setBalace(c.getFloat(bIndex));
                        transactions.add(tr);
                    } catch (ParseException ex) {
                        Log.e(LOG_TAG, c.getString(dIndex) + " --> " + ex.getMessage());
                    }
                } while (c.moveToNext());
            }
        }

        transactionsImpl.close();
        
        Collections.sort(transactions);
        
        return transactions;
    }
    
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
            
            for (int i=0; i<ts.size(); i++) {
                Transaction t = ts.get(i);
                double d = t.getDateTime().getTime();
//                double b = t.getBalace();
                String res = dateFormat.format(new Date((long)d)) + 
                        "\n(" + t.getAmount() + ")";
                series.add(d, t.getBalace());
                series.addAnnotation(res, d, t.getBalace());
                
                
//                mRenderer.addXTextLabel(d, res);
//                mRenderer.addYTextLabel(t.getBalace(), String.valueOf(t.getBalace()));
            }
            String desc = ba.getName() + " / " + ts.get(ts.size()-1).getBalace();
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
    
}
