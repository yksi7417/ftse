/*
 *
 * Created on Jan 3, 2010 | 6:11:44 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class TrainingOptions implements Serializable {

	private static final long serialVersionUID = 6322189261953270040L;

	private Date startDate;
	private Integer numberOfIterations = 20;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(Integer numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

}
