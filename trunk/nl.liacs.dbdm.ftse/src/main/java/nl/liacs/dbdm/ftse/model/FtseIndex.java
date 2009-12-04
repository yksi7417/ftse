/*
 *
 * Created on Nov 30, 2009 | 12:02:29 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.io.Serializable;
import java.util.Date;

import nl.liacs.dbdm.ftse.data.jdbc.FtseUtils;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseIndex extends ObservationVector implements Serializable, Comparable<FtseIndex> {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date date;
	private Double open;
	private Double low;
	private Double high;
	private Double close;
	private Double volume = 0.;
	private Double adjClose = 0.;

	public FtseIndex() {
		super(4);
	}

	public FtseIndex(Long id) {
		this();
		this.id = id;
	}

	public FtseIndex(Date date, Double open, Double low, Double high, Double close) {
		this(new double[] { open, low, high, close });
		setDate(date);
	}

	public FtseIndex(double[] value) {
		super(value);
		setDate(null);
		setOpen(value[0]);
		setLow(value[1]);
		setHigh(value[2]);
		setClose(value[3]);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "[FTSE on " + date + ":(" + open + ", " + low + ", " + high + ", " + close + ")]";
	}

	@Override
	public int compareTo(FtseIndex o) {
		if (id != null && o.id != null) {
			return id.compareTo(o.id);
		}
		if (date != null && o.date != null) {
			return date.compareTo(o.date);
		}
		if (open != null && o.open != null) {
			return open.compareTo(o.open);
		}
		if (low != null && o.low != null) {
			return low.compareTo(o.low);
		}
		if (high != null && o.high != null) {
			return high.compareTo(o.high);
		}
		if (close != null && o.close != null) {
			return close.compareTo(o.close);
		}
		return 0;
	}

	@Override
	public int hashCode() {
		if (id == null) {
			return Integer.MIN_VALUE;
		}
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FtseIndex)) {
			return false;
		}
		FtseIndex o = (FtseIndex) obj;
		return id.equals(o.id) || date.equals(o.date);
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		if (date == null) {
			return "null";
		}
		return FtseUtils.getMySqlDateTime(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(Double adjClose) {
		this.adjClose = adjClose;
	}

}
