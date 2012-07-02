/**
 * 
 */
package com.keithandthegirl.ui.activity;

import android.content.Intent;
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
public class ListenDashboardFragment extends Fragment {

	private final static String TAG = ListenDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_listen_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.listen_btn_feed ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "feed.onClick : enter" );
				
				startActivity( new Intent( getActivity(), FeedActivity.class ) );
//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Log.v( TAG, "feed.onClick : exit" );
			}

		} );

		root.findViewById( R.id.listen_btn_live ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "live.onClick : enter" );

				Log.v( TAG, "live.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
