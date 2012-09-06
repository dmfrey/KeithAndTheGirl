/**
 * 
 */
package com.keithandthegirl.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.services.MediaPlayerService;

/**
 * @author Daniel Frey
 *
 */
public class PlayerActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = PlayerActivity.class.getSimpleName();
	private static final int FEEDBACK_ID = Menu.FIRST + 1;
	private static final int CALL_ID = Menu.FIRST + 2;
	private static final int ABOUT_ID = Menu.FIRST + 3;

	private Intent mediaPlayerReceiverIntent;
	private TextView nowPlayingTitle;
	private Button stopButton;
	
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
	    
	    Bundle extras = getIntent().getExtras();
	    
	    mediaPlayerReceiverIntent = new Intent( this, MediaPlayerService.class );
	    mediaPlayerReceiverIntent.putExtras( extras );
	    
	    nowPlayingTitle = (TextView) findViewById( R.id.now_playing_title );
	    
	    stopButton = (Button) findViewById( R.id.player_stop );
	    stopButton.setOnClickListener( this );
	    
	    play();
	    
	    Log.d( TAG, "onCreate : exit" );
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

	    Bundle extras = getIntent().getExtras();

	    PlayType currentPlayType = PlayType.valueOf( extras.getString( "PLAY_TYPE" ) );
	    
		switch( currentPlayType ) {
			case RECORDED :
				
				nowPlayingTitle.setText( extras.getString( "TITLE" ) );
				
				break;
			case LIVE :
				
				nowPlayingTitle.setText( "Streaming Live!!" );
				
				break;
		}
		
		Log.d( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

	    Bundle extras = getIntent().getExtras();

	    PlayType currentPlayType = PlayType.valueOf( extras.getString( "PLAY_TYPE" ) );
		switch( currentPlayType ) {
			case LIVE :
	
		    	MenuItem feedback = menu.add( Menu.NONE, FEEDBACK_ID, Menu.NONE, "Send Feedback" );
		    	feedback.setIcon( android.R.drawable.ic_menu_send );
		    	if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
		    		feedback.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
		    	}

		    	MenuItem call = menu.add( Menu.NONE, CALL_ID, Menu.NONE, "Call" );
		    	call.setIcon( android.R.drawable.ic_menu_call );
		    	if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
		    		call.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
		    	}
            
				break;
			default:
				break;
		}
		
	    MenuItem about = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, getResources().getString( R.string.about_header ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	about.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    Log.d( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {		
	    Log.d( TAG, "onOptionsItemSelected : enter" );

	    Intent intent = new Intent();
	    
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		// Handle item selection
	    switch( item.getItemId() ) {
	    	case android.R.id.home:

	    	    Bundle extras = getIntent().getExtras();

	    	    PlayType currentPlayType = PlayType.valueOf( extras.getString( "PLAY_TYPE" ) );
	    		switch( currentPlayType ) {
	    			case RECORDED :
				
	    	            intent = new Intent( this, EpisodesActivity.class );
	    	            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
	    	            startActivity( intent );

	    	            return true;
	    			case LIVE :
				
	    	            intent = new Intent( this, HomeActivity.class );
	    	            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
	    	            startActivity( intent );
				
	    	            return true;
	    		}
			case FEEDBACK_ID:
				Log.d( TAG, "onOptionsItemSelected : about selected" );

				Fragment feedback = getSupportFragmentManager().findFragmentByTag( "feedbackDialog" );
				if( null != feedback ) {
					ft.remove( feedback );
				}
				ft.addToBackStack( null );

				DialogFragment feedbackFragment = FeedbackDialogFragment.newInstance();
				feedbackFragment.show( ft, "feedbackDialog" );
		    
				return true;
	    	case CALL_ID :
				Log.d( TAG, "onOptionsItemSelected : call selected" );

				Uri uri = Uri.parse( "tel:" + MainApplication.KATG_PHONE_NUMBER );
				intent = new Intent( Intent.ACTION_DIAL, uri );
				startActivity( intent );
	    		
				return true;
			case ABOUT_ID:
				Log.d( TAG, "onOptionsItemSelected : about selected" );

				Fragment about = getSupportFragmentManager().findFragmentByTag( "aboutDialog" );
				if( null != about ) {
					ft.remove( about );
				}
				ft.addToBackStack( null );

				DialogFragment aboutFragment = AboutDialogFragment.newInstance();
				aboutFragment.show( ft, "aboutDialog" );
		    
				return true;
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
				finish();
				
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
		
		if( !( (MainApplication) getApplicationContext() ).isPlaying() ) {
			startService( mediaPlayerReceiverIntent );
		}
		
		Log.d( TAG, "play : exit" );
	}
	
    private BroadcastReceiver mediaPlayerBroadcastReceiver = new BroadcastReceiver() {
    	
        @Override
        public void onReceive( Context context, Intent intent ) {
    		Log.d( TAG, "onReceive : enter" );

    		Log.d( TAG, "onReceive : exit" );
        }
        
    };

}
