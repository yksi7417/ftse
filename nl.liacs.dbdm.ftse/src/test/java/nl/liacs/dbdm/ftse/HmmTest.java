/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.model.BaseFtseIndexHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexList;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;
import nl.liacs.dbdm.ftse.model.OpdfMultiGaussianMixtureFactory;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.correlation.Covariance;
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
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;
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
public class HmmTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Resource
	protected FtseJdbcManager ftseJdbcManager;

	@Test
	@Transactional
	public void testHmm() throws Exception {
		Hmm<ObservationVector> hmm = buildHmm();
		BaumWelchLearner learner = new BaumWelchLearner();
		learner.learn(hmm, getSequences(hmm));
		// next we go to the prediction
	}

	@SuppressWarnings("unchecked")
	private BaseFtseIndexHmm buildHmm() {

		FtseIndexList data = getData();
		logger.warn("ftse list size:" + data.size());

		double openMean = computeOpenMean(data);
		double lowMean = computeLowMean(data);
		double highMean = computeHighMean(data);
		double closeMean = computeCloseMean(data);
		logger.warn(openMean + ":" + lowMean + ":" + highMean + ":" + closeMean);

		// 4 x 4 : open x low x high x close
		double[][] covs = computeCovariances(data);
		logger.warn(Arrays.toString(covs[0]));
		logger.warn(Arrays.toString(covs[1]));
		logger.warn(Arrays.toString(covs[2]));
		logger.warn(Arrays.toString(covs[3]));

		MultiGaussianDistribution mgdOpen = new MultiGaussianDistribution(
				new double[] { lowMean, highMean, closeMean }, omit(0, covs));

		MultiGaussianDistribution mgdLow = new MultiGaussianDistribution(
				new double[] { openMean, highMean, closeMean }, omit(1, covs));

		MultiGaussianDistribution mgdHigh = new MultiGaussianDistribution(
				new double[] { openMean, lowMean, closeMean }, omit(2, covs));

		OpdfFactory<Opdf<ObservationVector>> opdfFactory = new OpdfMultiGaussianMixtureFactory(
				new MultiGaussianDistribution[] { mgdOpen, mgdLow, mgdHigh }, new double[] { 0.34, 0.33, 0.33 });

		BaseFtseIndexHmm hmm = new BaseFtseIndexHmm(opdfFactory);
		return hmm;
	}

	private List<List<ObservationVector>> getSequences(Hmm<ObservationVector> hmm) {
		// List<List<FtseIndex>> seqs = new ArrayList<List<FtseIndex>>(200);
		// List<FtseIndex> some =
		// ftseJdbcManager.findByFromDateToDate("2005-01-01", "2007-01-01");
		// List<FtseIndex> cur = new ArrayList<FtseIndex>();
		// for (int i = 1; i < some.size(); i++) {
		// FtseIndex ftse = some.get(i);
		// cur.add(ftse);
		// if (i % 100 == 0) {
		// seqs.add(cur);
		// cur = new ArrayList<FtseIndex>();
		// }
		// }
		// return seqs;
		MarkovGenerator<ObservationVector> mg = new MarkovGenerator<ObservationVector>(hmm);
		List<List<ObservationVector>> sequences = new ArrayList<List<ObservationVector>>();
		for (int i = 0; i < 200; i++)
			sequences.add(mg.observationSequence(100));
		return sequences;
	}

	private FtseIndexList getData() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("2000-01-01", "2005-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return new FtseIndexList(data);
	}

	private double computeHighMean(FtseIndexList data) {
		return StatUtils.mean(data.getHighsArray());
	}

	private double computeLowMean(FtseIndexList data) {
		return StatUtils.mean(data.getLowsArray());
	}

	private double computeOpenMean(FtseIndexList data) {
		return StatUtils.mean(data.getOpensArray());
	}

	private double computeCloseMean(FtseIndexList data) {
		return StatUtils.mean(data.getClosesArray());
	}

	private double[][] computeCovariances(FtseIndexList data) {
		double[] opens = data.getOpensArray();
		double[] lows = data.getLowsArray();
		double[] highs = data.getHighsArray();
		double[] closes = data.getClosesArray();

		Covariance c = new Covariance();

		double openOpen = c.covariance(opens, opens);
		double lowLow = c.covariance(lows, lows);
		double highHigh = c.covariance(highs, highs);
		double closeClose = c.covariance(closes, closes);

		double openLow = c.covariance(opens, lows);
		double openHigh = c.covariance(opens, highs);
		double openClose = c.covariance(opens, closes);
		double lowHigh = c.covariance(lows, highs);
		double lowClose = c.covariance(lows, closes);
		double highClose = c.covariance(highs, closes);

		return new double[][] { { openOpen, openLow, openHigh, openClose }, { openLow, lowLow, lowHigh, lowClose },
				{ openHigh, lowHigh, highHigh, highClose }, { openClose, lowClose, highClose, closeClose } };
	}

	private double[][] omit(int i, double[][] mat) {
		RealMatrix mat2 = MatrixUtils.createRealMatrix(mat);
		int[] selection = new int[mat.length - 1];
		int index = 0;
		for (int j = 0; j < selection.length; j++) {
			if (j != i) {
				selection[index] = index++;
			}
		}
		mat2 = mat2.getSubMatrix(selection, selection);
		return mat2.getData();
	}

	public void setFtseJdbcManager(FtseJdbcManager ftseJdbcManager) {
		this.ftseJdbcManager = ftseJdbcManager;
	}
}
