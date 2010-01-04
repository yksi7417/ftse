/*
 *
 * Created on Dec 4, 2009 | 12:21:57 AM
 *
 */
package nl.liacs.dbdm.ftse.distribution;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class OpdfMultiGaussianMixtureFactory<O extends ObservationVector> implements
		OpdfFactory<OpdfMultiGaussianMixture<O>> {

	private MultiGaussianMixtureDistribution distribution;
	private OpdfMultiGaussianMixture opdf;

	public OpdfMultiGaussianMixtureFactory(int dimension, int number) {
		double[] proportions = new double[number];
		MultiGaussianDistribution[] dists = new MultiGaussianDistribution[number];
		for (int i = 0; i < number; i++) {
			proportions[i] = 1. / number;
			dists[i] = new MultiGaussianDistribution(dimension);
		}
		distribution = new MultiGaussianMixtureDistribution(dists, proportions);
	}

	public OpdfMultiGaussianMixtureFactory(MultiGaussianDistribution[] distributions, double[] proportions) {
		this.distribution = new MultiGaussianMixtureDistribution(distributions, proportions);
	}

	public OpdfMultiGaussianMixtureFactory(OpdfMultiGaussianMixture opdf) {
		this.opdf = opdf;
	}

	@Override
	public OpdfMultiGaussianMixture<O> factor() {
		if (distribution != null)
			return new OpdfMultiGaussianMixture<O>(distribution.clone());
		return (OpdfMultiGaussianMixture<O>) opdf.clone();
	}

}
