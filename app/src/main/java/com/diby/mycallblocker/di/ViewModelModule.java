package com.diby.mycallblocker.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.diby.mycallblocker.annotation.ViewModelKey;
import com.diby.mycallblocker.factory.CallViewModelFactory;
import com.diby.mycallblocker.viewmodel.CallViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Required by Dagger DI for binding the ViewModel required by Fragment
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CallViewModel.class)
    abstract ViewModel bindCallViewModel(CallViewModel callViewModel);

    @Binds abstract ViewModelProvider.Factory bindViewModelFactory(CallViewModelFactory factory);
}
