/*
 *
 * Created on Dec 27, 2009 | 11:03:08 AM
 *
 */
package nl.liacs.dbdm.ftse.learn;

import java.util.Arrays;

import nl.liacs.dbdm.ftse.distribution.MultiGaussianMixtureDistribution;
import nl.liacs.dbdm.ftse.distribution.OpdfMultiGaussianMixture;

import org.apache.commons.math.linear.MatrixUtils;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class DistributionProvider {

	MultiGaussianDistribution createMultiGaussianDistribution(double mean, double covariance, int dim) {
		double[] means = new double[dim];
		Arrays.fill(means, mean);
		double[][] covs = MatrixUtils.createRealIdentityMatrix(dim).scalarMultiply(covariance).getData();
		return new MultiGaussianDistribution(means, covs);
	}

	MultiGaussianMixtureDistribution createMultiGaussianMixtureDistribution(double mean, double covariance, int dim,
			int mixtures, double[] mixtureProps) {

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

	OpdfMultiGaussianMixture<ObservationVector> createOpdfMultiGaussianMixture(double mean, double covariance, int dim,
			int mixtures, double[] mixtureProps) {
		return new OpdfMultiGaussianMixture<ObservationVector>(createMultiGaussianMixtureDistribution(mean, covariance,
				dim, mixtures, mixtureProps));
	}

}
