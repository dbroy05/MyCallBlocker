package com.diby.mycallblocker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Holds phone call data to be persisted on local db
 */
@Entity(primaryKeys = "phone_number")
public class PhoneCall {
    public static final int CALL_TYPE_NORMAL = 1;
    public static final int CALL_TYPE_SUSPICIOUS = 2;
    public static final int CALL_TYPE_BLOCKED = 3;

    @NonNull
    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "call_type")
    public int callType;

    public PhoneCall(String phoneNumber, int callType) {
        this.phoneNumber = phoneNumber;
        this.callType = callType;
    }

}
