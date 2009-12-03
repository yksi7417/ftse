/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import java.util.List;

import nl.liacs.dbdm.ftse.model.BaseFtseIndexHmm;
import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.OpdfMultiGaussianMixtureFactory;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
@RunWith(SpringJUnit4ClassRunner.class)
public class HmmTest {

	public void testHmm() throws Exception {
		Hmm<FtseIndex> hmm = buildHmm();
		BaumWelchLearner learner = new BaumWelchLearner();
		learner.learn(hmm, getSequences());
		// next we go to the prediction
	}

	private BaseFtseIndexHmm buildHmm() {

		List<FtseIndex> data = getData();

		double[][] openCovariances = computeOpenCovariances(data);
		double[] openMean = compuateOpenMean(data);
		MultiGaussianDistribution mgdOpen = new MultiGaussianDistribution(openMean, openCovariances);

		double[][] lowCovariances = computeLowCovariances(data);
		double[] lowMean = computeLowMean(data);
		MultiGaussianDistribution mgdLow = new MultiGaussianDistribution(lowMean, lowCovariances);

		double[][] highCovariances = computeHighCovariances(data);
		double[] highMean = computeHighMean(data);
		MultiGaussianDistribution mgdHigh = new MultiGaussianDistribution(highMean, highCovariances);

		OpdfFactory<Opdf<FtseIndex>> opdfFactory = new OpdfMultiGaussianMixtureFactory(new MultiGaussianDistribution[] {
				mgdOpen, mgdLow, mgdHigh }, new double[] { 0.34, 0.33, 0.33 });

		BaseFtseIndexHmm hmm = new BaseFtseIndexHmm(opdfFactory);
		return hmm;
	}

	private List<List<FtseIndex>> getSequences() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<FtseIndex> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	private double[] computeHighMean(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private double[][] computeHighCovariances(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private double[] computeLowMean(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private double[][] computeLowCovariances(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private double[] compuateOpenMean(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private double[][] computeOpenCovariances(List<FtseIndex> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
