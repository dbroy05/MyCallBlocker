package com.diby.mycallblocker.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diby.mycallblocker.R;
import com.diby.mycallblocker.adapter.CallListAdapter;
import com.diby.mycallblocker.di.Injectable;
import com.diby.mycallblocker.model.PhoneCall;
import com.diby.mycallblocker.viewmodel.CallViewModel;

import javax.inject.Inject;

/**
 * Shows list of calls for three different call types. Handles blocking or unblocking calls from
 * the list.
 */

public class CallPageFragment extends Fragment implements Injectable, CallListAdapter.BlockCallHandler {

    public static final String FRAGMENT_POS = "position";

    /**
     *The main list for showing the call list
     */
    protected RecyclerView mRecyclerView;

    /**
     * Adapter for the call list RecyclerView
     */
    private CallListAdapter mAdapter;

    /**
     * Holds the current position
     */
    private int position;

    /**
     * Injected by Dagger DI
     */
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    /**
     * Uses the ViewModel to drive data on view
     */
    private CallViewModel viewModel;

    /**
     * Creates the Fragment with position
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(FRAGMENT_POS);
        }
    }

    /**
     * On fragment view create, it creates the recyclerview with its layout manager
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    /**
     * Once activity created, the ViewModel is introduced to observe LiveData for any data change
     * to update UI accordingly to better lifecycle management.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Creates the viewmodel to initialize
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CallViewModel.class);
        viewModel.init(getActivity(), position);
        //Observes for PhoneCall LiveData to add adapter to the recyclerview
        viewModel.getPhoneCalls().observe(this, new Observer<PhoneCall[]>() {
            @Override
            public void onChanged(@Nullable PhoneCall[] phoneCalls) {
                mAdapter = new CallListAdapter(phoneCalls,CallPageFragment.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

    }

    /**
     * Handles blocking the call from the list item.
     * @param phoneCall
     */
    @Override
    public void blockCall(PhoneCall phoneCall) {
        viewModel.getCallRepo().saveCall(phoneCall);
    }

    /**
     * Handles unblocking the call from the list item.
     * @param phoneCall
     */
    @Override
    public void unBlockCall(PhoneCall phoneCall) {
        viewModel.getCallRepo().deleteCall(phoneCall);
    }

    /**
     * For testing
     * @param position
     * @return
     */
    public static CallPageFragment create(int position) {
        CallPageFragment userFragment = new CallPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_POS, position);
        userFragment.setArguments(bundle);
        return userFragment;
    }
}
