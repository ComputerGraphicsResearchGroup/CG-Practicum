package gui;

import film.RGBSpectrum;

/**
 * A listener which is called when the mouse has moved in the image.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public interface ImagePanelListener {
	
	/**
	 * Called when the spectrum at the mouse position has changed.
	 * 
	 * @param spectrum
	 *            the spectrum at the mouse position (or null when mouse has
	 *            left the image).
	 */
	public void spectrumAtMouseChanged(RGBSpectrum spectrum);
}
