package yearly_financial_parameters;

/**
 * @author Hari:
 */

/**
 * 
 * Consists of year: Integer, value: Double, symbol: String
 */
public class BasicYearlyFinData  
{
	Integer _year;
	Double _val;
	String _sym;
	
	public BasicYearlyFinData(String Sym, Double v) 
	{
		_sym = Sym;
		_val = v;
	}
	public BasicYearlyFinData(String Sym, Double v, Integer y) 
	{
		_sym = Sym;
		_val = v;
		_year = y;
	}
	
	void setYear(Integer y)
	{
		_year = y;
	}
	
	public void setVal(Double v)
	{
		_val = v;
	}
	
	public Double getVal()
	{
		return _val;
	}
	
	Integer getYear()
	{
		return _year;
	}

	

	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == getClass())
			return ((BasicYearlyFinData)obj).getVal().equals(_val);
		return false;
	}
	
	public int compare(BasicYearlyFinData c2) {
		return this.getVal().compareTo(c2.getVal());
	}

}
