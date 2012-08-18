/**
 * 
 */
package com.keithandthegirl.api.guests;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Daniel Frey
 *
 */
@Root( name = "Guest" )
public class Guest {

	@Element( name = "ShowGuestId" )
	private int showGuestId;
	
	@Element( name = "RealName" )
	private String realName;
	
	@Element( name = "ForumName", required = false )
	private String forumName;
	
	@Element( name = "Description", required = false )
	private String description;
	
	@Element( name = "EpisodeCount", required = false )
	private int episodeCount;
	
	@Element( name = "PictureUrl", required = false )
	private String pictureUrl;
	
	@Element( name = "Urls", required = false )
	private Urls urls;
	
	public Guest() { }

	/**
	 * @return the showGuestId
	 */
	public int getShowGuestId() {
		return showGuestId;
	}

	/**
	 * @param showGuestId the showGuestId to set
	 */
	public void setShowGuestId( int showGuestId ) {
		this.showGuestId = showGuestId;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName( String realName ) {
		this.realName = realName;
	}

	/**
	 * @return the forumName
	 */
	public String getForumName() {
		return forumName;
	}

	/**
	 * @param forumName the forumName to set
	 */
	public void setForumName( String forumName ) {
		this.forumName = forumName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * @return the episodeCount
	 */
	public int getEpisodeCount() {
		return episodeCount;
	}

	/**
	 * @param episodeCount the episodeCount to set
	 */
	public void setEpisodeCount( int episodeCount ) {
		this.episodeCount = episodeCount;
	}

	/**
	 * @return the pictureUrl
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}

	/**
	 * @param pictureUrl the pictureUrl to set
	 */
	public void setPictureUrl( String pictureUrl ) {
		this.pictureUrl = pictureUrl;
	}

	/**
	 * @return the urls
	 */
	public Urls getUrls() {
		return urls;
	}

	/**
	 * @param urls the urls to set
	 */
	public void setUrls( Urls urls ) {
		this.urls = urls;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "Guest [showGuestId=" );
		builder.append( showGuestId );
		builder.append( ", " );
		if( realName != null ) {
			builder.append( "realName=" );
			builder.append( realName );
			builder.append( ", " );
		}
		if( forumName != null ) {
			builder.append( "forumName=" );
			builder.append( forumName );
			builder.append( ", " );
		}
		if( description != null ) {
			builder.append( "description=" );
			builder.append( description );
			builder.append( ", " );
		}
		builder.append( "episodeCount=" );
		builder.append( episodeCount );
		builder.append( ", " );
		if( pictureUrl != null ) {
			builder.append( "pictureUrl=" );
			builder.append( pictureUrl );
			builder.append( ", " );
		}
		if( urls != null ) {
			builder.append( "urls=" );
			builder.append( urls );
		}
		builder.append( "]" );
		return builder.toString();
	}
	
}
