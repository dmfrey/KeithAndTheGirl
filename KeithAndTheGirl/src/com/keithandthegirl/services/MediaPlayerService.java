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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.activities.FeedActivity;

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

	private SyndEntry currentEntry;
	
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
	public void onStart( Intent intent, int startId ) {
		super.onStart( intent, startId );
		Log.d( TAG, "onStart : enter" );

		applicationContext = (MainApplication) getApplicationContext();
		
		handler.removeCallbacks( sendUpdatesToUI );

		currentPlayType = applicationContext.getSelectedPlayType();

		switch( currentPlayType ) {
			case LIVE:
				playLive();
				break;
			case RECORDED:
				currentEntry = applicationContext.getSelectedEntry();
				if( null != currentEntry ) {
					playRecorded();
				}
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

	private void playRecorded() {
		Log.d( TAG, "playRecorded : enter" );
		
		try {
			start( ( (SyndEnclosure) currentEntry.getEnclosures().get( 0 ) ).getUrl() );

			String value = currentEntry.getDescription().getValue();
	        value = value.replace( "<p>", "" );
	        value = value.replace( "</p>", "" );
	        value = value.replace( "\"", "" );

			notify( currentEntry.getTitle(), value );
		} catch( IOException e ) {
			Log.w( TAG, e.getMessage() );

			clearNotification();
		}
		
		Log.d( TAG, "playRecorded : exit" );
	}

	private void notify( String title, String description ) {
		Notification notification = new Notification( R.drawable.ic_katg_notification, title, System.currentTimeMillis() );

		Intent notificationIntent = new Intent( this, FeedActivity.class );
		PendingIntent contentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

		notification.setLatestEventInfo( this, title, description, contentIntent );
		
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		nm.notify( NOTIFY_ID, notification );
	}

}
