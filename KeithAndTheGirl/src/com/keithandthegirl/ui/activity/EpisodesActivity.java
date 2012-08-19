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
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.keithandthegirl.MainApplication;
import com.keithandthegirl.MainApplication.PlayType;
import com.keithandthegirl.R;
import com.keithandthegirl.services.UpdateFeedService;
import com.keithandthegirl.utils.NotificationHelper;
import com.keithandthegirl.utils.NotificationHelper.NotificationType;

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

	    MenuItem about = menu.add( Menu.NONE, ABOUT_ID, Menu.NONE, getResources().getString( R.string.about_header ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	about.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
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

	// internal helpers
	
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

		private NotificationHelper mNotificationHelper;
		private ProgressDialog progressDialog;

		private Intent feedReceiverIntent;

		private boolean downloadInProgress = false;
		
		private FeedEntryListAdapter adapter;
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated( Bundle savedInstanceState ) {
			Log.v( TAG, "onActivityCreated : enter" );
			
			setHasOptionsMenu( true );
			
			mNotificationHelper = new NotificationHelper( getActivity() );

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

		    MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, getResources().getString( R.string.menu_refresh ) );
		    refresh.setIcon( android.R.drawable.ic_popup_sync );
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

		// internal helpers
		
		@SuppressWarnings( "unchecked" )
		private void refreshFeedEntries() {
			Log.d( TAG, "refreshFeedEntries : enter" );
			
			SyndFeed feed = ( (MainApplication) getActivity().getApplicationContext() ).getFeed();
			if( null != feed && !feed.getEntries().isEmpty() ) {
				Log.d( TAG, "refreshFeedEntries : adding feed to list" );
				
				adapter = new FeedEntryListAdapter( feed.getEntries(), getActivity() );
				setListAdapter( adapter );
			}

			dismissProgressDialog();
			
			Log.d( TAG, "refreshFeedEntries : exit" );
		}

		private class FeedEntryListAdapter extends BaseAdapter {

			private List<SyndEntry> entries;
			
			private Context mContext;
			
			public FeedEntryListAdapter( List<SyndEntry> entries, Context context ) {
				this.entries = entries;
				this.mContext = context;
			}
			
			/* (non-Javadoc)
			 * @see android.widget.Adapter#getCount()
			 */
			@Override
			public int getCount() {
				return entries.size();
			}

			/* (non-Javadoc)
			 * @see android.widget.Adapter#getItem(int)
			 */
			@Override
			public Object getItem( int position ) {
				return entries.get( position );
			}

			/* (non-Javadoc)
			 * @see android.widget.Adapter#getItemId(int)
			 */
			@Override
			public long getItemId( int position ) {
				return 0;
			}

			/* (non-Javadoc)
			 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
			 */
			@TargetApi( 8 )
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
		        
				final SyndEntry entry = entries.get( position );
		 
		        LinearLayout itemLayout= (LinearLayout) LayoutInflater.from( mContext ).inflate( R.layout.feed_entry, parent, false );
		 
		        TextView title = (TextView) itemLayout.findViewById( R.id.entry_title );
		        title.setText( entry.getTitle() );
		 
		        String value = entry.getDescription().getValue();
		        value = value.replace( "<p>", "" );
		        value = value.replace( "</p>", "" );
		        value = value.replace( "\"", "" );
		        
		        TextView description = (TextView) itemLayout.findViewById( R.id.entry_description );
		        description.setText( value );
		         
		        TextView date = (TextView) itemLayout.findViewById( R.id.entry_date );
		        date.setText( ( (MainApplication) mContext.getApplicationContext() ).getFormat().format( entry.getPublishedDate() ) );

		        ImageView play = (ImageView) itemLayout.findViewById( R.id.entry_play );
		        play.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {

						SyndEntry selectedEntry = ( (MainApplication) getActivity().getApplicationContext() ).getSelectedEntry();
						if( null != selectedEntry && !selectedEntry.equals( entry ) && ( (MainApplication) getActivity().getApplicationContext() ).isPlaying() ) {
							( (MainApplication) getActivity().getApplicationContext() ).setPlaying( false );
						}
						
						setCurrentEntry( entry );
						( (MainApplication) getActivity().getApplicationContext() ).setSelectedPlayType( PlayType.RECORDED );
						play();

					}
					
				});
		        
		        ImageView download = (ImageView) itemLayout.findViewById( R.id.entry_download );
	            File root;
	            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
	            	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS );
	            } else {
	            	root = Environment.getExternalStorageDirectory();
	            }
	            
	            String sTitle = entry.getTitle();
	            
	            File episodeDir = new File( root, "KATG" );
	            episodeDir.mkdirs();
	            
	            String address = ( (SyndEnclosure) entry.getEnclosures().get( 0 ) ).getUrl();
	            File f = new File( episodeDir, address.substring( address.lastIndexOf( '/' ) + 1 ) );
                if( f.exists() || sTitle.equalsIgnoreCase( "where are more katg shows" ) ) {
	            	download.setVisibility( View.GONE );
	            } else {
	            	
	            	download.setVisibility( View.VISIBLE );
	            	download.setOnClickListener( new View.OnClickListener() {
						
						@Override
						public void onClick( View v ) {
							
							if( !downloadInProgress ) {
								new DownloadEpisodeTask().execute( entry );
							} else {
						    	Toast toast = Toast.makeText( mContext, "Please wait until current download finishes.", Toast.LENGTH_SHORT );
						    	toast.show();
							}
						}
						
					});

	            }

		        return itemLayout;
			}

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
		
		private class DownloadEpisodeTask extends AsyncTask<SyndEntry, Integer, File> {

			private Exception e = null;

			private SyndEntry entry;
			
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
					downloadInProgress = true;
				} else {
					downloadInProgress = false;
				}
				
				mNotificationHelper.progressUpdate( percent );
				
				//Log.v( TAG, "DownloadEpisodeTask.onProgressUpdate : exit" );
			}

			@TargetApi( 8 )
			@Override
			protected File doInBackground( SyndEntry... params ) {
				Log.v( TAG, "DownloadEpisodeTask.doInBackground : enter" );

				downloadInProgress = true;
				
				entry = params[ 0 ];
				
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
		            
		            String address = ( (SyndEnclosure) entry.getEnclosures().get( 0 ) ).getUrl();
		            File f = new File( episodeDir, address.substring( address.lastIndexOf( '/' ) + 1 ) );
	                if( f.exists() ) {
						return null;
		            }
	                
	                mNotificationHelper.createNotification( "KeithAndTheGirl", "Downloading Episode: " + entry.getTitle(), NotificationType.DOWNLOAD );
	                
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

				downloadInProgress = false;

				mNotificationHelper.completed();

				if( null == e && null != result ) {
					Log.v( TAG, "DownloadEpisodeTask.onPostExecute : file downloaded successfully" );
								
					adapter.notifyDataSetChanged();
				
				} else {
					Log.e( TAG, "DownloadEpisodeTask.onPostExecute : error getting program group banner", e );
				}

				Log.v( TAG, "DownloadEpisodeTask.onPostExecute : exit" );
			}

		}

	}
	
}
