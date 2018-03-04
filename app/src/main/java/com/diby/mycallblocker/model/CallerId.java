package com.diby.mycallblocker.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model to hold data for caller Id info from API call
 */

public class CallerId {
    @SerializedName("data")
    public Data data;

    public static class Data {
        public String name;
    }

}
