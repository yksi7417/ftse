/*
 *
 * Created on Dec 27, 2009 | 1:07:23 PM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.Date;
import java.util.List;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.data.jdbc.FtseLikelihoodJdbcManager;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseLikelihoodService {

	private static final Integer LIKELIHOOD_INTERVAL = 100;

	protected final Log logger = LogFactory.getLog(getClass());

	protected String likelihoodStartDate;
	protected Integer likelihoodInterval = LIKELIHOOD_INTERVAL;

	protected FtseJdbcManager ftseJdbcManager;
	protected FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager;

	public FtseLikelihoodService() {
	}

	@Transactional(readOnly = false)
	public void updateLikelihoods(Hmm<ObservationVector> hmm, String startDate, int interval) {
		init(startDate, interval);
		Date date = FtseUtils.getDate(likelihoodStartDate);
		Date threshold = FtseUtils.getDateDaysAfter(date, interval);
		logger.info("Starting to update likelihood values from [" + date + "] for [" + interval + "] days.");
		for (int i = 0; i < likelihoodInterval;) {
			List<FtseIndex> ftses = ftseJdbcManager.findByDate(date);
			if (ftses == null || ftses.isEmpty()) {
				date = FtseUtils.getTomorrrow(date);
				if (date.getTime() >= threshold.getTime()) {
					// finish it!
					i = likelihoodInterval + 1;
				}
				continue;
			}
			double p = hmm.probability(ftses);
			double likelihood = Math.log10(p);
			ftseLikelihoodJdbcManager.saveOrUpdate(date, likelihood);
			logger.debug("Likelihood updated: [" + date + ", " + likelihood + "]");
			date = FtseUtils.getTomorrrow(date);
			++i;
		}
	}

	protected void init(String startDate, Integer interval) {
		try {
			FtseUtils.getDate(startDate);
			likelihoodStartDate = startDate;
		} catch (Exception e) {
			throw new IllegalArgumentException("likelihoodStartDate should be of form yyyy-MM-dd", e);
		}
		if (interval != null && interval != -1) {
			likelihoodInterval = interval;
		}
	}

	public void setLikelihoodInterval(Integer likelihoodInterval) {
		this.likelihoodInterval = likelihoodInterval;
	}

	public void setLikelihoodStartDate(String likelihoodStartDate) {
		this.likelihoodStartDate = likelihoodStartDate;
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}

	public void setFtseLikelihoodJdbcManager(FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager) {
		this.ftseLikelihoodJdbcManager = ftseLikelihoodJdbcManager;
	}

}
