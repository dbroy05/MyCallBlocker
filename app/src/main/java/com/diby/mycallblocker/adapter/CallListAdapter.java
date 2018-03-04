package com.diby.mycallblocker.adapter;

/**
 * Created by rdibyendu on 2/28/18.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diby.mycallblocker.R;
import com.diby.mycallblocker.fragment.CallPageFragment;
import com.diby.mycallblocker.model.PhoneCall;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.ViewHolder> {
    private static final String TAG = "CallListAdapter";
    private final CallPageFragment mFragment;

    private PhoneCall[] mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public final ImageView callerTypeImg;
        public ImageView cta;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.phone_number);
            callerTypeImg = v.findViewById(R.id.caller_type_icon);
            cta = v.findViewById(R.id.cta);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet PhoneCall[] containing the data to populate views to be used by RecyclerView.
     * @param fragment
     */
    public CallListAdapter(PhoneCall[] dataSet, CallPageFragment fragment) {
        mDataSet = dataSet;
        mFragment = fragment;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        final PhoneCall phoneCall = mDataSet[position];
        String formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneCall.phoneNumber,"US");
        viewHolder.getTextView().setText(formattedPhoneNumber);
        viewHolder.callerTypeImg.setImageResource(R.drawable.normal_profile);
        viewHolder.cta.setImageResource(R.drawable.info);
        if (phoneCall.callType == PhoneCall.CALL_TYPE_SUSPICIOUS) {
            viewHolder.callerTypeImg.setImageResource(R.drawable.span_call);
        } else if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
            viewHolder.callerTypeImg.setImageResource(R.drawable.blocked_call);
            viewHolder.cta.setImageResource(R.drawable.unblock);
        }

        viewHolder.cta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mFragment.getString(R.string.dialog_block_message,phoneCall.phoneNumber);
                String title = mFragment.getString(R.string.dialog_title);
                String buttonLabel = mFragment.getString(R.string.block_label);
                if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                    title = mFragment.getString(R.string.dialog_unblocktitle);
                    message = mFragment.getString(R.string.dialog_unblock_message,phoneCall.phoneNumber);
                    buttonLabel = mFragment.getString(R.string.unblock_label);
                }
                //Sets up the dialog when the info icon is clicked from the list item.
                AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
                builder.setMessage(message)
                        .setTitle(title).setIcon(R.drawable.block_tab)
                        .setPositiveButton(buttonLabel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                                    phoneCall.callType = PhoneCall.CALL_TYPE_NORMAL;
                                    mFragment.unBlockCall(phoneCall);
                                } else {
                                    phoneCall.callType = PhoneCall.CALL_TYPE_BLOCKED;
                                    mFragment.blockCall(phoneCall);
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }


    /**
     * Interface to handle Block and Unblock call types.
     */
    public interface BlockCallHandler {
        public void blockCall(PhoneCall phoneCall);

        void unBlockCall(PhoneCall phoneCall);
    }
}

