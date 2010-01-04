/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import nl.liacs.dbdm.ftse.distribution.MultiGaussianMixtureDistribution;

import org.apache.commons.math.linear.MatrixUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

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
public class TestDummy extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testDummy() throws Exception {
		MultiGaussianDistribution[] mgds = new MultiGaussianDistribution[3];
		for (int i = 0; i < mgds.length; i++) {
			mgds[i] = new MultiGaussianDistribution(new double[] { 5000, 5900, 5000, 5500 }, MatrixUtils
					.createRealMatrix(4, 4).scalarAdd(0.5).getData());
		}
		MultiGaussianMixtureDistribution mgmd = new MultiGaussianMixtureDistribution(mgds, new double[] { 1. / 3,
				1. / 3, 1. / 3 });
		ObservationVector o = new ObservationVector(new double[] { 5019.1, 6321.1, 4001, 5987.3 });
		double p = mgmd.probability(o.values());
		logger.warn("p=" + p);
		for (int i = 0; i < mgds.length; i++) {
			logger.warn("p[" + i + "]=" + mgds[i].probability(o.values()));
		}
	}

}
