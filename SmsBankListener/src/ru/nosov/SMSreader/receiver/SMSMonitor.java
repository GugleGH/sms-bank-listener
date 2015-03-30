/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.receiver;

import ru.nosov.SMSreader.services.SmsService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import static ru.nosov.SMSreader.ActivityMain.LOG_NAME;

/**
 * Принимаем входящее сообщение.
 * @author Носов А.В.
 */
public class SMSMonitor extends BroadcastReceiver {
    
    // Variables declaration
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final String LOG_TAG = LOG_NAME + "SMSMonitor";
    // End of variables declaration
    
    public void onReceive(Context context, Intent intent) {
        //Broadcast receiver получает в системе высокий приоритет, 
        // но он работает в фоновом режиме и должен выполняться за короткое 
        // время, так что наши возможности ограничены.
        
        if ( (intent != null) && 
                (intent.getAction() != null) && 
                (ACTION.compareToIgnoreCase(intent.getAction()) == 0) ) {
        
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }
            
            // Cоставляем текст сообщения (в случае, когда сообщение было 
            // длинным и пришло в нескольких смс-ках, каждая отдельная часть 
            // хранится в messages[i]) и вызываем метод abortBroadcast(), чтобы 
            // предотвратить дальнейшую обработку сообщения другими приложениями.
            String sms_from = messages[0].getDisplayOriginatingAddress();
            String sms_number = messages[0].getOriginatingAddress();
            Long time = messages[0].getTimestampMillis();
            StringBuilder bodyText = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                bodyText.append(messages[i].getMessageBody());
            }
            String sms_body = bodyText.toString();
            Intent smsIntent = new Intent(context, SmsService.class);
            smsIntent.putExtra(SmsService.SMS_DISPLAY_ADDRESS, sms_from);
            smsIntent.putExtra(SmsService.SMS_ORIGINATING_ADDRESS, sms_number);
            smsIntent.putExtra(SmsService.SMS_BODY, sms_body);
            smsIntent.putExtra(SmsService.SMS_TIME_SERVICE_CENTRE, time);
            
            context.startService(smsIntent);
            abortBroadcast();
        }
    }
}
