package daily_financial_parameters;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import other_structures.DateModif;
import reading_data_from_file.ReadColumnWithIndexFromFile;

public class CompanyQuotes {
	String Sym;
	List<Basic_daily_fin_data> _allCompanyQuotes = new ArrayList<Basic_daily_fin_data>();
	Basic_daily_fin_data _earliestQuote, _oldestQuote;
	Map<Integer , Double> _avgPerYQuotesClosingPrice =  new HashMap<Integer, Double>();
	Map<DateModif , Double> _quotesClosingPrice =  new HashMap<DateModif, Double>();
	HashSet<Integer> _allCompanyQuotesYears = new HashSet<Integer>() ;
	String _folderPath = "D:\\my documents\\Senior_Project_datasets\\Quotes-prices\\";
	
	public CompanyQuotes(String sym) throws IOException, ParseException
	{
		Sym = sym;		
		read_company_quotes();
	}
	
	public void add_quote(Basic_daily_fin_data Q)
	{
		_allCompanyQuotes.add(Q) ;
		_allCompanyQuotesYears.add(Q.get_date().get_year_in_date());
	}
	
	public void eraseQuote(Basic_daily_fin_data q)
	{
		_allCompanyQuotes.remove(q);
	}
	
	public Map<DateModif , Double> get_quotes_map()
	{
		return _quotesClosingPrice;
	}
	
	public List <Basic_daily_fin_data> get_All_company_quotes()
	{
		return  _allCompanyQuotes ;
	}
	
	
	public Map<Integer, Double> getAvgPerYQuotesClosingPrice ()
	{
		return _avgPerYQuotesClosingPrice;
	}
	
	public HashSet<Integer> Get_all_company_quotes_years ()
	{
		return  _allCompanyQuotesYears ;
	}
	
	public void eraseYear(Integer y)
	{
		_allCompanyQuotesYears.remove(y);
	}
	
	public void read_company_quotes() throws IOException, ParseException
	{
		DateModif date = new DateModif();
		Double c_price;
		
		String file_path = _folderPath + Sym + ".txt";
		ReadColumnWithIndexFromFile readDate = new ReadColumnWithIndexFromFile(file_path, 1);	
		ReadColumnWithIndexFromFile readPrice = new ReadColumnWithIndexFromFile(file_path, 2);	
		
		List<String> Normalized_close_price = (new ReadColumnWithIndexFromFile(file_path, 2)).getTheColumn();
		/*List<String> Normalized_open_price = 
				ReadColumnWithIndexFromFile.readOneColumnFromTabSeparColumns(file_path, 3);*/
		
		for(int i = 0; i < readDate.getTheColumn().size(); i++)
		{
			date = new DateModif(readDate.getTheColumn().get(i) );	
			//System.out.println("Normalized_close_price.get(i):" + Normalized_close_price.get(i));
			c_price = Double.parseDouble(Normalized_close_price.get(i));
			
			if(c_price.equals(Double.NaN) || c_price.equals(null))
				continue;
			Basic_daily_fin_data priceAtDate = new Basic_daily_fin_data(Sym, c_price, date);
			_allCompanyQuotes.add( priceAtDate );
			_allCompanyQuotesYears.add(date.get_year_in_date());
			_quotesClosingPrice.put(date, c_price);
		}
	}
	
	
	public void set_parameters() 
	{
		DateModif date = new DateModif();
		Integer year = 0;
		Double c_price, o_price;
		int days_year = 0;
		double total_c_price = 0 , total_o_price = 0;
		
		_avgPerYQuotesClosingPrice.clear();
		
		for(Basic_daily_fin_data Q : _allCompanyQuotes)
		{
			date = Q.get_date();
			if(!year.equals(date.get_year_in_date()) )		//different year
			{
				if(!year.equals(0))	//not the first year first entry
				{
					if(days_year==0)
					{
						System.out.println("CompanyQuotes. set_parameters() : cannot find _avgPerYQuotesClosingPrice - div with 0");
					}
					else
					{
						System.out.println("set _avgPerYQuotesClosingPrice:" + total_c_price / (double)days_year);
						_avgPerYQuotesClosingPrice.put(year, total_c_price / (double)days_year);
						//System.out.println("quote for year:" + year + " is:" + total_c_price / (double)days_year);
					}
				}
				days_year = 0;
				total_c_price = 0;
				total_o_price = 0;
				year = date.get_year_in_date();
			}
			
			
			c_price = Q.get_val();
			total_c_price +=c_price;
			days_year++;
			
			if(Q.equals(_allCompanyQuotes.toArray()[0]))	//first year first qoute
			{
				_oldestQuote = new  Basic_daily_fin_data(Sym,  c_price);
				_oldestQuote.set_date(date);
			}
			

			_earliestQuote = new Basic_daily_fin_data(Sym,  c_price);	//it will be reassigning till the very last
			_earliestQuote.set_date(date);
			//System.out.println("Qoute for:" + Sym + " " + date + " " + o_price + " " + c_price);
		}
		System.out.println("set _avgPerYQuotesClosingPrice:" + total_c_price / (double)days_year);
		_avgPerYQuotesClosingPrice.put(year, total_c_price / (double)days_year);	//for last year
	}
}
