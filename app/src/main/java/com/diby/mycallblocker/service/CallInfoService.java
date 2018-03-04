package com.diby.mycallblocker.service;

import android.content.Context;

import com.diby.mycallblocker.model.PhoneCall;

import java.util.List;

import javax.inject.Singleton;

/**
 * Main service to figure the call type
 */

@Singleton
public class CallInfoService {
    final static String SPAM_CALL = "4259501212";
    final static String BLOCK_CALL = "2539501212";

    /*
    This method should call the actual service endpoint to verify call type : Suspicious or Blocked.
    Currently, it's just hardcoded.
     */

    public PhoneCall getCallInfo(String phoneNumber) {
        PhoneCall phoneCall = new PhoneCall(phoneNumber,PhoneCall.CALL_TYPE_NORMAL);
        //
        if (phoneNumber.contains(SPAM_CALL)) {
            phoneCall = new PhoneCall(phoneNumber,PhoneCall.CALL_TYPE_SUSPICIOUS);
        }

        if (phoneNumber.contains(BLOCK_CALL)) {
            phoneCall = new PhoneCall(phoneNumber,PhoneCall.CALL_TYPE_BLOCKED);
        }

        return phoneCall;
    }


}
