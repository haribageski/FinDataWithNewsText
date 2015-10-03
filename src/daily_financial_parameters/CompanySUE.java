package daily_financial_parameters;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import other_structures.DateModif;
import reading_data_from_file.ReadOneColumnFromFile;

public class CompanySUE {
	String Symbol;
	BasicDailyFinData Oldest_SUE;
	BasicDailyFinData Earliest_SUE;
	Map<Integer,Double> avg_per_year_SUE =  new HashMap<Integer, Double>();		//TO DO
	List<BasicDailyFinData> All_company_SUE = new ArrayList<BasicDailyFinData>();
	HashSet<Integer> All_company_SUE_years = new HashSet<Integer>();
	String folder_path = "D:\\my documents\\Senior_Project_datasets\\Earning_surprises\\";
	
	public CompanySUE(String sym) throws IOException, ParseException
	{
		Symbol = sym;
		read_company_SUE();
	}
	
	public void add_SUE(BasicDailyFinData SUE)
	{
		All_company_SUE.add(SUE);
		All_company_SUE_years.add(SUE.getDate().get_year_in_date());
	}
	
	public List<BasicDailyFinData> get_All_company_SUE()
	{
		return All_company_SUE;
	}
	
	public HashSet<Integer> GET_all_company_SUE_years ()
	{
		return All_company_SUE_years;
	}
	
	public BasicDailyFinData Get_Earliest_SUE()
	{
		return Earliest_SUE;
	}
	
	public Map<Integer, Double> Get_avg_per_y_SUE ()
	{
		return avg_per_year_SUE;
	}
	
	public void eraseSUE(BasicDailyFinData SUE)
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
		ReadOneColumnFromFile Read_date = new ReadOneColumnFromFile(file_path, 1);
		
		List<String> Normalized_SUE = 
				(new ReadOneColumnFromFile()).readOneColumnFromTabSeparColumns(file_path, 2);	//in percentage 
		
		for(int i = 0; i < Read_date.getTheColumn().size(); i++)
		{
			date = new DateModif (Read_date.getTheColumn().get(i) );
			sue = Double.parseDouble(Normalized_SUE.get(i));
			
			if(sue.equals(Double.NaN) || sue.equals(null))
				continue;
			
			BasicDailyFinData Comp_SUE = new BasicDailyFinData(Symbol, sue);
			Comp_SUE.setDate(date);
			
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
		
		
		
		for(BasicDailyFinData S : All_company_SUE)
		{
			date = S.getDate();
			sue = S.getVal();
			
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
				Oldest_SUE = new BasicDailyFinData(Symbol, sue);
				Oldest_SUE.setDate(date);
			}
			Earliest_SUE = new BasicDailyFinData(Symbol, sue); 			//it will be reassigning till the very last	
			Earliest_SUE.setDate(date);
		}
		
		//for the last year we need to include some invariants that are waiting to be inserted when new year comes, which won't for the last year
		avg_per_year_SUE.put(year, total_sue/count);	
	}
}
