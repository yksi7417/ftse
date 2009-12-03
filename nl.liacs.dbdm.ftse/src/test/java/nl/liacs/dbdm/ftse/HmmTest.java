/*
 *
 * Created on Dec 2, 2009 | 12:39:07 AM
 *
 */
package nl.liacs.dbdm.ftse;

import java.util.ArrayList;
import java.util.List;

import nl.liacs.dbdm.ftse.model.FtseIndex;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

/**
 * 
 *
 *
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
@RunWith(SpringJUnit4ClassRunner.class)
public class HmmTest {
	
	public void testHmm() throws Exception {
		BaumWelchLearner learner = new BaumWelchLearner();
		List<List<FtseIndex>> sequences = new ArrayList<List<FtseIndex>>();
		OpdfFactory<Opdf<FtseIndex>> opdfFactory = null;
		Hmm<FtseIndex> hmm = new Hmm<FtseIndex>(4, opdfFactory);
		learner.learn(hmm, sequences);
	}

}
