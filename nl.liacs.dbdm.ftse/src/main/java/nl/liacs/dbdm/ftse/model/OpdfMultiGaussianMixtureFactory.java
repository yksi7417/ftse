/*
 *
 * Created on Dec 4, 2009 | 12:21:57 AM
 *
 */
package nl.liacs.dbdm.ftse.model;

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

	public OpdfMultiGaussianMixtureFactory(MultiGaussianDistribution[] distributions, double[] proportions) {
		this.distribution = new MultiGaussianMixtureDistribution(distributions, proportions);
	}

	@Override
	public OpdfMultiGaussianMixture<O> factor() {
		return new OpdfMultiGaussianMixture<O>(distribution);
	}

}
