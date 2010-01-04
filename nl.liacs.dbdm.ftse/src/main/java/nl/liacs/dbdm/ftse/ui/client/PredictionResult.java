/*
 *
 * Created on Jan 3, 2010 | 7:06:15 PM
 *
 */
package nl.liacs.dbdm.ftse.ui.client;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class PredictionResult implements Serializable {

	private static final long serialVersionUID = -228112848880143120L;

	private Map<Date, List<Double>> predictions;
	private Date startDate;
	private Date endDate;
	private Double mape;

	public Map<Date, List<Double>> getPredictions() {
		return predictions;
	}

	public void setPredictions(Map<Date, List<Double>> predictions) {
		this.predictions = predictions;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Double getMape() {
		return mape;
	}

	public void setMape(Double mape) {
		this.mape = mape;
	}

}
