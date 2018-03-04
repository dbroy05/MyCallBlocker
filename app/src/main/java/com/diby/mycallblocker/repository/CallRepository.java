package com.diby.mycallblocker.repository;

import android.arch.lifecycle.LiveData;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.diby.mycallblocker.api.CallerIdLookupService;
import com.diby.mycallblocker.dao.PhoneCallDao;
import com.diby.mycallblocker.model.CallerId;
import com.diby.mycallblocker.model.PhoneCall;
import com.diby.mycallblocker.service.CallInfoService;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

/**
 * Main repository to manage data from different sources : API Services or local DataSource
 */
@Singleton
public class CallRepository {

    /**
     * Uses DAO to handle data in local database
     */
    private final PhoneCallDao phoneCallDao;
    /**
     * Uses API service to determine call types
     */
    private final CallInfoService callInfoService;

    /**
     * CallerId look up service to show the caller id on phone call
     */
    private final CallerIdLookupService callerIdLookupService;

    /**
     * Injected by Dagger DI
     * @param callerIdLookupService
     * @param callInfoService
     * @param phoneCallDao
     */
    @Inject
    public CallRepository(CallerIdLookupService callerIdLookupService, CallInfoService callInfoService, PhoneCallDao phoneCallDao) {
        this.callerIdLookupService = callerIdLookupService;
        this.phoneCallDao = phoneCallDao;
        this.callInfoService = callInfoService;
    }

    /**
     * Returns the PhoneCall LiveData from DAO based on call type
     * @param callType
     * @return
     */
    public LiveData<PhoneCall[]> getCalls(int callType) {
        return phoneCallDao.loadByCallType(callType);
    }

    /**
     * Saves the phone call to local DB thru DAO
     * @param phoneCall
     */
    public void saveCall(final PhoneCall phoneCall) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                phoneCallDao.save(phoneCall);
                return null;
            }
        }.execute();
    }

    /**
     * Delete a call from local db
     * @param phoneCall
     */
    public void deleteCall(final PhoneCall phoneCall) {
        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                phoneCallDao.delete(phoneCall);
                return null;
            }
        }.execute();
    }

    /**
     * Checks if the number call type is found locally, if not fetches from the callInfoService API
     * and saves locally.
     * @param phoneNumber
     * @param listener
     */
    public void getCallInfo(final String phoneNumber, final OnCompleteListener listener) {

        new AsyncTask<Void, Void, PhoneCall>() {

            @Override
            protected PhoneCall doInBackground(Void... voids) {
                PhoneCall phoneCall =  phoneCallDao.loadByNumber(phoneNumber);
                if (phoneCall == null) {
                    phoneCall = callInfoService.getCallInfo(phoneNumber);
                    if (phoneCall.callType == PhoneCall.CALL_TYPE_SUSPICIOUS
                            || phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                        phoneCallDao.save(phoneCall);
                    }

                }
                return phoneCall;
            }

            @Override
            protected void onPostExecute(PhoneCall phoneCall) {
                listener.onComplete(phoneCall);
            }
        }.execute();



    }

    /**
     * Interface to be passed for handling result from getCallInfo method call
     */
    public interface OnCompleteListener {
        public void onComplete(PhoneCall phoneCall);
    }

    /**
     * Returns the handle for the result of Caller id info of a number
     * @param phoneNumber
     * @return
     */
    public Call<CallerId> getCallerId(String phoneNumber) {
        return callerIdLookupService.getCallerId(phoneNumber);
    }

}
