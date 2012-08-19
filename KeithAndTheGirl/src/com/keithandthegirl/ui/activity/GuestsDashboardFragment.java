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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.keithandthegirl.MainApplication;
import com.keithandthegirl.R;
import com.keithandthegirl.api.guests.Guest;
import com.keithandthegirl.api.guests.Guests;
import com.keithandthegirl.api.guests.Url;

/**
 * @author Daniel Frey
 *
 */
public class GuestsDashboardFragment extends ListFragment {

	private final static String TAG = GuestsDashboardFragment.class.getSimpleName();
	
	private static final int REFRESH_ID = Menu.FIRST + 10;
	private static final int MOST_RECENT_ID = Menu.FIRST + 11;
	private static final int TOP_COUNT_ID = Menu.FIRST + 12;
	private static final int NAME_ID = Menu.FIRST + 13;

	private MainApplication mainApplication;
	
	private List<Guest> guestList = new ArrayList<Guest>();
	
	private GuestRowAdapter adapter;
	private Map<Integer, Bitmap> images = new TreeMap<Integer, Bitmap>();
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		mainApplication = (MainApplication) getActivity().getApplicationContext();
		
		Log.v( TAG, "onCreate : enter" );
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

		setupAdapter();
		
		Log.v( TAG, "onActivityCreated : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		if( null != mainApplication.getGuest() ) {
			Log.v( TAG, "onResume : already downloaded guest list" );
			
			if( null != mainApplication.getGuest().getGuests() && !mainApplication.getGuest().getGuests().isEmpty() ) {
				Log.v( TAG, "onResume : guestList has entries" );
				
				guestList = mainApplication.getGuest().getGuests();
			}
		}

		if( guestList.isEmpty() ) {
			Log.v( TAG, "onResume : guestList is empty" );

			mainApplication.setGuestSort( MainApplication.Sort.MOST_RECENT );
			new DownloadGuestTask().execute( MainApplication.Sort.MOST_RECENT );
		}

		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	@TargetApi( 11 )
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

	    MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, getResources().getString( R.string.menu_refresh ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_NEVER );
	    }

	    MenuItem mostRecent = menu.add( Menu.NONE, MOST_RECENT_ID, Menu.NONE, getResources().getString( R.string.menu_most_recent ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	mostRecent.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
	    }

	    MenuItem topCount = menu.add( Menu.NONE, TOP_COUNT_ID, Menu.NONE, getResources().getString( R.string.menu_top_count ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	topCount.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
	    }

	    MenuItem name = menu.add( Menu.NONE, NAME_ID, Menu.NONE, getResources().getString( R.string.menu_name ) );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	name.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS );
	    }

	    Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
	    Log.v( TAG, "onOptionsItemSelected : enter" );
	    Log.v( TAG, "onOptionsItemSelected : item=" + item.getItemId() );
	    
	    switch( item.getItemId() ) {
	    case REFRESH_ID :
	    	Log.v( TAG, "onOptionsItemSelected : refresh selected" );
	    	
	    	if( null == mainApplication.getGuestSort() ) {
		    	mainApplication.setGuestSort( MainApplication.Sort.MOST_RECENT );
	    	}
	    	
	    	new DownloadGuestTask().execute( mainApplication.getGuestSort() );
	    	
	    	return true;
	    case MOST_RECENT_ID :
	    	Log.v( TAG, "onOptionsItemSelected : most recent selected" );
			
	    	mainApplication.setGuestSort( MainApplication.Sort.MOST_RECENT );
	    	new DownloadGuestTask().execute( MainApplication.Sort.MOST_RECENT );

			return true;
	    case TOP_COUNT_ID :
	    	Log.v( TAG, "onOptionsItemSelected : top count selected" );

	    	mainApplication.setGuestSort( MainApplication.Sort.TOP_COUNT );
	    	new DownloadGuestTask().execute( MainApplication.Sort.TOP_COUNT );

			return true;
	    case NAME_ID :
	    	Log.v( TAG, "onOptionsItemSelected : name selected" );

	    	mainApplication.setGuestSort( MainApplication.Sort.NAME );
	    	new DownloadGuestTask().execute( MainApplication.Sort.NAME );

	    	return true;
	    }
	    
	    Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers
	
	private void setupAdapter() {
	    
		getListView().invalidate();
		adapter = new GuestRowAdapter( getActivity().getApplicationContext(), guestList );
	    
	    setListAdapter( adapter );
	    getListView().setFastScrollEnabled( true );
	    
	}
	
	private void setDownloadedGuests( Guests guests ) {

		this.guestList = guests.getGuests();
		mainApplication.setGuest( guests );
		setupAdapter();
		
	    adapter.notifyDataSetChanged();

	}
	
	private class GuestRowAdapter extends BaseAdapter {

		//private Context mContext;
		private LayoutInflater mInflater;

		private List<Guest> guests;
		
		public GuestRowAdapter( Context context, List<Guest> guests ) {
			Log.v( TAG, "GuestRowAdapter : enter" );
			
			//mContext = context;
			mInflater = LayoutInflater.from( context );

			this.guests = guests;
			
			Log.v( TAG, "GuestRowAdapter : exit" );
		}
		
		@Override
		public int getCount() {
			if( null != guests ) {
				return guests.size(); 
			}
			
			return 0;
		}

		@Override
		public Guest getItem( int position ) {
			if( null != guests ) {
				return guests.get( position );
			}
			
			return null;
		}

		@Override
		public long getItemId( int position ) {
			if( null != guests ) {
				return position;
			}
			
			return 0;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "GuestRowAdapter.getView : enter" );

			View v = convertView;
			ViewHolder mHolder;

			if( null == v ) {
				//Log.v( TAG, "GuestRowAdapter.getView : creating new view holder" );

				v = mInflater.inflate( R.layout.guest_row, parent, false );

		        mHolder = new ViewHolder();
		        mHolder.image = (ImageView) v.findViewById( R.id.guest_image );
		        mHolder.details = (LinearLayout) v.findViewById( R.id.guest_details );
		        mHolder.name = (TextView) v.findViewById( R.id.guest_name );
		        mHolder.description = (TextView) v.findViewById( R.id.guest_description );
		        
		        mHolder.facebook = (ImageView) v.findViewById( R.id.guest_facebook );
		        mHolder.twitter = (ImageView) v.findViewById( R.id.guest_twitter );
		        mHolder.www = (ImageView) v.findViewById( R.id.guest_www );
		        
		        v.setTag( mHolder );
			} else {
				//Log.v( TAG, "GuestRowAdapter.getView : retrieving cached view holder" );

				mHolder = (ViewHolder) v.getTag();
			}
			
			Guest guest = getItem( position );
			if( null == guest.getPictureUrl() || "".equals( guest.getPictureUrl() ) ) {
				Log.v( TAG, "GuestRowAdapter.getView : guest does not have an image" );

				mHolder.image.setVisibility( View.GONE );
			} else {
				//Log.v( TAG, "GuestRowAdapter.getView : guest has an image" );

				mHolder.image.setVisibility( View.VISIBLE );
				
	            File root = getActivity().getExternalCacheDir();
	            
	            File pictureDir = new File( root, DownloadGuestImageTask.GUESTS_DIR );
	            pictureDir.mkdirs();
	            
	            File f = new File( pictureDir, guest.getShowGuestId() + ".jpg" );
	            if( f.exists() ) {
	    			//Log.v( TAG, "GuestRowAdapter.getView : guest image exists in cache dir" );

	            	if( !images.containsKey( guest.getShowGuestId() ) ) {
		    			//Log.v( TAG, "GuestRowAdapter.getView : guest image not in fragment cache" );

		    			try {
							InputStream is = new FileInputStream( f );
							Bitmap bitmap = BitmapFactory.decodeStream( is );
							
							images.put( guest.getShowGuestId(), bitmap );
							mHolder.image.setImageBitmap( bitmap );
							//Log.v( TAG, "GuestRowAdapter.getView : banner added to adapter cache" );
						} catch( Exception e ) {
							Log.e( TAG, "GuestRowAdapter.getView : error reading file", e );
						}
	            	} else {	            	
		    			//Log.v( TAG, "GuestRowAdapter.getView : guest image exists in fragment cache" );

		    			mHolder.image.setImageBitmap( images.get( guest.getShowGuestId() ) );
	            	}
	            } else {
	    			//Log.v( TAG, "GuestRowAdapter.getView : image does not exist in cache dir" );

	    			new DownloadGuestImageTask().execute( guest.getShowGuestId(), guest.getPictureUrl() );
	            }
			}
			mHolder.name.setText( guest.getRealName() + ( guest.getEpisodeCount() > 1 ? " (" + guest.getEpisodeCount() + " shows)" : "" ) );
			mHolder.description.setText( guest.getDescription() );
			
			Map<String, String> urls = new HashMap<String, String>();
			if( null != guest.getUrls() && null != guest.getUrls().getUrls() ) {
				for( Url url : guest.getUrls().getUrls() ) {
					if( url.getAddress().indexOf( "facebook" ) != -1 ) {
						urls.put( "facebook", url.getAddress() );
					}

					if( url.getAddress().indexOf( "twitter" ) != -1 ) {
						urls.put( "twitter", url.getAddress() );
					}

					if( url.getAddress().indexOf( "facebook" ) == -1 && url.getAddress().indexOf( "twitter" ) == -1 ) {
						urls.put( "www", url.getAddress() );
					}

				}
			}
			
			if( urls.containsKey( "facebook" ) ) {
				mHolder.facebook.setVisibility( View.VISIBLE );
				
				final String url = urls.get( "facebook" );
				mHolder.facebook.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {
						
						Intent facebookIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) ); 
						startActivity( facebookIntent );

					}
				});
			} else {
				mHolder.facebook.setVisibility( View.GONE );
			}
			
			if( urls.containsKey( "twitter" ) ) {
				mHolder.twitter.setVisibility( View.VISIBLE );
				
				final String url = urls.get( "twitter" );
				mHolder.twitter.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {
						
						Intent twitterIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) ); 
						startActivity( twitterIntent );

					}
				});
			} else {
				mHolder.twitter.setVisibility( View.GONE );
			}
			
			if( urls.containsKey( "www" ) ) {
				mHolder.www.setVisibility( View.VISIBLE );
				
				final String url = urls.get( "www" );
				mHolder.www.setOnClickListener( new View.OnClickListener() {
					
					@Override
					public void onClick( View v ) {
						
						Intent wwwIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) ); 
						startActivity( wwwIntent );

					}
				});
			} else {
				mHolder.www.setVisibility( View.GONE );
			}
			
			Log.v( TAG, "GuestRowAdapter.getView : exit" );
			return v;
		}
		
		private class ViewHolder {
			
			ImageView image;
			
			LinearLayout details;
			TextView name;
			TextView description;
			
			ImageView facebook;
			ImageView twitter;
			ImageView www;
			
			ViewHolder() { }

		}

	}
	
	private class DownloadGuestTask extends AsyncTask<MainApplication.Sort, Void, Guests> {

		public static final String DATA_DIR = "Data";

		@Override
		protected Guests doInBackground( MainApplication.Sort... params ) {
			Log.v( TAG, "DownloadGuestsTask.doInBackground : enter" );

			MainApplication.Sort sortType = (MainApplication.Sort) params[ 0 ];
			
			RestTemplate template = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_XML ) );
			requestHeaders.setAcceptEncoding( Collections.singletonList( ContentCodingType.GZIP ) );

			HttpEntity<?> entity = new HttpEntity<Object>( requestHeaders );
			
			try {
				Log.v( TAG, "DownloadGuestsTask.doInBackground : url=" + MainApplication.KATG_GUEST_URL + "?sortType=" + sortType.getType() );
				ResponseEntity<Guests> responseEntity = template.exchange( MainApplication.KATG_GUEST_URL + "?sortType=" + sortType.getType(), HttpMethod.GET, entity, Guests.class );
				switch( responseEntity.getStatusCode() ) {
					case OK :
						Log.v( TAG, "DownloadGuestsTask.doInBackground : exit, Ok" );

						return responseEntity.getBody();
					default:
						Log.v( TAG, "DownloadGuestsTask.doInBackground : exit, error" );

						return null;
				}
			} catch( Exception e ) {
				Log.e( TAG, "DownloadGuestsTask.doInBackground : exit, error", e );

				return null;
			}
			
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( Guests result ) {
			Log.v( TAG, "DownloadGuestsTask.onPostExecute : enter" );

			if( null != result ) {
				Log.v( TAG, "DownloadGuestsTask.onPostExecute : updating guests" );
				
		        try {
		            File root = getActivity().getExternalCacheDir();
		            
		            File dataDir = new File( root, DATA_DIR );
		            dataDir.mkdirs();
		            
		            File f = new File( dataDir, "Guests.xml" );
	                if( f.exists() ) {
						f.delete();
						
						f = new File( dataDir, "Guests.xml" );
		            }
		
	                Serializer serializer = new Persister();
	                serializer.write( result, f );
    
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }

		        setDownloadedGuests( result );
			}
			
			Log.v( TAG, "DownloadGuestsTask.onPostExecute : exit" );
		}
		
	}
	
	private class DownloadGuestImageTask extends AsyncTask<Object, Void, Bitmap> {

		public static final String GUESTS_DIR = "Guests";

		private Exception e = null;

		private Integer showGuestId;
		private String pictureUrl;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "DownloadGuestImageTask.doInBackground : enter" );

			showGuestId = (Integer) params[ 0 ];
			pictureUrl = (String) params[ 1 ];
			
			Bitmap bitmap = null;

			RestTemplate template = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAcceptEncoding( Collections.singletonList( ContentCodingType.GZIP ) );

			HttpEntity<?> entity = new HttpEntity<Object>( requestHeaders );
			
			try {
				ResponseEntity<byte[]> responseEntity = template.exchange( pictureUrl, HttpMethod.GET, entity, byte[].class );
				switch( responseEntity.getStatusCode() ) {
					case OK :
						Log.v( TAG, "DownloadGuestImageTask.doInBackground : exit, Ok" );

						byte[] bytes = responseEntity.getBody();
						bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
						return bitmap;
					default:
						Log.v( TAG, "DownloadGuestImageTask.doInBackground : exit, error" );

						return null;
				}
			} catch( Exception e ) {
				Log.e( TAG, "DownloadGuestImageTask.doInBackground : exit, error", e );

				return null;
			}
		}

		@Override
		protected void onPostExecute( Bitmap result ) {
			Log.v( TAG, "DownloadGuestImageTask.onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "DownloadGuestImageTask.onPostExecute : result size=" + result.getHeight() + "x" + result.getWidth() );

		        try {
		            File root = getActivity().getExternalCacheDir();
		            
		            File pictureDir = new File( root, GUESTS_DIR );
		            pictureDir.mkdirs();
		            
		            File f = new File( pictureDir, showGuestId + ".jpg" );
	                if( f.exists() ) {
						return;
		            }
		
	                String name = f.getAbsolutePath();
	                FileOutputStream fos = new FileOutputStream( name );
	                result.compress( Bitmap.CompressFormat.JPEG, 100, fos );
	                fos.flush();
	                fos.close();

					images.put( showGuestId, result );

					adapter.notifyDataSetChanged();
	                
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }
		 
			} else {
				Log.e( TAG, "error getting program group banner", e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

}
