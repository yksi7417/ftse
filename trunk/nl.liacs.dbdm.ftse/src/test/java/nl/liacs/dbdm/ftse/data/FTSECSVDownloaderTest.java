/*
 *
 * Created on Nov 30, 2009 | 11:12:15 AM
 *
 */
package nl.liacs.dbdm.ftse.data;

import junit.framework.TestCase;

import org.springframework.core.io.Resource;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FTSECSVDownloaderTest extends TestCase {

	FTSECSVDownloader downloader;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		downloader = new FTSECSVDownloader();
	}

	@Override
	protected void tearDown() throws Exception {
		downloader = null;
		super.tearDown();
	}

	public void testDownload() throws Exception {
		Resource resource = downloader.download();
		System.out.println(resource.getDescription());
	}

}
