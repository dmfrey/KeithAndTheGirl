/**
 * 
 */
package com.keithandthegirl.ui.activity;

import com.keithandthegirl.R;

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
public class TwitterDashboardFragment extends Fragment {

	private final static String TAG = TwitterDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_twitter_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.twitter_btn_keith ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "keith.onClick : enter" );
				
//				startActivity( new Intent( getActivity(), RecordingsActivity.class ) );
//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Log.v( TAG, "keith.onClick : exit" );
			}

		} );

		root.findViewById( R.id.twitter_btn_chemda ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "chemda.onClick : enter" );

				Log.v( TAG, "chemda.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
