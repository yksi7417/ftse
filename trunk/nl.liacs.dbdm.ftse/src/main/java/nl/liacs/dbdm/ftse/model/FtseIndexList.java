/*
 *
 * Created on Dec 4, 2009 | 12:48:47 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseIndexList extends ArrayList<FtseIndex> {

	private static final long serialVersionUID = -1982075197067693093L;

	private List<Double> opens;
	private List<Double> lows;
	private List<Double> highs;
	private List<Double> closes;

	public FtseIndexList() {
		opens = new ArrayList<Double>();
		lows = new ArrayList<Double>();
		highs = new ArrayList<Double>();
		closes = new ArrayList<Double>();
	}

	public FtseIndexList(List<FtseIndex> elements) {
		this();
		for (FtseIndex e : elements) {
			add(e);
		}
	}

	@Override
	public boolean add(FtseIndex e) {
		boolean added = super.add(e);
		if (added) {
			opens.add(e.getOpen());
			lows.add(e.getLow());
			highs.add(e.getHigh());
			closes.add(e.getClose());
		}
		return added;
	}

	@Override
	public void add(int index, FtseIndex element) {
		super.add(index, element);
		opens.add(index, element.getOpen());
		lows.add(index, element.getLow());
		highs.add(index, element.getHigh());
		closes.add(index, element.getClose());
	}

	@Override
	public FtseIndex remove(int index) {
		FtseIndex removed = super.remove(index);
		if (removed != null) {
			opens.remove(index);
			lows.remove(index);
			highs.remove(index);
			closes.remove(index);
		}
		return removed;
	}

	@Override
	public boolean remove(Object o) {
		int indexOf = indexOf(o);
		if (indexOf == -1) {
			return false;
		}
		remove(indexOf);
		return true;
	}

	@Override
	public void clear() {
		super.clear();
		opens.clear();
		lows.clear();
		highs.clear();
		closes.clear();
	}

	public double[] getOpensArray() {
		return convertToPrimitive(opens.toArray(new Double[] {}));
	}

	public double[] getLowsArray() {
		return convertToPrimitive(lows.toArray(new Double[] {}));
	}

	public double[] getHighsArray() {
		return convertToPrimitive(highs.toArray(new Double[] {}));
	}

	public double[] getClosesArray() {
		return convertToPrimitive(closes.toArray(new Double[] {}));
	}

	public List<Double> getOpensList() {
		return Collections.unmodifiableList(opens);
	}

	public List<Double> getLowsList() {
		return Collections.unmodifiableList(lows);
	}

	public List<Double> getHighsList() {
		return Collections.unmodifiableList(highs);
	}

	public List<Double> getClosesList() {
		return Collections.unmodifiableList(closes);
	}

	private double[] convertToPrimitive(Double[] wrapper) {
		double[] prim = new double[wrapper.length];
		for (int i = 0; i < prim.length; i++) {
			prim[i] = wrapper[i];
		}
		return prim;
	}

}
