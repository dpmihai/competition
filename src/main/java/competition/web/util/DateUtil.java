package competition.web.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {

	public static final int EASTER_ORTHODOX = 1;
	public static final int EASTER_CATHOLIC = 2;

	/**
	 * Compares dates <code>d1</code> and <code>d2</code> taking into
	 * consideration only the year, month and day
	 * 
	 * @param d1
	 *            the first date
	 * @param d2
	 *            the second date
	 * @return <code>true</code> if <code>d1</code> is after <code>d2</code>,
	 *         <code>false</code> otherwise
	 * @see java.util.Calendar#after(java.lang.Object)
	 * @see #before(java.util.Date , java.util.Date)
	 * @see #compare(java.util.Date , java.util.Date)
	 */
	public static boolean after(Date d1, Date d2) {
		d1 = floor(d1);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		d2 = floor(d2);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		return c1.after(c2);
	}

	/**
	 * Compares dates <code>d1</code> and <code>d2</code> taking into
	 * consideration only the year, month and day
	 * 
	 * @param d1
	 *            the first date
	 * @param d2
	 *            the second date
	 * @return <code>true</code> if <code>d1</code> is before <code>d2</code>,
	 *         <code>false</code> otherwise
	 * @see Calendar#before(java.lang.Object)
	 * @see #after(Date, Date)
	 * @see #compare(Date, Date)
	 */
	public static boolean before(Date d1, Date d2) {
		d1 = floor(d1);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		d2 = floor(d2);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		return c1.before(c2);
	}

	/**
	 * Compares two dates taking into consideration only the year, month and day
	 * 
	 * @param d1
	 *            the first date
	 * @param d2
	 *            the second date
	 * @return a negative integer, zero, or a positive integer as
	 *         <code>d1</code> is less than, equal to, or greater than
	 *         <code>d2</code>
	 * @see java.util.Comparator
	 * @see #after(Date, Date)
	 * @see #before(Date, Date)
	 */
	public static int compare(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
			if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
				return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
			}
			return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		}
		return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
	}

	public static boolean isWeekendDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		return (weekDay == Calendar.SATURDAY) || (weekDay == Calendar.SUNDAY);
	}

	public static boolean isRomanianHoliday(Date date) {
		int year = getYear(date);
		for (Date holiday : getRomanianHolidays(year)) {
			if (getDayOfYear(date) == getDayOfYear(holiday)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFriday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		return (weekDay == Calendar.FRIDAY);
	}

	public static Date resetTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getDate(int year, int month, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		;
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Compute the day of the year that Easter falls on. Step names E1 E2 etc.,
	 * are direct references to Knuth, Vol 1, p 155.
	 * 
	 * @Note: It's not proven correct, although it gets the right answer for
	 *        years around the present.
	 * 
	 * @Note: Orthodox Easter is then EasterDay of EasterMonth in the Julian
	 *        Calendar. You will need to add the correct offset to obtain the
	 *        date in the Gregorian Calendar. From Julian Mar 1, 1900, to Julian
	 *        Feb 29, 2100, the correction is to add 13 days to the Julian date
	 *        to obtain the Gregorian date
	 * 
	 *        http://www.smart.net/~mmontes/ortheast.html
	 *        http://www.assa.org.au/edm.html#List20
	 * 
	 * @param year
	 *            year
	 * @param easterType
	 *            catholic or orthodox
	 * @return easter day
	 * 
	 * @exception IllegalArgumentexception
	 *                If the year is before 1582 (since the algorithm only works
	 *                on the Gregorian calendar).
	 */
	public static final Calendar getEasterDay(int year, int easterType) {

		if (easterType == EASTER_CATHOLIC) {
			if (year <= 1582) {
				throw new IllegalArgumentException("Algorithm invalid before April 1583");
			}
			int golden, century, x, z, d, epact, n;

			golden = (year % 19) + 1; /* E1: metonic cycle */
			century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
			x = (3 * century / 4) - 12; /* E3: leap year correction */
			z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
			d = (5 * year / 4) - x - 10;
			epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
			if ((epact == 25 && golden > 11) || epact == 24) {
				epact++;
			}
			n = 44 - epact;
			n += 30 * (n < 21 ? 1 : 0); /* E6: */
			n += 7 - ((d + n) % 7);
			if (n > 31) {
				/* E7: */
				return new GregorianCalendar(year, 4 - 1, n - 31); /* April */
			} else {
				return new GregorianCalendar(year, 3 - 1, n); /* March */
			}
		} else {

			// G is the Golden Number-1
			// I is the number of days from 21 March to the Paschal full moon
			// J is the weekday for the Paschal full moon (0=Sunday, 1=Monday,
			// etc.)
			// L is the number of days from 21 March to the Sunday on or before
			// the Pascal full moon (a number between -6 and 28)
			int G = year % 19;
			int I = (19 * G + 15) % 30;
			int J = (year + year / 4 + I) % 7;
			int L = I - J;
			int EasterMonth = 3 + (L + 40) / 44;
			int EasterDay = L + 28 - 31 * (EasterMonth / 4);
			GregorianCalendar cal = new GregorianCalendar(year, EasterMonth - 1, EasterDay);
			cal.add(Calendar.DAY_OF_YEAR, 13); // see note for orthodox easter
			return cal;
		}
	}

	public static final Calendar getSecondEasterDay(int year, int easterType) {
		Calendar cal = getEasterDay(year, easterType);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal;
	}

	/**
	 * Get the seventh Sunday after Easter (rusalii)
	 * 
	 * @param year
	 *            year
	 * @param easterType
	 *            catholic or orthodox
	 * @return seventh sunday after easter
	 */
	public static final Calendar getWhitsun(int year, int easterType) {
		Calendar cal = getEasterDay(year, easterType);
		cal.add(Calendar.DAY_OF_YEAR, 49);
		return cal;
	}

	public static final Calendar getDayAfterWhitsun(int year, int easterType) {
		Calendar cal = getWhitsun(year, easterType);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal;
	}

	/**
	 * Get romanian holidays
	 * 
	 * @param year
	 * @return romanian holidays
	 */
	public static List<Date> getRomanianHolidays(int year) {
		List<Date> result = new ArrayList<Date>();
		result.add(getDate(year, Calendar.JANUARY, 1));
		result.add(getDate(year, Calendar.JANUARY, 2));

		result.add(getEasterDay(year, EASTER_ORTHODOX).getTime());
		result.add(getSecondEasterDay(year, EASTER_ORTHODOX).getTime());

		result.add(getDate(year, Calendar.MAY, 1));

		result.add(getWhitsun(year, EASTER_ORTHODOX).getTime());
		result.add(getDayAfterWhitsun(year, EASTER_ORTHODOX).getTime());

		result.add(getDate(year, Calendar.AUGUST, 15));

		result.add(getDate(year, Calendar.DECEMBER, 1));

		result.add(getDate(year, Calendar.DECEMBER, 25));
		result.add(getDate(year, Calendar.DECEMBER, 26));

		return result;
	}

	/**
	 * Get the year of the date
	 * 
	 * @param date
	 *            date
	 * @return year of the date
	 */
	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	/**
	 * Get the month of the date
	 * 
	 * @param date
	 *            date
	 * @return month of the date
	 */
	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	/**
	 * Get the day of year of the date
	 * 
	 * @param date
	 *            date
	 * @return day of year of the date
	 */
	public static int getDayOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Get the day of month of the date
	 * 
	 * @param date
	 *            date
	 * @return day of month of the date
	 */
	public static int getDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get the day of week of the date
	 * 
	 * @param date
	 *            date
	 * @return day of week of the date
	 */
	public static int getDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Get the hour of the date
	 * 
	 * @param date
	 *            date
	 * @return hour of the date
	 */
	public static int getHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Get the minute of the date
	 * 
	 * @param date
	 *            date
	 * @return minute of the date
	 */
	public static int getMinute(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MINUTE);
	}

	/**
	 * Get the second of the date
	 * 
	 * @param date
	 *            date
	 * @return second of the date
	 */
	public static int getSecond(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.SECOND);
	}

	/**
	 * Rounds a date to hour 0, minute 0, second 0 and millisecond 0
	 * 
	 * @param d
	 *            the date
	 * @return the rounded date
	 */
	public static Date floor(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * Rounds a date to hour 23, minute 59, second 59 and millisecond 999
	 * 
	 * @param d
	 *            the date
	 * @return the rounded date
	 */
	public static Date ceil(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	/**
	 * Test to see if two dates are in the same day of year
	 * 
	 * @param dateOne
	 *            first date
	 * @param dateTwo
	 *            second date
	 * @return true if the two dates are in the same day of year
	 */
	public static boolean sameDay(Date dateOne, Date dateTwo) {
		if ((dateOne == null) || (dateTwo == null)) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOne);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);

		cal.setTime(dateTwo);
		int year2 = cal.get(Calendar.YEAR);
		int day2 = cal.get(Calendar.DAY_OF_YEAR);

		return ((year == year2) && (day == day2));
	}

	/**
	 * Test to see if two dates are in the same week
	 * 
	 * @param dateOne
	 *            first date
	 * @param dateTwo
	 *            second date
	 * @return true if the two dates are in the same week
	 */
	public static boolean sameWeek(Date dateOne, Date dateTwo) {
		if ((dateOne == null) || (dateTwo == null)) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOne);
		int year = cal.get(Calendar.YEAR);
		int week = cal.get(Calendar.WEEK_OF_YEAR);

		cal.setTime(dateTwo);
		int year2 = cal.get(Calendar.YEAR);
		int week2 = cal.get(Calendar.WEEK_OF_YEAR);

		return ((year == year2) && (week == week2));
	}

	/**
	 * Test to see if two dates are in the same month
	 * 
	 * @param dateOne
	 *            first date
	 * @param dateTwo
	 *            second date
	 * @return true if the two dates are in the same month
	 */
	public static boolean sameMonth(Date dateOne, Date dateTwo) {
		if ((dateOne == null) || (dateTwo == null)) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOne);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		cal.setTime(dateTwo);
		int year2 = cal.get(Calendar.YEAR);
		int month2 = cal.get(Calendar.MONTH);

		return ((year == year2) && (month == month2));
	}

	/**
	 * Test to see if two dates are in the same hour of day
	 * 
	 * @param dateOne
	 *            first date
	 * @param dateTwo
	 *            second date
	 * @return true if the two dates are in the same hour of day
	 */
	public static boolean sameHour(Date dateOne, Date dateTwo) {
		if ((dateOne == null) || (dateTwo == null)) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOne);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);

		cal.setTime(dateTwo);
		int year2 = cal.get(Calendar.YEAR);
		int month2 = cal.get(Calendar.MONTH);
		int day2 = cal.get(Calendar.DAY_OF_YEAR);
		int hour2 = cal.get(Calendar.HOUR_OF_DAY);

		return ((year == year2) && (month == month2) && (day == day2) && (hour == hour2));
	}

	/**
	 * Get number of days between two dates
	 * 
	 * @param first
	 *            first date
	 * @param second
	 *            second date
	 * @return number of days if first date less than second date, 0 if first
	 *         date is bigger than second date, 1 if dates are the same
	 * 
	 */
	public static int getNumberOfDays(Date first, Date second) {
		Calendar c = Calendar.getInstance();
		int result = 0;
		int compare = first.compareTo(second);
		if (compare > 0)
			return 0;
		if (compare == 0)
			return 1;

		c.setTime(first);
		int firstDay = c.get(Calendar.DAY_OF_YEAR);
		int firstYear = c.get(Calendar.YEAR);
		int firstDays = c.getActualMaximum(Calendar.DAY_OF_YEAR);

		c.setTime(second);
		int secondDay = c.get(Calendar.DAY_OF_YEAR);
		int secondYear = c.get(Calendar.YEAR);

		// if dates in the same year
		if (firstYear == secondYear) {
			result = secondDay - firstDay + 1;
		}

		// different years
		else {
			// days from the first year
			result += firstDays - firstDay + 1;

			// add days from all years between the two dates years
			for (int i = firstYear + 1; i < secondYear; i++) {
				c.set(i, 0, 0);
				result += c.getActualMaximum(Calendar.DAY_OF_YEAR);
			}

			// days from last year
			result += secondDay;
		}

		return result;
	}

	/**
	 * Get elapsedtime between two dates
	 * 
	 * @param first
	 *            first date
	 * @param second
	 *            second date
	 * @return null if first date is after second date an integer array of three
	 *         elemets ( days, hours minutes )
	 */
	public static int[] getElapsedTime(Date first, Date second) {
		if (first.compareTo(second) == 1) {
			return null;
		}
		int difDays = 0;
		int difHours = 0;
		int difMinutes = 0;

		Calendar c = Calendar.getInstance();
		c.setTime(first);
		int h1 = c.get(Calendar.HOUR_OF_DAY);
		int m1 = c.get(Calendar.MINUTE);

		c.setTime(second);
		int h2 = c.get(Calendar.HOUR_OF_DAY);
		int m2 = c.get(Calendar.MINUTE);

		if (sameDay(first, second)) {
			difHours = h2 - h1;
		} else {
			difDays = getNumberOfDays(first, second) - 1;
			if (h1 >= h2) {
				difDays--;
				difHours = (24 - h1) + h2;
			} else {
				difHours = h2 - h1;
			}
		}

		if (m1 >= m2) {
			difHours--;
			difMinutes = (60 - m1) + m2;
		} else {
			difMinutes = m2 - m1;
		}

		int[] result = new int[3];
		result[0] = difDays;
		result[1] = difHours;
		result[2] = difMinutes;
		return result;
	}

	/**
	 * Add minutes to a date
	 * 
	 * @param d
	 *            date
	 * @param minutes
	 *            minutes
	 * @return new date
	 */
	public static Date addMinutes(Date d, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * Add hours to a date
	 * 
	 * @param d
	 *            date
	 * @param hours
	 *            hours
	 * @return new date
	 */
	public static Date addHours(Date d, int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		return cal.getTime();
	}

	/**
	 * Add days to a date
	 * 
	 * @param d
	 *            date
	 * @param days
	 *            days
	 * @return new date
	 */
	public static Date addDays(Date d, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return cal.getTime();
	}

	/**
	 * Add weeks to a date
	 * 
	 * @param d
	 *            date
	 * @param weeks
	 *            weeks
	 * @return new date
	 */
	public static Date addWeeks(Date d, int weeks) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.WEEK_OF_YEAR, weeks);
		return cal.getTime();
	}

	/**
	 * Add months to a date
	 * 
	 * @param d
	 *            date
	 * @param months
	 *            months
	 * @return new date
	 */
	public static Date addMonths(Date d, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	public static Date setCurrentYear(Date date) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.setTime(date);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
	public static boolean insideInterval(Date d, Date d1, Date d2) {
		return (sameDay(d1, d) || before(d1, d)) && (sameDay(d, d2) || before(d, d2));
	}

}
