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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.activities.FeedEntryListAdapter;
import com.keithandthegirl.services.UpdateFeedService;

/**
 * @author Daniel Frey
 *
 */
public class EpisodesActivity extends FragmentActivity {

	private static final String TAG = EpisodesActivity.class.getSimpleName();
	private static final int ABOUT_ID = Menu.FIRST + 2;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle bundle ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( bundle );

		setContentView( R.layout.fragment_feed );
		
		setupActionBar();
		
		Log.v( TAG, "onCreate : enter" );
	}

	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

	    MenuItem about = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, "About" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	about.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }

		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, HomeActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity( intent );
				
				return true;
			case ABOUT_ID:
				Log.d( TAG, "onOptionsItemSelected : about selected" );

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				Fragment prev = getSupportFragmentManager().findFragmentByTag( "aboutDialog" );
				if( null != prev ) {
					ft.remove( prev );
				}
				ft.addToBackStack( null );

				DialogFragment newFragment = AboutDialogFragment.newInstance();
				newFragment.show( ft, "aboutDialog" );
		    
				return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	@TargetApi( 11 )
	private void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

	public static class FeedListFragment extends ListFragment {
		
		private static final String TAG = FeedListFragment.class.getSimpleName();
		private static final int REFRESH_ID = Menu.FIRST + 1;

		private ProgressDialog progressDialog;

		private Intent feedReceiverIntent;

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated( Bundle savedInstanceState ) {
			Log.v( TAG, "onActivityCreated : enter" );
			
			setHasOptionsMenu( true );
			
			feedReceiverIntent = new Intent( getActivity(), UpdateFeedService.class );

			Log.v( TAG, "onActivityCreated : exit" );
			super.onActivityCreated( savedInstanceState );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onPause()
		 */
		@Override
		public void onPause() {
			Log.v( TAG, "onPause : enter" );
			super.onPause();
			
			getActivity().unregisterReceiver( updateFeedBroadcastReceiver );
			getActivity().stopService( feedReceiverIntent ); 		

			Log.v( TAG, "onPause : exit" );
		}
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			Log.v( TAG, "onResume : enter" );
		    super.onResume();

			getActivity().registerReceiver( updateFeedBroadcastReceiver, new IntentFilter( UpdateFeedService.BROADCAST_ACTION ) );
			if( null == ( (MainApplication) getActivity().getApplicationContext() ).getFeed() ) {
			    getActivity().startService( feedReceiverIntent );
			} else {
				refreshFeedEntries();
			}

			Log.v( TAG, "onResume : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
		 */
		@TargetApi( 11 )
		@Override
		public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
			Log.v( TAG, "onCreateOptionsMenu : enter" );
			super.onCreateOptionsMenu( menu, inflater );

		    MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh" );
		    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
		    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
		    }

		    Log.v( TAG, "onCreateOptionsMenu : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
		 */
		@Override
		public boolean onOptionsItemSelected( MenuItem item ) {
			Log.v( TAG, "onOptionsItemSelected : enter" );

			switch( item.getItemId() ) {
			case REFRESH_ID:
				Log.d( TAG, "onOptionsItemSelected : refresh selected" );

				showProgressDialog();
				
			    getActivity().startService( feedReceiverIntent );
		    
				return true;
			}
			
			Log.v( TAG, "onOptionsItemSelected : exit" );
			return super.onOptionsItemSelected( item );
		}

		@SuppressWarnings( "unchecked" )
		private void refreshFeedEntries() {
			Log.d( TAG, "refreshFeedEntries : enter" );
			
			SyndFeed feed = ( (MainApplication) getActivity().getApplicationContext() ).getFeed();
			if( null != feed && !feed.getEntries().isEmpty() ) {
				Log.d( TAG, "refreshFeedEntries : adding feed to list" );
				
				ListAdapter adapter = new FeedEntryListAdapter( feed.getEntries(), getActivity() );
				setListAdapter( adapter );
				
				registerForContextMenu( getListView() );

				ListView lv = getListView();
			    lv.setAdapter( adapter );
			    lv.setClickable( true );
			    lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick( AdapterView<?> arg0, View v, int position, long arg3 ) {
						Log.v( TAG, "onItemClick : enter" );
						
						SyndEntry selectedEntry = ( (MainApplication) getActivity().getApplicationContext() ).getSelectedEntry();
						if( null != selectedEntry && !selectedEntry.equals( ( (SyndEntry) ( (MainApplication) getActivity().getApplicationContext() ).getFeed().getEntries().get( position ) ) ) && ( (MainApplication) getActivity().getApplicationContext() ).isPlaying() ) {
							( (MainApplication) getActivity().getApplicationContext() ).setPlaying( false );
						}
						
						setCurrentEntry( (SyndEntry) ( (MainApplication) getActivity().getApplicationContext() ).getFeed().getEntries().get( position ) );
						( (MainApplication) getActivity().getApplicationContext() ).setSelectedPlayType( PlayType.RECORDED );
						play();
						
						Log.v( TAG, "onItemClick : exit" );
					}
			    	
			    });
			}

			dismissProgressDialog();
			
			Log.d( TAG, "refreshFeedEntries : exit" );
		}

		private void setCurrentEntry( SyndEntry entry ) {
			Log.d( TAG, "setCurrentEntry : enter" );

			( (MainApplication) getActivity().getApplicationContext() ).setSelectedEntry( entry );

			Log.d( TAG, "Entry Title=" + entry.getTitle() );
			Log.d( TAG, "Entry Description=" + entry.getDescription() );
			Log.d( TAG, "Entry Link=" + entry.getLink() );
			Log.d( TAG, "Entry Publish Date=" + entry.getPublishedDate() );

			for( Object obj1 : entry.getEnclosures() ) {
				SyndEnclosure enclosure = (SyndEnclosure) obj1;
				Log.d( TAG, "Entry Enclosure Url=" + enclosure.getUrl() );
				Log.d( TAG, "Entry Enclosure Length=" + enclosure.getLength() );
				Log.d( TAG, "Entry Enclosure Type=" + enclosure.getType() );
			}

			Log.d( TAG, "setCurrentEntry : exit" );
		}

		private void play() {
			Log.d( TAG, "play : enter" );
			
			startActivity( new Intent( getActivity(), PlayerActivity.class ) );
			
			Log.d( TAG, "play : exit" );
		}
		
	    private BroadcastReceiver updateFeedBroadcastReceiver = new BroadcastReceiver() {
	    	
	        @Override
	        public void onReceive( Context context, Intent intent ) {
	    		Log.d( TAG, "onReceive : enter" );

	    		refreshFeedEntries();
	    		
	    		Log.d( TAG, "onReceive : exit" );
	        }
	        
	    };
	    
		public void showProgressDialog() {
			showProgressDialog( "Refreshing Episodes. Please wait..." );
		}
		
		private void showProgressDialog( String message) {
			if( progressDialog == null ) {
				progressDialog = new ProgressDialog( getActivity() );
				progressDialog.setIndeterminate( true );
			}
			
			progressDialog.setMessage( message );
			progressDialog.show();
		}
		
		private void dismissProgressDialog() {
			if( progressDialog != null ) {
				progressDialog.dismiss();
			}
		}
		
	}
	
}
