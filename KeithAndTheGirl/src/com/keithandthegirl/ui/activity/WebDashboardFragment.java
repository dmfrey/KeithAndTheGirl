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

import com.keithandthegirl.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Daniel Frey
 *
 */
public class WebDashboardFragment extends Fragment {

	private final static String TAG = WebDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_web_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.web_btn_website ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "web.site.onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://www.keithandthegirl.com/" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "web.site.onClick : exit" );
			}

		} );

		root.findViewById( R.id.web_btn_forums ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "web.forums.onClick : enter" );

				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://www.keithandthegirl.com/forums/" ) ); 
				startActivity( tweetIntent );

	            Log.v( TAG, "web.forums.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
