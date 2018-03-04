package com.diby.mycallblocker.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.telephony.PhoneNumberUtils;

import com.diby.mycallblocker.model.PhoneCall;
import com.diby.mycallblocker.repository.CallRepository;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

/**
 * ViewModel that drives the CallPageFragment UI data. It abstracts the logic from fragment
 * to determine data for three different call types.
 */

public class CallViewModel extends ViewModel {

    /**
     * Uses repository to load data for different call types
     */
    private final CallRepository callRepo;

    /**
     * Holds LiveData on list of PhoneCalls to be observed by fragment
     */
    private LiveData<PhoneCall[]> phoneCalls;

    @Inject // CallRepository parameter is provided by Dagger 2
    public CallViewModel(CallRepository callRepo) {
        this.callRepo = callRepo;
    }

    /**
     * Loads data based on call types and makes that ready for fragment
     * @param ctx
     * @param position
     */
    public void init(Activity ctx, int position) {

        if (position == 0) { //Recent Calls
            MutableLiveData<PhoneCall[]> mutableLiveData = getRecentCallLiveData(ctx);
            phoneCalls = mutableLiveData;

        } else if (position == 1) { //Suspicious Calls
            phoneCalls = callRepo.getCalls(PhoneCall.CALL_TYPE_SUSPICIOUS);
        } else if (position == 2) { //Blocked Calls
            phoneCalls = callRepo.getCalls(PhoneCall.CALL_TYPE_BLOCKED);
        }
    }

    /**
     * Loads recent call from device phone call logs
     * @param ctx
     * @return
     */
    @NonNull
    private MutableLiveData<PhoneCall[]> getRecentCallLiveData(Activity ctx) {
        ArrayList<PhoneCall> normalCalls = new ArrayList<>();
        Cursor managedCursor = ctx.managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, null);
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);

            if (phNumber != null && !phNumber.isEmpty()) {
                if ( !numberExist(normalCalls, phNumber) ) {
                    normalCalls.add(new PhoneCall(phNumber, PhoneCall.CALL_TYPE_NORMAL));
                }
            }

        }
        //Reverses to show the latest on top of the list
        Collections.reverse(normalCalls);
        //Creates the LiveData with recent calls
        MutableLiveData<PhoneCall[]> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(normalCalls.toArray(new PhoneCall[normalCalls.size()]));
        return mutableLiveData;
    }

    /**
     * Util method to check if number already exists in the list
     * @param normalCalls
     * @param phNumber
     * @return
     */
    private boolean numberExist(ArrayList<PhoneCall> normalCalls, String phNumber) {
        for (PhoneCall phoneCall : normalCalls) {
            if (phoneCall.phoneNumber.contains(phNumber)) return true;
        }
        return false;
    }

    @VisibleForTesting
    public LiveData<PhoneCall[]> getPhoneCalls() {
        return this.phoneCalls;
    }

    public CallRepository getCallRepo() {
        return callRepo;
    }

}
