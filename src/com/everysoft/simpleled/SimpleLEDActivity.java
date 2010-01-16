package com.everysoft.simpleled;

import java.lang.reflect.Field;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.IHardwareService;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SimpleLEDActivity extends Activity implements OnClickListener {
	SharedPreferences prefs;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getBoolean("enabled", false)) {
			try {
				/* We know the Vibrator object has a reference to the hardware
				 * service, so get it using reflection.  This is evil, and will
				 * break if the implementation of Vibrator changes, but it's
				 * probably the cleanest way to get what we need.  Thanks to
				 * the codetastrophe blog for this idea! 
				 */
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				Field f = Class.forName(vibrator.getClass().getName()).getDeclaredField("mService");  
				f.setAccessible(true);
				IHardwareService hardware = (IHardwareService)f.get(vibrator);
				if (hardware.getFlashlightEnabled()) {
					hardware.setFlashlightEnabled(false);
				}
				else {
					hardware.setFlashlightEnabled(true);
				}
			} catch (Exception e) {
				Log.e("SimpleLED", e.getMessage());
			}
			finish();
		}
		else {
			setContentView(R.layout.main);
			findViewById(R.id.accept).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View arg0) {
		SharedPreferences.Editor pref_edit = prefs.edit();
		pref_edit.putBoolean("enabled", true);
		pref_edit.commit();
		finish();
	}
}