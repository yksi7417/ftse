/*
 *
 * Created on Dec 3, 2009 | 6:21:47 PM
 *
 */
package nl.liacs.dbdm.ftse.hmm;

import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;

/**
 * 
 * Also known as Baskin HMM. The rules of transition in a left right HMM is:
 * <ol>
 * <li><code>a(i, i) = 0</code></li>
 * <li><code>a(i, i) = 0</code> if <code>i < j</code></li>
 * <li><code>a(i, j) = 0</code> if <code>i > j + DELTA</code></li>
 * </ol>
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class LeftRightHmm<O extends Observation> extends Hmm<O> {

	private static final long serialVersionUID = -7558110664963322390L;

	protected Integer delta;

	public LeftRightHmm(int delta, double[] pi, double[][] a, List<? extends Opdf<O>> opdfs) {
		super(pi, a, opdfs);
		this.delta = delta;
		refineTrnsitionRules();
	}

	public LeftRightHmm(int nbStates, int delta, OpdfFactory<? extends Opdf<O>> opdfFactory) {
		super(nbStates, opdfFactory);
		this.delta = delta;
	}

	public LeftRightHmm(int nbStates, int delta) {
		super(nbStates);
		this.delta = delta;
	}

	public Integer getDelta() {
		return delta;
	}

	@Override
	public void setAij(int i, int j, double value) {
		if (i == j) {
			super.setAij(i, j, 0);
			return;
		}
		if (i < j) {
			super.setAij(i, j, 0);
			return;
		}
		if (i > j + delta) {
			super.setAij(i, j, 0);
			return;
		}
		super.setAij(i, j, value);
	}

	protected void refineTrnsitionRules() {
		for (int i = 0; i < nbStates(); ++i) {
			for (int j = 0; j < nbStates(); ++j) {
				setAij(i, j, getAij(i, j));
			}
		}
	}

}
