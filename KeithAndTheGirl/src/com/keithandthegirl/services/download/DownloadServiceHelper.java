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
package com.keithandthegirl.services.download;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.keithandthegirl.services.KatgService.Method;
import com.keithandthegirl.services.download.DownloadService.Resource;

/**
 * @author Daniel Frey
 *
 */
public class DownloadServiceHelper {

	private static final String TAG = DownloadServiceHelper.class.getSimpleName();
	
	public static String DOWNLOAD_RESULT = "DOWNLOAD_RESULT";

	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String DOWNLOADS_HASHKEY = "downloads";
	
	private static Object lock = new Object();
	private static DownloadServiceHelper instance;
	
	private Map<String,Long> pendingRequests = new HashMap<String,Long>();
	private Context ctx;

	private DownloadServiceHelper( Context ctx ) {
		Log.v( TAG, "initialize : enter" );
		
		this.ctx = ctx;

		Log.v( TAG, "initialize : exit" );
	}
	
	public static DownloadServiceHelper getInstance( Context ctx ) {
		Log.d( TAG, "getInstance : enter" );

		synchronized( lock ) {
			if( null == instance ){
				instance = new DownloadServiceHelper( ctx );			
			}
		}

		Log.d( TAG, "getInstance : exit" );
		return instance;		
	}
	
	public boolean isRequestPending( long requestId ) {
		return pendingRequests.containsValue( requestId );
	}

	public long download( String url, long id, String show, String title, Resource resource ) {
		Log.d( TAG, "download : enter" );

		long requestId = generateRequestID();
		pendingRequests.put( DOWNLOADS_HASHKEY, requestId );
		
		ResultReceiver serviceCallback = new ResultReceiver( null ) {

			@Override
			protected void onReceiveResult( int resultCode, Bundle resultData ) {
				handleDownloadResponse( resultCode, resultData );
			}
		
		};

		Intent intent = new Intent( ctx, DownloadService.class );
        intent.setData( Uri.parse( url ) );
		intent.putExtra( DownloadService.METHOD_EXTRA, Method.GET.name() );
		intent.putExtra( DownloadService.RESOURCE_TYPE_EXTRA, resource.name() );
		intent.putExtra( DownloadService.SERVICE_CALLBACK, serviceCallback );
        intent.putExtra( "urlpath", url );
        intent.putExtra( "id", id );
        intent.putExtra( "directory", show );
        intent.putExtra( "title", title );
		intent.putExtra( REQUEST_ID, requestId );

		ctx.startService( intent );

		Log.d( TAG, "getRecordingsList : exit" );
		return requestId;
	}

	// internal helpers
	
	private long generateRequestID() {
		return UUID.randomUUID().getLeastSignificantBits();
	}

	private void handleDownloadResponse( int resultCode, Bundle resultData ){

		Intent origIntent = (Intent) resultData.getParcelable( DownloadService.ORIGINAL_INTENT_EXTRA );

		if( null != origIntent ) {
			long requestId = origIntent.getLongExtra( REQUEST_ID, 0 );

			pendingRequests.remove( DOWNLOADS_HASHKEY );

			Intent resultBroadcast = new Intent( DOWNLOAD_RESULT );
			resultBroadcast.putExtra( EXTRA_REQUEST_ID, requestId );
			resultBroadcast.putExtra( EXTRA_RESULT_CODE, resultCode );

			ctx.sendBroadcast( resultBroadcast );
		}

	}

}
