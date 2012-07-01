/**
 *  This file is part of KeithAndTheGirl for Android
 * 
 *  KeithAndTheGirl for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  KeithAndTheGirl for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeithAndTheGirl for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/dmfrey/KeithAndTheGirl/>
 *
 */
package com.keithandthegirl.activities;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
public class AboutActivity extends AbstractKatgActivity {

	private static final String TAG = AboutActivity.class.getSimpleName();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );

	    setContentView( R.layout.about );

	    final TextView description = (TextView) findViewById( R.id.about_description_text_view );
	    description.setMovementMethod( LinkMovementMethod.getInstance() );

	    Log.d( TAG, "onCreate : exit" );
	}

}
