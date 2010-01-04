/*
 *
 * Created on Dec 27, 2009 | 10:44:01 AM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.ArrayList;
import java.util.List;

import nl.liacs.dbdm.ftse.distribution.OpdfMultiGaussianMixture;
import nl.liacs.dbdm.ftse.hmm.LeftRightHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.utils.FtseUtils;
import nl.liacs.dbdm.ftse.utils.HmmUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class HmmTrainer implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	/** N: number of states */
	protected int N = 4;

	/** D: dimension used for multivariate Gaussian distributions */
	protected int D = 4;

	/**
	 * M: the number of mixtures in a multivariate Gaussian mixture distribution
	 */
	protected int M = 3;

	/** Delta: the delta used for a left-right HMM. */
	protected int delta = 3;

	protected String learningStartDate;
	protected LeftRightHmm<ObservationVector> hmm;

	protected BaumWelchLearner learner = new BaumWelchLearner();
	protected DistributionProvider distributionProvider = new DistributionProvider();
	protected FtseDataSequenceProviderImpl dataSequenceProvider;

	public HmmTrainer() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(learner, "learner must not be null.");
		Assert.notNull(dataSequenceProvider, "dataSequenceProvider must not be null.");
		learner.setNbIterations(20);
		logger.info("Left-Right HMM Trainer initialized [N=" + N + ", M=" + M + ", D=" + D + ", delta=" + delta + "]");
	}

	public Hmm<ObservationVector> train() {
		List<List<FtseIndex>> ftseSeq = dataSequenceProvider.provide(N, learningStartDate);
		if (ftseSeq.size() != N) {
			throw new IllegalStateException("The data sequence size [" + ftseSeq.size()
					+ "] should be of the same size as the number of states [" + N + "]");
		}
		logger.debug("Total [" + getTotalNumberOfFtseSequence(ftseSeq) + "] FTSE indices are loaded for HMM training.");
		List<Opdf<ObservationVector>> opdfs = initOpdfs(ftseSeq);
		hmm = initHmm(ftseSeq, opdfs);
		logger.debug("A collection of [" + opdfs.size() + "] Gaussian distributions initialized for HMM training.");
		logger.info("Starting to train the HMM with start date: " + learningStartDate);
		hmm = (LeftRightHmm<ObservationVector>) learner.learn(hmm, ftseSeq);
		logger.debug("HMM trained: " + hmm);
		logger.info("Left-Right HMM training completed.");
		return hmm;
	}

	public LeftRightHmm<ObservationVector> getTrainedHmm() {
		return hmm;
	}

	protected LeftRightHmm<ObservationVector> initHmm(List<List<FtseIndex>> ftseSeq, List<Opdf<ObservationVector>> opdfs) {
		int total = getTotalNumberOfFtseSequence(ftseSeq);
		LeftRightHmm<ObservationVector> hmm = new LeftRightHmm<ObservationVector>(N, delta, opdfs);
		HmmUtils.refineTransitions(hmm, total);
		return hmm;
	}

	protected List<Opdf<ObservationVector>> initOpdfs(List<List<FtseIndex>> ftseSeq) {
		List<Opdf<ObservationVector>> opdfs = new ArrayList<Opdf<ObservationVector>>();
		for (int i = 0; i < N; i++) {
			OpdfMultiGaussianMixture<ObservationVector> opdf = distributionProvider.createOpdfMultiGaussianMixture(0,
					1, D, M, null);
			opdf.fit(ftseSeq.get(i));
			opdfs.add(opdf);
		}
		return opdfs;
	}

	private int getTotalNumberOfFtseSequence(List<List<FtseIndex>> seq) {
		int size = 0;
		for (List<FtseIndex> list : seq) {
			size += list.size();
		}
		return size;
	}

	public void setNumberOfStates(int states) {
		this.N = states;
	}

	public void setDistributionDimension(int dim) {
		this.D = dim;
	}

	public void setNumberOfDistributionMixtures(int mix) {
		this.M = mix;
	}

	public void setLeftRightHmmDelta(int delta) {
		this.delta = delta;
	}

	public void setDataSequenceProvider(FtseDataSequenceProviderImpl dataSequenceProvider) {
		this.dataSequenceProvider = dataSequenceProvider;
	}

	public void setLearningStartDate(String learningStartDate) {
		try {
			FtseUtils.getDate(learningStartDate);
		} catch (Exception e) {
			throw new IllegalArgumentException("learningStartDate must be of form yyyy-MM-dd", e);
		}
		this.learningStartDate = learningStartDate;
	}

	public void setLearner(BaumWelchLearner learner) {
		this.learner = learner;
	}

	public void setNumberOfTrainingIterations(Integer numberOfIterations) {
		learner.setNbIterations(numberOfIterations);
	}

}
