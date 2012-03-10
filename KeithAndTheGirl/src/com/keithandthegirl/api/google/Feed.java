/**
 * 
 */
package com.keithandthegirl.api.google;

import java.util.List;

import org.joda.time.DateTime;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "feed", strict = false )
@NamespaceList({
	@Namespace( reference = "http://a9.com/-/spec/opensearchrss/1.0/", prefix = "openSearch" ),
	@Namespace( reference = "http://schemas.google.com/gCal/2005", prefix = "gCal" ),
	@Namespace( reference = "http://schemas.google.com/g/2005", prefix = "gd" ),
})
public class Feed {

	@Element( name = "id" )
	private String id;
	
	@Element( name = "updated" )
	@Convert( DateConverter.class )
	private DateTime updated;
	
	@Element( name = "title" )
	private String title;
	
	@Element( name = "subtitle" )
	private String subTitle;
	
	@ElementList( name = "entries", inline = true )
	private List<Entry> entries;
	
	public Feed() { }

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId( String id ) {
		this.id = id;
	}

	/**
	 * @return the updated
	 */
	public DateTime getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated( DateTime updated ) {
		this.updated = updated;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	/**
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * @param subTitle the subTitle to set
	 */
	public void setSubTitle( String subTitle ) {
		this.subTitle = subTitle;
	}

	/**
	 * @return the entries
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries( List<Entry> entries ) {
		this.entries = entries;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Feed [" );
		
		if( id != null ) {
			builder.append( "id=" );
			builder.append( id );
			builder.append( ", " );
		}
		
		if( updated != null ) {
			builder.append( "updated=" );
			builder.append( updated );
			builder.append( ", " );
		}
		
		if( title != null ) {
			builder.append( "title=" );
			builder.append( title );
			builder.append( ", " );
		}
		
		if( subTitle != null ) {
			builder.append( "subTitle=" );
			builder.append( subTitle );
			builder.append( ", " );
		}
		
		if( entries != null ) {
			builder.append( "entries=" );
			builder.append( entries.subList( 0, Math.min( entries.size(), maxLen ) ) );
		}
		
		builder.append( "]" );
		
		return builder.toString();
	}
	
}
