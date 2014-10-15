/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import ru.nosov.SMSreader.db.DBHelper;
import ru.nosov.SMSreader.db.Profile;
import ru.nosov.SMSreader.db.impl.ProfileImpl;
import ru.nosov.SMSreader.db.loaders.CursorLoaderProfile;

/**
 * Редактирование профилей.
 * @author Носов А.В.
 */
public class ActivityProfilesSettings extends Activity implements LoaderCallbacks<Cursor> {

    // Variables declaration
    /** Основная разметка. */
//    private LinearLayout layoutLinear;
    /** Список профилей. */
    private ListView listViewProfiles;
    /** Подключение к БД. */
    private SimpleCursorAdapter adapterProfile;
    private ProfileImpl profileImpl;
    /** Добавить профиль. */
    private Button buttonAdd;
    /** Редактировать профиль. */
    private Button buttonEdit;
    /** Удалить профиль. */
    private Button buttonDelete;
    private TextView textView;
    // End of variables declaration
    
    /**
     * Called when the activity is first created.
     * @param icicle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_profiles_settings);
        
//        layoutLinear = (LinearLayout) findViewById(R.id.layoutLinear);
        listViewProfiles = (ListView) findViewById(R.id.listViewProfiles);
        listViewProfiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        
        textView = (TextView) findViewById(R.id.textViewTest);
        
        String[] from = new String[] { Profile.COLUMN_ID,
                                       Profile.COLUMN_VISIBLE_NAME};
        int[] to = new int[] { R.id.textViewVisibleName, R.id.textViewSMSName};
        adapterProfile = new SimpleCursorAdapter( this, 
                                                  R.layout.list_profiles4settings, 
                                                  null, from, to, 0);
        profileImpl = new ProfileImpl(this);
        
        buttonAdd.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonEdit.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        buttonDelete.setOnClickListener( new OnClickListener() {

            public void onClick(View v) {
                buttonClick(v);
            }
        });
        
        listViewProfiles.setAdapter(adapterProfile);
        
        profileImpl.open();
        getLoaderManager().initLoader(DBHelper.LOADER_ID_PROFILE, null, this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_profiles_list, menu);
      return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_main:
                visibleMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileImpl != null) profileImpl.close();
    }
    
    /**
     * Переход на страницу ActivityMain.
     */
    private void visibleMainActivity() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
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
    
    public void buttonClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                break;
            case R.id.buttonDelete:
//                Object obj = listViewProfiles.getSelectedItem();
                String strID = String.valueOf(listViewProfiles.getSelectedItemId());
                String strV = "null";
                if (listViewProfiles.getSelectedView() != null)
                    strV = String.valueOf(listViewProfiles.getSelectedView().getId());
                String strPos = String.valueOf(listViewProfiles.getCheckedItemPosition());
                textView.setText(strID + " " + strV + " " + strPos);
                break;
            case R.id.buttonEdit:
                break;
            default:
        }
    }
}
