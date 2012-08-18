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
@Root( name = "Urls" )
public class Urls {

	@ElementList( inline = true, required = false )
	private List<Url> urls;
	
	public Urls() { }

	/**
	 * @return the urls
	 */
	public List<Url> getUrls() {
		return urls;
	}

	/**
	 * @param urls the urls to set
	 */
	public void setUrls( List<Url> urls ) {
		this.urls = urls;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Urls [" );
		if( urls != null ) {
			builder.append( "urls=" );
			builder.append( urls );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
