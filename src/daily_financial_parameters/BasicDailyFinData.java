package daily_financial_parameters;

import other_structures.DateModif;


public class BasicDailyFinData 
{
	String sym;
	Double val;
	DateModif date;
	
	public BasicDailyFinData(String Sym, Double v)
	{
		sym = Sym;
		val = v;
	}
	public BasicDailyFinData(String Sym, Double v, DateModif d)
	{
		sym = Sym;
		val = v;
		date = d;
	}
	
	protected void setDate(DateModif d)
	{
		date = d;
	}
	
	public DateModif getDate()
	{
		return date;
	}
	
	public String getSym()
	{
		return sym;
	}
	
	public Double getVal()
	{
		return val;
	}
	
	public void setVal(Double d)
	{
		val = d;
	}
	
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == getClass())
			return ((BasicDailyFinData)obj).getVal().equals(val);
		return false;
	}
	
	public int compare(BasicDailyFinData c2) {
		return this.val.compareTo(c2.getVal());
	};
}
