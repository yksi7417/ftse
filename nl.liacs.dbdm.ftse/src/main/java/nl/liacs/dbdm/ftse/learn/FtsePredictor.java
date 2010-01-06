/*
 *
 * Created on Dec 27, 2009 | 1:51:49 PM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.data.jdbc.FtseLikelihoodJdbcManager;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.ui.client.PredictionResult;
import nl.liacs.dbdm.ftse.ui.client.SerializableListOrderedMap;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtsePredictor {

	private static final Integer PREDICTION_DAYS = 30;
	private static final Double PREDICTION_LIKELIHOOD_TOLERANCE = 0.001;

	protected final Log logger = LogFactory.getLog(getClass());

	protected String predictionDate;
	protected Integer predictionDays = PREDICTION_DAYS;
	protected Double predictionLikelihoodTolerance = PREDICTION_LIKELIHOOD_TOLERANCE;
	protected Double mape = 0.;

	protected FtseJdbcManager ftseJdbcManager;
	protected FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager;

	public PredictionResult predict(String predictionDate, Integer days, Double tolerance) {
		PredictionResult result = new PredictionResult();
		Map<Date, List<Double>> predictions = new SerializableListOrderedMap();
		init(predictionDate, days, tolerance);
		Date curDate = FtseUtils.getDate(predictionDate);
		Date from = (Date) curDate.clone();
		Date to = FtseUtils.getDateDaysAfter(curDate, days);
		result.setStartDate(from);
		result.setEndDate(to);
		double sum = 0.;
		double n = 0.;
		do {
			try {
				Date yesterPredDate = FtseUtils.getYesterday(curDate);
				FtseIndex yesterFtse = ftseJdbcManager.findByDate(yesterPredDate).get(0);
				FtseIndex realPredFtse = ftseJdbcManager.findByDate(curDate).get(0);
				Double yesterPredDateLikelihood = ftseLikelihoodJdbcManager.findLikelihoodByDate(yesterPredDate);
				List<FtseIndex> guesses = ftseLikelihoodJdbcManager.findByLikelihoodTolerance(yesterPredDate,
						yesterPredDateLikelihood, predictionLikelihoodTolerance);
				if (!guesses.isEmpty()) {
					FtseIndex bestGuess = findBestGuess(guesses);
					FtseIndex bestGuessTomorrow = ftseJdbcManager.findNextByDate(FtseUtils.getTomorrrow(bestGuess
							.getDate()));
					Double predictedClose = yesterFtse.getClose()
							+ (bestGuessTomorrow.getClose() - bestGuess.getClose());
					sum = sum + Math.abs((realPredFtse.getClose() - predictedClose) / realPredFtse.getClose());
					n++;
					predictions.put(curDate, Arrays.asList(new Double[] { realPredFtse.getClose(), predictedClose }));
				}
			} catch (Exception e) {
			}
			curDate = FtseUtils.getTomorrrow(curDate);
		} while (curDate.getTime() <= to.getTime());
		this.mape = sum / n;
		logger.info("Prediction complete from [" + predictionDate + "] for [" + days + "] days with ["
				+ predictions.size() + "] results");
		result.setMape(mape);
		result.setPredictions(predictions);
		return result;
	}

	protected FtseIndex findBestGuess(List<FtseIndex> guesses) {
		double max = Double.MIN_VALUE;
		FtseIndex best = guesses.get(0);
		for (FtseIndex g : guesses) {
			Double likelihood = ftseLikelihoodJdbcManager.findLikelihoodByDate(g.getDate());
			if (likelihood > max) {
				max = likelihood;
				best = g;
			}
		}
		return best;
	}

	protected void init(String predictionDate, Integer days, Double tolerance) {
		try {
			FtseUtils.getDate(predictionDate);
			this.predictionDate = predictionDate;
		} catch (Exception e) {
			throw new IllegalArgumentException("predictionDate must be of form yyyy-MM-dd", e);
		}
		if (days != null && days != -1) {
			this.predictionDays = days;
		}
		if (tolerance != null && tolerance != -1) {
			this.predictionLikelihoodTolerance = tolerance;
		}
	}

	public void setPredictionDate(String predictionDate) {
		this.predictionDate = predictionDate;
	}

	public void setPredictionDays(Integer predictionDays) {
		this.predictionDays = predictionDays;
	}

	public void setPredictionLikelihoodTolerance(Double predictionLikelihoodTolerance) {
		this.predictionLikelihoodTolerance = predictionLikelihoodTolerance;
	}

	public Double getMAPE() {
		return mape;
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}

	public void setFtseLikelihoodJdbcManager(FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager) {
		this.ftseLikelihoodJdbcManager = ftseLikelihoodJdbcManager;
	}

}
