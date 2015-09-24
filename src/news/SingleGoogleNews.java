package news;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import other_structures.DateModif;

public class SingleGoogleNews {
	String sym;
	DateModif _dateOfNews = new DateModif();
	Integer _yearOfNews;
	String _title;
	String _description;
	Boolean _isRelevant;
	String _source = "";

	
	public SingleGoogleNews(String Sym, DateModif date, Integer Year, String Titl, String Descript, String sourc)	//for now source will be unknown (null value) 
	{
		sym = Sym;
		_dateOfNews = date;
		_yearOfNews = Year;
		_title = Titl;
		_description = Descript;
		_source = sourc;
	}
	
	
	/**
	 * Returns true if SUE_t of prev year exists,consider the avg,  AND  
	 * SUE_t last of the company is with date > date of the news, AND
	 * Fin_param of prev year exists , there is one per year anyway, AND 
	 * Quotes of prev year exists (avg_per_year), AND
	 * Dividend of any prev date exists
	 * 
	 * @param allCompanySUE_Years : HashSet<Integer>
	 * @param earliestSUEDate : Date_modif
	 * @param allCompanyFinFundamYears : HashSet<Integer>
	 * @param allCompanyQuotesYears : HashSet<Integer>
	 * @param allCompanyQuotes : Map<Date_modif , Double>
	 * @param allCompanyDividendYears : Set<Integer>
	 * 
	 * @return Boolean
	 * 
	 */
	public Boolean isRelevant (HashSet<Integer> allCompanySUE_Years, 
			HashSet<Integer> allCompanyFinFundamYears, HashSet<Integer> allCompanyQuotesYears, 
			Map<DateModif , Double> allCompanyQuotes, Set<Integer> allCompanyDividendYears) throws ParseException
			{
				int prevYearOfNews = _yearOfNews - 1;
				/*System.out.println("prevYearOfNews:" + _dateOfNews.get_string_date() + " - " + _dateOfNews.get_prev_day_as_datemodif().get_string_date() +
						", news_prev_year:" + prevYearOfNews);
				System.out.println(allCompanySUE_Years.contains(prevYearOfNews));
				//System.out.println(All_company_SUE_years.contains(year_news));
				System.out.println(allCompanyFinFundamYears.contains(prevYearOfNews));
				System.out.println(allCompanyQuotesYears.contains(prevYearOfNews));
				System.out.println(allCompanyQuotes.keySet().contains(_dateOfNews.get_prev_day_as_datemodif()));	//end of prev day price
				System.out.println(allCompanyQuotes.keySet().contains(_dateOfNews));	//end of day price
				System.out.println(allCompanyDividendYears.contains(prevYearOfNews));
				*/
				if(allCompanySUE_Years.contains(_yearOfNews) && //All_company_SUE_years.contains(year_news)  &&
						allCompanyFinFundamYears.contains(_yearOfNews) && 
						allCompanyQuotesYears.contains(_yearOfNews) &&  allCompanyDividendYears.contains(_yearOfNews) &&
						allCompanyQuotes.keySet().contains(_dateOfNews.get_prev_day_as_datemodif()) &&
						allCompanyQuotes.keySet().contains(_dateOfNews))
				{
					System.out.println("Relevant news");
					return true;
				}
				
				else
				{
					System.out.println("Irrelevant news");
					return false;
				}
			}
}
