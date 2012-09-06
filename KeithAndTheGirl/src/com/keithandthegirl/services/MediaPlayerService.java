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
package com.keithandthegirl.services;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.db.EpisodeConstants;
import com.keithandthegirl.ui.activity.PlayerActivity;

/**
 * @author Daniel Frey
 *
 */
public class MediaPlayerService extends Service {

	private static final String TAG = MediaPlayerService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.keithandthegirl.broadcast.mediaPlayerStopped";
    private final Handler handler = new Handler();
	private Intent broadcastIntent;

	private MainApplication applicationContext;
	private PlayType currentPlayType;
	
	private MediaPlayer mp;
	
	private NotificationManager nm;
	private static final int NOTIFY_ID = 1;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d( TAG, "onCreate : enter" );
		
		broadcastIntent = new Intent( BROADCAST_ACTION );	

		mp = new MediaPlayer();
		mp.setLooping( false );
		
		nm = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d( TAG, "onDestroy : enter" );

		clearNotification();
		mp.stop();
		mp.release();
		
		Log.d( TAG, "onDestroy : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	@SuppressWarnings( "deprecation" )
	public void onStart( Intent intent, int startId ) {
		super.onStart( intent, startId );
		Log.d( TAG, "onStart : enter" );

		applicationContext = (MainApplication) getApplicationContext();
		
		handler.removeCallbacks( sendUpdatesToUI );

		Bundle extras = intent.getExtras();
		
		currentPlayType = PlayType.valueOf( extras.getString( "PLAY_TYPE" ) );

		applicationContext.setPlaying( true );
		
		switch( currentPlayType ) {
			case LIVE:
				playLive();
				
				break;
			case RECORDED:
				long id = extras.getLong( "EPISODE", -1 );
				playRecorded( id );

				break;
			default:
				break;
		}
	
		Log.d( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}

	// internal helpers
	
	private Runnable sendUpdatesToUI = new Runnable() {
	    
		public void run() {
			Log.d( TAG, "Thread.sendUpdatesToUI.run : enter" );

			sendBroadcast( broadcastIntent );

    	    Log.d( TAG, "Thread.sendUpdatesToUI.run : exit" );
    	}

	};
    
	private void clearNotification() {
		Log.d( TAG, "clearNotification : enter" );

		applicationContext.setPlaying( false );

		nm.cancel( NOTIFY_ID );

		Log.d( TAG, "clearNotification : exit" );
	}
	
	private void start( String url ) throws IOException {
		Log.d( TAG, "start : enter" );
		
		mp.reset();
		mp.setDataSource( url );
		mp.prepare();
		mp.start();

		mp.setOnCompletionListener( new OnCompletionListener() {

			public void onCompletion( MediaPlayer arg0 ) {
				clearNotification();
				
				handler.postDelayed( sendUpdatesToUI, 1000 ); // 1 second
			}
		});

		Log.d( TAG, "start : exit" );
	}
	
	private void playLive() {
		Log.d( TAG, "playLive : enter" );
		
		try {
			start( MainApplication.KATG_LIVE_STREAM );

			notify( "KATG Live!", "KATG is streaming live" );
		} catch( IOException e ) {
			Log.w( TAG, e.getMessage() );
			
			clearNotification();
		}
		
		Log.d( TAG, "playLive : exit" );
	}

	@TargetApi( 8 )
	private void playRecorded( long id ) {
		Log.d( TAG, "playRecorded : enter" );
		
		try {
			Cursor cursor = getContentResolver().query( ContentUris.withAppendedId( EpisodeConstants.CONTENT_URI, id), null, null, null, null );
			if( cursor.moveToFirst() ) {
		        final String title = cursor.getString( cursor.getColumnIndexOrThrow( EpisodeConstants.FIELD_TITLE ) );
		        final String description = cursor.getString( cursor.getColumnIndexOrThrow( EpisodeConstants.FIELD_DESCRIPTION ) );
		        final String url = cursor.getString( cursor.getColumnIndexOrThrow( EpisodeConstants.FIELD_URL ) );
		        final String file = null != cursor.getString( cursor.getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) ? cursor.getString( cursor.getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) : "";

				if( null != file && !"".equals( file ) ) {
					Log.d( TAG, "playRecorded : play local=" + file );

					start( file );
				} else {
					Log.d( TAG, "playRecorded : play stream" );

					start( url );
				}

				notify( title, description );
			}
			cursor.close();
		} catch( IOException e ) {
			Log.w( TAG, e.getMessage() );

			clearNotification();
		}
		
		Log.d( TAG, "playRecorded : exit" );
	}

	@SuppressWarnings( "deprecation" )
	private void notify( String title, String description ) {
		Notification notification = new Notification( R.drawable.ic_notification, title, System.currentTimeMillis() );

		Intent notificationIntent = new Intent( this, PlayerActivity.class );
		PendingIntent contentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

		notification.setLatestEventInfo( this, title, description, contentIntent );
		
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		nm.notify( NOTIFY_ID, notification );
	}

}
