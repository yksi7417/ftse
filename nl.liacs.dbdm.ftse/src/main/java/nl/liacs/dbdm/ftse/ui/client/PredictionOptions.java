/*
 *
 * Created on Jan 3, 2010 | 6:45:52 PM
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
public class PredictionOptions implements Serializable {

	private static final long serialVersionUID = -2569124284950873226L;

	private Date date;
	private Integer days = 100;
	private Double tolerance = 0.01;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Double getTolerance() {
		return tolerance;
	}

	public void setTolerance(Double tolerance) {
		this.tolerance = tolerance;
	}

}
