package other_structures;

/**
 * 
 * @author Hari
 *
 * Simple class consisting of symbol : String and year : Integer 
 */
public class Sym_Year implements Comparable<Sym_Year>{
	String sym;
	Integer year;
	public Sym_Year(String Sym, Integer y)
	{
		year = y;
		sym = Sym;
	}
	
	
	public Integer get_year()
	{
		return year;
	}
	
	public String get_sym()
	{
		return sym;
	}

	public int compareTo(Integer y) {
		// TODO Auto-generated method stub
		return this.year.compareTo(y);
	}


	@Override
	public int compareTo(Sym_Year arg0) {
		if(this.get_sym().toString().compareTo(arg0.get_sym().toString())!=0)
			return this.get_sym().toString().compareTo(arg0.get_sym().toString());
		return this.get_year().toString().compareTo(arg0.get_year().toString());
	}
}
