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

import java.net.URL;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.keithandthegirl.MainApplication;

/**
 * @author Daniel Frey
 *
 */
public class UpdateFeedService extends Service {

	private static final String TAG = UpdateFeedService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.keithandthegirl.broadcast.rssFeedUpdated";
    private final Handler handler = new Handler();
	private Intent broadcastIntent;

    private MainApplication applicationContext;
    
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
		Log.d( TAG, "onCreate : enter" );

		broadcastIntent = new Intent( BROADCAST_ACTION );	

		Log.d( TAG, "onCreate : exit" );
    }
    
	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@SuppressWarnings( "deprecation" )
	@Override
	public void onStart( Intent intent, int startId ) {
		super.onStart( intent, startId );
		Log.d( TAG, "onStart : enter" );

		handler.removeCallbacks( sendUpdatesToUI );
		
		applicationContext = (MainApplication) getApplicationContext();
		
		new DownloadFeedTask().execute( MainApplication.KATG_RSS_FEED );
		
		Log.d( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
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
    
	private class DownloadFeedTask extends AsyncTask<String, Void, SyndFeed> {
		
		@Override
		protected void onPreExecute() {
			Log.v( TAG, "onPreExecute : enter" );
			
			Log.v( TAG, "onPreExecute : exit" );
		}
		
		@Override
		protected SyndFeed doInBackground( String... params ) {
			Log.v( TAG, "doInBackground : enter" );

			try {
				FeedFetcher feedFetcher = new HttpURLFeedFetcher();
				
				Log.v( TAG, "doInBackground : exit" );
				return feedFetcher.retrieveFeed( new URL( params[ 0 ] ) );
			} catch( Exception e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
			
			Log.v( TAG, "doInBackground : exit, no feed" );
			return null;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate( Void... values ) {
			super.onProgressUpdate( values );
			Log.v( TAG, "onProgressUpdate : enter" );
			
			Log.v( TAG, "onProgressUpdate : exit" );
		}

		@Override
		protected void onPostExecute( SyndFeed syndFeed ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null != syndFeed ) {
				setFeed( syndFeed );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

	private void setFeed( SyndFeed feed ) {
		Log.d( TAG, "setFeed : enter" );

		applicationContext.setFeed( feed );

		handler.postDelayed( sendUpdatesToUI, 1000 ); // 1 second

		Log.d( TAG, "setFeed : exit" );
	}

}
