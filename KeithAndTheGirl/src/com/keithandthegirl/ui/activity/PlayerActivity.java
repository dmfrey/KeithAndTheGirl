/**
 * 
 */
package com.keithandthegirl.ui.activity;

import java.net.URLEncoder;

import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.R;
import com.keithandthegirl.services.MediaPlayerService;

/**
 * @author Daniel Frey
 *
 */
public class PlayerActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = PlayerActivity.class.getSimpleName();

	private static final String FEEDBACK_URL = "http://www.attackwork.com/Voxback/Comment-Form-Iframe.aspx";
	private static final String FEEDBACK_URL_ENCODER = "UTF-8";
	
	private static final String NAME_KEY = "NAME";
	private static final String LOCATION_KEY = "LOCATION";
	
	private Intent mediaPlayerReceiverIntent;
	private TextView nowPlayingTitle;
	private EditText editName, editLocation, editComment;
	private Button stopButton, submitButton;
	
	//***************************************
    // Activity methods
    //***************************************
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
	    Log.d( TAG, "onCreate : enter" );

	    super.onCreate( savedInstanceState );

	    setContentView( R.layout.activity_player );

	    setupActionBar();
	    
	    mediaPlayerReceiverIntent = new Intent( this, MediaPlayerService.class );

	    nowPlayingTitle = (TextView) findViewById( R.id.now_playing_title );
	    
	    editName = (EditText) findViewById( R.id.player_feedback_name );
	    editLocation = (EditText) findViewById( R.id.player_feedback_location );
	    editComment = (EditText) findViewById( R.id.player_feedback_comment );
	    
	    stopButton = (Button) findViewById( R.id.player_stop );
	    stopButton.setOnClickListener( this );
	    submitButton = (Button) findViewById( R.id.player_submit );
	    submitButton.setOnClickListener( this );
	    
	    play();
	    
	    Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	public void onStart() {
	    Log.d( TAG, "onStart : enter" );

	    super.onStart();
	    
		Log.d( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
	    Log.d( TAG, "onPause : enter" );

		super.onPause();
		
		unregisterReceiver( mediaPlayerBroadcastReceiver );

	    Log.d( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
	    Log.d( TAG, "onResume : enter" );

	    super.onResume();

		registerReceiver( mediaPlayerBroadcastReceiver, new IntentFilter( MediaPlayerService.BROADCAST_ACTION ) );

		switch( ( (MainApplication) getApplicationContext() ).getSelectedPlayType() ) {
			case RECORDED :
				
				if( null != ( (MainApplication) getApplicationContext() ).getSelectedEntry() ) {
					nowPlayingTitle.setText( ( (MainApplication) getApplicationContext() ).getSelectedEntry().getTitle() );
				}

//				editName.setEnabled( false );
//				editLocation.setEnabled( false );
//				editComment.setEnabled( false );
				
				break;
			case LIVE :
				
				nowPlayingTitle.setText( "Streaming Live!!" );
				
				editName.setEnabled( true );
				editLocation.setEnabled( true );
				editComment.setEnabled( true );

				break;
		}
		
		SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE );
		String name = sharedPreferences.getString( NAME_KEY, "" );
		String location = sharedPreferences.getString( LOCATION_KEY, "" );
		
		editName.setText( name );
		editLocation.setText( location );
		
		Log.d( TAG, "onResume : exit" );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {		
	    Log.d( TAG, "onOptionsItemSelected : enter" );

	    Intent intent = new Intent();
	    
	    // Handle item selection
	    switch( item.getItemId() ) {
	    	case android.R.id.home:

	    		switch( ( (MainApplication) getApplicationContext() ).getSelectedPlayType() ) {
	    			case RECORDED :
				
	    	            intent = new Intent( this, FeedActivity.class );
	    	            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
	    	            startActivity( intent );

	    				break;
	    			case LIVE :
				
	    	            intent = new Intent( this, HomeActivity.class );
	    	            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
	    	            startActivity( intent );
				
	    				break;
	    		}
	    }
	    
	    Log.d( TAG, "onOptionsItemSelected : exit" );
	    return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick( View v ) {
	    Log.d( TAG, "onClick : enter" );
		
		switch( v.getId() ) {
			case R.id.player_stop :
				Log.v( TAG, "onClick : stop button pressed" );
			
				stopService( new Intent( this, MediaPlayerService.class ) );
				clearNowPlaying();
				finish();
				
				break;
			case R.id.player_submit :
				Log.v( TAG, "onClick : submit button pressed" );
				
				String name = editName.getText().toString();
				if( null != name && !"".equals( name ) ) {
					name = name.trim();
				}
				
				String location = editLocation.getText().toString();
				if( null != location && !"".equals( location ) ) {
					location = location.trim();
				}

				String comment = editComment.getText().toString();
				if( null != comment && !"".equals( comment ) ) {
					comment = comment.trim();
				}
				
				savePreferences( NAME_KEY, name );
				savePreferences( LOCATION_KEY, location );
				
				new PostCommentTask().execute( name, location, comment );
				
				editComment.setText( "" );
				
				break;
		}
		
	    Log.d( TAG, "onClick : exit" );
	}

	// internal helpers
	
	@TargetApi( 11 )
	private void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

	private void play() {
		Log.d( TAG, "play : enter" );
		
	    startService( mediaPlayerReceiverIntent );
		
		Log.d( TAG, "play : exit" );
	}
	
	private void clearNowPlaying() {
		Log.d( TAG, "clearNowPlaying : enter" );

		( (MainApplication) getApplicationContext() ).setSelectedEntry( null );
		( (MainApplication) getApplicationContext() ).setSelectedPlayType( null );
		
		Log.d( TAG, "clearNowPlaying : exit" );
	}

    private BroadcastReceiver mediaPlayerBroadcastReceiver = new BroadcastReceiver() {
    	
        @Override
        public void onReceive( Context context, Intent intent ) {
    		Log.d( TAG, "onReceive : enter" );

    		clearNowPlaying();
    		
    		Log.d( TAG, "onReceive : exit" );
        }
        
    };

    private void savePreferences( String key, String value ) {
    	SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE );
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
    			if( null != name && !"".equals( name ) ) {
    				name = name.trim();
    				
    				if( name.length() > 50 ) {
    					name = name.substring( 0, 50 );
    				}
    			}
    			
    			String location = params[ 1 ];
    			if( null != location && !"".equals( location ) ) {
    				location = location.trim();
    				
    				if( location.length() > 50 ) {
    					location = location.substring( 0, 50 );
    				}
    			}
    			
    			String comment = params[ 2 ];
    			if( null != comment && !"".equals( comment ) ) {
    				comment = comment.trim();
    				
    				if( comment.length() > 50 ) {
    					comment = comment.substring( 0, 50 );
    				}
    			}
    			
    			
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
				
				if( null != result ) {
					Log.i( TAG, "result=" + result );
	
					toastComment( "Comment sent successfully!" );
				} else {
					toastComment( "Comment failed!" );
				}
				
			} else {
				toastComment( "Comment failed!" );
			}
			
		}
        
    }

    private void toastComment( String message ) {
    	Toast toast = Toast.makeText( this, message, Toast.LENGTH_SHORT );
    	toast.show();
    }
    
}
