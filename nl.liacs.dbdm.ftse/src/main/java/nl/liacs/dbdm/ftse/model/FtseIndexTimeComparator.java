/*
 *
 * Created on Dec 4, 2009 | 3:38:57 PM
 *
 */
package nl.liacs.dbdm.ftse.model;

import java.util.Comparator;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseIndexTimeComparator implements Comparator<FtseIndex> {

	public static final FtseIndexTimeComparator INSTANCE = new FtseIndexTimeComparator();

	@Override
	public int compare(FtseIndex o1, FtseIndex o2) {
		if (o2 == null || o1 == null) {
			return 0;
		}
		if (o1.getDate() == null || o2.getDate() == null) {
			return 0;
		}
		return o1.getDate().compareTo(o2.getDate());
	}

}
