package com.diby.mycallblocker.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.diby.mycallblocker.R;
import com.diby.mycallblocker.fragment.CallPageFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * The main activity showing the list calls on three separate Fragments
 * for Recent, Suspicious and Blocked calls. It implements HasSupportFragmentInjector for Dagger DI.
 */

public class HomeActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;


    String[] pageTitles = new String[] {"Recent", "Suspicious", "Blocked"};
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Creates the Activity prompting for required device Permissions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setting up the top action bar with logo and app name
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.appbar_logo);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //Seeking required device permissions in order for app to work.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG
                        ,Manifest.permission.READ_CONTACTS};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            } else {
                //Already granted before, continue initializing the UI comoponents
                initUI();
            }
        } else {
            //For all older version continue
            initUI();
        }
    }

    /**
     * Handles when permissions are accepted or denied
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                //Init UI only when permissions are granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initUI();
                } else { // Permission denied, so app closes
                    Toast.makeText(this, "Permission NOT granted, the app will be closed!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                return;
            }
        }
    }

    /**
     * Initializing the UI components.
     */
    private void initUI() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //Three tabs would handle showing Recent, Suspicious and Blocked calls
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        //Setting up the tab icons
        tabLayout.getTabAt(0).setIcon(R.drawable.recent_tab);
        tabLayout.getTabAt(1).setIcon(R.drawable.spam_tab);
        tabLayout.getTabAt(2).setIcon(R.drawable.block_tab_icn);
    }

    /**
     * Required for Dagger to inject
     * @return
     */
    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    /**
     * A simple pager adapter that represents 3 CallPageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //For each viewpager position, a new CallPageFragment fragment is created
            Bundle bundle = new Bundle();
            bundle.putInt(CallPageFragment.FRAGMENT_POS, position);
            Fragment fragment = new CallPageFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        /*@Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }*/

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /**
     * Creates the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

}
