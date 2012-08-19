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
package com.keithandthegirl;

import java.text.SimpleDateFormat;

import android.app.Application;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.keithandthegirl.api.google.Feed;
import com.keithandthegirl.api.guests.Guests;

/**
 * @author Daniel Frey
 *
 */
public class MainApplication extends Application {

	public static final String KATG_CALENDAR_FEED = "http://www.google.com/calendar/feeds/katgcalendar@gmail.com/public/full?orderby=starttime&max-results=1&singleevents=true&sortorder=ascending&futureevents=true&key=AIzaSyBgDE29wuN7U53aJzFWKJ2gzBYBxLLZmF4";
	public static final String KATG_WEB_SITE = "http://www.keithandthegirl.com";
	public static final String KATG_LIVE_STREAM = "http://liveshow.keithandthegirl.com:8004/";
	public static final String KATG_RSS_FEED = "http://www.keithandthegirl.com/rss/";
	public static final String KATG_PHONE_NUMBER = "6465028682";
	public static final String KATG_GUEST_URL = "http://www.keithandthegirl.com/Api/App/guest/list-all/";
	
	public enum PlayType{ LIVE, RECORDED };

	public enum Sort{
		MOST_RECENT( 0 ),
		TOP_COUNT( 1 ),
		NAME( 2 );
		
		private Integer type;
		
		Sort( Integer type ) {
			this.type = type;
		}
		
		public Integer getType() {
			return type;
		}
		
	}
	
	private SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
	
	private SyndFeed feed;
	
	private SyndEntry selectedEntry;
	
	private PlayType selectedPlayType;
	
	private Feed calendarFeed;
	
	private boolean playing;
	
	private Guests guest;
	
	private Sort guestSort;
	
	//***************************************
    // Application methods
    //***************************************
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	
    // ***************************************
    // Private methods
    // ***************************************

	
    // ***************************************
    // Public methods
    // ***************************************

	/**
	 * @return the format
	 */
	public SimpleDateFormat getFormat() {
		return format;
	}
	
	/**
	 * @return the feed
	 */
	public SyndFeed getFeed() {
		return feed;
	}

	/**
	 * @param feed the feed to set
	 */
	public void setFeed( SyndFeed feed ) {
		this.feed = feed;
	}


	/**
	 * @return the selectedEntry
	 */
	public SyndEntry getSelectedEntry() {
		return selectedEntry;
	}


	/**
	 * @param selectedEntry the selectedEntry to set
	 */
	public void setSelectedEntry( SyndEntry selectedEntry ) {
		this.selectedEntry = selectedEntry;
	}


	/**
	 * @return the selectedPlayType
	 */
	public PlayType getSelectedPlayType() {
		return selectedPlayType;
	}


	/**
	 * @param selectedPlayType the selectedPlayType to set
	 */
	public void setSelectedPlayType( PlayType selectedPlayType ) {
		this.selectedPlayType = selectedPlayType;
	}


	/**
	 * @return the feed
	 */
	public Feed getCalendarFeed() {
		return calendarFeed;
	}


	/**
	 * @param events the events to set
	 */
	public void setCalendarFeed( Feed calendarFeed ) {
		this.calendarFeed = calendarFeed;
	}


	/**
	 * @return the playing
	 */
	public boolean isPlaying() {
		return playing;
	}


	/**
	 * @param playing the playing to set
	 */
	public void setPlaying( boolean playing ) {
		this.playing = playing;
	}


	/**
	 * @return the guest
	 */
	public Guests getGuest() {
		return guest;
	}


	/**
	 * @param guest the guest to set
	 */
	public void setGuest( Guests guest ) {
		this.guest = guest;
	}


	/**
	 * @return the guestSort
	 */
	public Sort getGuestSort() {
		return guestSort;
	}


	/**
	 * @param guestSort the guestSort to set
	 */
	public void setGuestSort( Sort guestSort ) {
		this.guestSort = guestSort;
	}

}
