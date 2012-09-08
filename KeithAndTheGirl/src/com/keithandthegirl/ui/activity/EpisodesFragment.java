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

import java.io.File;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.db.EpisodeConstants;
import com.keithandthegirl.services.download.DownloadService.Resource;
import com.keithandthegirl.services.download.DownloadServiceHelper;
import com.keithandthegirl.services.episode.EpisodeServiceHelper;

/**
 * @author Daniel Frey
 *
 */
public class EpisodesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = EpisodesFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 30;

	private EpisodeCursorAdapter adapter;
	private EpisodesReceiver episodesReceiver;
	private DownloadReceiver downloadReceiver;
	
	private EpisodeServiceHelper mEpisodeServiceHelper;
	private DownloadServiceHelper mDownloadServiceHelper;
	
	private MainApplication mainApplication;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		int vip = args.getInt( "VIP" );
		
		String selection = EpisodeConstants.FIELD_VIP + " = ? OR (" + EpisodeConstants.FIELD_VIP + " = ? AND " + EpisodeConstants.FIELD_FILE + " != ?)";
		String[] selectionArgs = new String[] { "0", "1", "" };

		if( vip == 1 ) {
			selection = null;
			selectionArgs = null;
		}
		
		String sortOrder = EpisodeConstants.FIELD_PUBLISH_DATE + " DESC";
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), EpisodeConstants.CONTENT_URI, null, selection, selectionArgs, sortOrder );
		
	    Log.v( TAG, "onCreateLoader : exit" );
		return cursorLoader;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		adapter.swapCursor( cursor );
		
	    getListView().setFastScrollEnabled( true );

		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		adapter.swapCursor( null );
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		mainApplication = (MainApplication) getActivity().getApplicationContext();
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );
		setRetainInstance( true );

        Bundle args = new Bundle();
        args.putInt( "VIP", ( mainApplication.isVIP() ? 1 : 0 ) );
        
		getLoaderManager().initLoader( 0, args, this );
		 
	    adapter = new EpisodeCursorAdapter( getActivity().getApplicationContext() );
	    
	    setListAdapter( adapter );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
		if( null != episodesReceiver ) {
			try {
				getActivity().unregisterReceiver( episodesReceiver );
				episodesReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		if( null != downloadReceiver ) {
			try {
				getActivity().unregisterReceiver( downloadReceiver );
				downloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
	    
		mEpisodeServiceHelper = EpisodeServiceHelper.getInstance( getActivity() );
		mDownloadServiceHelper = DownloadServiceHelper.getInstance( getActivity() );

		IntentFilter episodeFilter = new IntentFilter( EpisodeServiceHelper.EPISODE_RESULT );
		episodeFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
		episodesReceiver = new EpisodesReceiver();
        getActivity().registerReceiver( episodesReceiver, episodeFilter );

		IntentFilter downloadFilter = new IntentFilter( DownloadServiceHelper.DOWNLOAD_RESULT );
		downloadFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
		downloadReceiver = new DownloadReceiver();
        getActivity().registerReceiver( downloadReceiver, downloadFilter );

        Cursor episodeCursor = getActivity().getContentResolver().query( EpisodeConstants.CONTENT_URI, new String[] { EpisodeConstants._ID }, null, null, null );
		if( episodeCursor.getCount() == 0 ) {
			loadData();
		}
		episodeCursor.close();
        
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
		refresh.setIcon( android.R.drawable.ic_popup_sync );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			loadData();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		mEpisodeServiceHelper.getEpisodes();
	    
		Log.v( TAG, "loadData : exit" );
	}

	private class EpisodeCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public EpisodeCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			Log.v( TAG, "newView : enter" );

	        View view = mInflater.inflate( R.layout.feed_entry, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.title = (TextView) view.findViewById( R.id.entry_title );
			refHolder.description = (TextView) view.findViewById( R.id.entry_description );
			refHolder.date = (TextView) view.findViewById( R.id.entry_date );
			refHolder.play = (ImageView) view.findViewById( R.id.entry_play );
			refHolder.download = (ImageView) view.findViewById( R.id.entry_download );
			refHolder.delete = (ImageView) view.findViewById( R.id.entry_delete );

			view.setTag( refHolder );
			
			Log.v( TAG, "newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@TargetApi( 8 )
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
	        Log.v( TAG, "bindView : enter" );

	        final long id = getCursor().getLong( getCursor().getColumnIndexOrThrow( EpisodeConstants._ID ) );
	        final String show = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_SHOW ) );
	        final String title = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_TITLE ) );
	        final String description = cursor.getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_DESCRIPTION ) );
	        final Date date = new Date( getCursor().getLong( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_PUBLISH_DATE ) ) );
	        final String url = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_URL ) );
	        final String type = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_TYPE ) );
	        final String file = null != getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) ? getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) : "";
	        final long vip = getCursor().getLong( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_VIP ) );
	        Log.v( TAG, "bindView : file=" + file + ", vip=" + vip + ", type=" + type );
	        
		    final Resource extension = Resource.findByMimeType( type );

	        ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.title.setText( title );
			mHolder.description.setText( description );
			mHolder.date.setText( ( (MainApplication) mContext.getApplicationContext() ).getFormat().format( date ) );

	        mHolder.play.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v ) {

					play( PlayType.RECORDED, extension, id, title, description, url, file );

				}
				
			});

            File root;
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
        	    switch( extension ) {
        	    case MP3 :
        	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS );
        	    	
        	    	break;
        	    case MP4 :
        	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES );
        	    	
        	    	break;
        	    case M4V :
        	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES );
        	    	
        	    	break;
        	    default :
        	    	root = Environment.getExternalStorageDirectory();
        	    	
        	    	break;
        	    }
            } else {
            	root = Environment.getExternalStorageDirectory();
            }
            
            File episodeDir = new File( root, show );
            episodeDir.mkdirs();
            
            if( !"".equals( file ) ) {
            	mHolder.download.setVisibility( View.GONE );
            	mHolder.delete.setVisibility( View.VISIBLE );

            	mHolder.delete.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {

			            File f = new File( file );
		                if( f.exists() ) {
		                	
							ContentValues values = new ContentValues();
							values.put( EpisodeConstants.FIELD_FILE, "" );
							
							getActivity().getContentResolver().update( ContentUris.withAppendedId( EpisodeConstants.CONTENT_URI, id ), values, null, null );
							
							f.delete();
							
							notifyDataSetChanged();
			            }
						
						
					}
					
				});
            } else {
            	
            	mHolder.delete.setVisibility( View.GONE );
            	mHolder.download.setVisibility( View.VISIBLE );
            	mHolder.download.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {
						
						if( !isDownloadInProgress() ) {
							
			        		mDownloadServiceHelper.download( url, id, show, title, extension );
			                
			        		v.setVisibility( View.GONE );
			        		
						} else {
					    	Toast toast = Toast.makeText( mContext, "Please wait until current download finishes.", Toast.LENGTH_SHORT );
					    	toast.show();
						}
					}
					
				});

            }

            if( title.equalsIgnoreCase( "where are more katg shows" ) ) {
            	mHolder.download.setVisibility( View.GONE );
            	mHolder.delete.setVisibility( View.GONE );
            }
            
            Log.v( TAG, "bindView : exit" );
		}

		private class ViewHolder {
			
			TextView title;
			TextView description;
			TextView date;
			ImageView play;
			ImageView download;
			ImageView delete;
			
			ViewHolder() { }

		}
	
	}
	
	private void play( PlayType type, Resource resource, Long id, String title, String description, String url, String file ) {
		Log.d( TAG, "play : enter" );
		
		switch( resource ) {
		case MP3 :
			Log.d( TAG, "play : playing mp3" );
			
			Intent mp3Activity = new Intent( getActivity(), PlayerActivity.class );
			mp3Activity.putExtra( "PLAY_TYPE", type.name() );
			mp3Activity.putExtra( "EPISODE", id );
			mp3Activity.putExtra( "TITLE", title );
			mp3Activity.putExtra( "DESCRIPTION", description );
			
			startActivity( mp3Activity );
			
			break;
		case MP4 :
			Log.d( TAG, "play : playing mp4" );
			
			Intent mp4Activity = new Intent( Intent.ACTION_VIEW );
			mp4Activity.setDataAndType( Uri.parse( ( null != file && !"".equals( file ) ? file : url ) ), resource.getMimeType() );
			startActivity( mp4Activity );
			
			break;
		case M4V :
			Log.d( TAG, "play : playing m4v" );
			
			Intent m4vActivity = new Intent( Intent.ACTION_VIEW );
			m4vActivity.setDataAndType( Uri.parse( ( null != file && !"".equals( file ) ? file : url ) ), resource.getMimeType() );
			startActivity( m4vActivity );
			
			break;
		default :
			Log.d( TAG, "play : unknown play type" );
			
			break;
		}
		
		Log.d( TAG, "play : exit" );
	}

	private class EpisodesReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "EpisodesReceiver.onReceive : enter" );

			adapter.notifyDataSetChanged();
			
			Log.v( TAG, "EpisodesReceiver.onReceive : exit" );
		}
		
	}

	private class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "DownloadReceiver.onReceive : enter" );

			adapter.notifyDataSetChanged();
			
			Log.v( TAG, "DownloadReceiver.onReceive : exit" );
		}
		
	}

	private boolean isDownloadInProgress() {
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService( Context.ACTIVITY_SERVICE );
	    for( RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {
	        if( "com.keithandthegirl.services.download.DownloadService".equals( service.service.getClassName() ) ) {
	            return true;
	        }
	    }
	    
	    return false;
	}
	
}
