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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.R;
import com.keithandthegirl.api.google.Feed;
import com.keithandthegirl.api.google.When;
import com.keithandthegirl.services.UpdateCalendarService;

/**
 * @author Daniel Frey
 * 
 */
public class HomeActivity extends FragmentActivity {

	private static final String TAG = HomeActivity.class.getSimpleName();
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern( "MMM dd '@' hh:mm a" );
	private static final int ABOUT_ID = Menu.FIRST + 1;

	private Intent calendarReceiverIntent;
	private Resources resources;
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractLocationAwareFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_home );

		resources = getResources();
		
		setupActionBar();
		
	    calendarReceiverIntent = new Intent( this, UpdateCalendarService.class );

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add( Fragment.instantiate( this, ListenDashboardFragment.class.getName() ) );
		fragments.add( Fragment.instantiate( this, TwitterDashboardFragment.class.getName() ) );

		KatgPagerAdapter mAdapter = new KatgPagerAdapter( getSupportFragmentManager(), fragments );
		ViewPager mPager = (ViewPager) findViewById( R.id.home_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 0 );

		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d( TAG, "onPause : enter" );
		super.onPause();
		
		unregisterReceiver( updateCalendarBroadcastReceiver );
		stopService( calendarReceiverIntent ); 		

		Log.d( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.d( TAG, "onResume : enter" );
		super.onResume();

		registerReceiver( updateCalendarBroadcastReceiver, new IntentFilter( UpdateCalendarService.BROADCAST_ACTION ) );
		if( null == ( (MainApplication) getApplicationContext() ).getCalendarFeed() ) {
			Log.d( TAG, "onResume : starting calendarReceiverIntent" );

			startService( calendarReceiverIntent );
//			//showProgressDialog();
		} else {
//			refreshFeedEntries();
		}

		Log.d( TAG, "onResume : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@TargetApi( 11 )
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

	    MenuItem prefs = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, "ABOUT" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	prefs.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }

		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return super.onCreateOptionsMenu( menu );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onOptionsItemSelected(android.view.
	 * MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
//		case R.id.menu_frontends:
//			Log.d( TAG, "onOptionsItemSelected : setup selected" );
//
//			startActivity( new Intent( this, MythmoteActivity.class ) );
//			return true;
//		case R.id.menu_setup:
//			Log.d( TAG, "onOptionsItemSelected : setup selected" );
//
//			startActivity( new Intent( this, SetupActivity.class ) );
//			return true;
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


	// internal helpers
	
	private void refreshLiveStreamInfo() {
		Log.d( TAG, "refreshLiveStreamInfo : enter" );
		
		Feed calendarFeed = ( (MainApplication) getApplicationContext() ).getCalendarFeed();
		if( null != calendarFeed && !calendarFeed.getEntries().isEmpty() ) {

			DateTime now = new DateTime();
			When when = calendarFeed.getEntries().get( 0 ).getWhen();
			
			Log.d( TAG, "refreshLiveStreamInfo : after start=" + now.isAfter( when.getStartTime() )  );
			Log.d( TAG, "refreshLiveStreamInfo : before end=" + now.isBefore( when.getEndTime() )  );

			if( now.isAfter( when.getStartTime() ) && now.isBefore( when.getEndTime() ) ) {
//				Log.v( TAG, "refreshLiveStreamInfo : live streaming now!" );
//				
//				liveButton.setEnabled( true );
//				callButton.setEnabled( true );
			} else {
//				Log.v( TAG, "refreshLiveStreamInfo : NOT live streaming now!" );
//
//				liveButton.setEnabled( false );
//				callButton.setEnabled( false );
			}
//			
//			nextShow.setText( fmt.print( when.getStartTime() ) );
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

	@TargetApi( 11 )
	private void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( false );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

	private class KatgPagerAdapter extends FragmentStatePagerAdapter {

		private List<Fragment> fragments;
		
		public KatgPagerAdapter( FragmentManager fm, List<Fragment> fragments ) {
			super( fm );
			
			this.fragments = fragments;
			
		}

		@Override
		public Fragment getItem( int position ) {
			return fragments.get( position );
		}

		public int getCount() {
			return fragments.size();
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
		 */
		@Override
		public CharSequence getPageTitle( int position ) {

			switch( position ) {
			case 0:
				return resources.getString( R.string.tab_listen );
			case 1:
				return resources.getString( R.string.tab_twitter );
			}

			return super.getPageTitle( position );
		}
		
	}

}
