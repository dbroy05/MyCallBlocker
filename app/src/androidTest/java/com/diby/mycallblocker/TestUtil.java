package com.diby.mycallblocker;

import com.diby.mycallblocker.model.PhoneCall;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static PhoneCall createPhoneCall(String number, int callType) {
        return new PhoneCall(number,callType);
    }

}
