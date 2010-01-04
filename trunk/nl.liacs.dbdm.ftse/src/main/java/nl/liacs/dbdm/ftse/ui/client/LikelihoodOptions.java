/*
 *
 * Created on Jan 3, 2010 | 7:35:38 PM
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
public class LikelihoodOptions implements Serializable {

	private static final long serialVersionUID = -2945126521134550839L;

	private Date startDate;
	private Integer days = 100;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

}
