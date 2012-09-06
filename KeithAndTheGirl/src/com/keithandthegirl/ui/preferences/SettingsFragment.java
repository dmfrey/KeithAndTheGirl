/**
 * 
 */
package com.keithandthegirl.ui.preferences;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
@TargetApi( 11 )
public class SettingsFragment extends PreferenceFragment {

	private static final String TAG = SettingsFragment.class.getSimpleName();
	
	/* (non-Javadoc)
	 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );
		
		addPreferencesFromResource( R.xml.preferences );
		
		Log.v( TAG, "onCreate : exit" );
	}

	
}
