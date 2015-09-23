package daily_financial_parameters;

import other_structures.DateModif;


public class Basic_daily_fin_data 
{
	String sym;
	Double val;
	DateModif date;
	
	public Basic_daily_fin_data(String Sym, Double v)
	{
		sym = Sym;
		val = v;
	}
	public Basic_daily_fin_data(String Sym, Double v, DateModif d)
	{
		sym = Sym;
		val = v;
		date = d;
	}
	
	protected void set_date(DateModif d)
	{
		date = d;
	}
	
	public DateModif get_date()
	{
		return date;
	}
	
	public String get_symbol()
	{
		return sym;
	}
	
	public Double get_val()
	{
		return val;
	}
	
	public void set_val(Double d)
	{
		val = d;
	}
	
	/*
	public int compareTo(Object obj) 
	{
		if(obj.getClass() == getClass())
			return ((Basic_Daily_Fin_Struct)obj).get_val().compareTo(val);
		return 0;
	};*/
	
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == getClass())
			return ((Basic_daily_fin_data)obj).get_val().equals(val);
		return false;
	}
	
	public int compare(Basic_daily_fin_data c2) {
		return this.val.compareTo(c2.get_val());
	};
}
