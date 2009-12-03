/*
 *
 * Created on Nov 30, 2009 | 12:24:13 PM
 *
 */
package nl.liacs.dbdm.ftse.data.jdbc;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import nl.liacs.dbdm.ftse.data.FTSEDownloader;
import nl.liacs.dbdm.ftse.model.FtseIndex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:**/configs/*.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional(readOnly = false)
public class FtseJdbcManagerTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Resource
	protected FtseJdbcManager ftseJdbcManager = null;

	@Test
	@Transactional(readOnly = false)
	public void testSaveAll() throws Exception {
		FTSEDownloader downloader = new FTSEDownloader();
		List<FtseIndex> all = downloader.download();
		logger.warn("Fetched a collection of [" + all.size() + "] FTSE indices.");
		ftseJdbcManager.saveAll(all);
	}

	@Transactional(readOnly = false)
	public void testSave() throws Exception {
		FtseIndex ftse = new FtseIndex();
		ftse.setDate(new Date());
		ftse.setOpen(1000.);
		ftse.setLow(1000.);
		ftse.setHigh(1000.);
		ftse.setClose(1000.);
		ftseJdbcManager.save(ftse);
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}

}
