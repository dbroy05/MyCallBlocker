package com.diby.mycallblocker.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.diby.mycallblocker.model.PhoneCall;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Data Access Object to handle/access data on local Room Database
 */

@Dao
public interface PhoneCallDao {
    //Saves a call
    @Insert(onConflict = REPLACE)
    void save(PhoneCall phoneCall);

    //Gets live data based on call type
    @Query("SELECT * FROM PhoneCall WHERE call_type = :callType")
    LiveData<PhoneCall[]> loadByCallType(int callType);

    //Checks if the number is found in local db
    @Query("SELECT * FROM PhoneCall WHERE phone_number = :phoneNumber")
    PhoneCall loadByNumber(String phoneNumber);

    //Deletes the phonecall from local db
    @Delete
    void delete(PhoneCall... phoneCall);
}
