package com.diby.mycallblocker.api;

import android.arch.lifecycle.LiveData;

import com.diby.mycallblocker.model.CallerId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * API to figure the caller id for a phone number
 */

public interface CallerIdLookupService {

    @GET("phone/{number}?account_sid=AC44b9defc54c94d2e8ec4f665fdb44c29&auth_token=AUa8701159cc244440ac590ed6a1290f16&data=name")
    Call<CallerId> getCallerId(@Path("number") String number);
}
