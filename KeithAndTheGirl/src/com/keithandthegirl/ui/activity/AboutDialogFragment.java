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
package com.keithandthegirl.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
public class AboutDialogFragment extends DialogFragment {

	private static final String TAG = AboutDialogFragment.class.getSimpleName();
	
    /**
     * @return
     */
    public static AboutDialogFragment newInstance() {
    	return new AboutDialogFragment();
    }

    /* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateDialog : enter" );

		Log.v( TAG, "onCreateDialog : exit" );
		return new AlertDialog.Builder( getActivity() )
			.setView( getActivity().getLayoutInflater().inflate( R.layout.fragment_about, null ) )
        	.setIcon( android.R.drawable.ic_dialog_info )
        	.setTitle( getResources().getString( R.string.about_header ) )
        	.setPositiveButton( R.string.about_close,
        		new DialogInterface.OnClickListener() {
               		public void onClick( DialogInterface dialog, int whichButton ) {
               			getDialog().dismiss();
               		}
            	}
        	)
        	.setCancelable( true )
        	.show();
	}

}
