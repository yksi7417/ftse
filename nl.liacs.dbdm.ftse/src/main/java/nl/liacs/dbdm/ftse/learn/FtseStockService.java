/*
 *
 * Created on Dec 27, 2009 | 4:14:25 PM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.Date;
import java.util.List;

import nl.liacs.dbdm.ftse.data.FTSEDownloader;
import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.data.jdbc.FtseLikelihoodJdbcManager;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.ui.client.FtseService;
import nl.liacs.dbdm.ftse.ui.client.LikelihoodOptions;
import nl.liacs.dbdm.ftse.ui.client.PredictionOptions;
import nl.liacs.dbdm.ftse.ui.client.PredictionResult;
import nl.liacs.dbdm.ftse.ui.client.TrainingOptions;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseStockService implements FtseService {

	protected final Log logger = LogFactory.getLog(getClass());

	private Hmm<ObservationVector> hmm;

	private HmmTrainer trainer;
	private FtseLikelihoodService likelihoodService;
	private FtsePredictor predictor;
	private FtseJdbcManager ftseJdbcManager;
	private FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager;
	private FTSEDownloader downloader = new FTSEDownloader();

	@Override
	public String train(TrainingOptions options) {
		try {
			trainer.setLearningStartDate(FtseUtils.getMySqlDateString(options.getStartDate()));
			trainer.setNumberOfTrainingIterations(options.getNumberOfIterations());
			hmm = trainer.train();
		} catch (Exception e) {
			return "HMM training failed.";
		}
		return "HMM training complete.";
	}

	@Override
	public String updateLikelihoods(LikelihoodOptions options) {
		try {
			likelihoodService.updateLikelihoods(hmm, FtseUtils.getMySqlDateString(options.getStartDate()), options
					.getDays());
		} catch (Exception e) {
			return "Likelihood update failed.";
		}
		return "Likelihood update complete.";
	}

	@Override
	public PredictionResult predict(PredictionOptions options) {
		try {
			PredictionResult result = predictor.predict(FtseUtils.getMySqlDateString(options.getDate()), options
					.getDays(), options.getTolerance());

			return result;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String clearData() {
		try {
			ftseLikelihoodJdbcManager.clearData();
			ftseJdbcManager.clearData();
		} catch (Exception e) {
			logger.error("Clearing data failed; reason:", e);
			throw new RuntimeException("Clearing data failed", e);
		}
		return null;
	}

	@Override
	public String loadData(Date from, Date to) {
		try {
			downloader.setFromDate(from);
			downloader.setToDate(to);
			List<FtseIndex> list = downloader.download();
			logger.info("Fetched a list of FTSE Indices [" + list.size() + "] from Yahoo! Finance.");
			ftseJdbcManager.saveAll(list);
			logger.info("Persisted a list of FTSE Indices " + "[" + list.size()
					+ "] from Yahoo! Finance to local database.");
		} catch (Exception e) {
			logger.error("Failed to load data on [" + from + "," + to + "]:", e);
			throw new RuntimeException(e);
		}
		return null;
	}

	public void setTrainer(HmmTrainer trainer) {
		this.trainer = trainer;
	}

	public void setLikelihoodService(FtseLikelihoodService likelihoodService) {
		this.likelihoodService = likelihoodService;
	}

	public void setPredictor(FtsePredictor predictor) {
		this.predictor = predictor;
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}

	public void setFtseLikelihoodJdbcManager(FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager) {
		this.ftseLikelihoodJdbcManager = ftseLikelihoodJdbcManager;
	}

}
