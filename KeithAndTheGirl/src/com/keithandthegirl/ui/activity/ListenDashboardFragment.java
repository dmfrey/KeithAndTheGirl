/**
 * 
 */
package com.keithandthegirl.ui.activity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.api.google.Feed;
import com.keithandthegirl.api.google.When;
import com.keithandthegirl.services.UpdateCalendarService;

/**
 * @author Daniel Frey
 *
 */
public class ListenDashboardFragment extends Fragment {

	private final static String TAG = ListenDashboardFragment.class.getSimpleName();
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern( "MMM dd '@' hh:mm a" );

	private Intent calendarReceiverIntent;
	private Button liveButton;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
	    calendarReceiverIntent = new Intent( getActivity(), UpdateCalendarService.class );

		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_listen_dashboard, container, false );

	    liveButton = (Button) root.findViewById( R.id.listen_btn_live );

		// Attach event handlers
		root.findViewById( R.id.listen_btn_feed ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "feed.onClick : enter" );
				
				startActivity( new Intent( getActivity(), FeedActivity.class ) );

				Log.v( TAG, "feed.onClick : exit" );
			}

		} );

		root.findViewById( R.id.listen_btn_live ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "live.onClick : enter" );

				( (MainApplication) getActivity().getApplicationContext() ).setSelectedPlayType( PlayType.LIVE );
				startActivity( new Intent( getActivity(), PlayerActivity.class ) );
				
				Log.v( TAG, "live.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.d( TAG, "onPause : enter" );
		super.onPause();
		
		getActivity().unregisterReceiver( updateCalendarBroadcastReceiver );
		getActivity().stopService( calendarReceiverIntent ); 		

		Log.d( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d( TAG, "onResume : enter" );
		super.onResume();

		getActivity().registerReceiver( updateCalendarBroadcastReceiver, new IntentFilter( UpdateCalendarService.BROADCAST_ACTION ) );
		if( null == ( (MainApplication) getActivity().getApplicationContext() ).getCalendarFeed() ) {
			Log.d( TAG, "onResume : starting calendarReceiverIntent" );

			getActivity().startService( calendarReceiverIntent );
		} else {
			refreshLiveStreamInfo();
		}

		Log.d( TAG, "onResume : exit" );
	}

	// internal helpers
	
	private void refreshLiveStreamInfo() {
		Log.d( TAG, "refreshLiveStreamInfo : enter" );
		
		Feed calendarFeed = ( (MainApplication) getActivity().getApplicationContext() ).getCalendarFeed();
		if( null != calendarFeed && !calendarFeed.getEntries().isEmpty() ) {

			DateTime now = new DateTime();
			When when = calendarFeed.getEntries().get( 0 ).getWhen();
			
			Log.d( TAG, "refreshLiveStreamInfo : after start=" + now.isAfter( when.getStartTime() )  );
			Log.d( TAG, "refreshLiveStreamInfo : before end=" + now.isBefore( when.getEndTime() )  );

			if( now.isAfter( when.getStartTime() ) && now.isBefore( when.getEndTime() ) ) {
				Log.v( TAG, "refreshLiveStreamInfo : live streaming now!" );
				
				liveButton.setEnabled( true );
				liveButton.setText( getResources().getString( R.string.btn_live ) + " NOW!!" );
			} else {
				Log.v( TAG, "refreshLiveStreamInfo : NOT live streaming now!" );

				liveButton.setEnabled( false );
				liveButton.setText( getResources().getString( R.string.btn_live ) + " " + fmt.print( when.getStartTime() ) );
			}
			
		}
		
		Log.d( TAG, "refreshLiveStreamInfo : exit" );
	}

    private BroadcastReceiver updateCalendarBroadcastReceiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.d( TAG, "onReceive : enter" );

			refreshLiveStreamInfo();
		
			Log.d( TAG, "onReceive : exit" );
		}
    
	};

}
