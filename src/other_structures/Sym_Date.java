package other_structures;

/**
 * 
 * @author Hari
 *
 * Simple class consisting of symbol : String and modified date : DateModif 
 */
public class Sym_Date implements Comparable<Sym_Date>{
	String sym;
	DateModif D;
	public Sym_Date(String Sym, DateModif DateM)
	{
		D = DateM;
		sym = Sym;
	}
	
	public DateModif get_Date_modif()
	{
		return D;
	}
	
	public String get_sym()
	{
		return sym;
	}

	@Override
	public int compareTo(Sym_Date arg0) {
		if(this.get_sym().toString().compareTo(arg0.get_sym().toString())!=0)
			return this.get_sym().toString().compareTo(arg0.get_sym().toString());
		return this.get_Date_modif().get_date().compareTo(arg0.get_Date_modif().get_date());
	}
}
