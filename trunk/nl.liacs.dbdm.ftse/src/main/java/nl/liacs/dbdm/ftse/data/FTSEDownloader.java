/*
 *
 * Created on Nov 30, 2009 | 10:58:16 AM
 *
 */
package nl.liacs.dbdm.ftse.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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

	protected String downloadUrl;
	protected String toMonth = "12";
	protected String toDay = "1";
	protected String toYear = "2009";
	protected String fromMonth = "03";
	protected String fromDay = "2";
	protected String fromYear = "1984";

	public FTSEDownloader() {
	}

	public FTSEDownloader(String url) {
		this.downloadUrl = url;
	}

	public List<FtseIndex> download() {
		downloadUrl = contructDownloadUrl();
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

	protected String contructDownloadUrl() {
		return "http://ichart.finance.yahoo.com/table.csv?s=%5EFTSE&a=" + fromMonth + "&b=" + fromDay + "&c="
				+ fromYear + "&d=" + toMonth + "&e=" + toDay + "&f=" + toYear + "&g=d&ignore=.csv";
	}

	public void setFromDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String string = format.format(date);
		StringTokenizer tokenizer = new StringTokenizer(string, "-");
		fromYear = tokenizer.nextToken();
		fromMonth = tokenizer.nextToken();
		fromDay = tokenizer.nextToken();
	}

	public void setToDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String string = format.format(date);
		StringTokenizer tokenizer = new StringTokenizer(string, "-");
		toYear = tokenizer.nextToken();
		toMonth = tokenizer.nextToken();
		toDay = tokenizer.nextToken();
	}

}
