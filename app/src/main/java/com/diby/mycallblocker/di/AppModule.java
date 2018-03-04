package com.diby.mycallblocker.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.diby.mycallblocker.api.CallerIdLookupService;
import com.diby.mycallblocker.dao.PhoneCallDao;
import com.diby.mycallblocker.database.CallDatabase;
import com.diby.mycallblocker.service.CallInfoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Manages the Provider for all different components like DAO and Services
 */
@Module(includes = ViewModelModule.class)
class AppModule {

    @Singleton @Provides
    CallerIdLookupService provideCallerIdLookupService() {
        return new Retrofit.Builder()
                .baseUrl("https://api.everyoneapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CallerIdLookupService.class);
    }

    @Singleton
    @Provides
    CallDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, CallDatabase.class,"phonecall.db").build();
    }

    @Singleton @Provides
    PhoneCallDao provideUserDao(CallDatabase db) {
        return db.phoneCallDao();
    }

    @Singleton @Provides
    CallInfoService provideCallInfoService() {
        return new CallInfoService();
    }
}
