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
