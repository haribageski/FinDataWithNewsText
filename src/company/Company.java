package company;

import java.awt.List;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import daily_financial_parameters.BasicDailyFinData;
import daily_financial_parameters.CompanyDividend;
import daily_financial_parameters.CompanyQuotes;
import daily_financial_parameters.CompanySUE;
import news.CompanyNewsSentiment;
import other_structures.DateModif;
import other_structures.Sym_Date;
import other_structures.Sym_Year;
import yearly_financial_parameters.BasicYearlyFinData;
import yearly_financial_parameters.CompanyYearlyFinFundamentals;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Company 
{
	String Sym;
	//String Long_Name; 	TO DO
	
	CompanyQuotes _compQuotes;
	CompanyDividend _compDivid ;
	CompanyYearlyFinFundamentals _compFinFundamPerYear ;
	CompanySUE _companySUE;
	CompanyNewsSentiment News;
	StanfordCoreNLP pipeline;
	HashSet<Integer> _consistentYears;
	Set<DateModif> _consistentDates;
	//Set<Sym_Year> companySymYears ;
	
	public Company(String Symbol, StanfordCoreNLP pipeln) throws IOException, ParseException
	{
		Sym = Symbol;
		pipeline = pipeln;
		_compQuotes = new CompanyQuotes(Sym);
		_compDivid = new CompanyDividend(Sym);
		_companySUE = new CompanySUE(Sym);
		_compFinFundamPerYear = new CompanyYearlyFinFundamentals(Sym);
		News = new CompanyNewsSentiment(Sym, pipeline);
	}
	public void addConsistentYear (Integer year)
	{
		_consistentYears.add(year);
	}
	
	public void add_quote(BasicDailyFinData Q)
	{
		if(Q.getVal().isNaN())
		{
			System.out.println("add_quote failes since NaN val");
			return;
		}
		_compQuotes.add_quote(Q);
	}
	public void add_dividend(BasicDailyFinData Divi)
	{
		if(Divi.getVal().isNaN())
		{
			System.out.println("add_dividend failes since NaN val");
			return;
		}
		_compDivid.add_dividend( Divi);
	}
	public void add_SUE(BasicDailyFinData SU)
	{
		if(SU.getVal().isNaN())
		{
			System.out.println("add_SUE failes since NaN val");
			return;
		}
		_companySUE.add_SUE(SU);
	}
	
	public void add_ROE(Sym_Year S_D,  BasicYearlyFinData Fin_Unit)
	{
		if(Fin_Unit.getVal().isNaN())
		{
			System.out.println("addROE failes since NaN val");
			return;
		}
		_compFinFundamPerYear.addROE(S_D, Fin_Unit);
	}
	
	public void add_share(Sym_Year S_D,  BasicYearlyFinData Fin_Unit)
	{
		if(Fin_Unit.getVal().isNaN())
		{
			System.out.println("addShare failes since NaN val");
			return;
		}
		_compFinFundamPerYear.addShare(S_D, Fin_Unit);
	}
	
	public void add_book_val(Sym_Year S_D,  BasicYearlyFinData Fin_Unit)
	{
		if(Fin_Unit.getVal().isNaN())
		{
			System.out.println("addBookVal failes since NaN val");
			return;
		}
		_compFinFundamPerYear.addBookVal(S_D, Fin_Unit);
	}
	
	public void add_accrual(Sym_Year S_D,  BasicYearlyFinData Fin_Unit)
	{
		if(Fin_Unit.getVal().isNaN())
		{
			System.out.println("addAccrual failes since NaN val");
			return;
		}
		_compFinFundamPerYear.addAccrual(S_D, Fin_Unit);  
	}
	
	
	public CompanyQuotes get_Company_Qoutes()
	{
		return _compQuotes;
	}
	public CompanyDividend get_Company_Dividend()
	{
		return _compDivid;
	}
	public CompanyYearlyFinFundamentals get_Fin_fundamentals()
	{
		return _compFinFundamPerYear;
	}
	public CompanySUE get_Company_SUE()
	{
		return _companySUE;
	}
	
	public CompanyNewsSentiment get_Company_all_Google_news()
	{
		return News;
	}
	
	
	
	public Map<DateModif, Double[]> getNewsAvgSentiPerDateTitle()
	{
		return News.getAvgSentiPerDateTitle();
	}
	
	public Map <DateModif, Double[]> getNewsAvgSentiPerDateDescript()
	{
		return News.getAvgSentiPerDateDescript();
	}
	
	
	public void readFinancialParameters() throws IOException, ParseException
	{
		_compQuotes.read_company_quotes();
		System.out.println("after readFinancialParameters.read_company_quotes:" + _compQuotes.get_All_company_quotes().size());
		_compDivid.readCompanyDividends();		//not normalized
		System.out.println("after readFinancialParameters.readCompanyDividends:" + _compDivid.get_All_company_dividends().size());
		_compFinFundamPerYear.read_copany_fin_fundamentals();	//reread after normalizing Comp_Quotes
		System.out.println("after readFinancialParameters.read_copany_fin_fundamentals:" + _compFinFundamPerYear.getAllCompanyBookVal().size());
		_companySUE.read_company_SUE();
		System.out.println("after readFinancialParameters.read_company_SUE:" + _companySUE.get_All_company_SUE().size());
		
	}
	
	public void  findConsistentYearsFromFinancialData()
	{
		_consistentYears = _compQuotes.Get_all_company_quotes_years();
		System.out.println("_consistentYears size after adding _compQuotes:" + _consistentYears.size());
		_consistentYears.retainAll(_compDivid.Get_all_company_divi_years());
		System.out.println("_consistentYears size after adding _compDivid:" + _consistentYears.size());
		
		_compFinFundamPerYear.makeConsistentAllCompanyFinFundamYears();
		_consistentYears.retainAll(_compFinFundamPerYear.getAllCompanyFinFundamYears());
		System.out.println("_consistentYears size after adding _compFinFundamPerYear:" + _consistentYears.size());
		_consistentYears.retainAll(_companySUE.GET_all_company_SUE_years());
		System.out.println("_consistentYears size after adding _companySUE:" + _consistentYears.size());
		System.out.println("after findConsistentYearsFromFinancialData we get size of _consistentYears:" + _consistentYears.size());
	}
	
	
	public void  findConsistentDatesFromFinancialData()
	{
		_consistentDates = _compQuotes.get_quotes_map().keySet();
		System.out.println("_consistentYears size after adding _compQuotes:" + _consistentYears.size());
		
		HashSet<DateModif> tempDates = new HashSet<DateModif>();
		for(DateModif dateModif : _consistentDates)
		{
			if(_consistentYears.contains(dateModif.get_year_in_date()))
				tempDates.add(dateModif);
		}
		_consistentDates = tempDates;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Filter inconsistent data in _compFinFundamPerYear, _compQuotes, _companySUE, _compDivid
	 */
	public void filterInconsistentFinDataInMapsOfParameters()
	{
		//if findConsistentYearsFromFinancialData() is not called first then we will get an error
		
		System.out.println("Company.filterInconsistentFinData()..." );
		@SuppressWarnings({ "rawtypes" })
		ArrayList companySymYears = new ArrayList<Sym_Year>(_compFinFundamPerYear.getAllCompanyROE().keySet());
		ArrayList companySymDates;
		
		for(int i = 0;i < companySymYears.size(); i++)
		{
			Sym_Year symYear = (Sym_Year) companySymYears.get(i);
			Integer year = symYear.get_year();
			
			if(!_consistentYears.contains(year))
			{
				_compFinFundamPerYear.eraseAccrualAtDate(symYear);
				_compFinFundamPerYear.eraseBookValAtDate(symYear);
				_compFinFundamPerYear.eraseROEAtDate(symYear);
				_compFinFundamPerYear.eraseShareAtDate(symYear);
				_compFinFundamPerYear.eraseYear(symYear);
			}
			/*else
				System.out.println("year consistent:" + year);*/
		}
		
		companySymDates = new ArrayList<BasicDailyFinData> (_compQuotes.get_All_company_quotes());
		for(int i = 0; i < companySymDates.size(); i++)
		{
			BasicDailyFinData priceAtDate = (BasicDailyFinData) companySymDates.get(i);
			Integer year = priceAtDate.getDate().get_year_in_date();
			
			if(!_consistentYears.contains(year))
			{
				_compQuotes.eraseQuote(priceAtDate);
				_compQuotes.eraseYear(year);
			}
			/*else
				System.out.println("\n\n\ndate consistent:" + priceAtDate.get_date());*/
		}
		
		companySymDates =  new ArrayList<BasicDailyFinData> ( _compDivid.get_All_company_dividends());
		for(int i = 0;i < companySymDates.size(); i++)
		{
			BasicDailyFinData compDivid = (BasicDailyFinData) companySymDates.get(i);
			Integer year = compDivid.getDate().get_year_in_date();
			
			if(!_consistentYears.contains(year))
			{
				_compDivid.eraseDividend(compDivid);
				_compDivid.eraseYear(year);
			}
			/*
			else
				System.out.println("\n\n\ndate consistent:" + compDivid.get_date());*/
		}
		
		companySymDates =  new ArrayList<BasicDailyFinData> ( _companySUE.get_All_company_SUE());
		for(int i = 0;i < companySymDates.size(); i++)
		{
			BasicDailyFinData compSUE = (BasicDailyFinData) companySymDates.get(i);
			Integer year = compSUE.getDate().get_year_in_date();
			
			if(!_consistentYears.contains(year))
			{
				_companySUE.eraseSUE(compSUE);
				_companySUE.eraseYear(year);
			}
			/*
			else
				System.out.println("\n\n\ndate consistent:" + compSUE.get_date());*/
		}
	}
	
	/**
	 * Set all needed financial parameters such as average data, 
	 * oldest and earlies data in a year or in overall, 
	 * set all years and dates
	 */
	public void setFinancialParameters() 
	{
		System.out.println("Company.setFinancialParameters():");
		_compQuotes.set_parameters();
		_compDivid.setAllParameters();
		_compFinFundamPerYear.deriveAdditionalFinParameters(_compQuotes);	
		
		//_compFinFundamPerYear.filter_inconsistent_entries();
		
		_companySUE.set_parameters();
	}
	
	
	/**
	 * First try to read sentiment from file to avoid costly sentiment evaluation. If that returns false we call 
	 * News.evalSentiForAllNewsOfACompany().
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void readOrEvalSentiOfNews() throws IOException, ParseException		//only after parameters set
	{
		System.out.println("readOrEvalSentiOfNews:");
		//read_parameters();	//CHECK THIS AGAIN!!!!!!!!!!!
		
		//First try to read sentiment from file to avoid costly sentiment evaluation
		boolean sentiFileExists = News.readSentiFile();
		
		if(!sentiFileExists)	//finds sentiments and sets the value of avg_senti_per_date  and  total_news
			News.evalSentiForAllNewsOfACompany(
				_companySUE.GET_all_company_SUE_years(), 
				_compFinFundamPerYear.getAllCompanyFinFundamYears(), 
				_compQuotes.Get_all_company_quotes_years(), 
				_compQuotes.get_quotes_map(),
				_compDivid.Get_all_company_divi_years()
				);
		System.out.println("Company.readOrEvalSentiOfNews() -> getAvgSentiPerDateTitle:");
		for(int i = 0; i<News.getAvgSentiPerDateTitle().values().size(); i++)
			System.out.print(News.getAvgSentiPerDateTitle().values().toArray()[i].toString());
	}
	
	
	
	/**
	 * First find all the dates for which there is a news.
	 * Then find the intersection with the consistent years of Financial data.
	 * Then erase Financial entries for which there is no News.
	 */
	public Set<DateModif> makeConsistentNewsWithFinancialData()
	{
		findConsistentDatesFromFinancialData();
		Set<DateModif> datesFromNews = new HashSet<DateModif>();
		Set<Integer> yearsFromNews = new HashSet<Integer>();
		Set<DateModif> tempDatesFromNews = new HashSet<DateModif>();
		
		datesFromNews = News.getAvgSentiPerDateDescript().keySet();
		//consider only news with date in which there are financial parameters available
		for(DateModif dateModif : datesFromNews)
		{
			if(_consistentYears.contains(dateModif.get_year_in_date()))
				yearsFromNews.add(dateModif.get_year_in_date());
			if(_consistentDates.contains(dateModif))
				tempDatesFromNews.add(dateModif);
		}
		
		datesFromNews = tempDatesFromNews;
		//keep only financial years for which year there is a news
		if(!yearsFromNews.isEmpty())
			_consistentYears.retainAll(yearsFromNews);
		//keep only financial dates for which date there is a news
		if(!datesFromNews.isEmpty())
			_consistentDates.retainAll(datesFromNews);
		
		filterInconsistentFinDataInMapsOfParameters();
		System.out.println("After makeConsistentNewsWithFinancialData() we get _consistentYears size:" + _consistentYears.size());
		
		return _consistentDates;
	}
	
	//HashSet<Employee> Team = new HashSet<Employee>(); //may not use it at all since not enough data in Employee		TO DO
}
