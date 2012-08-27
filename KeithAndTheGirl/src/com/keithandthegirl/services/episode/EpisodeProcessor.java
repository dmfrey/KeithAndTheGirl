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

import java.net.URL;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.db.EpisodeConstants;
import com.keithandthegirl.services.AbstractKatgProcessor;
import com.keithandthegirl.utils.NotificationHelper;
import com.keithandthegirl.utils.NotificationHelper.NotificationType;

/**
 * @author Daniel Frey
 *
 */
public class EpisodeProcessor extends AbstractKatgProcessor {

	protected static final String TAG = EpisodeProcessor.class.getSimpleName();

	private NotificationHelper mNotificationHelper;
	
	public interface EpisodeProcessorCallback {

		void send( int resultCode );

	}

	public EpisodeProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		mNotificationHelper = new NotificationHelper( context );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getEpisodes( EpisodeProcessorCallback callback ) {
		Log.v( TAG, "getEpisodes : enter" );

		try {
            mNotificationHelper.createNotification( "KeithAndTheGirl", "Refreshing KATG RSS Feed", NotificationType.SYNC );

            // if not VIP, then reset all episodes where VIP is 0 to 1;
            resetVipEpisodes();
            
            FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
            FeedFetcher feedFetcher = new HttpURLFeedFetcher( feedInfoCache );
			SyndFeed feed = feedFetcher.retrieveFeed( new URL( MainApplication.KATG_RSS_FEED ) );
			
			processFeed( feed );
			
			callback.send( 1 );
			
			mNotificationHelper.completed();
		} catch( Exception e ) {
			Log.e( TAG, e.getLocalizedMessage(), e );
		}
		
		Log.v( TAG, "getRecordedList : exit" );
	}

	// internal helpers
	
	@SuppressWarnings( "unchecked" )
	private void processFeed( SyndFeed feed ) {
		Log.v( TAG, "processFeed : enter" );

		if( null != feed ) {

			if( null != feed.getEntries() && !feed.getEntries().isEmpty() ) {
				
				for( SyndEntry entry : (List<SyndEntry>) feed.getEntries() ) {
				
					if( !entry.getTitle().equalsIgnoreCase( "where are more katg shows" ) ) {
						
						String[] title = entry.getTitle().split( ":" );
						if( title.length == 2 ) { 
						
							String value = entry.getDescription().getValue();
							value = value.replace( "<p>", "" );
							value = value.replace( "</p>", "" );
							value = value.replace( "\"", "" );

							ContentValues values = new ContentValues();
							values.put( EpisodeConstants.FIELD_PUBLISH_DATE, entry.getPublishedDate().getTime() );
							values.put( EpisodeConstants.FIELD_NUMBER, Integer.parseInt( title[ 0 ] ) );
							values.put( EpisodeConstants.FIELD_TITLE, title[ 1 ].trim() );
							values.put( EpisodeConstants.FIELD_DESCRIPTION, value );
							values.put( EpisodeConstants.FIELD_URL, ( (SyndEnclosure) entry.getEnclosures().get( 0 ) ).getUrl()  );
							values.put( EpisodeConstants.FIELD_TYPE, ( (SyndEnclosure) entry.getEnclosures().get( 0 ) ).getType() );
							values.put( EpisodeConstants.FIELD_LENGTH, ( (SyndEnclosure) entry.getEnclosures().get( 0 ) ).getLength() );
							values.put( EpisodeConstants.FIELD_VIP, 0 );

							String[] projection = new String[] { EpisodeConstants._ID };

							StringBuilder sb = new StringBuilder();
							sb.append( EpisodeConstants.FIELD_NUMBER ).append( " = ?" );

							String[] args = new String[] { title[ 0 ] };

							long episodeId;
							Cursor cursor = mContext.getContentResolver().query( EpisodeConstants.CONTENT_URI, projection, sb.toString(), args, null );
							if( cursor.moveToFirst() ) {
								episodeId = cursor.getLong( cursor.getColumnIndexOrThrow( EpisodeConstants._ID ) );
								mContext.getContentResolver().update( ContentUris.withAppendedId( EpisodeConstants.CONTENT_URI, episodeId ), values, null, null );
							} else {
								Uri programUri = mContext.getContentResolver().insert( EpisodeConstants.CONTENT_URI, values );
								episodeId = ContentUris.parseId( programUri );
							}
							cursor.close();
						
						}
						
					}
				
				}
			
			}

		}

		mNotificationHelper.completed();
		
		Log.v( TAG, "processFeed : exit" );
	}

	// internal helpers
	
	private int resetVipEpisodes() {
		Log.v( TAG, "resetVipEpisodes : enter" );
		
		ContentValues values = new ContentValues();
		values.put( EpisodeConstants.FIELD_VIP, 1 );
		
		int updated = mContext.getContentResolver().update( EpisodeConstants.CONTENT_URI, values, EpisodeConstants.FIELD_VIP + " = ?", new String[] { "0" } );
		
		Log.v( TAG, "resetVipEpisodes : exit" );
		return updated;
	}

}
