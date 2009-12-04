/*
 *
 * Created on Dec 3, 2009 | 11:21:26 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

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

	protected MultiGaussianDistribution[] distributions;
	protected double[][] proportions;

	/**
	 * 
	 * @param mgds
	 * @param proportions
	 *            We assume that all rows use the same proportions.
	 */
	public MultiGaussianMixtureDistribution(MultiGaussianDistribution[] mgds, double[] proportions) {
		distributions = mgds;
		this.proportions = new double[distributions.length][proportions.length];

		// if the sum(proportions) != 0 then scale them through [0, 1]
		double sum = 0.;
		for (int i = 0; i < proportions.length; ++i) {
			sum += proportions[i];
		}
		for (int i = 0; i < distributions.length; ++i) {
			for (int j = 0; j < proportions.length; ++j) {
				this.proportions[i][j] = proportions[i] / sum;
			}
		}
	}

	public int nbGaussians() {
		return distributions.length;
	}

	public MultiGaussianDistribution[] distributions() {
		return distributions.clone();
	}

	public double[][] proportions() {
		return proportions.clone();
	}

	@Override
	public int dimension() {
		return distributions[0].dimension();
	}

	public double[] generate() {
		double r = RANDOM.nextDouble();
		for (int i = 0; i < proportions.length; ++i) {
			double sum = 0.;
			for (int j = 0; j < distributions.length; ++j) {
				sum += proportions[j][i];
				if (r <= sum) {
					return distributions[i].generate();
				}

			}
		}
		throw new RuntimeException("Internal Error");
	}

	public double probability(double[] v) {
		double sum = 0.;
		for (int i = 0; i < distributions.length; ++i) {
			sum += distributions[i].probability(v);
		}
		return sum;
	}
}
