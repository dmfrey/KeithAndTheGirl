/**
 * 
 */
package com.keithandthegirl.ui.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.services.playback.IMediaPlayerServiceClient;
import com.keithandthegirl.services.playback.MediaPlayerService;
import com.keithandthegirl.services.playback.MediaPlayerService.MediaPlayerBinder;
import com.keithandthegirl.services.playback.StatefulMediaPlayer;
import com.keithandthegirl.services.playback.StreamStation;

/**
 * @author Daniel Frey
 * 
 */
public class PlayerActivity extends FragmentActivity implements IMediaPlayerServiceClient {

	private static final String TAG = PlayerActivity.class.getSimpleName();
	private static final int FEEDBACK_ID = Menu.FIRST + 1;
	private static final int CALL_ID = Menu.FIRST + 2;
	private static final int ABOUT_ID = Menu.FIRST + 3;

	public static final String PLAY_TYPE = "PLAY_TYPE";
	public static final String PLAYBACK_URL = "PLAYBACK_URL";
	public static final String EPISODE = "EPISODE";
	public static final String TITLE = "TITLE";
	public static final String DESCRIPTION = "DESCRIPTION";

	// private Intent mediaPlayerReceiverIntent;
	private TextView nowPlayingTitle;

	private StatefulMediaPlayer mMediaPlayer;
	private StreamStation mSelectedStream = null;
	private MediaPlayerService mService;
	private boolean mBound;

	private ProgressDialog mProgressDialog;

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_player );

		setupActionBar();

		bindToService();
        mProgressDialog = new ProgressDialog( this );

		initializeButtons();

		nowPlayingTitle = (TextView) findViewById( R.id.now_playing_title );

		Log.d( TAG, "onCreate : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d( TAG, "onPause : enter" );

		super.onPause();

		// unregisterReceiver( mediaPlayerBroadcastReceiver );
		unbindService( mConnection );
		
		Log.d( TAG, "onPause : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
		Log.d( TAG, "onResume : enter" );

		super.onResume();

		Bundle extras = getIntent().getExtras();
		PlayType currentPlayType = PlayType.valueOf( extras.getString( PLAY_TYPE ) );
		mSelectedStream = new StreamStation( currentPlayType, extras.getString( TITLE ), extras.getString( PLAYBACK_URL ), extras.getString( DESCRIPTION ) );

		switch( currentPlayType ) {
		case RECORDED:

			nowPlayingTitle.setText( extras.getString( TITLE ) );

			break;
		case LIVE:

			nowPlayingTitle.setText( "Streaming Live!!" );

			break;
		}

		// play();

		Log.d( TAG, "onResume : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

		Bundle extras = getIntent().getExtras();

		PlayType currentPlayType = PlayType.valueOf( extras.getString( "PLAY_TYPE" ) );
		switch( currentPlayType ) {
		case LIVE:

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
			finish();
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
		case CALL_ID:
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

	/**
	 * Binds to the instance of MediaPlayerService. If no instance of
	 * MediaPlayerService exists, it first starts a new instance of the service.
	 */
	private void bindToService() {
		Log.v( TAG, "bindToService : enter" );

		Intent intent = new Intent( this, MediaPlayerService.class );

		if( MediaPlayerServiceRunning() ) {
			Log.i( TAG, "bindToService : MediaPlayerServices is running" );

			// Bind to LocalService
			bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
		} else {
			Log.i( TAG, "bindToService : MediaPlayerServices is NOT running" );

			startService( intent );
			bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
		}

		Log.v( TAG, "bindToService : exit" );
	}

	/**
	 * Initializes buttons by setting even handlers and listeners, etc.
	 */
	private void initializeButtons() {
		Log.v( TAG, "initializeButtons : enter" );
		
		final ImageButton playButton = (ImageButton) findViewById( R.id.player_play );
		final ImageButton pauseButton = (ImageButton) findViewById( R.id.player_pause );

		playButton.setOnClickListener( new OnClickListener() {
        	 
            @Override
            public void onClick(View v) {
        		Log.v( TAG, "playButton.onClick : enter" );

                if( mBound ) {
                	Log.v( TAG, "playButton.onClick : mBound" );
                	
                    mMediaPlayer = mService.getMediaPlayer();

                    // STOPPED, CREATED, EMPTY, -> initialize
                    if( mMediaPlayer.isStopped()
                            || mMediaPlayer.isCreated()
                            || mMediaPlayer.isEmpty()
                    ) {
                      	Log.v( TAG, "playButton.onClick : mMediaPlayer is stopped or empty" );
                        	
                        mService.initializePlayer( mSelectedStream );
                    }
 
                    //prepared, paused -> resume play
                    else if( mMediaPlayer.isPrepared()
                             || mMediaPlayer.isPaused()
                    ) {
                      	Log.v( TAG, "playButton.onClick : mMediaPlayer is prepared or paused" );

                       	mService.startMediaPlayer();
                    }
 
                    playButton.setEnabled( false );
                    pauseButton.setEnabled( true );
                }

                Log.v( TAG, "playButton.onClick : exit" );
            }
 
        });

        pauseButton.setOnClickListener( new OnClickListener() {
        	 
            @Override
            public void onClick(View v) {
        		Log.v( TAG, "pauseButton.onClick : enter" );

                if( mBound ) {
                	Log.v( TAG, "pauseButton.onClick : mBound" );
                	
                    mMediaPlayer = mService.getMediaPlayer();
 
                    if( mMediaPlayer.isStarted() ) {
                        mService.pauseMediaPlayer();
                    }
 
                    playButton.setEnabled( true );
                    pauseButton.setEnabled( false );
                }

                Log.v( TAG, "pauseButton.onClick : exit" );
            }
 
        });

		Log.v( TAG, "initializeButtons : exit" );
	}

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected( ComponentName className, IBinder serviceBinder ) {
			Log.d( TAG, "onServiceConnected : enter" );

			// bound with Service. get Service instance
			MediaPlayerBinder binder = (MediaPlayerBinder) serviceBinder;
			mService = binder.getService();

			// send this instance to the service, so it can make callbacks on
			// this instance as a client
			mService.setClient( PlayerActivity.this );
			mBound = true;
			Log.i( TAG, "onServiceConnected : service connected" );

			Log.d( TAG, "onServiceConnected : exit" );
		}

		@Override
		public void onServiceDisconnected( ComponentName arg0 ) {
			mBound = false;
		}
	
	};

	/**
	 * Determines if the MediaPlayerService is already running.
	 * 
	 * @return true if the service is running, false otherwise.
	 */
	private boolean MediaPlayerServiceRunning() {
		Log.v( TAG, "MediaPlayerServiceRunning : enter" );

		ActivityManager manager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );

		for( RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {
			Log.v( TAG, "MediaPlayerServiceRunning : service=" + service.service.getClassName() );

			if( "com.keithandthegirl.services.playback.MediaPlayerService".equals( service.service.getClassName() ) ) {

				Log.v( TAG, "MediaPlayerServiceRunning : exit, MediaPlayerService is running" );
				return true;
			}
		}

		Log.v( TAG, "MediaPlayerServiceRunning : exit, MediaPlayerService is NOT running" );
		return false;
	}

	public void onInitializePlayerSuccess() {
		Log.v( TAG, "onInitializePlayerSuccess : enter" );

		mProgressDialog.dismiss();

		final ImageButton playButton = (ImageButton) findViewById( R.id.player_play );
		final ImageButton pauseButton = (ImageButton) findViewById( R.id.player_pause );
        playButton.setEnabled( false );
        pauseButton.setEnabled( true );

		Log.v( TAG, "onInitializePlayerSuccess : exit" );
	}

	public void onInitializePlayerStart( String message ) {
		Log.v( TAG, "onInitializePlayerStart : enter" );

		mProgressDialog = ProgressDialog.show( this, "", message, true );
		mProgressDialog.getWindow().setGravity( Gravity.TOP );
		mProgressDialog.setCancelable( true );
		mProgressDialog.setOnCancelListener( new OnCancelListener() {

			@Override
			public void onCancel( DialogInterface dialogInterface ) {
				PlayerActivity.this.mService.resetMediaPlaer();

				final ImageButton playButton = (ImageButton) findViewById( R.id.player_play );
				final ImageButton pauseButton = (ImageButton) findViewById( R.id.player_pause );
		        playButton.setEnabled( true );
		        pauseButton.setEnabled( false );
			}

		} );

		Log.v( TAG, "onInitializePlayerStart : exit" );
	}

	@Override
	public void onError() {
		mProgressDialog.cancel();
	}

	/**
	 * Closes unbinds from service, stops the service, and calls finish()
	 */
	public void shutdownActivity() {

		if( mBound ) {
			mService.stopMediaPlayer();
			// Detach existing connection.
			unbindService( mConnection );
			mBound = false;
		}

		Intent intent = new Intent( this, MediaPlayerService.class );
		stopService( intent );
		finish();

	}

}
