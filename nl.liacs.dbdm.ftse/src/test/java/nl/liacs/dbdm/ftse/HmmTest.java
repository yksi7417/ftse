/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.distribution.ExtendedOpdfMultiGaussianFactory;
import nl.liacs.dbdm.ftse.distribution.MultiGaussianMixtureDistribution;
import nl.liacs.dbdm.ftse.distribution.OpdfMultiGaussianMixture;
import nl.liacs.dbdm.ftse.distribution.OpdfMultiGaussianMixtureFactory;
import nl.liacs.dbdm.ftse.model.BaseFtseIndexHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexList;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
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
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
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

//	@Test
//	@Transactional
	public void testHmmBWL() throws Exception {
		Hmm<ObservationVector> hmm = buildHmm();
		BaumWelchLearner learner = new BaumWelchLearner();
		learner.learn(hmm, getSequences(hmm));
		logHmm(hmm, "final.dot");
		// next we go to the prediction
	}

	 @Test
	 @Transactional
	public void testHmmKML() throws Exception {
		List<List<ObservationVector>> data = getSequences2();
		// OpdfMultiGaussianFactory f1 = buildSimpleMultiGaussians(getData());
		// OpdfMultiGaussianMixtureFactory f2 = new
		// OpdfMultiGaussianMixtureFactory(4, 4);
		OpdfFactory f3 = aaa2(getData());
		KMeansLearner<ObservationVector> kml = new KMeansLearner<ObservationVector>(4, f3, data);
		Hmm<ObservationVector> kmlHmm = kml.learn();
		logHmm(kmlHmm, "kml.dot");
		BaumWelchLearner bwl = new BaumWelchLearner();
		Hmm<ObservationVector> finalHmm = bwl.learn(kmlHmm, getSequences2());
		logHmm(finalHmm, "final.dot");
	}

	@SuppressWarnings("unchecked")
	private BaseFtseIndexHmm buildHmm() {
		List<FtseIndex> data = getData();
		FtseIndexList list = new FtseIndexList(data);
		logger.warn("ftse list size:" + list.size());
		List<Opdf> opdfs = new ArrayList<Opdf>();
		opdfs.add(buildSimpleMultiGaussians(getData1()));
		opdfs.add(buildSimpleMultiGaussians(getData2()));
		opdfs.add(buildSimpleMultiGaussians(getData3()));
		opdfs.add(buildSimpleMultiGaussians(getData4()));
		BaseFtseIndexHmm hmm = new BaseFtseIndexHmm(opdfs);
		return hmm;
	}

	private List<List<ObservationVector>> getSequences2() {
		List<List<ObservationVector>> seqs = new ArrayList<List<ObservationVector>>(200);
		List<FtseIndex> some = ftseJdbcManager.findByFromDateToDate("2005-01-01", "2007-01-01");
		List<ObservationVector> cur = new ArrayList<ObservationVector>();
		for (int i = 1; i < some.size(); i++) {
			FtseIndex ftse = some.get(i);
			cur.add(ftse);
			if (i % 100 == 0) {
				seqs.add(cur);
				cur = new ArrayList<ObservationVector>();
			}
		}
		return seqs;
	}

	private List<List<ObservationVector>> getSequences(Hmm<ObservationVector> hmm) {
		MarkovGenerator<ObservationVector> mg = new MarkovGenerator<ObservationVector>(hmm);
		List<List<ObservationVector>> sequences = new ArrayList<List<ObservationVector>>();
		for (int i = 0; i < 200; i++)
			sequences.add(mg.observationSequence(100));
		return sequences;
	}

	private List<FtseIndex> getData() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("2000-01-01", "2005-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
	}

	private List<FtseIndex> getData1() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("1998-01-01", "2000-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
	}

	private List<FtseIndex> getData2() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("2000-01-01", "2002-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
	}

	private List<FtseIndex> getData3() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("2002-01-01", "2004-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
	}

	private List<FtseIndex> getData4() {
		List<FtseIndex> data = ftseJdbcManager.findByFromDateToDate("2004-01-01", "2006-01-01");
		Collections.sort(data, FtseIndexTimeComparator.INSTANCE);
		return data;
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

	private OpdfMultiGaussian buildSimpleMultiGaussians(List<FtseIndex> data) {
		FtseIndexList list = new FtseIndexList(data);

		double[] opens = list.getOpensArray();
		double[] lows = list.getLowsArray();
		double[] highs = list.getHighsArray();
		double[] closes = list.getClosesArray();

		RealVector mu = MatrixUtils.createRealVector(new double[] { StatUtils.mean(opens), StatUtils.mean(lows),
				StatUtils.mean(highs), StatUtils.mean(closes) });
		RealMatrix transposedDataMatrix = MatrixUtils.createRealMatrix(new double[][] { opens, lows, highs, closes });
		RealMatrix dataMatrix = transposedDataMatrix.transpose();
		Covariance cov = new Covariance(dataMatrix);
		return new OpdfMultiGaussian(mu.getData(), cov.getCovarianceMatrix().getData());
	}

	private OpdfMultiGaussianFactory buildSimpleMultiGaussiansFactory(List<FtseIndex> data) {
		OpdfMultiGaussian pdf = buildSimpleMultiGaussians(data);
		return new ExtendedOpdfMultiGaussianFactory(pdf.mean(), pdf.covariance());
	}

	private List<MultiGaussianDistribution> buildSingleMultiGaussians(List<FtseIndex> data) {

		FtseIndexList list = new FtseIndexList(data);

		double[] opens = list.getOpensArray();
		double[] lows = list.getLowsArray();
		double[] highs = list.getHighsArray();
		double[] closes = list.getClosesArray();

		RealVector mu = MatrixUtils.createRealVector(new double[] { StatUtils.mean(opens), StatUtils.mean(lows),
				StatUtils.mean(highs), StatUtils.mean(closes) });
		RealMatrix transposedDataMatrix = MatrixUtils.createRealMatrix(new double[][] { opens, lows, highs, closes });
		RealMatrix dataMatrix = transposedDataMatrix.transpose();
		Covariance cov = new Covariance(dataMatrix);

		MultiGaussianDistribution pdf_open = new MultiGaussianDistribution(new double[] { mu.getEntry(1),
				mu.getEntry(2), mu.getEntry(3) }, cov.getCovarianceMatrix().getSubMatrix(new int[] { 1, 2, 3 },
				new int[] { 1, 2, 3 }).getData());

		MultiGaussianDistribution pdf_low = new MultiGaussianDistribution(new double[] { mu.getEntry(0),
				mu.getEntry(2), mu.getEntry(3) }, cov.getCovarianceMatrix().getSubMatrix(new int[] { 0, 2, 3 },
				new int[] { 0, 2, 3 }).getData());

		MultiGaussianDistribution pdf_high = new MultiGaussianDistribution(new double[] { mu.getEntry(0),
				mu.getEntry(1), mu.getEntry(3) }, cov.getCovarianceMatrix().getSubMatrix(new int[] { 0, 1, 3 },
				new int[] { 0, 1, 3 }).getData());

		MultiGaussianDistribution pdf_close = new MultiGaussianDistribution(new double[] { mu.getEntry(0),
				mu.getEntry(1), mu.getEntry(2) }, cov.getCovarianceMatrix().getSubMatrix(new int[] { 0, 1, 2 },
				new int[] { 0, 1, 2 }).getData());

		List<MultiGaussianDistribution> dists = new ArrayList<MultiGaussianDistribution>();
		dists.add(pdf_open);
		dists.add(pdf_low);
		dists.add(pdf_high);
		dists.add(pdf_close);

		return dists;
	}

	private List<Opdf> buildMixtureOpdfFactory(List<FtseIndex> data) {

		List<MultiGaussianDistribution> dists = buildSingleMultiGaussians(data);

		MultiGaussianDistribution pdf_open = dists.get(0);
		MultiGaussianDistribution pdf_low = dists.get(1);
		MultiGaussianDistribution pdf_high = dists.get(2);
		MultiGaussianDistribution pdf_close = dists.get(3);

		MultiGaussianMixtureDistribution opdf_open = new MultiGaussianMixtureDistribution(
				new MultiGaussianDistribution[] { pdf_low, pdf_high, pdf_close }, new double[] { 0.34, 0.33, 0.33 });

		MultiGaussianMixtureDistribution opdf_low = new MultiGaussianMixtureDistribution(
				new MultiGaussianDistribution[] { pdf_open, pdf_low, pdf_high }, new double[] { 0.34, 0.33, 0.33 });

		MultiGaussianMixtureDistribution opdf_high = new MultiGaussianMixtureDistribution(
				new MultiGaussianDistribution[] { pdf_open, pdf_low, pdf_close }, new double[] { 0.34, 0.33, 0.33 });

		MultiGaussianMixtureDistribution opdf_close = new MultiGaussianMixtureDistribution(
				new MultiGaussianDistribution[] { pdf_open, pdf_low, pdf_high }, new double[] { 0.34, 0.33, 0.33 });

		int M = 3; // mixtures
		int D = 3; // dimensions
		int N = 4; // states / observation vector size

		List<Opdf> opdfs = new ArrayList<Opdf>();
		opdfs.add(new OpdfMultiGaussianMixture<ObservationVector>(opdf_open));
		opdfs.add(new OpdfMultiGaussianMixture<ObservationVector>(opdf_low));
		opdfs.add(new OpdfMultiGaussianMixture<ObservationVector>(opdf_high));
		opdfs.add(new OpdfMultiGaussianMixture<ObservationVector>(opdf_close));

		return opdfs;

	}

	private Opdf aaa(List<FtseIndex> data) {

		FtseIndexList list = new FtseIndexList(data);

		double[] opens = list.getOpensArray();
		double[] lows = list.getLowsArray();
		double[] highs = list.getHighsArray();
		double[] closes = list.getClosesArray();

		RealVector mu = MatrixUtils.createRealVector(new double[] { StatUtils.mean(opens), StatUtils.mean(lows),
				StatUtils.mean(highs), StatUtils.mean(closes) });
		RealMatrix transposedDataMatrix = MatrixUtils.createRealMatrix(new double[][] { opens, lows, highs, closes });
		RealMatrix dataMatrix = transposedDataMatrix.transpose();
		Covariance cov = new Covariance(dataMatrix);

		MultiGaussianDistribution pdf1 = new MultiGaussianDistribution(mu.getData(), cov.getCovarianceMatrix()
				.getData());

		double[] proportions = new double[] { 0.25, 0.25, 0.25, 0.25 };
		MultiGaussianMixtureDistribution pdf2 = new MultiGaussianMixtureDistribution(new MultiGaussianDistribution[] {
				pdf1, pdf1, pdf1, pdf1 }, proportions);

		return new OpdfMultiGaussianMixture<ObservationVector>(pdf2);
	}

	private OpdfFactory aaa2(List<FtseIndex> data) {
		return new OpdfMultiGaussianMixtureFactory((OpdfMultiGaussianMixture) aaa(data));
	}

	private void logHmm(Hmm<ObservationVector> hmm, String filename) throws IOException {
		System.out.println("------------------------------");
		(new GenericHmmDrawerDot()).write(hmm, filename);
		System.out.println(hmm);
	}

}
