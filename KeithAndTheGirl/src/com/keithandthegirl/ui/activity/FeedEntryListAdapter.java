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
package com.keithandthegirl.ui.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
public class FeedEntryListAdapter extends BaseAdapter {

	private List<SyndEntry> entries;
	
	private Context context;
	
	public FeedEntryListAdapter( List<SyndEntry> entries, Context context ) {
		this.entries = entries;
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return entries.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem( int position ) {
		return entries.get( position );
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId( int position ) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
        SyndEntry entry = entries.get( position );
 
        LinearLayout itemLayout= (LinearLayout) LayoutInflater.from( context ).inflate( R.layout.feed_entry, parent, false );
 
        TextView title = (TextView) itemLayout.findViewById( R.id.entry_title );
        title.setText( entry.getTitle() );
 
        String value = entry.getDescription().getValue();
        value = value.replace( "<p>", "" );
        value = value.replace( "</p>", "" );
        value = value.replace( "\"", "" );
        
        TextView description = (TextView) itemLayout.findViewById( R.id.entry_description );
        description.setText( value );
         
        TextView date = (TextView) itemLayout.findViewById( R.id.entry_date );
        date.setText( ( (MainApplication) context.getApplicationContext() ).getFormat().format( entry.getPublishedDate() ) );

        return itemLayout;
	}

}
