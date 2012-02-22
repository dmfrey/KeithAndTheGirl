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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/KeithAndTheGirl/>
 *
 */
package com.keithandthegirl.activities;

import java.net.URL;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.services.MediaPlayerService;

/**
 * @author Daniel Frey
 *
 */
public class FeedActivity extends AbstractKatgListActivity implements OnClickListener {

	private static final String TAG = FeedActivity.class.getSimpleName();
	
	private Button liveButton, stopButton, wwwButton;
	private TextView nowPlaying;
	
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

	    setContentView( R.layout.main );
	    
//	    liveButton = (Button) findViewById( R.id.live_button );
	    stopButton = (Button) findViewById( R.id.stop_button );
	    wwwButton = (Button) findViewById( R.id.www_button );
	    
	    nowPlaying = (TextView) findViewById( R.id.now_playing );
	    
//	    liveButton.setOnClickListener( this );
	    stopButton.setOnClickListener( this );
	    wwwButton.setOnClickListener( this );
	    
	    Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
	    Log.d( TAG, "onStart : enter" );

	    super.onStart();
	    
		Log.d( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
	    Log.d( TAG, "onResume : enter" );

	    super.onResume();

	    refreshFeed();
	    updateNowPlaying();
	    
		Log.d( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
	    Log.d( TAG, "onCreateOptionsMenu : enter" );

	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.feed_menu, menu );

	    Log.d( TAG, "onCreateOptionsMenu : exit" );
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {		
	    Log.d( TAG, "onOptionsItemSelected : enter" );

	    // Handle item selection
	    switch( item.getItemId() ) {
	    case R.id.feed_menu_refresh:
	    	
	    	getApplicationContext().setFeed( null );
	    	refreshFeed();

	    	Log.d( TAG, "onOptionsItemSelected : exit, refresh option selected" );
	    	return true;
	    case R.id.feed_menu_about:
			Intent intent = new Intent();
			intent.setClass( this, AboutActivity.class );
			startActivity( intent );

	    	Log.d( TAG, "onOptionsItemSelected : exit, about option selected" );
	    	return true;
	    case R.id.feed_menu_quit:
		    stopService( new Intent( this, MediaPlayerService.class ) );
			clearNowPlaying();

			finish();
	    	
	    	Log.d( TAG, "onOptionsItemSelected : exit, quit option selected" );
	    	return true;
	    default:
	    	Log.d( TAG, "onOptionsItemSelected : exit, default option selected" );
	        return super.onOptionsItemSelected( item );
	    }
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

        setCurrentEntry( (SyndEntry) getApplicationContext().getFeed().getEntries().get( info.position ) );
        
        switch( item.getItemId() ) {
	        case R.id.feed_entry_context_menu_play:
	            getApplicationContext().setSelectedPlayType( PlayType.RECORDED );
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

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick( View v ) {
		Log.d( TAG, "onClick : enter" );
		
		switch( v.getId() ) {
//			case R.id.live_button :
//				Log.v( TAG, "onClick : live button pressed" );
//				
//				getApplicationContext().setSelectedPlayType( PlayType.LIVE );
//				new VerifyLiveStreamTask().execute( MainApplication.KATG_LIVE_STREAM );
//				
//				break;
			case R.id.stop_button :
				Log.v( TAG, "onClick : stop button pressed" );
				
				stopService( new Intent( this, MediaPlayerService.class ) );
				clearNowPlaying();
				
				break;
		    case R.id.www_button:
		    	Log.d( TAG, "onClick : www button pressed" );

				Uri uri = Uri.parse( MainApplication.KATG_WEB_SITE );
				Intent intent = new Intent( Intent.ACTION_VIEW, uri );
				startActivity( intent );
				
		    	break;
			default:
				Log.v( TAG, "onClick : no button pressed" );
				
				break;
		}
		
		Log.d( TAG, "onClick : exit" );
	}


	//***************************************
    // Private methods
    //***************************************
//	private class VerifyLiveStreamTask extends AsyncTask<String, Void, Void> {
//		
//		private Exception exception;
//		
//		@Override
//		protected void onPreExecute() {
//			showProgressDialog(); 
//		}
//		
//		@Override
//		protected Void doInBackground( String... params ) {
//			try {
//				MediaPlayer mp = new MediaPlayer();
//				mp.setLooping( false );
//				mp.setDataSource( MainApplication.KATG_LIVE_STREAM );
//				mp.prepareAsync();
//				mp.start();
//				mp.stop();
//				mp.release();
//			} catch( Exception e ) {
//				Log.e( TAG, e.getLocalizedMessage(), e );
//				exception = e;
//			}
//			
//			return null;
//		}
//		
//		@Override
//		protected void onPostExecute( Void v ) {
//			dismissProgressDialog();
//			processException( exception );
//			
//			if( null == exception ) {
//				play();
//			}
//		}
//	}

	private class DownloadFeedTask extends AsyncTask<String, Void, SyndFeed> {
		
		private Exception exception;
		
		@Override
		protected void onPreExecute() {
			showProgressDialog(); 
		}
		
		@Override
		protected SyndFeed doInBackground( String... params ) {
			try {
				FeedFetcher feedFetcher = new HttpURLFeedFetcher();
				return feedFetcher.retrieveFeed( new URL( params[ 0 ] ) );
			} catch( Exception e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
				exception = e;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute( SyndFeed syndFeed ) {
			dismissProgressDialog();
			processException( exception );
			setCurrentFeed( syndFeed );
		}
	}

	private void setCurrentFeed( SyndFeed feed ) {
		Log.d( TAG, "setCurrentFeed : enter" );

		getApplicationContext().setFeed( feed );
		
		Log.d( TAG, "Title=" + ( null != feed.getTitle() ? feed.getTitle() : "" ) );
		Log.d( TAG, "Author=" + ( null != feed.getAuthor() ? feed.getAuthor() : "" ) );
		Log.d( TAG, "Description=" + ( null != feed.getDescription() ? feed.getDescription() : "" ) );
		Log.d( TAG, "Encoding=" + ( null != feed.getEncoding() ? feed.getEncoding() : "" ) );
		Log.d( TAG, "Type=" + ( null != feed.getFeedType() ? feed.getFeedType() : "" ) );
		Log.d( TAG, "Link=" + ( null != feed.getLink() ? feed.getLink() : "" ) );
		Log.d( TAG, "Uri=" + ( null != feed.getUri() ? feed.getUri() : "" ) );
		Log.d( TAG, "Published Date=" + ( null != feed.getPublishedDate() ? feed.getPublishedDate() : "" ) );
		
		for( Object obj : feed.getEntries() ) {
			SyndEntry entry = ((SyndEntry) obj);
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
		}

		refreshFeedEntries();
		
		Log.d( TAG, "setCurrentFeed : exit" );
	}
	
	@SuppressWarnings( "unchecked" )
	private void refreshFeedEntries() {
		Log.d( TAG, "refreshFeedEntries : enter" );
		
		SyndFeed feed = getApplicationContext().getFeed();
		if( null != feed && !feed.getEntries().isEmpty() ) {
			ListAdapter adapter = new FeedEntryListAdapter( feed.getEntries(), this );
	
			registerForContextMenu( getListView() );

			ListView lv = getListView();
		    lv.setAdapter( adapter );
		    lv.setClickable( true );
		    lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick( AdapterView<?> arg0, View v, int position, long arg3 ) {
					Log.d( TAG, "onItemClick : enter" );
					
					setCurrentEntry( (SyndEntry) getApplicationContext().getFeed().getEntries().get( position ) );
					getApplicationContext().setSelectedPlayType( PlayType.RECORDED );
					play();
					
					Log.d( TAG, "onItemClick : exit" );
				}
		    	
		    });
		}

		Log.d( TAG, "refreshFeedEntries : exit" );
	}
	
	private void setCurrentEntry( SyndEntry entry ) {
		Log.d( TAG, "setCurrentEntry : enter" );

		getApplicationContext().setSelectedEntry( entry );

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
	
	private void refreshFeed() {
		Log.d( TAG, "refreshFeed : enter" );

		if( null == getApplicationContext().getFeed() ) {
			new DownloadFeedTask().execute( MainApplication.KATG_RSS_FEED );
		}
		
		refreshFeedEntries();
		
		Log.d( TAG, "refreshFeed : exit" );
	}

	private void play() {
		Log.d( TAG, "play : enter" );
		
		startService( new Intent( this, MediaPlayerService.class ) );
		updateNowPlaying();
		
		Log.d( TAG, "play : exit" );
	}
	
//	private void download() {
//		Log.d( TAG, "download : enter" );
//		
//		Log.d( TAG, "download : exit" );
//	}
	
	private void updateNowPlaying() {
		Log.d( TAG, "updateNowPlaying : enter" );

		if( null != getApplicationContext().getSelectedEntry() ) {
			nowPlaying.setText( getApplicationContext().getSelectedEntry().getTitle() );
		}
		
		Log.d( TAG, "updateNowPlaying : exit" );
	}
	
	private void clearNowPlaying() {
		Log.d( TAG, "clearNowPlaying : enter" );

		nowPlaying.setText( "" );
		
		Log.d( TAG, "clearNowPlaying : exit" );
	}

}
