package com.diby.mycallblocker.receiver;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.persistence.room.util.StringUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.diby.mycallblocker.activity.IncomingCallActivity;
import com.diby.mycallblocker.model.CallerId;
import com.diby.mycallblocker.model.PhoneCall;
import com.diby.mycallblocker.repository.CallRepository;
import com.diby.mycallblocker.util.NotificationUtils;

import java.lang.reflect.Method;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The main incoming call processor to apply various rules to determine call types and notifies the
 * user for the same.
 */

public class PhoneCallBroadcastReceiver extends BroadcastReceiver {

    @Inject
    public CallRepository callRepo;

    TelephonyManager telephonyManager;
    NotificationUtils mNotificationUtils;
    NotificationCompat.Builder mBuilder;
    private int mNotificationId = 111111;
    private Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {

        AndroidInjection.inject(this, context);

        mContext = context;

        // TELEPHONY MANAGER class object to register one listner
        telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        //Create Listener
        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

        // Register listener for LISTEN_CALL_STATE
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        mNotificationUtils = new NotificationUtils(context);

    }

    /**
     * Listener for handling the call state
     */
    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            if ( state == TelephonyManager.CALL_STATE_RINGING) {

                //Check if the number exists in Contacts
                String contactName = getContactNameInContacts(incomingNumber);
                if (contactName != null) {  //If number found in contact, just notify normal call.
                    notifyCall(false, incomingNumber, contactName);
                } else {    //If not in contact, try to get Call Info from the repository
                    callRepo.getCallInfo(incomingNumber,
                            new CallRepository.OnCompleteListener() {
                                @Override
                                public void onComplete(PhoneCall phoneCall) {
                                    switch (phoneCall.callType) {
                                        case PhoneCall.CALL_TYPE_SUSPICIOUS :
                                            //For suspicious call, notify suspicious call
                                            notifyCall(true, incomingNumber, null);
                                            break;
                                        case PhoneCall.CALL_TYPE_BLOCKED :
                                            //For blocked call, just block the call
                                            blockCall(incomingNumber);
                                            break;

                                        default:
                                            //Block all calls that are not in contact by default
                                            blockCall(incomingNumber);
                                            break;
                                    }
                                }
                            });
                }



            }
        }

        /**
         * Check of the number is in Contact list
         * @param incomingPhoneNumber
         * @return name of the contact
         */
        private String getContactNameInContacts(String incomingPhoneNumber) {
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
            while (phones.moveToNext())
            {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phNumber = phNumber.replace("(","").replace(")","")
                            .replace("-","").replace(" ","");

                if (incomingPhoneNumber.contains(phNumber)) {
                    phones.close();
                    return name;
                }

            }
            phones.close();
            return null;
        }

        /**
         * Notifies user about the call
         * @param isSuspicious
         * @param incomingNumber
         * @param contactName
         */
        private void notifyCall(final boolean isSuspicious, final String incomingNumber, final String contactName) {
            //Main intent to show the call status on an Activity
            final Intent intent = new Intent(mContext, IncomingCallActivity.class);
            intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            if ( !isSuspicious ) { //For non suspicious calls
                //If number exists in Contacts, pass the contact name, no need to call CallerId service
                if (contactName != null && contactName.matches("\\d+(?:\\.\\d+)?")) {
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            intent.putExtra(IncomingCallActivity.CALLER_NAME,contactName);
                            mContext.startActivity(intent);
                        }
                    },1000);
                } else {
                    //Get caller id only when the name is not in contact
                    callRepo.getCallerId(incomingNumber).enqueue(new Callback<CallerId>() {
                        @Override
                        public void onResponse(final Call<CallerId> call, final Response<CallerId> response) {
                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    intent.putExtra(IncomingCallActivity.CALLER_NAME,response.body().data.name);
                                    mContext.startActivity(intent);
                                }
                            },500);
                        }

                        @Override
                        public void onFailure(Call<CallerId> call, Throwable t) {
                        }
                    });
                }

            } else { //For suspicious call set the suspicious flag for the showing activity
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        intent.putExtra(IncomingCallActivity.CALL_SUSPICIOUS, true);
                        mContext.startActivity(intent);
                    }
                },1000);

                //Notify user about suspicious info, so they can check later
                mBuilder = mNotificationUtils.getChannelNotification("Suspicious Call!"
                        ,"You got a suspicious call from number "+incomingNumber);
                mNotificationUtils.getManager().notify(mNotificationId, mBuilder.build());
            }

        }

        /**
         * Blockes the incoming call
         * @param incomingNumber
         */
        private void blockCall(String incomingNumber) {
            try {
                //Gets the TelephonyManager's getITelephony by reflection
                Method m = telephonyManager.getClass().getDeclaredMethod("getITelephony");

                m.setAccessible(true);
                ITelephony telephonyService = (ITelephony) m.invoke(telephonyManager);

                if ((incomingNumber != null)) {
                    telephonyService.endCall(); //Ends the call, so it goes straight to VoiceMail
                }
                //Record the call on local db
                callRepo.saveCall(new PhoneCall(incomingNumber,PhoneCall.CALL_TYPE_BLOCKED));

                //Notify user about the blocked call info
                mBuilder = mNotificationUtils.getChannelNotification("Blocked Call"
                        ,"The call from number "+incomingNumber+" has been blocked.");
                mNotificationUtils.getManager().notify(mNotificationId, mBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
