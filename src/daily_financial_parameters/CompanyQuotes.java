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
import reading_data_from_file.ReadOneColumnFromFile;

public class CompanyQuotes {
	String Sym;
	List<BasicDailyFinData> _allCompanyQuotes = new ArrayList<BasicDailyFinData>();
	BasicDailyFinData _earliestQuote, _oldestQuote;
	Map<Integer , Double> _avgPerYQuotesClosingPrice =  new HashMap<Integer, Double>();
	Map<DateModif , Double> _quotesClosingPrice =  new HashMap<DateModif, Double>();
	HashSet<Integer> _allCompanyQuotesYears = new HashSet<Integer>() ;
	String _folderPath = "D:\\my documents\\Senior_Project_datasets\\Quotes-prices\\";
	
	public CompanyQuotes(String sym) throws IOException, ParseException
	{
		Sym = sym;		
		read_company_quotes();
	}
	
	public void add_quote(BasicDailyFinData Q)
	{
		_allCompanyQuotes.add(Q) ;
		_allCompanyQuotesYears.add(Q.getDate().get_year_in_date());
	}
	
	public void eraseQuote(BasicDailyFinData q)
	{
		_allCompanyQuotes.remove(q);
	}
	
	public Map<DateModif , Double> get_quotes_map()
	{
		return _quotesClosingPrice;
	}
	
	public List <BasicDailyFinData> get_All_company_quotes()
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
		ReadOneColumnFromFile readDate = new ReadOneColumnFromFile(file_path, 1);	
		ReadOneColumnFromFile readPrice = new ReadOneColumnFromFile(file_path, 2);	
		
		List<String> Normalized_close_price = (new ReadOneColumnFromFile(file_path, 2)).getTheColumn();
		/*List<String> Normalized_open_price = 
				ReadColumnWithIndexFromFile.readOneColumnFromTabSeparColumns(file_path, 3);*/
		
		for(int i = 0; i < readDate.getTheColumn().size(); i++)
		{
			date = new DateModif(readDate.getTheColumn().get(i) );	
			//System.out.println("Normalized_close_price.get(i):" + Normalized_close_price.get(i));
			c_price = Double.parseDouble(Normalized_close_price.get(i));
			
			if(c_price.isNaN()|| c_price.equals(null) || c_price.isInfinite())
				continue;
			
			BasicDailyFinData priceAtDate = new BasicDailyFinData(Sym, c_price, date);
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
		
		for(BasicDailyFinData Q : _allCompanyQuotes)
		{
			date = Q.getDate();
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
			
			
			c_price = Q.getVal();
			total_c_price += c_price;
			days_year++;
			
			if(Q.equals(_allCompanyQuotes.toArray()[0]))	//first year first qoute
			{
				_oldestQuote = new  BasicDailyFinData(Sym,  c_price);
				_oldestQuote.setDate(date);
			}
			

			_earliestQuote = new BasicDailyFinData(Sym,  c_price);	//it will be reassigning till the very last
			_earliestQuote.setDate(date);
			//System.out.println("Qoute for:" + Sym + " " + date + " " + o_price + " " + c_price);
		}

		if(days_year==0)
		{
			System.out.println("CompanyQuotes. set_parameters() : cannot find _avgPerYQuotesClosingPrice - div with 0");
		}
		else
		{
			System.out.println("set _avgPerYQuotesClosingPrice:" + total_c_price / (double)days_year);
			_avgPerYQuotesClosingPrice.put(year, total_c_price / (double)days_year);	//for last year	
		}
	}
}
