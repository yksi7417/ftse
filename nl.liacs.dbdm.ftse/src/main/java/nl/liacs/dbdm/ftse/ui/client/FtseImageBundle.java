/*
 *
 * Created on Jan 3, 2010 | 4:54:12 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public interface FtseImageBundle extends ClientBundle {

	FtseImageBundle INSTANCE = GWT.create(FtseImageBundle.class);

	@Source("calendar.png")
	ImageResource iconCalendar();

}
