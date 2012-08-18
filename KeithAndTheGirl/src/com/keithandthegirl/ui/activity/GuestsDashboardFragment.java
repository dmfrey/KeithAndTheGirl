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

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

/**
 * @author Daniel Frey
 *
 */
public class GuestsDashboardFragment extends ListFragment {

	private final static String TAG = GuestsDashboardFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 1;

	private MainApplication mainApplication;
	
	private GuestRowAdapter adapter;
	
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

		if( null == mainApplication.getGuest() ) {
			new DownloadGuestTask().execute( MainApplication.Sort.MOST_RECENT );
		} else {
			setupAdapter();
		}
		
		Log.v( TAG, "onActivityCreated : exit" );
	}
	
	// internal helpers
	
	private void setupAdapter() {
	    
		adapter = new GuestRowAdapter( getActivity().getApplicationContext(), mainApplication.getGuest().getGuests() );
	    
		
	    setListAdapter( adapter );
	    getListView().setFastScrollEnabled( true );

	}
	
	private void setDownloadedGuests( Guests guests ) {
		
		mainApplication.setGuest( guests );
		setupAdapter();
		
	}
	
	private class GuestRowAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;

		private List<Guest> guests;
		
		public GuestRowAdapter( Context context, List<Guest> guests ) {
			Log.v( TAG, "GuestRowAdapter : enter" );
			
			mContext = context;
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
		        v = mInflater.inflate( R.layout.guest_row, parent, false );

		        mHolder = new ViewHolder();
		        mHolder.image = (ImageView) v.findViewById( R.id.guest_image );
		        mHolder.details = (LinearLayout) v.findViewById( R.id.guest_details );
		        mHolder.name = (TextView) v.findViewById( R.id.guest_name );
		        mHolder.description = (TextView) v.findViewById( R.id.guest_description );
		        
		        v.setTag( mHolder );
			} else {
				mHolder = (ViewHolder) v.getTag();
			}
			
			Guest guest = getItem( position );
			mHolder.name.setText( guest.getRealName() + ( guest.getEpisodeCount() > 1 ? " (" + guest.getEpisodeCount() + " shows)" : "" ) );
			mHolder.description.setText( guest.getDescription() );
			
			Log.v( TAG, "GuestRowAdapter.getView : exit" );
			return v;
		}
		
		private class ViewHolder {
			
			ImageView image;
			
			LinearLayout details;
			TextView name;
			TextView description;
			
			ViewHolder() { }

		}

	}
	
	private class DownloadGuestTask extends AsyncTask<MainApplication.Sort, Void, Guests> {

		@Override
		protected Guests doInBackground( MainApplication.Sort... params ) {
			Log.v( TAG, "DownloadGuestsTask.doInBackground : enter" );

			MainApplication.Sort sortType = (MainApplication.Sort) params[ 0 ];
			
			RestTemplate template = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_XML ) );

			HttpEntity<?> entity = new HttpEntity<Object>( requestHeaders );
			
			try {
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
				
				setDownloadedGuests( result );
			}
			
			Log.v( TAG, "DownloadGuestsTask.onPostExecute : exit" );
		}
		
	}
	
}
