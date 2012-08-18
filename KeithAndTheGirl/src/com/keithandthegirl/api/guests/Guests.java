/**
 * 
 */
package com.keithandthegirl.api.guests;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name="root" )
public class Guests {

	@ElementList( inline = true )
	private List<Guest> guests;
	
	public Guests() { }

	/**
	 * @return the guests
	 */
	public List<Guest> getGuests() {
		return guests;
	}

	/**
	 * @param guests the guests to set
	 */
	public void setGuests( List<Guest> guests ) {
		this.guests = guests;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Guests [" );
		if( guests != null ) {
			builder.append( "guests=" );
			builder.append( guests );
		}
		builder.append( "]" );
		return builder.toString();
	}

}
