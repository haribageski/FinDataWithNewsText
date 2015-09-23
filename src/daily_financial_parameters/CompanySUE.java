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

public class CompanySUE {
	String Symbol;
	Basic_daily_fin_data Oldest_SUE;
	Basic_daily_fin_data Earliest_SUE;
	Map<Integer,Double> avg_per_year_SUE =  new HashMap<Integer, Double>();		//TO DO
	List<Basic_daily_fin_data> All_company_SUE = new ArrayList<Basic_daily_fin_data>();
	HashSet<Integer> All_company_SUE_years = new HashSet<Integer>();
	String folder_path = "D:\\my documents\\Senior_Project_datasets\\Earning_surprises\\";
	
	public CompanySUE(String sym) throws IOException, ParseException
	{
		Symbol = sym;
		read_company_SUE();
	}
	
	public void add_SUE(Basic_daily_fin_data SUE)
	{
		All_company_SUE.add(SUE);
		All_company_SUE_years.add(SUE.get_date().get_year_in_date());
	}
	
	public List<Basic_daily_fin_data> get_All_company_SUE()
	{
		return All_company_SUE;
	}
	
	public HashSet<Integer> GET_all_company_SUE_years ()
	{
		return All_company_SUE_years;
	}
	
	public Basic_daily_fin_data Get_Earliest_SUE()
	{
		return Earliest_SUE;
	}
	
	public Map<Integer, Double> Get_avg_per_y_SUE ()
	{
		return avg_per_year_SUE;
	}
	
	public void eraseSUE(Basic_daily_fin_data SUE)
	{
		All_company_SUE.remove(SUE) ;
	}
	
	public void eraseYear(Integer year)
	{
		All_company_SUE_years.remove(year) ;
	}
	
	public void read_company_SUE() throws IOException, ParseException
	{
		DateModif date = new DateModif();
		Integer year = 0;
		Double sue;
		
		String file_path = folder_path + Symbol + ".txt";
		ReadColumnWithIndexFromFile Read_date = new ReadColumnWithIndexFromFile(file_path, 1);
		
		List<String> Normalized_SUE = 
				(new ReadColumnWithIndexFromFile()).readOneColumnFromTabSeparColumns(file_path, 2);	//in percentage 
		
		for(int i = 0; i < Read_date.getTheColumn().size(); i++)
		{
			date = new DateModif (Read_date.getTheColumn().get(i) );
			sue = Double.parseDouble(Normalized_SUE.get(i));
			
			if(sue.equals(Double.NaN) || sue.equals(null))
				continue;
			
			Basic_daily_fin_data Comp_SUE = new Basic_daily_fin_data(Symbol, sue);
			Comp_SUE.set_date(date);
			
			All_company_SUE.add( Comp_SUE );
			
			All_company_SUE_years.add(date.get_year_in_date());
		}
	}
	
	
	public void set_parameters()
	{
		DateModif date = new DateModif();
		Integer year = 0;
		Double sue;
		Integer count = 0;
		Double total_sue = 0.0;
		
		
		
		for(Basic_daily_fin_data S : All_company_SUE)
		{
			date = S.get_date();
			sue = S.get_val();
			
			if(!year.equals(date.get_year_in_date())) 		//different year
			{
				if(!year.equals(0))	//not the first year first entry
				{
					avg_per_year_SUE.put(year, total_sue/count);
				}
				count = 0;
				total_sue = 0.0;
				year = date.get_year_in_date();
				
			}
			
			total_sue += sue;
			count++;

			if(S.equals(All_company_SUE.toArray()[0]))
			{
				Oldest_SUE = new Basic_daily_fin_data(Symbol, sue);
				Oldest_SUE.set_date(date);
			}
			Earliest_SUE = new Basic_daily_fin_data(Symbol, sue); 			//it will be reassigning till the very last	
			Earliest_SUE.set_date(date);
		}
		
		//for the last year we need to include some invariants that are waiting to be inserted when new year comes, which won't for the last year
		avg_per_year_SUE.put(year, total_sue/count);	
	}
}
