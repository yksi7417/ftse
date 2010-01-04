/*
 *
 * Created on Dec 27, 2009 | 10:09:46 AM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseDataSequenceProviderImpl {

	private static final Integer THRESHOLD = 30;
	protected final Log logger = LogFactory.getLog(getClass());

	private Integer threshold = THRESHOLD;

	private FtseJdbcManager ftseJdbcManager;

	public List<List<FtseIndex>> provide(int states, String startDate) {
		List<List<FtseIndex>> sequence = new ArrayList<List<FtseIndex>>();
		Calendar c = Calendar.getInstance();
		c.setTime(FtseUtils.getDate(startDate));
		for (int i = 0; i < states; i++) {
			Date from = c.getTime();
			Date to = FtseUtils.getDateDaysAfter(from, threshold);
			List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate(from, to);
			Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
			logger.debug("Loaded FTSE data from '" + from + "' to '" + to + "' with size '" + data.size() + "'.");
			sequence.add(data);
			c.add(Calendar.DAY_OF_MONTH, +(threshold % 30));
			c.add(Calendar.MONTH, +(threshold / 30));
		}
		logger.debug("A sequence of [" + sequence.size() + "] FTSE list values loaded.");
		return sequence;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}

}
