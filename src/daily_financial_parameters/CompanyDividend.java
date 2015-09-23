package daily_financial_parameters;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import other_structures.DateModif;
import reading_data_from_file.ReadColumnWithIndexFromFile;

public class CompanyDividend {
	String Sym;
	Basic_daily_fin_data Oldest_dividend;
	Basic_daily_fin_data Earliest_dividend;
	Map<Integer,Double> avg_per_year_dividend =  new HashMap<Integer, Double>();		
	List <Basic_daily_fin_data> All_company_dividends = new ArrayList <Basic_daily_fin_data>();
	HashSet<Integer> All_company_divi_years = new HashSet<Integer>() ;
	String folder_path = "D:\\my documents\\Senior_Project_datasets\\Dividends\\";
	
	public CompanyDividend(String Symbol)
	{
		Sym = Symbol;
	}
	
	public Map<Integer, Double> get_avg_dividends()
	{
		return avg_per_year_dividend;
	}
	
	public void add_dividend(Basic_daily_fin_data divi)
	{
		All_company_dividends.add(divi) ;
		All_company_divi_years.add(divi.get_date().get_year_in_date());
	}
	
	public void eraseDividend(Basic_daily_fin_data divi)
	{
		All_company_dividends.remove(divi) ;
	}
	
	public HashSet<Integer> Get_all_company_divi_years ()
	{
		return  All_company_divi_years ;
	}
	
	public void eraseYear(Integer year)
	{
		All_company_divi_years.remove(year) ;
	}
	
	public Basic_daily_fin_data Get_oldest_dividend()
	{
		return Oldest_dividend;
	}
	public String get_folder_path()
	{
		return folder_path;
	}
	
	public List <Basic_daily_fin_data> get_All_company_dividends()
	{
		return  All_company_dividends ;
	}
	
	
	public void readCompanyDividends() throws IOException, ParseException
	{
		DateModif date = new DateModif(); 
		
		String file_path = folder_path + Sym + ".txt";
		ReadColumnWithIndexFromFile Read_date = new ReadColumnWithIndexFromFile(file_path, 1);
		
		List<String> Normalized_dividend_price = 
				(new ReadColumnWithIndexFromFile(file_path, 2)).getTheColumn();
		
		for(int i = 0; i < Read_date.getTheColumn().size(); i++)
		{
			date = new DateModif(Read_date.getTheColumn().get(i) );
			Double dividend = Double.parseDouble(Normalized_dividend_price.get(i));
			
			if(dividend.equals(Double.NaN) || dividend.equals(null))
				continue;
			
			Basic_daily_fin_data Divid = new Basic_daily_fin_data(Sym, dividend);
			Divid.set_date(date);
			
			All_company_dividends.add( Divid );
			All_company_divi_years.add(date.get_year_in_date());
		}
	}
	
	public void setAllParameters()
	{
			DateModif date= new DateModif(); 
			Integer year = 0;
			Double dividend;
			int days_year = 0;
			Double total_dividend = 0.0;
			
			avg_per_year_dividend.clear();
			
			
			for(Basic_daily_fin_data Divi : All_company_dividends)
			{
				date = Divi.get_date();
				dividend = Divi.get_val();
				
				if(!year.equals(date.get_year_in_date())) 		//different year
				{
					if(!year.equals(0) && days_year!=0)	//not the first year first entry => store averages of that year
					{
						avg_per_year_dividend.put(year, total_dividend/days_year);
					}
					days_year = 0;
					year = date.get_year_in_date();
					total_dividend = 0.0;
				}
				
				total_dividend +=dividend;
				days_year++;
				
				
				if(Divi.equals(All_company_dividends.toArray()[0]) )	//first year first qoute
				{
					Oldest_dividend = new Basic_daily_fin_data(Sym, dividend);
					Oldest_dividend.set_date(date);
				}
				
				
				Earliest_dividend = new Basic_daily_fin_data(Sym, dividend);	//it will be reassigning till the very last	
				Earliest_dividend.set_date(date);
			}
			
			if(days_year!=0)
				avg_per_year_dividend.put(year, total_dividend/days_year);	
			// otherwise we do not have the avg for current year (last in the list we are reading from)
		}
}

