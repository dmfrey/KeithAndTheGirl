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

import android.net.Uri;
import android.provider.BaseColumns;

import com.keithandthegirl.provider.KatgProvider;

/**
 * @author Daniel Frey
 *
 */
public class EpisodeConstants implements BaseColumns {

	public static final String TABLE_NAME = "katg_episodes";
	
	public static final Uri CONTENT_URI = Uri.parse( "content://" + KatgProvider.AUTHORITY + "/" + TABLE_NAME );

	// db fields
	public static final String FIELD_ID_DATA_TYPE = "INTEGER";
	public static final String FIELD_ID_PRIMARY_KEY = "PRIMARY KEY AUTOINCREMENT";
	
	public static final String FIELD_SHOW = "SHOW";
	public static final String FIELD_SHOW_DATA_TYPE = "TEXT";

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