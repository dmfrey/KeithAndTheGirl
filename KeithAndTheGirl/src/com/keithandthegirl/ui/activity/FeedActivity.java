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
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
public class FeedActivity extends ListActivity {

	private static final String TAG = FeedActivity.class.getSimpleName();

	private Intent feedReceiverIntent;
	
	private ProgressDialog progressDialog;

	//***************************************
    // Activity methods
    //***************************************
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
	    Log.d( TAG, "onCreate : enter" );

	    super.onCreate( savedInstanceState );

	    setContentView( R.layout.activity_feed );

	    setupActionBar();
	    
	    feedReceiverIntent = new Intent( this, UpdateFeedService.class );
	    
	    showProgressDialog();
	    
	    Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
	    Log.d( TAG, "onPause : enter" );

		super.onPause();
		
		unregisterReceiver( updateFeedBroadcastReceiver );
		stopService( feedReceiverIntent ); 		

	    Log.d( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
	    Log.d( TAG, "onResume : enter" );

	    super.onResume();

		registerReceiver( updateFeedBroadcastReceiver, new IntentFilter( UpdateFeedService.BROADCAST_ACTION ) );
		if( null == ( (MainApplication) getApplicationContext() ).getFeed() ) {
		    startService( feedReceiverIntent );
		} else {
			refreshFeedEntries();
		}

		Log.d( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {		
	    Log.d( TAG, "onOptionsItemSelected : enter" );

	    Intent intent = new Intent();
	    
	    // Handle item selection
	    switch( item.getItemId() ) {
	    case android.R.id.home:
            // app icon in action bar clicked; go home
            intent = new Intent( this, HomeActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
	    	break;
	    case R.id.feed_menu_refresh:
	    	
		    startService( feedReceiverIntent );

	    	Log.d( TAG, "onOptionsItemSelected : exit, refresh option selected" );
	    	break;
	    }
	    
	    return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		Log.d( TAG, "onCreateContextMenu : enter" );
		
		super.onCreateContextMenu( menu, v, menuInfo );
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.feed_entry_context_menu, menu );

	    Log.d( TAG, "onCreateContextMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		Log.d( TAG, "onContextItemSelected : enter" );
		
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Log.d( TAG, "onContextItemSelected : info.id=" + info.id + ", info.position=" + info.position );

        setCurrentEntry( (SyndEntry) ( (MainApplication) getApplicationContext() ).getFeed().getEntries().get( info.position ) );
        
        switch( item.getItemId() ) {
	        case R.id.feed_entry_context_menu_play:
	        	( (MainApplication) getApplicationContext() ).setSelectedPlayType( PlayType.RECORDED );
	        	play();
	        	
	            Log.d( TAG, "onContextItemSelected : exit, play selected" );
	            return true;
//	        case R.id.feed_entry_context_menu_download:
//	            download();
//
//	            Log.d( TAG, "onContextItemSelected : exit, download selected" );
//	            return true;
	        default:
	    		Log.d( TAG, "onContextItemSelected : exit" );
	            return super.onContextItemSelected( item );
	    }
    }


	//***************************************
    // Private methods
    //***************************************

	@TargetApi( 11 )
	private void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

	@SuppressWarnings( "unchecked" )
	private void refreshFeedEntries() {
		Log.d( TAG, "refreshFeedEntries : enter" );
		
		SyndFeed feed = ( (MainApplication) getApplicationContext() ).getFeed();
		if( null != feed && !feed.getEntries().isEmpty() ) {
			ListAdapter adapter = new FeedEntryListAdapter( feed.getEntries(), this );
	
			registerForContextMenu( getListView() );

			ListView lv = getListView();
		    lv.setAdapter( adapter );
		    lv.setClickable( true );
		    lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick( AdapterView<?> arg0, View v, int position, long arg3 ) {
					Log.v( TAG, "onItemClick : enter" );
					
					setCurrentEntry( (SyndEntry) ( (MainApplication) getApplicationContext() ).getFeed().getEntries().get( position ) );
					( (MainApplication) getApplicationContext() ).setSelectedPlayType( PlayType.RECORDED );
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

		( (MainApplication) getApplicationContext() ).setSelectedEntry( entry );

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
		
		startActivity( new Intent( this, PlayerActivity.class ) );
		
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
		showProgressDialog( "Loading Episodes. Please wait..." );
	}
	
	private void showProgressDialog( String message) {
		if( progressDialog == null ) {
			progressDialog = new ProgressDialog( this );
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