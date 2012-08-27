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
package com.keithandthegirl.services.episode;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.keithandthegirl.services.KatgService;
import com.keithandthegirl.services.episode.EpisodeProcessor.EpisodeProcessorCallback;

/**
 * @author Daniel Frey
 *
 */
public class EpisodeService extends KatgService {

	protected static final String TAG = EpisodeService.class.getSimpleName();

	public static enum Resource { EPISODE_LISTS };

	private Intent mOriginalRequestIntent;
	private ResultReceiver mCallback;

	public EpisodeService() {
		super( "DvrService" );
	}

	@Override
	protected void onHandleIntent( Intent requestIntent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		
		mOriginalRequestIntent = requestIntent;
		
		Method method = Method.valueOf( requestIntent.getStringExtra( METHOD_EXTRA ) );
		Resource resourceType = Resource.valueOf( requestIntent.getStringExtra( RESOURCE_TYPE_EXTRA ) );
		mCallback = requestIntent.getParcelableExtra( SERVICE_CALLBACK );

		switch( resourceType ) {
		case EPISODE_LISTS:

			if( method.equals( Method.GET ) ) {
				Log.v( TAG, "onHandleIntent : getting recording list" );
				
				EpisodeProcessor processor = new EpisodeProcessor( getApplicationContext() );
				processor.getEpisodes( makeEpisodeProcessorCallback() );
			} else {
				Log.w( TAG, "onHandleIntent : incorrect method for retrieving episodes" );
				
				mCallback.send( REQUEST_INVALID, getOriginalIntentBundle() );
			}
			
			break;

		default:
			Log.w( TAG, "onHandleIntent : incorrect request" );

			mCallback.send( REQUEST_INVALID, getOriginalIntentBundle() );
		
			break;
		}

		Log.v( TAG, "onHandleIntent : exit" );
	}

	protected Bundle getOriginalIntentBundle() {
		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable( ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent );
		
		return originalRequest;
	}

	// internal helpers
	
	private EpisodeProcessorCallback makeEpisodeProcessorCallback() {
		EpisodeProcessorCallback callback = new EpisodeProcessorCallback() {

			@Override
			public void send( int resultCode ) {
				if( null != mCallback ) {
					mCallback.send( resultCode, getOriginalIntentBundle() );
				}
			}
		};
		
		return callback;
	}

}
