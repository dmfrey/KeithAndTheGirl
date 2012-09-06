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

import java.net.URLEncoder;

import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
public class FeedbackDialogFragment extends DialogFragment {

	private static final String TAG = FeedbackDialogFragment.class.getSimpleName();
	
	private static final String FEEDBACK_URL = "http://www.attackwork.com/Voxback/Comment-Form-Iframe.aspx";
	private static final String FEEDBACK_URL_ENCODER = "UTF-8";

	private static final String NAME_KEY = "NAME";
	private static final String LOCATION_KEY = "LOCATION";
	
	private EditText editName, editLocation, editComment;

	/**
     * @return
     */
    public static FeedbackDialogFragment newInstance() {
    	return new FeedbackDialogFragment();
    }

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateDialog : enter" );

		View v = getActivity().getLayoutInflater().inflate( R.layout.fragment_feedback, null );
		
	    editName = (EditText) v.findViewById( R.id.feedback_name );
	    editLocation = (EditText) v.findViewById( R.id.feedback_location );
	    editComment = (EditText) v.findViewById( R.id.feedback_comment );
	    
		SharedPreferences sharedPreferences = getActivity().getPreferences( Context.MODE_PRIVATE );
		String name = sharedPreferences.getString( NAME_KEY, "" );
		String location = sharedPreferences.getString( LOCATION_KEY, "" );
		
		editName.setText( name );
		editLocation.setText( location );

		Log.v( TAG, "onCreateDialog : exit" );
		return new AlertDialog.Builder( getActivity() )
			.setView( v )
        	.setIcon( android.R.drawable.ic_dialog_info )
        	.setTitle( getResources().getString( R.string.feedback_title ) )
        	.setPositiveButton( R.string.feedback_positive_button,
        		new DialogInterface.OnClickListener() {
               		public void onClick( DialogInterface dialog, int whichButton ) {
               			
        				String name = editName.getText().toString();
        				if( null != name && !"".equals( name ) ) {
        					name = name.trim();

            				if( name.length() > 50 ) {
            					name = name.substring( 0, 50 );
            				}
        				}
        				
        				String location = editLocation.getText().toString();
        				if( null != location && !"".equals( location ) ) {
        					location = location.trim();
        				
            				if( location.length() > 50 ) {
            					location = location.substring( 0, 50 );
            				}
        				}

        				String comment = editComment.getText().toString();
        				if( null != comment && !"".equals( comment ) ) {
        					comment = comment.trim();
        					
            				if( comment.length() > 512 ) {
            					comment = comment.substring( 0, 512 );
            				}
        				}
        				
        				savePreferences( NAME_KEY, name );
        				savePreferences( LOCATION_KEY, location );
        				
        				if( null != comment && !"".equals( comment ) ) {
        					new PostCommentTask().execute( name, location, comment );
        				}
        				
        				editComment.setText( "" );

               		}
            	}
        	)
        	.setNegativeButton( R.string.feedback_negative_button,
        		new DialogInterface.OnClickListener() {
               		public void onClick( DialogInterface dialog, int whichButton ) {
               			getDialog().dismiss();
               		}
            	}
        	)
        	.setCancelable( true )
        	.show();
	}

	// internal helpers
	
	private void savePreferences( String key, String value ) {
    	SharedPreferences sharedPreferences = getActivity().getPreferences( Context.MODE_PRIVATE );
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putString( key, value );
    	editor.commit();
    }

    private class PostCommentTask extends AsyncTask<String, Void, String> {
    	
    	private Exception exception;
    	
    	@Override
    	protected String doInBackground( String... params ) {
    		
    		try {
    			RestTemplate template = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );

    			String name = params[ 0 ];
    			String location = params[ 1 ];
    			String comment = params[ 2 ];
    			
    			String encodedName = ( null != name && !"".equals( name ) ) ? URLEncoder.encode( name, FEEDBACK_URL_ENCODER ) : "";
    			String encodedLocation = ( null != location && !"".equals( location ) ) ? URLEncoder.encode( location, FEEDBACK_URL_ENCODER ) : "";
    			String encodedComment = ( null != comment && !"".equals( comment ) ) ? URLEncoder.encode( comment, FEEDBACK_URL_ENCODER ) : "";

    			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    			map.add( "Name", encodedName );
    			map.add( "Location", encodedLocation );
    			map.add( "Comment", encodedComment );
    			map.add( "ButtonSubmit", "Send+Comment" );
    			map.add( "HiddenVoxbackId", "3" );
    			map.add( "HiddenMixerCode", "IEOSE" );

    			return template.postForObject( FEEDBACK_URL, map, String.class );
    		} catch( Exception e ) {
    			exception = e;
    		}
    		
    		return null;
    	}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( String result ) {
			
			if( null == exception ) {
				Log.i( TAG, "result=" + result );
				
				if( null != result && result.indexOf( "Message Sent" ) != -1 ) {
	
					Log.i( TAG, "Comment sent successfully!" );
				} else {
					Log.i( TAG, "Comment failed!" );
				}
				
			} else {
				Log.i( TAG, "Comment failed!" );
			}
			
		}
        
    }

}
