/*
 *
 * Created on Nov 30, 2009 | 12:02:29 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.io.Serializable;
import java.util.Date;

import nl.liacs.dbdm.ftse.data.jdbc.FtseUtils;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseIndex implements Serializable, Comparable<FtseIndex> {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date date;
	private Float open;
	private Float low;
	private Float high;
	private Float close;
	private Float volume = 0f;
	private Float adjClose = 0f;

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

	public Float getOpen() {
		return open;
	}

	public void setOpen(Float open) {
		this.open = open;
	}

	public Float getLow() {
		return low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getClose() {
		return close;
	}

	public void setClose(Float close) {
		this.close = close;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Float getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(Float adjClose) {
		this.adjClose = adjClose;
	}

}
