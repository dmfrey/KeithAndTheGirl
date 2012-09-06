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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keithandthegirl.R;

/**
 * @author Daniel Frey
 *
 */
public class HostDashboardFragment extends Fragment {

	private final static String TAG = HostDashboardFragment.class.getSimpleName();
	private static final String TWITTER_ADDRESS = "http://twitter.com/#!/";
	private static final String FACEBOOK_ADDRESS = "http://facebook.com/";
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_hosts, container, false );

		// Attach event handlers
		root.findViewById( R.id.host_btn_facebook ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "show facebook onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( FACEBOOK_ADDRESS + "keithandthegirl" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "show facebook onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_btn_twitter ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "show twitter onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( TWITTER_ADDRESS + "keithandthegirl" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "show twitter onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_btn_website ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "show website onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://www.keithandthegirl.com/" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "show website onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_keith_facebook ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "keith facebook onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( FACEBOOK_ADDRESS + "KeithMalley" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "keith facebook onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_keith_twitter ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "keith twitter onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( TWITTER_ADDRESS + "KeithMalley" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "keith twitter onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_chemda_facebook ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "chemda facebook onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( FACEBOOK_ADDRESS + "chemda" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "chemda facebook onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_chemda_twitter ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "chemda twitter onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( TWITTER_ADDRESS + "chemda" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "chemda twitter onClick : exit" );
			}

		} );

		root.findViewById( R.id.host_chemda_www ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "chemda www onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "http://www.chemda.com" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "chemda www onClick : exit" );
			}

		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
