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

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.provider.BaseColumns;

import com.keithandthegirl.provider.KatgProvider;

/**
 * @author Daniel Frey
 *
 */
public class EpisodeConstants implements BaseColumns {

	public static enum Show {
		KATG( "KATG", "Keith And The Girl" ),
		WMN( "WMN", "What's My Name" ),
		MNIK( "MNIK", "My Name Is Keith" ),
		TTSWD( "TTSWD", "That's The Show With Danny" ),
		INTERNMENT( "INTERNment", "INTERNment" ),
		KATG_TV( "KATGtv", "KATGtv" ),
		BEGINNINGS( "Beginnings", "Beginnings" );
		
		String key;
		
		String showName;
		
		private Show( String key, String showName ) {
			this.key = key;
			this.showName = showName;
		}

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @return the showName
		 */
		public String getShowName() {
			return showName;
		}
		
		public static Show findByKey( String value ) {
			
			for( Show show : Show.values() ) {
				if( show.getKey().equals( value ) ) {
					return show;
				}
			}
			
			return null;
		}
		
		public static Show findByShowName( String value ) {
			
			for( Show show : Show.values() ) {
				if( show.getShowName().equals( value ) ) {
					return show;
				}
			}
			
			return null;
		}
		
		public static String[] getKeys() {

			String[] array = new String[ Show.values().length ];
			List<String> values = new ArrayList<String>( Show.values().length );
			for( Show show : Show.values() ) {
				values.add( show.getKey() );
			}

			return values.toArray( array );
		}
		
	}
	
	public static enum FileType {
		MP3( "mp3", "audio/mpeg" ),
		MP4( "mp4", "video/mp4" ),
		M4V( "m4v", "video/x-m4v" ),
		JPG( "jpg", "image/jpeg" );
		
		private String extension;
		private String mimeType;
		
		private FileType( String extension, String mimeType ) {
			this.extension = extension;
			this.mimeType = mimeType;
		}

		/**
		 * @return the extension
		 */
		public String getExtension() {
			return extension;
		}

		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}
		
		public static FileType findByFilename( String value ) {
			
			if( null != value && !"".equals( value ) ) {
				
				String extension = value.substring( value.lastIndexOf( '.' ) + 1 );
				
				return findByExtension( extension );
			}
			
			return null;
		}
		
		public static FileType findByExtension( String value ) {
			
			for( FileType fileType : FileType.values() ) {
				if( fileType.getExtension().equals( value ) ) {
					return fileType;
				}
			}
			
			return null;
		}
		
		public static FileType findByMimeType( String value ) {
			
			for( FileType fileType : FileType.values() ) {
				if( fileType.getMimeType().equals( value ) ) {
					return fileType;
				}
			}
			
			return null;
		}

	}

	public static final String TABLE_NAME = "katg_episodes";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + KatgProvider.AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_SHOW = "SHOW";
	public static final String FIELD_SHOW_DATA_TYPE = "TEXT";

	public static final String FIELD_SHOW_KEY = "SHOW_KEY";
	public static final String FIELD_SHOW_KEY_DATA_TYPE = "TEXT";

	public static final String FIELD_PUBLISH_DATE = "PUBLISH";
	public static final String FIELD_PUBLISH_DATE_DATA_TYPE = "INTEGER";

	public static final String FIELD_TITLE = "TITLE";
	public static final String FIELD_TITLE_DATA_TYPE = "TEXT";

	public static final String FIELD_DESCRIPTION = "DESCRIPTION";
	public static final String FIELD_DESCRIPTION_DATA_TYPE = "TEXT";

	public static final String FIELD_URL = "URL";
	public static final String FIELD_URL_DATA_TYPE = "TEXT";

	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TYPE_DATA_TYPE = "TEXT";
	
	public static final String FIELD_LENGTH = "LENGTH";
	public static final String FIELD_LENGTH_DATA_TYPE = "INTEGER";

	public static final String FIELD_FILE = "FILE";
	public static final String FIELD_FILE_DATA_TYPE = "TEXT";

	public static final String FIELD_VIP = "VIP";
	public static final String FIELD_VIP_DATA_TYPE = "INTEGER";

}
