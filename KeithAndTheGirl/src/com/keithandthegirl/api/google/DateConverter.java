/**
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
