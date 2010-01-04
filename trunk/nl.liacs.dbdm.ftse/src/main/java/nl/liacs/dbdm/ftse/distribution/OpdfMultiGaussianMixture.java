/*
 *
 * Created on Dec 4, 2009 | 12:10:09 AM
 *
 */
package nl.liacs.dbdm.ftse.distribution;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.correlation.Covariance;

import be.ac.ulg.montefiore.run.distributions.MultiGaussianDistribution;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;

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

	public double probability(ObservationVector o) {
		return distribution.probability(o.values());
	}

	@Override
	public O generate() {
		return (O) new ObservationVector(distribution.generate());
	}

	public int nbGaussians() {
		return distribution.nbGaussians();
	}

	public double[] propportions() {
		return distribution.proportions();
	}

	public MultiGaussianMixtureDistribution getDistribution() {
		return distribution;
	}

	@Override
	public void fit(Collection<? extends O> co) {
		double[] weights = new double[co.size()];
		Arrays.fill(weights, 1. / co.size());
		fit(co, weights);
	}

	@Override
	public void fit(Collection<? extends O> co, double[] weights) {
		ObservationVector[] o = co.toArray(new ObservationVector[co.size()]);
		RealMatrix obs = convertToMatrix(o);
		int mixtures = distribution.nbGaussians();
		double[][] delta = computeDelta(o);
		double[] newProportions = computeNewMixingProportions(delta, o, weights);

		RealMatrix deltaMatrix = MatrixUtils.createRealMatrix(delta);
		RealVector weightsVector = MatrixUtils.createRealVector(weights);

		MultiGaussianDistribution[] mgds = new MultiGaussianDistribution[distribution.nbGaussians()];
		MultiGaussianDistribution[] distributions = distribution.distributions();
		for (int mix = 0; mix < mixtures; mix++) {
			// double[] newMeans = computeNewMeans(deltaMatrix, obs,
			// weightsVector, dim);
			// double[][] newCovariances = computeNewCovariances(deltaMatrix,
			// obs, weightsVector, dim);
			// mgds[dim] = new MultiGaussianDistribution(newMeans,
			// newCovariances);
			OpdfMultiGaussian tempOpdf = new OpdfMultiGaussian(distributions[mix].mean(), distributions[mix]
					.covariance());
			tempOpdf.fit(co, weights);
			mgds[mix] = new MultiGaussianDistribution(tempOpdf.mean(), tempOpdf.covariance());
		}
		distribution = new MultiGaussianMixtureDistribution(mgds, newProportions);
	}

	private RealMatrix convertToMatrix(ObservationVector[] o) {
		int dimension = o[0].dimension();
		RealMatrix m = MatrixUtils.createRealMatrix(o.length, dimension);
		for (int i = 0; i < o.length; i++) {
			for (int j = 0; j < dimension; j++) {
				m.setEntry(i, j, o[i].value(j));
			}
		}
		return m;
	}

	private double[][] computeDelta(ObservationVector[] o) {
		double[][] delta = new double[distribution.nbGaussians()][o.length];
		MultiGaussianDistribution[] distributions = distribution.distributions();
		double[] proportions = distribution.proportions();
		for (int i = 0; i < distribution.nbGaussians(); ++i) {
			for (int t = 0; t < o.length; ++t) {
				double d1 = distributions[i].probability(o[t].values());
				double d2 = probability(o[t]);
				if (d1 == 0.0 || d2 == 0.0 || proportions[i] == 0.0) {
					delta[i][t] = 0.0;
				} else {
					delta[i][t] = proportions[i] * d1 / d2;
				}
			}
		}
		return delta;
	}

	private double[] computeNewMixingProportions(double[][] delta, ObservationVector[] o, double[] weights) {
		double[] proprotions = distribution.proportions().clone();
		double sum = 0.;
		for (int i = 0; i < distribution.nbGaussians(); ++i) {
			for (int t = 0; t < weights.length; ++t) {
				double weightedDelta = weights[t] * delta[i][t];
				proprotions[i] += weightedDelta;
				sum += weightedDelta;
			}
		}
		if (sum != 0.0) {
			for (int i = 0; i < proprotions.length; ++i) {
				proprotions[i] = proprotions[i] / sum;
			}
		}
		return proprotions;
	}

	private double[] computeNewMeans(RealMatrix delta, RealMatrix obs, RealVector weights, int oDimension) {
		RealMatrix tDelta = delta.transpose();
		double[] mean = new double[distribution.dimension()];
		int index = 0;
		for (int i = 0; i < distribution.dimension(); i++) {
			RealVector obsCol = obs.getColumnVector(i);
			RealVector weightedDelta = tDelta.getColumnVector(i).ebeMultiply(weights);
			mean[index++] = StatUtils.mean(obsCol.ebeMultiply(weightedDelta).getData());
		}
		return mean;
	}

	private double[][] computeNewCovariances(RealMatrix delta, RealMatrix obs, RealVector weights, int oDimension) {
		obs = obs.copy();
		for (int i = 0; i < obs.getRowDimension(); i++) {
			for (int j = 0; j < obs.getColumnDimension(); j++) {
				obs.setEntry(i, j, obs.getEntry(i, j) * weights.getEntry(j) * delta.getEntry(j, i));
			}
		}
		RealMatrix subObs = obs.getSubMatrix(computeSelectorIndices(0, obs.getRowDimension()), computeSelectorIndices(
				obs, oDimension));
		return new Covariance(obs).getCovarianceMatrix().getData();
	}

	private int[] computeSelectorIndices(RealMatrix rm, int col) {
		int[] selectors = new int[rm.getColumnDimension() - 1];
		int j = 0;
		for (int i = 0; i < rm.getColumnDimension(); i++) {
			if (i == col) {
				continue;
			}
			selectors[j++] = i;
		}
		return selectors;
	}

	private int[] computeSelectorIndices(int start, int end) {
		int[] selectors = new int[end - start];
		for (int i = 0; i < selectors.length; i++) {
			selectors[i] = i;
		}
		return selectors;
	}

	@Override
	public void fit(O[] o, double[] weights) {
		fit(Arrays.asList(o), weights);
	}

	@Override
	public void fit(O... oa) {
		fit(Arrays.asList(oa));
	}

	@Override
	public Opdf<O> clone() {
		return new OpdfMultiGaussianMixture<O>(distribution.clone());
	}

	// TODO
	@Override
	public String toString(NumberFormat numberFormat) {
		return toString();
	}

}
