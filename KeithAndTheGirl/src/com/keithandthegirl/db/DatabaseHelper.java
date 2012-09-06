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
package com.keithandthegirl.db;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "katgdb";
	private static final int DATABASE_VERSION = 4;

	public DatabaseHelper( Context context ) {
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate( SQLiteDatabase db ) {
		Log.v( TAG, "onCreate : enter" );

		dropKatgEpisodes( db );
		createKatgEpisodes( db );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		Log.v( TAG, "onUpgrade : enter" );

		if( oldVersion < 4 ) {
			Log.v( TAG, "onUpgrade : upgrading to db version 4" );
				
			dropKatgEpisodes( db );
			createKatgEpisodes( db );
		}

		Log.v( TAG, "onUpgrade : enter" );
	}

	// internal helpers
	
	private void createKatgEpisodes( SQLiteDatabase db ) {
		Log.v( TAG, "createKatgEpisodes : enter" );
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append( "CREATE TABLE " + EpisodeConstants.TABLE_NAME + " (" );
		sqlBuilder.append( _ID ).append( " " ).append( EpisodeConstants.FIELD_ID_DATA_TYPE ).append( " " ).append( EpisodeConstants.FIELD_ID_PRIMARY_KEY ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_SHOW ).append( " " ).append( EpisodeConstants.FIELD_SHOW_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_PUBLISH_DATE ).append( " " ).append( EpisodeConstants.FIELD_PUBLISH_DATE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_TITLE ).append( " " ).append( EpisodeConstants.FIELD_TITLE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_DESCRIPTION ).append( " " ).append( EpisodeConstants.FIELD_DESCRIPTION_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_URL ).append( " " ).append( EpisodeConstants.FIELD_URL_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_TYPE ).append( " " ).append( EpisodeConstants.FIELD_TYPE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_LENGTH ).append( " " ).append( EpisodeConstants.FIELD_LENGTH_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_FILE ).append( " " ).append( EpisodeConstants.FIELD_FILE_DATA_TYPE ).append( ", " );
		sqlBuilder.append( EpisodeConstants.FIELD_VIP ).append( " " ).append( EpisodeConstants.FIELD_VIP_DATA_TYPE );
		sqlBuilder.append( ");" );
		String sql = sqlBuilder.toString();
		if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
			Log.v( TAG, "createKatgEpisodes : sql=" + sql );
		}
		db.execSQL( sql );
	
		Log.v( TAG, "createKatgEpisodes : exit" );
	}

	private void dropKatgEpisodes( SQLiteDatabase db ) {
		Log.v( TAG, "dropKatgEpisodes : enter" );
		
		db.execSQL( "DROP TABLE IF EXISTS " + EpisodeConstants.TABLE_NAME );
		
		Log.v( TAG, "dropKatgEpisodes : exit" );
	}
	

}
