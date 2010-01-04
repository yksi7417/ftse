/*
 *
 * Created on Dec 22, 2009 | 3:10:10 PM
 *
 */
package nl.liacs.dbdm.ftse.distribution;

import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussian;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class ExtendedOpdfMultiGaussianFactory extends OpdfMultiGaussianFactory {

	private double[] mean;
	private double[][] covariance;

	public ExtendedOpdfMultiGaussianFactory(double[] mean, double[][] covariance) {
		super(0);
		this.mean = mean;
		this.covariance = covariance;
	}

	@Override
	public OpdfMultiGaussian factor() {
		return new OpdfMultiGaussian(mean, covariance);
	}

}
