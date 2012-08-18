/**
 * 
 */
package com.keithandthegirl.api.guests;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;


/**
 * @author Daniel Frey
 *
 */
@Root( name = "Url" )
public class Url {

	@Text
	private String address;
	
	public Url() { }

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress( String address ) {
		this.address = address;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Url [" );
		if( address != null ) {
			builder.append( "address=" );
			builder.append( address );
		}
		builder.append( "]" );
		return builder.toString();
	}

}
