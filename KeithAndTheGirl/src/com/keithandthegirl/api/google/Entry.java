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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * @author Daniel Frey
 *
 */
@Root( strict = false )
public class Entry {

	@Element( name = "id" )
	private String id;

	@Element( name = "published" )
	@Convert( DateConverter.class )
	private DateTime published;
	
	@Element( name = "updated" )
	@Convert( DateConverter.class )
	private DateTime updated;
	
	@Element( name = "title" )
	private String title;

	@Element( name = "content" )
	private String content;
	
	@Element( name = "when" )
	@Namespace( prefix = "gd" )
	@Convert( WhenConverter.class )
	private When when;
	
	/**
	 * 
	 */
	public Entry() { }
	
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
	 * @return the published
	 */
	public DateTime getPublished() {
		return published;
	}

	/**
	 * @param published the published to set
	 */
	public void setPublished( DateTime published ) {
		this.published = published;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	/**
	 * @return the when
	 */
	public When getWhen() {
		return when;
	}

	/**
	 * @param when the when to set
	 */
	public void setWhen( When when ) {
		this.when = when;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "Entry [" );
		
		if( id != null ) {
			builder.append( "id=" );
			builder.append( id );
			builder.append( ", " );
		}
		
		if( published != null ) {
			builder.append( "published=" );
			builder.append( published );
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
		
		if( content != null ) {
			builder.append( "content=" );
			builder.append( content );
			builder.append( ", " );
		}
		
		if( when != null ) {
			builder.append( "when=" );
			builder.append( when );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
