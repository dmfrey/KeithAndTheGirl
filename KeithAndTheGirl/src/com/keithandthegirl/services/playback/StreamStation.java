/**
 * 
 */
package com.keithandthegirl.services.playback;

import com.keithandthegirl.MainApplication.PlayType;

/**
 * A class to represent Streaming media stations. Holds label, URL, and other
 * information.
 * 
 * @author rootlicker http://speakingcode.com
 */
public class StreamStation {

	private PlayType mStationPlayType;
	private String mStationLabel;
	private String mStationUrl;
	private String mStationDescription;

	/**
	 * Constructs a new StreamStation object with empty label and URL
	 */
	public StreamStation() {
		this( null, "", "", "" );
	}

	/**
	 * Constructs a new StreamStation object with specified label and URL
	 * 
	 * @param stationLabel
	 * @param stationUrl
	 */
	public StreamStation( PlayType playType, String stationLabel, String stationUrl, String stationDescription ) {
		mStationPlayType = playType;
		mStationLabel = stationLabel;
		mStationUrl = stationUrl;
		mStationDescription = stationDescription;
	}

	@Override
	public boolean equals( Object obj ) {
		if( this == obj )
			return true;
		
		if( obj == null )
			return false;
		
		if( getClass() != obj.getClass() )
			return false;
		
		StreamStation other = (StreamStation) obj;
		if( mStationLabel == null ) {
			if( other.mStationLabel != null )
				return false;
		} else if( !mStationLabel.equals( other.mStationLabel ) )
			return false;
		
		if( mStationUrl == null ) {
			if( other.mStationUrl != null )
				return false;
		} else if( !mStationUrl.equals( other.mStationUrl ) )
			return false;
		
		return true;
	}

	/**
	 * Gets the station's PlayType as a String
	 * 
	 * @return the station PlayType
	 */
	public PlayType getStationPlayType() {
		return mStationPlayType;
	}

	/**
	 * Gets the station's label as a String
	 * 
	 * @return the station label
	 */
	public String getStationLabel() {
		return mStationLabel;
	}

	/**
	 * Gets the station's URL, as a String
	 * 
	 * @return the URL of the station
	 */
	public String getStationUrl() {
		return mStationUrl;
	}

	/**
	 * Gets the station's description, as a String
	 * 
	 * @return the mStationDescription
	 */
	public String getmStationDescription() {
		return mStationDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( mStationLabel == null ) ? 0 : mStationLabel.hashCode() );
		result = prime * result + ( ( mStationUrl == null ) ? 0 : mStationUrl.hashCode() );
		result = prime * result + ( ( mStationDescription == null ) ? 0 : mStationDescription.hashCode() );
		return result;
	}

	/**
	 * Sets a PlayType as the station's PlayType
	 * 
	 * @param playType
	 *            the PlayType to set
	 */
	public void setStationPlayType( PlayType playType ) {
		this.mStationPlayType = playType;
	}

	/**
	 * Sets a String as the station's label
	 * 
	 * @param stationLabel
	 *            the label to set
	 */
	public void setStationLabel( String stationLabel ) {
		this.mStationLabel = stationLabel;
	}

	/**
	 * Set's a String as the station's URL
	 * 
	 * @param stationUrl
	 *            the URL of the Station
	 */
	public void setStationUrl( String mStationUrl ) {
		this.mStationUrl = mStationUrl;
	}

	/**
	 * Set's a String as the station's Description
	 * 
	 * @param mStationDescription the mStationDescription to set
	 */
	public void setmStationDescription( String mStationDescription ) {
		this.mStationDescription = mStationDescription;
	}

}
