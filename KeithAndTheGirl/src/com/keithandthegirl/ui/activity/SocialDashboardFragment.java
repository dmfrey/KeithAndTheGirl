/**
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
public class SocialDashboardFragment extends Fragment {

	private final static String TAG = SocialDashboardFragment.class.getSimpleName();
	private static final String TWITTER_ADDRESS = "http://twitter.com/#!/";
	private static final String FACEBOOK_ADDRESS = "http://facebook.com/";
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_social_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.twitter_btn_keith ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "twitter.keith.onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( TWITTER_ADDRESS + "KeithMalley" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "twitter.keith.onClick : exit" );
			}

		} );

		root.findViewById( R.id.twitter_btn_chemda ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "twitter.chemda.onClick : enter" );

				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( TWITTER_ADDRESS + "chemda" ) ); 
				startActivity( tweetIntent );

	            Log.v( TAG, "twitter.chemda.onClick : exit" );
			}
		} );

		root.findViewById( R.id.facebook_btn_keith ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "facebook.keith.onClick : enter" );
				
				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( FACEBOOK_ADDRESS + "KeithMalley" ) ); 
				startActivity( tweetIntent );
				
				Log.v( TAG, "facebook.keith.onClick : exit" );
			}

		} );

		root.findViewById( R.id.facebook_btn_chemda ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "facebook.chemda.onClick : enter" );

				Intent tweetIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( FACEBOOK_ADDRESS + "chemda" ) ); 
				startActivity( tweetIntent );

	            Log.v( TAG, "facebook.chemda.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
