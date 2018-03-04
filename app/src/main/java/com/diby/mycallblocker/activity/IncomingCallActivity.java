package com.diby.mycallblocker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.diby.mycallblocker.R;

/**
 * The activity to show the status of call types. For normal calls with number found in the contact
 * the background is in Blue, but for suspicious calls, it shows the Suspicious Call in Red.
 * It also shows the status on locked phone screen.
 */

public class IncomingCallActivity extends Activity {

    public static final String CALL_SUSPICIOUS = "Suspicious";
    public static final String CALLER_NAME = "Name";

    /**
     * Creates the screen with no title and width 80% for device screen width and 20% of screen
     * height.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Shows on device locked screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //No title showing
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.incoming_call_layout);

        //Re-sizes the screen 80% of screen width and 20% height.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.2));

        //Checks in intent if call is suspicious
        boolean isSuspiciousCall = getIntent().getBooleanExtra(CALL_SUSPICIOUS,false);
        String callerName = getIntent().getStringExtra(CALLER_NAME);
        //Sets call name
        TextView callerNameTxt = findViewById(R.id.caller_name);
        //sets call type icon
        ImageView callIcon = findViewById(R.id.call_icon);
        callerNameTxt.setText(callerName);
        if (isSuspiciousCall) { //for suspicious calls, sets the background and icon differently
            findViewById(R.id.notify_dialog).setBackgroundColor(
                    getResources().getColor(android.R.color.holo_red_dark));
            callerNameTxt.setText("Suspicious Call!");
            callIcon.setImageResource(R.drawable.span_call);
        }

        String number = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        TextView phoneNumber = findViewById(R.id.phone_number);
        phoneNumber.setText(number);

    }

}
