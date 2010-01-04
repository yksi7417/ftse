/*
 *
 * Created on Dec 27, 2009 | 4:14:25 PM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import nl.liacs.dbdm.ftse.ui.client.FtseService;
import nl.liacs.dbdm.ftse.ui.client.LikelihoodOptions;
import nl.liacs.dbdm.ftse.ui.client.PredictionOptions;
import nl.liacs.dbdm.ftse.ui.client.PredictionResult;
import nl.liacs.dbdm.ftse.ui.client.TrainingOptions;
import nl.liacs.dbdm.ftse.utils.FtseUtils;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseStockService implements FtseService {

	private Hmm<ObservationVector> hmm;

	private HmmTrainer trainer;
	private FtseLikelihoodService likelihoodService;
	private FtsePredictor predictor;

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

	public void setTrainer(HmmTrainer trainer) {
		this.trainer = trainer;
	}

	public void setLikelihoodService(FtseLikelihoodService likelihoodService) {
		this.likelihoodService = likelihoodService;
	}

	public void setPredictor(FtsePredictor predictor) {
		this.predictor = predictor;
	}

}
