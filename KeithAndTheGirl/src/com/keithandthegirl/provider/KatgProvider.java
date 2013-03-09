/**
 * 
 */
package com.keithandthegirl.provider;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.keithandthegirl.db.DatabaseHelper;
import com.keithandthegirl.db.EpisodeConstants;

/**
 * @author Daniel Frey
 *
 */
public class KatgProvider extends AbstractKatgContentProvider {

	private static final String TAG = KatgProvider.class.getSimpleName();
	
	public static final String AUTHORITY = "com.keithandthegirl.provider.KatgProvider";
	
	private static final UriMatcher URI_MATCHER;

	private static final String EPISODE_CONTENT_TYPE = "vnd.keithandthegirl.cursor.dir/episode";
	private static final String EPISODE_CONTENT_ITEM_TYPE = "vnd.keithandthegirl.cursor.item/episode";
	private static final int EPISODES = 1;
	private static final int EPISODE_ID = 2;
	private static final int EPISODE_BATCH = 8;

	static {
		URI_MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
		URI_MATCHER.addURI( AUTHORITY, EpisodeConstants.TABLE_NAME, EPISODES );
		URI_MATCHER.addURI( AUTHORITY, EpisodeConstants.TABLE_NAME + "/#", EPISODE_ID );
		URI_MATCHER.addURI( AUTHORITY, EpisodeConstants.TABLE_NAME + "/episodeBatch", EPISODE_BATCH );
	}

	private DatabaseHelper database = null;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Log.v( TAG, "onCreate : enter" );
		
		database = new DatabaseHelper( getContext() );
		
		Log.v( TAG, "onCreate : exit" );
		return ( null == database ? false : true );
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		//Log.v( TAG, "query : enter" );
		final SQLiteDatabase db = database.getReadableDatabase();
		
		Cursor cursor = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case EPISODES:
				cursor = db.query( EpisodeConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				//Log.v( TAG, "query : exit, querying episodes" );
				return cursor;
	
			case EPISODE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				cursor = db.query( EpisodeConstants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
				cursor.setNotificationUri( getContext().getContentResolver(), uri );
				
				//Log.v( TAG, "query : exit, querying single episode" );
				return cursor;
	
			default:
				//Log.w( TAG, "query : exit, unknown URI" );

				throw new IllegalArgumentException( "Unknown URI " + uri );
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType( Uri uri ) {
		
		switch( URI_MATCHER.match( uri ) ) {
		case EPISODES:
			return EPISODE_CONTENT_TYPE;
		
		case EPISODE_ID:
			return EPISODE_CONTENT_ITEM_TYPE;
		
		case EPISODE_BATCH:
			return EPISODE_CONTENT_TYPE;
		
		default:
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		//Log.v( TAG, "insert : enter" );

		final SQLiteDatabase db = database.getWritableDatabase();
		
		Uri newUri = null;
		
		switch( URI_MATCHER.match( uri ) ) {
			case EPISODES:
				newUri = ContentUris.withAppendedId( EpisodeConstants.CONTENT_URI, db.insertOrThrow( EpisodeConstants.TABLE_NAME, null, values ) );
				
				getContext().getContentResolver().notifyChange( newUri, null );
				
				//Log.v( TAG, "insert : exit" );
				return newUri;
	
			default:
				Log.w( TAG, "insert : exit, unknown URI" );

				throw new IllegalArgumentException( "Unknown URI " + uri );
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		//Log.v( TAG, "delete : enter" );

		final SQLiteDatabase db = database.getWritableDatabase();
		
		switch( URI_MATCHER.match( uri ) ) {
			case EPISODES:
				//Log.v( TAG, "delete : exit, deleting episodes" );

				return db.delete( EpisodeConstants.TABLE_NAME, selection, selectionArgs );
		
			case EPISODE_ID:
				//Log.v( TAG, "delete : exit, deleting single episode" );

				return db.delete( EpisodeConstants.TABLE_NAME, EpisodeConstants._ID
						+ "="
						+ Long.toString( ContentUris.parseId( uri ) )
						+ ( !TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "" ), selectionArgs );
		
			default:
				Log.w( TAG, "delete : exit, unknown URI" );

				throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		//Log.v( TAG, "update : enter" );

		final SQLiteDatabase db = database.getWritableDatabase();

		int affected = 0;
		
		switch( URI_MATCHER.match( uri ) ) {
			case EPISODES:
				affected = db.update( EpisodeConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				//Log.v( TAG, "update : exit, updating episodes" );
				return affected;

			case EPISODE_ID:
				selection = appendRowId( selection, Long.parseLong( uri.getPathSegments().get( 1 ) ) );

				affected = db.update( EpisodeConstants.TABLE_NAME, values, selection , selectionArgs );
				
				getContext().getContentResolver().notifyChange( uri, null );
				
				//Log.v( TAG, "update : exit, updating single episode" );
				return affected;

			default:
				Log.w( TAG, "update : exit, unknown URI" );

				throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#applyBatch(java.util.ArrayList)
	 */
	@Override
	public ContentProviderResult[] applyBatch( ArrayList<ContentProviderOperation> operations )	 throws OperationApplicationException {
		//Log.v( TAG, "applyBatch : enter" );

		final SQLiteDatabase db = database.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[ numOperations ];
			for( int i = 0; i < numOperations; i++ ) {
				results[ i ] = operations.get( i ).apply( this, results, i );
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}

	}

}
