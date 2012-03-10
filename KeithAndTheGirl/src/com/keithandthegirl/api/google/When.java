/**
 * 
 */
package com.keithandthegirl.api.google;

import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root
@Namespace( prefix = "gd" )
public class When {

	@Attribute( name = "endTime" )
	private DateTime endTime;
	
	@Attribute( name = "startTime" )
	private DateTime startTime;
	
	public When() { }

	public When( DateTime endTime, DateTime startTime ) {
		this.endTime = endTime;
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public DateTime getEndTime() {
		return endTime;
	}

	/**
	 * @return the startTime
	 */
	public DateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime( DateTime startTime ) {
		this.startTime = startTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime( DateTime endTime ) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "When [" );
		
		if( endTime != null ) {
			builder.append( "endTime=" );
			builder.append( endTime );
			builder.append( ", " );
		}
		
		if( startTime != null ) {
			builder.append( "startTime=" );
			builder.append( startTime );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
