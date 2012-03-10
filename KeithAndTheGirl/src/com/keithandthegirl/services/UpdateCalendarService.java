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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/KeithAndTheGirl/>
 *
 */
package com.keithandthegirl.services;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.springframework.web.client.RestTemplate;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.api.google.Feed;

/**
 * @author Daniel Frey
 *
 */
public class UpdateCalendarService extends Service {

	private static final String TAG = UpdateCalendarService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "com.keithandthegirl.broadcast.streamCalendarUpdated";
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
	@Override
	public void onStart( Intent intent, int startId ) {
		super.onStart( intent, startId );
		Log.d( TAG, "onStart : enter" );

		handler.removeCallbacks( sendUpdatesToUI );
		
		applicationContext = (MainApplication) getApplicationContext();
		
		new UpdateStreamCalendarTask().execute( MainApplication.KATG_CALENDAR_FEED );
		
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
    
    private class UpdateStreamCalendarTask extends AsyncTask<String, Void, Feed> {
		
		@Override
		protected void onPreExecute() {
			Log.v( TAG, "onPreExecute : enter" );
			
			Log.v( TAG, "onPreExecute : exit" );
		}
		
		@Override
		protected Feed doInBackground( String...params ) {
			Log.v( TAG, "doInBackground : enter" );

			try {
				RestTemplate template = new RestTemplate();
				String xml =  template.getForObject( params[ 0 ], String.class );
				
				Strategy strategy = new AnnotationStrategy();
				Serializer serializer = new Persister( strategy );
				Feed feed = serializer.read( Feed.class, xml );
				Log.i( TAG, "feed=" + feed.toString() );
				
				Log.v( TAG, "doInBackground : exit" );
				return feed;
			} catch( Exception e ) {
				Log.e( TAG, "doInBackground : error " + e.getLocalizedMessage(), e );
			}
			
			Log.v( TAG, "doInBackground : exit, no feed" );
			return null;
		}
		
		@Override
		protected void onPostExecute( Feed feed ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null != feed ) {
				setStreamCalendar( feed );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

	private void setStreamCalendar( Feed calendarFeed ) {
		Log.d( TAG, "setStreamCalendar : enter" );

		applicationContext.setCalendarFeed( calendarFeed );

		handler.postDelayed( sendUpdatesToUI, 1000 ); // 1 second

		Log.d( TAG, "setStreamCalendar : exit" );
	}

}
