/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diby.mycallblocker.fragment;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.diby.mycallblocker.EspressoTestUtil;
import com.diby.mycallblocker.R;
import com.diby.mycallblocker.RecyclerViewMatcher;
import com.diby.mycallblocker.SingleFragmentActivity;
import com.diby.mycallblocker.TestUtil;
import com.diby.mycallblocker.ViewModelUtil;
import com.diby.mycallblocker.model.PhoneCall;
import com.diby.mycallblocker.viewmodel.CallViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing the fragment component
 */

@RunWith(AndroidJUnit4.class)
public class CallPageFragmentTest {
    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

    private CallViewModel viewModel;
    private MutableLiveData<PhoneCall[]> userData = new MutableLiveData<>();

    @Before
    public void init() throws Throwable {
        EspressoTestUtil.disableProgressBarAnimations(activityRule);
        CallPageFragment fragment = CallPageFragment.create(0);
        viewModel = mock(CallViewModel.class);
        when(viewModel.getPhoneCalls()).thenReturn(userData);

        fragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        activityRule.getActivity().setFragment(fragment);
    }

    @Test
    public void loadingWithPhoneCall() {
        PhoneCall phoneCall = TestUtil.createPhoneCall("4259501212", PhoneCall.CALL_TYPE_SUSPICIOUS);
        userData.postValue(new PhoneCall[]{phoneCall});
        onView(withId(R.id.phone_number)).check(matches(withText(phoneCall.phoneNumber)));

    }

    @Test
    public void nullUser() {
        userData.postValue(null);
        onView(withId(R.id.phone_number)).check(matches(not(isDisplayed())));
    }


    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recyclerView);
    }

}