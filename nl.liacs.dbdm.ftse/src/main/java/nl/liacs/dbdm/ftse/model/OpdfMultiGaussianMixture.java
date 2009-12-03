/*
 *
 * Created on Dec 4, 2009 | 12:10:09 AM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.text.NumberFormat;
import java.util.Collection;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class OpdfMultiGaussianMixture<O extends ObservationVector> implements Opdf<O> {

	private static final long serialVersionUID = 4057179251746887056L;

	private MultiGaussianMixtureDistribution distribution;

	public OpdfMultiGaussianMixture(MultiGaussianMixtureDistribution distribution) {
		this.distribution = distribution;
	}

	public double probability(O o) {
		return distribution.probability(o.values());
	}

	@Override
	public O generate() {
		return (O) new ObservationVector(distribution.generate());
	}

	public int nbGaussians() {
		return distribution.nbGaussians();
	}

	public double[][] propportions() {
		return distribution.proportions();
	}

	// TODO
	@Override
	public void fit(Collection<? extends O> co) {
	}

	// TODO
	@Override
	public void fit(Collection<? extends O> co, double[] weights) {
	}

	// TODO
	@Override
	public void fit(O[] o, double[] weights) {
	}

	// TODO
	@Override
	public void fit(O... oa) {
	}

	// TODO
	@Override
	public Opdf<O> clone() {
		return this;
	}

	// TODO
	@Override
	public String toString(NumberFormat numberFormat) {
		return toString();
	}

}
