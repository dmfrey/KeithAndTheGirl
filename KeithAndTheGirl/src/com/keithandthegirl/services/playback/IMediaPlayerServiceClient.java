/**
 * 
 */
package com.keithandthegirl.services.playback;

/**
 * @author rootlicker http://speakingcode.com
 */
public interface IMediaPlayerServiceClient {

	/**
	 * A callback made by a MediaPlayerService onto its clients to indicate that
	 * a player is initializing.
	 * 
	 * @param message
	 *            A message to propagate to the client
	 */
	public void onInitializePlayerStart( String message );

	/**
	 * A callback made by a MediaPlayerService onto its clients to indicate that
	 * a player was successfully initialized.
	 */
	public void onInitializePlayerSuccess();

	/**
	 * A callback made by a MediaPlayerService onto its clients to indicate that
	 * a player encountered an error.
	 */
	public void onError();

}
