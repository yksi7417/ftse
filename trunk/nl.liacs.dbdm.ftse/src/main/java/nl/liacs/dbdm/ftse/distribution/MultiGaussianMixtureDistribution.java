/*
 *
 * Created on Dec 3, 2009 | 11:21:26 PM
 *
 */
package nl.liacs.dbdm.ftse.distribution;

import java.util.Random;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.distributions.MultiRandomDistribution;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class MultiGaussianMixtureDistribution implements MultiRandomDistribution {

	private static final long serialVersionUID = -1484990992176542812L;

	private static final Random RANDOM = new Random();

	protected MultiGaussianDistribution[] components;
	protected double[] mixtures;

	/**
	 * 
	 * @param mgds
	 * @param proportions
	 *            We assume that all components use the same proportions.
	 */
	public MultiGaussianMixtureDistribution(MultiGaussianDistribution[] mgds, double[] proportions) {
		components = mgds.clone();
		this.mixtures = proportions.clone();

		// if the sum(proportions) != 1 then scale them through [0, 1]
		double sum = 0.;
		for (int i = 0; i < proportions.length; ++i) {
			sum += proportions[i];
		}
		for (int i = 0; i < proportions.length; ++i) {
			this.mixtures[i] = proportions[i] / sum;
		}
	}

	public int nbGaussians() {
		return components.length;
	}

	public MultiGaussianDistribution[] distributions() {
		return components.clone();
	}

	public double[] proportions() {
		return mixtures.clone();
	}

	@Override
	public int dimension() {
		return components[0].dimension();
	}

	public double[] generate() {
		double r = RANDOM.nextDouble();
		double sum = 0.;
		for (int i = 0; i < mixtures.length; ++i) {
			sum += mixtures[i];
			if (r <= sum) {
				return components[i].generate();
			}
		}
		throw new RuntimeException("Internal Error");
	}

	public double probability(double[] v) {
		double sum = 0.;
		for (int i = 0; i < components.length; ++i) {
			sum += components[i].probability(v) * mixtures[i];
		}
		return sum;
	}

	@Override
	public MultiGaussianMixtureDistribution clone() {
		return new MultiGaussianMixtureDistribution(components, mixtures);
	}
}
