/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.data.jdbc.FtseLikelihoodJdbcManager;
import nl.liacs.dbdm.ftse.distribution.MultiGaussianMixtureDistribution;
import nl.liacs.dbdm.ftse.distribution.OpdfMultiGaussianMixture;
import nl.liacs.dbdm.ftse.hmm.CsvObservationVectorWriter;
import nl.liacs.dbdm.ftse.hmm.LeftRightHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;
import nl.liacs.dbdm.ftse.utils.FtseUtils;
import nl.liacs.dbdm.ftse.utils.HmmUtils;

import org.apache.commons.math.linear.MatrixUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesWriter;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

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
public class HmmTest3 extends AbstractTransactionalJUnit4SpringContextTests {

	private static final String FROM1 = "2009-05-01";
	private static final String TO1 = "2009-06-01";
	private static final String FROM2 = TO1;
	private static final String TO2 = "2009-07-01";
	private static final String FROM3 = TO2;
	private static final String TO3 = "2009-08-01";
	private static final String FROM4 = TO3;
	private static final String TO4 = "2009-09-01";

	private static final String LIKELIHOOD_START_DATE = "2009-09-01";
	private static final int LIKELIHOOD_INTERVAL = 60;

	private static final double LIKELIHOOD_TOLERANCE = 0.01;
	private static final String PREDICTION_DATE = "2009-05-01";
	private static final double PREDICTION_RANGE = 100;

	/**
	 * N: number of states
	 */
	private int N = 4;

	/**
	 * D: dimension used for multivariate Gaussian distributions
	 */
	private int D = 4;

	/**
	 * M: the number of mixtures in a multivariate Gaussian mixture distribution
	 */
	private int M = 3;

	/**
	 * Delta: the delta used for a left-right HMM.
	 */
	private int delta = 3;

	private List<FtseIndex> data1, data2, data3, data4;

	@Resource
	protected FtseJdbcManager ftseJdbcManager;

	@Resource
	protected FtseLikelihoodJdbcManager ftseLikelihoodJdbcManager;

	@Test
	@Transactional(readOnly = false)
	public void testHmmBWL() throws Exception {
		initData();
		Hmm<ObservationVector> hmm = (Hmm<ObservationVector>) initHmm();
		Hmm<ObservationVector> learnt = learnBWL(hmm);
		// computeLikelihoods(learnt);
		predict();
	}

	private void predict() {
		int count = 0;
		Date predDate = FtseUtils.getDate(PREDICTION_DATE);
		double sum = 0.;
		double n = 0.;
		do {
			try {
				Date yesterPredDate = FtseUtils.getYesterday(predDate);
				FtseIndex yesterFtse = ftseJdbcManager.findByDate(yesterPredDate).get(0);
				FtseIndex realPredFtse = ftseJdbcManager.findByDate(predDate).get(0);
				Double yesterPredDateLikelihood = ftseLikelihoodJdbcManager.findLikelihoodByDate(yesterPredDate);
				List<FtseIndex> guesses = ftseLikelihoodJdbcManager.findByLikelihoodTolerance(yesterPredDate,
						yesterPredDateLikelihood, LIKELIHOOD_TOLERANCE);
				if (!guesses.isEmpty()) {
					FtseIndex bestGuess = findBestGuess(guesses);
					FtseIndex bestGuessTomorrow = ftseJdbcManager.findNextByDate(FtseUtils.getTomorrrow(bestGuess
							.getDate()));
					Double predictedClose = yesterFtse.getClose()
							+ (bestGuessTomorrow.getClose() - bestGuess.getClose());
					sum = sum + Math.abs((realPredFtse.getClose() - predictedClose) / realPredFtse.getClose());
					n++;
					System.out.println(FtseUtils.getMySqlDateString(predDate) + "," + realPredFtse.getClose() + ","
							+ predictedClose);
				}
			} catch (Exception e) {
				// logger.error("Skipped prediction for: " +
				// FtseUtils.getMySqlDateString(predDate));
			}
			predDate = FtseUtils.getTomorrrow(predDate);
		} while (++count <= PREDICTION_RANGE);
		System.out.println(100 * sum / n);
	}

	private FtseIndex findBestGuess(List<FtseIndex> guesses) {
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

	private void computeLikelihoods(Hmm<ObservationVector> learnt) {
		Calendar c = Calendar.getInstance();
		c.setTime(FtseUtils.getDate(LIKELIHOOD_START_DATE));
		for (int i = 0; i < LIKELIHOOD_INTERVAL;) {
			c.add(Calendar.DAY_OF_MONTH, -1);
			String dateString = FtseUtils.getMySqlDateString(c.getTime());
			List<ObservationVector> observations = findObservations(dateString, 0);
			if (observations != null && !observations.isEmpty()) {
				double likelihood = Math.log10(learnt.probability(observations));
				ftseLikelihoodJdbcManager.saveOrUpdate(dateString, likelihood);
				++i;
			}
		}
	}

	/**
	 * generate some sequences using the learnt hmm
	 * 
	 * @param learnt
	 * @throws IOException
	 */
	private void generateEstimates(Hmm<ObservationVector> learnt) throws IOException {
		MarkovGenerator<ObservationVector> mg = new MarkovGenerator<ObservationVector>(learnt);
		List<List<ObservationVector>> estimateSeq = new ArrayList<List<ObservationVector>>();
		List<ObservationVector> sample = mg.observationSequence(600);
		Collections.reverse(sample);
		estimateSeq.add(sample);
		ObservationSequencesWriter.write(new BufferedWriter(new FileWriter("estimates3.csv")),
				new CsvObservationVectorWriter(), estimateSeq);
	}

	private Hmm<ObservationVector> learnBWL(Hmm<ObservationVector> hmm) throws IOException {
		BaumWelchLearner bwl = new BaumWelchLearner();
		bwl.setNbIterations(20);
		List<List<ObservationVector>> sequences = getSequences(hmm);
		logHmm(hmm, "before3.dot");
		Hmm<ObservationVector> learnt = bwl.learn(hmm, sequences);
		logHmm(learnt, "after3.dot");
		return learnt;
	}

	private Hmm<?> initHmm() {
		int sequenceSize = data1.size() + data2.size() + data3.size() + data4.size();

		OpdfMultiGaussianMixture<ObservationVector> opdf1 = createOpdfMultiGaussianMixture(0, 1, D, M, null);
		OpdfMultiGaussianMixture<ObservationVector> opdf2 = createOpdfMultiGaussianMixture(0, 1, D, M, null);
		OpdfMultiGaussianMixture<ObservationVector> opdf3 = createOpdfMultiGaussianMixture(0, 1, D, M, null);
		OpdfMultiGaussianMixture<ObservationVector> opdf4 = createOpdfMultiGaussianMixture(0, 1, D, M, null);

		opdf1.fit(data1);
		opdf2.fit(data2);
		opdf3.fit(data3);
		opdf4.fit(data4);

		List<Opdf<ObservationVector>> opdfs = new ArrayList<Opdf<ObservationVector>>();
		opdfs.add(opdf1);
		opdfs.add(opdf2);
		opdfs.add(opdf3);
		opdfs.add(opdf4);

		LeftRightHmm<ObservationVector> hmm = new LeftRightHmm<ObservationVector>(N, delta, opdfs);
		HmmUtils.refineTransitions(hmm, sequenceSize);
		// hmm.setPis(new double[] { 1., 0., 0., 0. });

		return hmm;
	}

	private MultiGaussianDistribution createMultiGaussianDistribution(double mean, double covariance, int dim) {
		double[] means = new double[dim];
		Arrays.fill(means, mean);
		double[][] covs = MatrixUtils.createRealIdentityMatrix(dim).scalarMultiply(covariance).getData();
		return new MultiGaussianDistribution(means, covs);
	}

	private MultiGaussianMixtureDistribution createMultiGaussianMixtureDistribution(double mean, double covariance,
			int dim, int mixtures, double[] mixtureProps) {

		if (mixtureProps == null) {
			mixtureProps = new double[mixtures];
			Arrays.fill(mixtureProps, 1. / mixtures);
		}
		mixtures = mixtureProps.length;
		MultiGaussianDistribution[] mgds = new MultiGaussianDistribution[mixtures];
		for (int i = 0; i < mgds.length; i++) {
			mgds[i] = createMultiGaussianDistribution(mean, covariance, dim);
		}
		return new MultiGaussianMixtureDistribution(mgds, mixtureProps);
	}

	private OpdfMultiGaussianMixture<ObservationVector> createOpdfMultiGaussianMixture(double mean, double covariance,
			int dim, int mixtures, double[] mixtureProps) {
		return new OpdfMultiGaussianMixture<ObservationVector>(createMultiGaussianMixtureDistribution(mean, covariance,
				dim, mixtures, mixtureProps));
	}

	private List<List<ObservationVector>> getSequences(Hmm<ObservationVector> hmm) {

		List<ObservationVector> obs1 = new ArrayList<ObservationVector>();
		obs1.addAll(data1);
		List<ObservationVector> obs2 = new ArrayList<ObservationVector>();
		obs2.addAll(data2);
		List<ObservationVector> obs3 = new ArrayList<ObservationVector>();
		obs3.addAll(data3);
		List<ObservationVector> obs4 = new ArrayList<ObservationVector>();
		obs4.addAll(data4);

		List<List<ObservationVector>> obs = new ArrayList<List<ObservationVector>>();
		obs.add(obs1);
		obs.add(obs2);
		obs.add(obs3);
		obs.add(obs4);

		return obs;
	}

	private List<FtseIndex> getData(String from, String to) {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate(from, to);
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
	}

	private void initData() {
		data1 = getData(FROM1, TO1);
		data2 = getData(FROM2, TO2);
		data3 = getData(FROM3, TO3);
		data4 = getData(FROM4, TO4);
		logger.warn("Sequence sizes [" + data1.size() + "," + data2.size() + "," + data3.size() + "," + data4.size()
				+ "]: " + data1);
	}

	private List<ObservationVector> findObservations(String today, int interval) {
		String from = FtseUtils.getDateDaysBeforeString(today, interval);
		List<FtseIndex> data = getData(from, today);
		List<ObservationVector> obs = new ArrayList<ObservationVector>();
		obs.addAll(data);
		return obs;
	}

	private void logHmm(Hmm<?> hmm, String filename) throws IOException {
		System.out.println("------------------------------");
		(new GenericHmmDrawerDot()).write(hmm, filename);
		System.out.println(hmm);
	}

}
