/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package com.keithandthegirl.services.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.FloatMath;
import android.util.Log;

import com.keithandthegirl.db.EpisodeConstants;
import com.keithandthegirl.services.KatgService;
import com.keithandthegirl.utils.NotificationHelper;
import com.keithandthegirl.utils.NotificationHelper.NotificationType;

/**
 * @author Daniel Frey
 *
 */
public class DownloadService extends KatgService {

	private static final String TAG = DownloadService.class.getSimpleName();
	
	public static enum Resource {
		MP3( "mp3", "audio/mpeg" ),
		MP4( "mp4", "video/mp4" ),
		M4V( "m4v", "video/x-m4v" ),
		JPG( "jpg", "image/jpeg" );
		
		private String extension;
		private String mimeType;
		
		Resource( String extension, String mimeType ) {
			this.extension = extension;
			this.mimeType = mimeType;
		}

		/**
		 * @return the extension
		 */
		public String getExtension() {
			return extension;
		}

		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}
		
		public static Resource findByExtension( String value ) {
			
			for( Resource resource : Resource.values() ) {
				if( resource.getExtension().equals( value ) ) {
					return resource;
				}
			}
			
			return null;
		}
		
		public static Resource findByMimeType( String value ) {
			
			for( Resource resource : Resource.values() ) {
				if( resource.getMimeType().equals( value ) ) {
					return resource;
				}
			}
			
			return null;
		}

	}
	
	private int result = FragmentActivity.RESULT_CANCELED;

	private NotificationHelper mNotificationHelper;
	
	private RestTemplate template = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );
	private HttpEntity<?> entity;
	
	public DownloadService() {
		super( "DownloadService" );

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAcceptEncoding( Collections.singletonList( ContentCodingType.GZIP ) );

		entity = new HttpEntity<Object>( requestHeaders );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@TargetApi( 8 )
	@Override
	protected void onHandleIntent( Intent requestIntent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		
		mNotificationHelper = new NotificationHelper( this );

		mOriginalRequestIntent = requestIntent;
		
		Resource resourceType = Resource.valueOf( requestIntent.getStringExtra( RESOURCE_TYPE_EXTRA ) );
		mCallback = requestIntent.getParcelableExtra( SERVICE_CALLBACK );

		Uri data = requestIntent.getData();
	    String urlPath = requestIntent.getStringExtra( "urlpath" );
	    String directory = requestIntent.getStringExtra( "directory" );
	    
	    String filename = data.getLastPathSegment();
	    
        File root;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
    	    switch( resourceType ) {
    	    case MP3 :
    	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS );
    	    	
    	    	break;
    	    case MP4 :
    	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES );
    	    	
    	    	break;
    	    case M4V :
    	    	root = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MOVIES );
    	    	
    	    	break;
    	    case JPG :
    	    	root = getExternalCacheDir();
    	    	filename = requestIntent.getStringExtra( "filename" ) + ".jpg";
    	    	
    	    	break;
    	    default :
    	    	root = getExternalCacheDir();
    	    	
    	    	break;
    	    }
        } else {
        	root = Environment.getExternalStorageDirectory();
        }

        File outputDir = new File( root, directory );
        outputDir.mkdirs();
        
	    File output = new File( outputDir, filename );
	    if( output.exists() ) {
	    	result = FragmentActivity.RESULT_OK;
	    	
		    mCallback.send( result, getOriginalIntentBundle() );

	    	Log.v( TAG, "onHandleIntent : exit, image exists" );
	    	return;
	    }

	    switch( resourceType ) {
	    case MP3 :
			Log.v( TAG, "onHandleIntent : saving mp3" );
	    	
			savePodcast( requestIntent.getLongExtra( "id", -1 ), requestIntent.getStringExtra( "title" ), urlPath, output );
		    mCallback.send( result, getOriginalIntentBundle() );
		    
	    	break;
	    	
	    case MP4 :
			Log.v( TAG, "onHandleIntent : saving mp4" );
	    	
	    	savePodcast( requestIntent.getLongExtra( "id", -1 ), requestIntent.getStringExtra( "title" ), urlPath, output );
		    mCallback.send( result, getOriginalIntentBundle() );
		    
	    	break;
	    	
	    case M4V :
			Log.v( TAG, "onHandleIntent : saving m4v" );

	    	savePodcast( requestIntent.getLongExtra( "id", -1 ), requestIntent.getStringExtra( "title" ), urlPath, output );
		    mCallback.send( result, getOriginalIntentBundle() );
		    
	    	break;
	    	
	    case JPG :
			Log.v( TAG, "onHandleIntent : saving jpg" );

	    	saveBitmap( requestIntent.getStringExtra( "filename" ), urlPath, output );
		    mCallback.send( result, getOriginalIntentBundle() );
		    
	    	break;
	    default:
			Log.w( TAG, "onHandleIntent : unknown extension '" + resourceType.getExtension() + "'" );

		    mCallback.send( REQUEST_INVALID, getOriginalIntentBundle() );
	    	break;
	    }
	    
	    Log.v( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void saveBitmap( String filename, String urlPath, File output ) {
		Log.v( TAG, "saveBitmap : enter" );
	
		try {
			ResponseEntity<byte[]> responseEntity = template.exchange( urlPath, HttpMethod.GET, entity, byte[].class );
			switch( responseEntity.getStatusCode() ) {
				case OK :
					Log.v( TAG, "saveBitmap : file downloaded" );

					byte[] bytes = responseEntity.getBody();
					Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
					
	                FileOutputStream fos = new FileOutputStream( output );
	                bitmap.compress( Bitmap.CompressFormat.JPEG, 100, fos );
	                fos.flush();
	                fos.close();

	                result = FragmentActivity.RESULT_OK;
	                
	                break;
				default:
					Log.v( TAG, "saveBitmap : file not downloaded" );

					break;
			}
		} catch( Exception e ) {
			Log.e( TAG, "saveBitmap : exit, error", e );
		}

		Log.v( TAG, "saveBitmap : exit" );
	}
	
	private void savePodcast( Long id, String title, String urlPath, File output ) {
		Log.v( TAG, "savePodcast : enter" );

		mNotificationHelper.createNotification( "KeithAndTheGirl", "Downloading Episode: " + title, NotificationType.DOWNLOAD );
		
        URL url;
        URLConnection con;
        InputStream is;
        FileOutputStream fos;
        byte[] buffer = new byte[ 4096 ];

        try {
            url = new URL( urlPath );
        	con = url.openConnection();
            is = con.getInputStream();
            fos = new FileOutputStream( output );
            
            boolean notified = false;
            int length = con.getContentLength();
            int total = 0, read = -1;
            while( ( read = is.read( buffer ) ) != -1 ) {
				fos.write(  buffer, 0, read );
				
				total += read;
				
				int percent = (int) FloatMath.ceil( ( ( 100 * (float) total ) / (float) length ) );
				//Log.v( TAG, "savePodcast : download percent=" + percent + "%" );
				if( percent % 5 == 0 && !notified ) {
					mNotificationHelper.progressUpdate( percent );
					notified = true;
				} else {
					notified = false;
				}
			}
            is.close();
            fos.close();
            
            result = FragmentActivity.RESULT_OK;

			ContentValues values = new ContentValues();
			values.put( EpisodeConstants.FIELD_FILE, output.getAbsolutePath() );
			
			getContentResolver().update( ContentUris.withAppendedId( EpisodeConstants.CONTENT_URI, id ), values, null, null );

        } catch( Exception e ) {
			Log.e( TAG, "savePodcast : error", e );
		} finally {
			mNotificationHelper.completed();
		}

		Log.v( TAG, "savePodcast : exit" );
	}
	
}
