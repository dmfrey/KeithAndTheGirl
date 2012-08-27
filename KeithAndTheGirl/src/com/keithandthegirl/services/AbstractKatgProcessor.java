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
package com.keithandthegirl.services;

import android.content.Context;

import com.keithandthegirl.MainApplication;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractKatgProcessor {

	protected static final String TAG = AbstractKatgProcessor.class.getSimpleName();

	protected MainApplication application;
	protected Context mContext;

	public AbstractKatgProcessor( Context context ) {
		mContext = context;
		application = (MainApplication) context.getApplicationContext();
	}

	public interface NotifyCallback {

		void notify( String message );
		
	}

	public MainApplication getMainApplication() {
		return application;
	}
	
}
