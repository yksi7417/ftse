/*
 *
 * Created on Nov 30, 2009 | 12:29:08 PM
 *
 */
package nl.liacs.dbdm.ftse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nl.liacs.dbdm.ftse.model.FtseIndex;

import org.springframework.util.StringUtils;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseUtils {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static FtseIndex extract(String line) {
		FtseIndex ftse = new FtseIndex();
		String[] elements = StringUtils.commaDelimitedListToStringArray(line);
		if (Character.isLetter(elements[0].charAt(0))) {
			return null;
		}
		try {
			ftse.setDate(dateFormat.parse(elements[0]));
			ftse.setOpen(Double.parseDouble(elements[1]));
			ftse.setLow(Double.parseDouble(elements[2]));
			ftse.setHigh(Double.parseDouble(elements[3]));
			ftse.setClose(Double.parseDouble(elements[4]));
			return ftse;
		} catch (ParseException e) {
		}
		return null;
	}

	public static String getMySqlDateTimeString(Date d) {
		return getMySqlDateString(d) + " 00:00:00";
	}

	public static String getMySqlDateString(Date d) {
		return mysqlDateFormat.format(d);
	}

	public static Date getDate(String dateString) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
		}
		return null;
	}

	public static Date getDateDaysBefore(String today, int interval) {
		Calendar c = Calendar.getInstance();
		Date date = getDate(today);
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -(interval % 30));
		c.add(Calendar.MONTH, -(interval / 30));
		return c.getTime();
	}

	public static String getDateDaysBeforeString(String today, int interval) {
		return getMySqlDateString(getDateDaysBefore(today, interval));
	}

	public static Date getDateDaysAfter(Date date, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, +(interval % 30));
		c.add(Calendar.MONTH, +(interval / 30));
		return c.getTime();
	}

	public static Date getYesterday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	public static Date getTomorrrow(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, +1);
		return c.getTime();
	}

}
