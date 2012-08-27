/**
 * 
 */
package com.keithandthegirl.provider;

import static android.provider.BaseColumns._ID;
import android.content.ContentProvider;
import android.text.TextUtils;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractKatgContentProvider extends ContentProvider {

	/**
	 * Append an id test to a SQL selection expression
	 */
	protected String appendRowId( String selection, long id ) {
		return _ID
				+ "="
				+ id
				+ (!TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : "");
	}

}
