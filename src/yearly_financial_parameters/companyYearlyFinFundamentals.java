package yearly_financial_parameters;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import daily_financial_parameters.CompanyQuotes;
import other_structures.DateModif;
import other_structures.Sym_Year;
import reading_data_from_file.ReadColumnWithIndexFromFile;

public class companyYearlyFinFundamentals  {
	String  _sym;
	
	Integer _oldestYear = 0, _earliestYear;
	
	//fundamental financial parameters
	Map<Sym_Year, basicYearlyFinData> _perYearBookVal =  new TreeMap<Sym_Year, basicYearlyFinData>();
	Map<Sym_Year, basicYearlyFinData> _perYearNumShares =  new TreeMap<Sym_Year, basicYearlyFinData> ();
	Map<Sym_Year, basicYearlyFinData> _perYearROE =  new TreeMap<Sym_Year, basicYearlyFinData> ();
	Map<Sym_Year, basicYearlyFinData> _perYearAccrual =  new TreeMap<Sym_Year, basicYearlyFinData> ();	
	
	//derived financial parameters from the fundamental financial parameters
	Map<Sym_Year,Double> _perYearMarketVal =  new TreeMap<Sym_Year, Double>();	
	Map<Sym_Year,Double> _perYearBMRatio =  new TreeMap<Sym_Year, Double>();
	Map<Sym_Year,Double> _perYearSize =  new TreeMap<Sym_Year, Double>();		
	
	
	HashSet<Integer> _allCompanyExtendedFinParamYears = new HashSet<Integer>();
	HashSet<Integer> _allCompanyFinFundamYears = new HashSet<Integer>();
	
	String _folderPath = "D:\\my documents\\Senior_Project_datasets\\Financial_parameters\\";
	
	
	public companyYearlyFinFundamentals(String Symbol)
	{
		_sym = Symbol;
	}
	
	public void addAccrual(Sym_Year S_D, basicYearlyFinData Fin_Unit)
	{
		if(!Fin_Unit.getVal().isNaN())
		{
			_perYearAccrual.put(S_D, Fin_Unit);
			_allCompanyFinFundamYears.add(S_D.get_year());
		}
	}
	
	public void eraseAccrualAtDate(Sym_Year symYear)
	{
		_perYearAccrual.remove(symYear);
	}
	
	public void addROE(Sym_Year S_D, basicYearlyFinData Fin_Unit)
	{
		if(!Fin_Unit.getVal().isNaN())
		{
			_perYearROE.put(S_D, Fin_Unit);
			_allCompanyFinFundamYears.add(S_D.get_year());
		}
	}
	
	public void eraseROEAtDate(Sym_Year symYear)
	{
		_perYearROE.remove(symYear);
	}
	
	public void addShare(Sym_Year S_D, basicYearlyFinData Fin_Unit)
	{
		if(!Fin_Unit.getVal().isNaN())
		{
			_perYearNumShares.put(S_D, Fin_Unit);
			_allCompanyFinFundamYears.add(S_D.get_year());
		}
	}
	
	public void eraseShareAtDate(Sym_Year symYear)
	{
		_perYearAccrual.remove(symYear);
	}
	
	public void addBookVal(Sym_Year S_D, basicYearlyFinData Fin_Unit)
	{
		if(!Fin_Unit.getVal().isNaN())
		{
			_perYearBookVal.put(S_D, Fin_Unit);
			_allCompanyFinFundamYears.add(S_D.get_year());
		}
	}
	
	public void eraseBookValAtDate(Sym_Year symYear)
	{
		_perYearAccrual.remove(symYear);
	}
	
	public void eraseYear(Sym_Year symYear)
	{
		_allCompanyFinFundamYears.remove(symYear.get_year());
	}
	
	public Map<Sym_Year, basicYearlyFinData>  getAllCompanyBookVal()
	{
			return _perYearBookVal;
	}
	
	public Map<Sym_Year, basicYearlyFinData>   getAllCompanyShares()
	{
		return _perYearNumShares;
	}
	public Map<Sym_Year, Double>   getAllCompanyMarketVals()
	{
		return _perYearMarketVal;
	}
	public Map<Sym_Year, Double>   getAllBM_Ratios()
	{
		return _perYearBMRatio;
	}
	public Map<Sym_Year, Double>   getAllCompanyYearSizes()
	{
		return _perYearSize;
	}
	public Map<Sym_Year, basicYearlyFinData>  getAllCompanyROE()
	{
		return _perYearROE;
	}
	public Map<Sym_Year, basicYearlyFinData>  getAllCompanyAccrual()
	{
		return _perYearAccrual;
	}
	
	public HashSet<Integer> getAllCompanyFinFundamYears()
	{
		return _allCompanyFinFundamYears;
	}
	
	public void makeConsistentAllCompanyFinFundamYears()
	{
		HashSet<Integer> consistentYears = new HashSet<Integer>();
		HashSet<Integer> yearsToCheck = new HashSet<Integer>();
		for(Sym_Year sY :_perYearBookVal.keySet())
		{
			consistentYears.add(sY.get_year());
		}
		
		for(Sym_Year sY : _perYearAccrual.keySet())
		{
			yearsToCheck.add(sY.get_year());
		}
		consistentYears.retainAll(yearsToCheck);
		yearsToCheck.clear();
		
		for(Sym_Year sY : _perYearROE.keySet())
		{
			yearsToCheck.add(sY.get_year());
		}
		consistentYears.retainAll(yearsToCheck);
		yearsToCheck.clear();
		
		for(Sym_Year sY : _perYearNumShares.keySet())
		{
			yearsToCheck.add(sY.get_year());
		}
		consistentYears.retainAll(yearsToCheck);
		
		_allCompanyFinFundamYears = consistentYears;
	}

	public void read_copany_fin_fundamentals() throws IOException, ParseException
	{
		String filePath = _folderPath + _sym + ".txt";
		ReadColumnWithIndexFromFile Read_date = new ReadColumnWithIndexFromFile(filePath, 1);
		
		List<String> Normalized_book_value = 
				(new ReadColumnWithIndexFromFile(filePath, 2)).getTheColumn();
		System.out.println("companyYearlyFinFundamentals.Normalized_book_value size:" + Normalized_book_value.size());
		List<String> Normalized_num_of_shares = 
				(new ReadColumnWithIndexFromFile(filePath, 3)).getTheColumn();
		List<String> Normalized_ROE = 
				(new ReadColumnWithIndexFromFile(filePath, 4)).getTheColumn();
		List<String> Normalized_Accrual = 
				(new ReadColumnWithIndexFromFile(filePath, 5)).getTheColumn();
		
		for(int i = 0; i < Read_date.getTheColumn().size(); i++)
		{
			DateModif date = new DateModif (Read_date.getTheColumn().get(i) );
			Sym_Year newSymYear = new Sym_Year(_sym, date.get_year_in_date());
			
			Integer year = date.get_year_in_date();
			if(Normalized_num_of_shares.get(i).equals("0"))		//double-check this
			{
				continue;
			}
			
			basicYearlyFinData To_insert1 = 
					new basicYearlyFinData	(_sym, Double.parseDouble(Normalized_book_value.get(i)), year);
			basicYearlyFinData To_insert2 = 
					new basicYearlyFinData (_sym, Double.parseDouble(Normalized_num_of_shares.get(i)), year);
			basicYearlyFinData To_insert3 = 
					new basicYearlyFinData (_sym,Double.parseDouble(Normalized_ROE.get(i)), year);		
			basicYearlyFinData To_insert4 = 
					new basicYearlyFinData (_sym,Double.parseDouble(Normalized_Accrual.get(i)), year);
			
			if(To_insert1.getVal().isNaN() || To_insert2.getVal().isNaN() ||
				To_insert3.getVal().isNaN() || To_insert4.getVal().isNaN() ||
				To_insert1.getVal().equals(null) || To_insert2.getVal().equals(null) ||
				To_insert3.getVal().equals(null) || To_insert4.getVal().equals(null) )
					continue;
			
			_perYearBookVal.	put	(newSymYear,  To_insert1);
			_perYearNumShares.put	(newSymYear,  To_insert2);
			_perYearROE.put	(newSymYear,  To_insert3);
			_perYearAccrual.put	(newSymYear,  To_insert4);
			_allCompanyFinFundamYears.add(newSymYear.get_year());
		}
		
	}
	
	/**
	 * Delete the inconsistent entries that have data for some but not all parameters.
	 * NaN values are already filtered before they are stored in the _perYear data.
	 */
	/*
	public void filter_inconsistent_entries()
	{
		Set<Sym_Year> konsistentEntries = _perYearMarketVal.keySet();
		konsistentEntries.retainAll(_perYearBookVal.keySet());
		konsistentEntries.retainAll(_perYearNumShares.keySet());
		konsistentEntries.retainAll(_perYearROE.keySet());
		konsistentEntries.retainAll(_perYearAccrual.keySet());
		//konsistentEntries.retainAll(All_company_fin_fundam_years);
		System.out.println("konsistentEntries size:" + konsistentEntries.size());
		
				
		for(Sym_Year key : konsistentEntries)
		{
			Set<Sym_Year> Keys = _perYearAccrual.keySet();
			if(!allCompanyFinFundamYears.contains(key.get_year())	//Comp_quotes doesn't have such year
			){
				System.out.println("\n\n\nyear inconsistent:" + key.get_year() + " removed");
				_perYearBookVal.remove(key);
				_perYearNumShares.remove(key);
				_perYearROE.remove(key);
				_perYearAccrual.remove(key);
				allCompanyFinFundamYears.remove(key.get_year());
			}
			else
				System.out.println("\n\n\nyear consistent:" + key.get_year() );
				
		}
	}
	*/
	
	public void deriveAdditionalFinParameters(CompanyQuotes companyQuotes)
	{	
		_perYearMarketVal.clear();
		_perYearSize.clear();
		_perYearBMRatio.clear();
		_allCompanyExtendedFinParamYears.clear();
		
		for(Sym_Year S_D : _perYearBookVal.keySet())
		{
			Integer year = S_D.get_year();
			
			/*
			if(!companyQuotes.Get_all_company_quotes_years().contains(year))
			{
				System.out.println("No such year in Comp_quotes:" + year);
				continue;
			}
			*/
			
			Double avgPerYQuotesClosingPrice = companyQuotes.getAvgPerYQuotesClosingPrice().get(year);
			
			if(avgPerYQuotesClosingPrice == null)
				System.out.println("companyQuotes.getAvgPerYQuotesClosingPrice().get(year) for year " + year + " doesn't exist");
			else
			if(_perYearNumShares.get(S_D).getVal() * avgPerYQuotesClosingPrice == 0)
				continue;
			
			else
			{
				_perYearMarketVal.put	(S_D, 	_perYearNumShares.get(S_D).getVal() * avgPerYQuotesClosingPrice);	//not sure if close_price
				
				/*System.out.println("Finances for year:" + year + " are: per_year_market_val:" + per_year_market_val.get(year) + 
						" ,per_year_num_shares:" + per_year_num_shares.get(year) + " ,per_year_ROE:" + per_year_ROE.get(year) + 
						" ,per_year_accrual:" + per_year_accrual.get(year) + " ,avg_close_price_y:" + avg_close_price_y + 
						" ,per_year_market_val:" + per_year_market_val.get(year));*/
				
				_perYearBMRatio.put	(S_D ,	Math.log10(_perYearBookVal.get(S_D).getVal() / _perYearMarketVal.get(S_D)));
				_perYearSize.put(S_D , Math.log10(_perYearMarketVal.get(S_D)));
				_allCompanyExtendedFinParamYears.add(year);
				
				if(_perYearMarketVal.size()==1)		//first set of parameters, hence oldest
					_oldestYear = year;
				
				_earliestYear = year;		//it will assign up to the last newest news
		
			}
		}
	}

}
