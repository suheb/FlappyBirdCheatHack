package com.suhaib.flappycheatNhack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {
	
	public static CustomDialogFragment newInstance(String msg, Boolean toggle) {
        CustomDialogFragment fragment = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putString("msg", msg);
        args.putBoolean("toggle", toggle);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    	final Boolean toggle = (Boolean) this.getArguments().get("toggle");
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.getArguments().get("msg").toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               if(toggle)
            	   getActivity().finish();
           }
        });
        if(!toggle)
        {
        	builder.setNegativeButton("Dev's FB Profile", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/suhebjerk"));
	       			startActivity(browserIntent);
                }
            });
        }
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
