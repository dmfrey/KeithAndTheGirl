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

import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Gravity;
import android.widget.Toast;

import com.keithandthegirl.MainApplication;

/**
 * @author Daniel Frey
 */
public abstract class AbstractKatgActivity extends Activity implements KatgActivity {
	
	protected static final String TAG = AbstractKatgActivity.class.getSimpleName();
	
	private ProgressDialog progressDialog;
	
	
	//***************************************
    // KatgActivity methods
    //***************************************
	
	/* (non-Javadoc)
	 * @see android.content.ContextWrapper#getApplicationContext()
	 */
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}
	
	/* (non-Javadoc)
	 * @see com.keithandthegirl.activities.KatgActivity#showProgressDialog()
	 */
	public void showProgressDialog() {
		showProgressDialog( "Loading. Please wait..." );
	}
	
	/* (non-Javadoc)
	 * @see com.keithandthegirl.activities.KatgActivity#showProgressDialog(java.lang.String)
	 */
	public void showProgressDialog( String message ) {
		if( progressDialog == null ) {
			progressDialog = new ProgressDialog( this );
			progressDialog.setIndeterminate( true );
		}
		
		progressDialog.setMessage( message );
		progressDialog.show();
	}
	
	/* (non-Javadoc)
	 * @see com.keithandthegirl.activities.KatgActivity#dismissProgressDialog()
	 */
	public void dismissProgressDialog() {
		if( progressDialog != null ) {
			progressDialog.dismiss();
		}
	}
	
	
	//***************************************
    // Protected methods
    //***************************************	
	protected void processException(Exception e) {
		if( null != e ) {
			if( e instanceof ResourceAccessException ) {
				displayNetworkError();
			}
		}
	}
	
	protected void displayNetworkError() {
		Toast toast = Toast.makeText( this, "A problem occurred with the network connection while attempting to communicate with KATG.", Toast.LENGTH_LONG );
		toast.setGravity( Gravity.CENTER, 0, 0 );
		toast.show();
	}
	
}
