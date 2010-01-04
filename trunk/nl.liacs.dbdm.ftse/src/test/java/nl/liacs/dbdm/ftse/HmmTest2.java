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
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import nl.liacs.dbdm.ftse.data.jdbc.FtseJdbcManager;
import nl.liacs.dbdm.ftse.hmm.CsvObservationVectorWriter;
import nl.liacs.dbdm.ftse.hmm.LeftRightHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;
import nl.liacs.dbdm.ftse.utils.HmmUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;
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
public class HmmTest2 extends AbstractTransactionalJUnit4SpringContextTests {

	private static final String FROM1 = "1999-12-01";
	private static final String TO1 = "2000-01-15";
	private static final String FROM2 = TO1;
	private static final String TO2 = "2000-03-01";
	private static final String FROM3 = TO2;
	private static final String TO3 = "2000-04-15";
	private static final String FROM4 = TO3;
	private static final String TO4 = "2000-06-01";

	/**
	 * N: number of states
	 */
	private int N = 4;

	/**
	 * D: dimension used for multi-variate gaussian distributions
	 */
	private int D = 4;

	/**
	 * M: the number of mixtures in a multi-variate gaussian mixture
	 * distribution
	 */
	private int M = 4;

	/**
	 * Delta: the delta used for a left-right HMM.
	 */
	private int delta = 1;

	private List<FtseIndex> data1, data2, data3, data4;

	@Resource
	protected FtseJdbcManager ftseJdbcManager;

	@Test
	@Transactional
	public void testHmmBWL() throws Exception {
		initData();
		Hmm<ObservationVector> hmm = (Hmm<ObservationVector>) initHmm();
		BaumWelchLearner learner = new BaumWelchLearner();
		List<List<ObservationVector>> sequences = getSequences(hmm);
		logHmm(hmm, "before2.dot");
		Hmm<ObservationVector> learnt = learner.learn(hmm, sequences);
		logHmm(learnt, "after2.dot");

		// generate some sequences using the learnt hmm
		MarkovGenerator<ObservationVector> mg = new MarkovGenerator<ObservationVector>(learnt);
		List<List<ObservationVector>> estimateSeq = new ArrayList<List<ObservationVector>>();
		List<ObservationVector> sample = mg.observationSequence(600);
		Collections.reverse(sample);
		estimateSeq.add(sample);
		ObservationSequencesWriter.write(new BufferedWriter(new FileWriter("estimates2.csv")),
				new CsvObservationVectorWriter(), estimateSeq);

		// next we go to the prediction
	}

	private Hmm<?> initHmm() {

		int sequenceSize = data1.size() + data2.size() + data3.size() + data4.size();

		OpdfMultiGaussian opdf1 = createOpdfMultiGaussian(0, 1, D);
		OpdfMultiGaussian opdf2 = createOpdfMultiGaussian(0, 1, D);
		OpdfMultiGaussian opdf3 = createOpdfMultiGaussian(0, 1, D);
		OpdfMultiGaussian opdf4 = createOpdfMultiGaussian(0, 1, D);

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
		hmm.setPis(new double[] { 1., 0., 0., 0. });

		return hmm;
	}

	private OpdfMultiGaussian createOpdfMultiGaussian(double mean, double covariance, int dim) {
		double[] means = new double[dim];
		Arrays.fill(means, mean);
		double[][] covs = new double[dim][dim];
		for (int i = 0; i < covs.length; i++) {
			for (int j = 0; j < covs.length; j++) {
				covs[i][j] = covariance;
			}
		}
		return new OpdfMultiGaussian(means, covs);
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

	private void logHmm(Hmm<?> hmm, String filename) throws IOException {
		System.out.println("------------------------------");
		(new GenericHmmDrawerDot()).write(hmm, filename);
		System.out.println(hmm);
	}

}
