package com.suhaib.flappycheatNhack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.suhaib.flappycheatNhack.R;

public class AdsActivity extends Activity {

	private AdView adView;
	private String MY_AD_UNIT_ID;
	private Boolean adClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ads);

		// Create the adView.
		MY_AD_UNIT_ID = "a153062bad3fead";
		adView = new AdView(this);
		adView.setAdUnitId(MY_AD_UNIT_ID);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdOpened() {
				adClicked = true;
			}
		});
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.setOnClickListener(new LinearLayout.OnClickListener() {
			public void onClick(View v) {
				adClicked = true;
			}
		});
		layout.addView(adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	public void startMainActivity(View v) {
		if (adClicked) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else {
			new MainActivity().showToast(this,
					"Please click on the ad atleast once to support us.");
		}
	}

}
