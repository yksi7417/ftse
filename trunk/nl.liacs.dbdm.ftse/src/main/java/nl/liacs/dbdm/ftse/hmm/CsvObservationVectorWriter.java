/*
 *
 * Created on Dec 23, 2009 | 11:52:26 AM
 *
 */
package nl.liacs.dbdm.ftse.hmm;

import java.io.IOException;
import java.io.Writer;

import nl.liacs.dbdm.ftse.utils.HmmUtils;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorWriter;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class CsvObservationVectorWriter extends ObservationVectorWriter {

	@Override
	public void write(ObservationVector observation, Writer writer) throws IOException {
		writer.write(HmmUtils.getCsvFormat(observation) + "\n");
	}

}
