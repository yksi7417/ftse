/*
 *
 * Created on Dec 3, 2009 | 10:49:20 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.util.List;

import nl.liacs.dbdm.ftse.hmm.LeftRightHmm;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class BaseFtseIndexHmm extends LeftRightHmm<FtseIndex> {

	private static final long serialVersionUID = -2722611366010618919L;

	protected static final int FTSE_INDEX_NBSTATES = 4;
	protected static final int FTSE_INDEX_DELTA = 1;

	public BaseFtseIndexHmm() {
		super(FTSE_INDEX_NBSTATES, FTSE_INDEX_DELTA);
		initPi();
	}

	public BaseFtseIndexHmm(double[] pi, double[][] a, List<? extends Opdf<FtseIndex>> opdfs) {
		super(FTSE_INDEX_DELTA, pi, a, opdfs);
	}

	public BaseFtseIndexHmm(OpdfFactory<? extends Opdf<FtseIndex>> opdfFactory) {
		super(FTSE_INDEX_NBSTATES, FTSE_INDEX_DELTA, opdfFactory);
		initPi();
	}

	protected void initPi() {
		setPi(0, 1);
		for (int i = 1; i < nbStates(); ++i) {
			setPi(i, 0);
		}
	}

}
