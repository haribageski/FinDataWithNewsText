package other_structures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Included implementations for useful methods related to the structure Date
 */
public class DateModif  implements Comparable<DateModif>
{
	Date _d = new Date();
	
	public DateModif(String date) throws ParseException
	{
		if(date!="")
			_d = string_to_date(date);
	}

	public DateModif() 
	{
	}	
	
	public static Date string_to_date(String date) 
	{
	     try {
	         return new SimpleDateFormat("dd/MM/yyyy").parse(date);
	     } catch (ParseException e) {
	    	 System.out.println("DateModif.string_to_date(): Probably wrong format");
	         return null;
	     }
	}
	
	public DateModif get_prev_day_as_datemodif() throws ParseException
	{
		SimpleDateFormat date_simple = new SimpleDateFormat("dd/MM/yyyy");
		String dt_str = date_simple.format(_d);
		
		Calendar c = Calendar.getInstance();
		c.setTime(date_simple.parse(dt_str));
		c.add(Calendar.DATE, -1);  // number of days to add
		dt_str = date_simple.format(c.getTime());  // dt is now the new date
		
		DateModif Date_prev = new DateModif(dt_str);
		return Date_prev;
		
	}
	
	public Integer get_year_in_date()
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime(_d);
	    Integer year = cal.get(Calendar.YEAR);
	    
		return year;
	}
	
	public Integer get_month_in_date()
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime(_d);
	    Integer month = cal.get(Calendar.MONTH);
	    
		return month;
	}
	
	public Integer get_day_in_date()
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime(_d);
	    Integer day = cal.get(Calendar.DAY_OF_MONTH);
	    
		return day;
	}
	
	public Date get_date()
	{
		return _d;
	}
	
	public String dateModifToString()
	{
		SimpleDateFormat date_simple = new SimpleDateFormat("dd/MM/yyyy");
		return date_simple.format(_d);
	}

	@Override
	public int compareTo(DateModif dat) 
	{
		return this._d.compareTo(dat.get_date());
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == getClass())
			return ((DateModif)obj).get_date().equals(_d);
		return false;
	};
	
	@Override
	public int hashCode() 
	{
		return _d.hashCode();
	};
	
	@Override
	public String toString()
	{
		return _d.toString();
	}
}
