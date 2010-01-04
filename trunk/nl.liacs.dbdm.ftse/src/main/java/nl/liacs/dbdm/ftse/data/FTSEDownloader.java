/*
 *
 * Created on Nov 30, 2009 | 10:58:16 AM
 *
 */
package nl.liacs.dbdm.ftse.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FTSEDownloader {

	private String downloadUrl = "http://ichart.finance.yahoo.com/table.csv?s=%5EFTSE&a=03&b=2&c=1984&d=10&e=26&f=2009&g=d&ignore=.csv";

	public FTSEDownloader() {
	}

	public FTSEDownloader(String url) {
		this.downloadUrl = url;
	}

	public List<FtseIndex> download() {
		ResourceLoader loader = new DefaultResourceLoader();
		Resource resource = loader.getResource(downloadUrl);
		BufferedReader reader;
		try {
			List<FtseIndex> all = new ArrayList<FtseIndex>();
			reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				FtseIndex ftse = FtseUtils.extract(line);
				if (ftse != null) {
					all.add(ftse);
				}
				line = reader.readLine();
			}
			reader.close();
			return all;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

}
