/*
 *
 * Created on Nov 30, 2009 | 10:58:16 AM
 *
 */
package nl.liacs.dbdm.ftse.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FTSECSVDownloader {

	private String downloadUrl = "http://ichart.finance.yahoo.com/table.csv?s=%5EFTSE&a=03&b=2&c=1984&d=10&e=26&f=2009&g=d&ignore=.csv";
	private String targetUrl = "nl/liacs/dbdm/ftse/data/ftse.csv";

	public FTSECSVDownloader() {
	}

	public FTSECSVDownloader(String url) {
		this.downloadUrl = url;
	}

	public Resource download() {
		ResourceLoader loader = new DefaultResourceLoader();
		Resource resource = loader.getResource(downloadUrl);
		BufferedReader reader;
		try {
			ClassPathResource target = new ClassPathResource(targetUrl);
			File file = target.getFile();
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset
					.forName("utf-8")));
			reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				writer.write(line);
				writer.newLine();
				line = reader.readLine();
			}
			reader.close();
			writer.close();
			return new ClassPathResource(targetUrl);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

}
