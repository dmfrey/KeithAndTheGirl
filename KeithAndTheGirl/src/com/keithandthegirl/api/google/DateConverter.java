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
package com.keithandthegirl.api.google;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class DateConverter implements Converter<DateTime> {

	private static final String TAG = DateConverter.class.getSimpleName();
	
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ); 
	
	@Override
	public DateTime read( InputNode node ) throws Exception {
		Log.d( TAG, "read : enter" );
		
		DateTime date = formatter.parseDateTime( node.getValue() );
		Log.v( TAG, "read : date=" + date.toString() );
		
		Log.d( TAG, "read : exit" );
		return date;
	}

	@Override
	public void write( OutputNode node, DateTime date ) throws Exception {
		
	}

}
