/**
 * 
 */
package com.keithandthegirl.api.google;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class WhenConverter implements Converter<When> {

	private static final String TAG = WhenConverter.class.getSimpleName();
	
	private static final DateTimeFormatter formatter = ISODateTimeFormat.dateTime(); 

	@Override
	public When read( InputNode node ) throws Exception {
		Log.d( TAG, "read : enter" );
		
		String endTime = node.getAttribute( "endTime" ).getValue();
		String startTime = node.getAttribute( "startTime" ).getValue();
		
		DateTime endDate = formatter.parseDateTime( endTime );
		Log.v( TAG, "read : endDate=" + endDate.toString() );
		
		DateTime startDate = formatter.parseDateTime( startTime );
		Log.v( TAG, "read : startDate=" + startDate.toString() );
		
		Log.d( TAG, "read : exit" );
		return new When( endDate, startDate );
	}

	@Override
	public void write( OutputNode node, When when ) throws Exception {
	}

}
