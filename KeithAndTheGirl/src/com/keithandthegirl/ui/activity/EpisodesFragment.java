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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.keithandthegirl.services.episode.EpisodeServiceHelper;
import com.keithandthegirl.utils.NotificationHelper;
import com.keithandthegirl.utils.NotificationHelper.NotificationType;

/**
 * @author Daniel Frey
 *
 */
public class EpisodesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = EpisodesFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 10;

	private static final String DOWNLOAD_IN_PROGRESS_KEY = "DOWNLOAD_IN_PROGRESS";
	
	private EpisodeCursorAdapter adapter;
	private EpisodesReceiver episodesReceiver;
	
	private EpisodeServiceHelper mEpisodeServiceHelper;
	private NotificationHelper mNotificationHelper;
	
	private boolean downloadInProgress = false;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String selection = EpisodeConstants.FIELD_VIP + " = ? OR " + EpisodeConstants.FIELD_FILE + " IS NOT NULL";
		
		String[] selectionArgs = new String[] { "0" };
		
		String sortOrder = EpisodeConstants.FIELD_NUMBER + " DESC";
		
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
		
		restartLoader();
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mNotificationHelper = new NotificationHelper( getActivity() );

		setHasOptionsMenu( true );
		setRetainInstance( true );

		getLoaderManager().initLoader( 0, null, this );
		 
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

		IntentFilter episodeFilter = new IntentFilter( EpisodeServiceHelper.EPISODE_RESULT );
		episodeFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
		episodesReceiver = new EpisodesReceiver();
        getActivity().registerReceiver( episodesReceiver, episodeFilter );

		Cursor episodeCursor = getActivity().getContentResolver().query( EpisodeConstants.CONTENT_URI, new String[] { EpisodeConstants._ID }, EpisodeConstants.FIELD_VIP + " = ?", new String[] { "0" }, null );
		if( episodeCursor.getCount() == 0 ) {
			loadData();
		}
		episodeCursor.close();
        
		SharedPreferences sharedPreferences = getActivity().getPreferences( Context.MODE_PRIVATE );
		downloadInProgress = sharedPreferences.getBoolean( DOWNLOAD_IN_PROGRESS_KEY, false );
		
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

	        final int number = getCursor().getInt( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_NUMBER ) );
	        final String title = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_TITLE ) );
	        String description = cursor.getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_DESCRIPTION ) );
	        Date date = new Date( getCursor().getLong( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_PUBLISH_DATE ) ) );
	        final String url = getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_URL ) );
	        final String file = null != getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) ? getCursor().getString( getCursor().getColumnIndexOrThrow( EpisodeConstants.FIELD_FILE ) ) : "";
	        Log.v( TAG, "bindView : file=" + file );

	        ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.title.setText( number + ": " + title );
			mHolder.description.setText( description );
			mHolder.date.setText( ( (MainApplication) mContext.getApplicationContext() ).getFormat().format( date ) );

	        mHolder.play.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick( View v ) {

					play( PlayType.RECORDED, number );

				}
				
			});

            File root;
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
            	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS );
            } else {
            	root = Environment.getExternalStorageDirectory();
            }
            
            File episodeDir = new File( root, "KATG" );
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
							
							getActivity().getContentResolver().update( EpisodeConstants.CONTENT_URI, values, EpisodeConstants.FIELD_NUMBER + " = ?", new String[] { "" + number } );
							
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
						
						if( !downloadInProgress ) {
							new DownloadEpisodeTask().execute( "" + number, number + ": " + title, url );
						} else {
					    	Toast toast = Toast.makeText( mContext, "Please wait until current download finishes.", Toast.LENGTH_SHORT );
					    	toast.show();
						}
					}
					
				});

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
	
	private class DownloadEpisodeTask extends AsyncTask<String, Integer, File> {

		private Exception e = null;

		private String number;
		private String filename;
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate( Integer... values ) {
			//Log.v( TAG, "DownloadEpisodeTask.onProgressUpdate : enter" );
			super.onProgressUpdate( values );

			int read = values[ 0 ];
			int length = values[ 1 ];
			double percent = ( ( (float) read / (float) length ) * 100 );
			
			//Log.v( TAG, "DownloadEpisodeTask.onProgressUpdate : percent complete=" + percent );

			if( percent < 100.0 ) {
				updateDownloadingStatus( true );
			} else {
				updateDownloadingStatus( false );
			}
			
			mNotificationHelper.progressUpdate( percent );
			
			//Log.v( TAG, "DownloadEpisodeTask.onProgressUpdate : exit" );
		}

		@TargetApi( 8 )
		@Override
		protected File doInBackground( String... params ) {
			Log.v( TAG, "DownloadEpisodeTask.doInBackground : enter" );

			updateDownloadingStatus( true );
			
			number = params[ 0 ];
			String title = params[ 1 ];
			String address = params[ 2 ];
			
            URL url;
            URLConnection con;
            InputStream is;
            FileOutputStream fos;
            byte[] buffer = new byte[ 4096 ];

            try {
                File root;
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
                	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS );
                } else {
                	root = Environment.getExternalStorageDirectory();
                }
	            
	            File episodeDir = new File( root, "KATG" );
	            episodeDir.mkdirs();
	            
	            File f = new File( episodeDir, address.substring( address.lastIndexOf( '/' ) + 1 ) );
                if( f.exists() ) {
                	Log.v( TAG, "DownloadEpisodeTask.doInBackground : exit, episode already downloaded" );
                	
					ContentValues values = new ContentValues();
					values.put( EpisodeConstants.FIELD_FILE, f.getAbsolutePath() );
					
					getActivity().getContentResolver().update( EpisodeConstants.CONTENT_URI, values, EpisodeConstants.FIELD_NUMBER + " = ?", new String[] { number } );
					
					return null;
	            }
                
                mNotificationHelper.createNotification( "KeithAndTheGirl", "Downloading Episode: " + title, NotificationType.DOWNLOAD );
                
                filename = f.getAbsolutePath();
                
                url = new URL( address );
            	con = url.openConnection();
                is = con.getInputStream();
                fos = new FileOutputStream( f );
                
                int length = con.getContentLength();
                int total = 0, read = -1;
                while( ( read = is.read( buffer ) ) != -1 ) {
					fos.write(  buffer, 0, read );
					
					total += read;
					
					Float percent = ( ( (float) total / (float) length ) * 100 );
					
					if( percent.intValue() % 10 == 0 ) {
						publishProgress( total, length );
					}
				}
                is.close();
                fos.close();
                
				Log.v( TAG, "DownloadEpisodeTask.doInBackground : exit" );
                return f;
			} catch( Exception e ) {
				Log.e( TAG, "DownloadEpisodeTask.doInBackground : exit, error", e );
				
				this.e = e;
				
				return null;
			}
            
		}

		@Override
		protected void onPostExecute( File result ) {
			Log.v( TAG, "DownloadEpisodeTask.onPostExecute : enter" );

			updateDownloadingStatus( false );

			if( null == e && null != result ) {
				Log.v( TAG, "DownloadEpisodeTask.onPostExecute : file downloaded successfully" );
							
				mNotificationHelper.completed();

				ContentValues values = new ContentValues();
				values.put( EpisodeConstants.FIELD_FILE, filename );
				
				getActivity().getContentResolver().update( EpisodeConstants.CONTENT_URI, values, EpisodeConstants.FIELD_NUMBER + " = ?", new String[] { number } );

			}

			adapter.notifyDataSetChanged();
			
			Log.v( TAG, "DownloadEpisodeTask.onPostExecute : exit" );
		}

	}

	private void play( PlayType type, Integer number ) {
		Log.d( TAG, "play : enter" );
		
		Intent intent = new Intent( getActivity(), PlayerActivity.class );
		intent.putExtra( "PLAY_TYPE", type.name() );
		intent.putExtra( "EPISODE", number );
		
		startActivity( intent );
		
		Log.d( TAG, "play : exit" );
	}

	private class EpisodesReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "EpisodesReceiver.onReceive : enter" );

			restartLoader();
			
			Log.v( TAG, "EpisodesReceiver.onReceive : exit" );
		}
		
	}

	private void restartLoader() {
		Log.v( TAG, "restartLoader : enter" );
		
		getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "restartLoader : exit" );
	}

	private void updateDownloadingStatus( boolean status ) {
		
		downloadInProgress = status;
		
		SharedPreferences sharedPreferences = getActivity().getPreferences( Context.MODE_PRIVATE );
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.putBoolean( DOWNLOAD_IN_PROGRESS_KEY, status );
    	editor.commit();

		
	}
	
}
