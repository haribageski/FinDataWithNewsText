package neural_network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import other_structures.DateModif;
import other_structures.Sym_Date;
import other_structures.Sym_Year;
import company.Company;

public class Training_X_Y_matrix {
	
	static Set<Sym_Date> _symDates = new HashSet<Sym_Date>();
	public Training_X_Y_matrix(){}
	
	public static Set<Sym_Date> getSymDates()
	{
		return _symDates;
	}
	
	
	/**
	 * Returns the number of filled columns with samples (and the rest of the allocated memory of the X and Y arrays 
	 * being empty)
	 * @param symDatesForTrainingML
	 * @param mapOfCompanies
	 * @param X
	 * @param Y
	 * @param trainingSize
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("resource")
	public static void Create_X_Y_finnance_Matrix(Set<Sym_Date>symDatesForTrainingML, Map<String, Company> mapOfCompanies,
			Double [][] X, Double[][] Y , int [] trainingSize) throws FileNotFoundException, UnsupportedEncodingException
	{
		int skipped = 0;
		PrintWriter writer;
		writer = new PrintWriter("sym_date.txt", "UTF-8");
		
		for(int i=0; i < symDatesForTrainingML.size(); i++)
		{
			boolean allRecordsOfSymSkipped = true;
			Sym_Date symDate = new ArrayList<Sym_Date>(symDatesForTrainingML).get(i);
			String sym = symDate.get_sym();
			Integer year = symDate.get_Date_modif().get_year_in_date();
			//System.out.println("news in year:" + year); 
			Sym_Year currentSymYear = new Sym_Year(sym, year);
			Sym_Year S_Y_prev = new Sym_Year(sym, year-1);
			
			
			//System.out.println(mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().get(symDate).isNaN() );
			
			
			try {
				//make sure the current year is available in all parameters to learn the quote for a date in the current year
				if(!mapOfCompanies.get(sym).get_Company_SUE().Get_avg_per_y_SUE().containsKey(year) ||
						!mapOfCompanies.get(sym).get_Company_Dividend().get_avg_dividends().containsKey(year) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyBookVal().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyShares().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyROE().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyAccrual().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyMarketVals().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllBM_Ratios().containsKey(currentSymYear)  || 
						!mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyYearSizes().containsKey(currentSymYear) ||
						!mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().containsKey(symDate.get_Date_modif().get_prev_day_as_datemodif()) ||
						(!mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().containsKey(symDate.get_Date_modif() ) && 
								Y != null) ||
						
						mapOfCompanies.get(sym).get_Company_SUE().Get_avg_per_y_SUE().get(year).isNaN() ||
						mapOfCompanies.get(sym).get_Company_Dividend().get_avg_dividends().get(year).isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyBookVal().get(currentSymYear).getVal().isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyShares().get(currentSymYear).getVal().isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyROE().get(currentSymYear).getVal().isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyAccrual().get(currentSymYear).getVal().isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyMarketVals().get(currentSymYear).isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllBM_Ratios().get(currentSymYear).isNaN() ||
						mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyYearSizes().get(currentSymYear).isNaN() ||
						mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().get(symDate.get_Date_modif().get_prev_day_as_datemodif()).isNaN() ||
						mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().get(symDate.get_Date_modif()).isNaN() 
						
					){
					//System.out.println(sym + " skipped with index = " + i);
					skipped++;
					continue;
				}
				else
				{
					//System.out.println("containsKey:" + Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_book_val().containsKey(S_Y_prev));
					X[0][i-skipped] = mapOfCompanies.get(sym).get_Company_SUE().Get_avg_per_y_SUE().get(year);
					X[1][i-skipped] = mapOfCompanies.get(sym).get_Company_Dividend().get_avg_dividends().get(year);
					X[2][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyBookVal().get(currentSymYear).getVal();
					X[3][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyShares().get(currentSymYear).getVal();
					X[4][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyROE().get(currentSymYear).getVal();
					X[5][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyAccrual().get(currentSymYear).getVal();
					X[6][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyMarketVals().get(currentSymYear);
					X[7][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllBM_Ratios().get(currentSymYear);
					X[8][i-skipped] = mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().get(symDate.get_Date_modif().get_prev_day_as_datemodif());
					X[9][i-skipped] = mapOfCompanies.get(sym).get_Fin_fundamentals().getAllCompanyYearSizes().get(currentSymYear);
					
					Double currentDateQuote = mapOfCompanies.get(sym).get_Company_Qoutes().get_quotes_map().get(symDate.get_Date_modif());
					if((currentDateQuote - X[8][i-skipped]) < 0)
					{
						Y[i-skipped][0] =  1.0;
						Y[i-skipped][1] =  0.0;
					}
					else
					{
						Y[i-skipped][1] =  1.0;
						Y[i-skipped][0] =  0.0;
					}
					/*
					if( currentDateQuote.compareTo(X[8][i-skipped])< 0)
					{
						Y[i-skipped] = 1.0; 
					}
					else
					{
						Y[i-skipped] = 0.0; 
					}*/
					//System.out.println(Companies_match.get(Sym) + " added for training");
					
					writer.println("sym :" + symDate.get_sym() + " with date" + symDate.get_Date_modif().dateModifToString());	
					
					_symDates.add(symDate);
					allRecordsOfSymSkipped = false;
					trainingSize[0] = i - skipped + 1;
				}
			} catch (ParseException e) {
				System.out.println("Error in accessing some parameter inside Training_X_Y.Create_X_Y_Training_Matrix:" + e);
			}
			/*if(allRecordsOfSymSkipped == true)
				mapOfCompanies.remove(sym);*/
		} 
		
		System.out.println("X training matrix size before correction:" + X[0].length);
		System.out.println("Create_X_Y_finnance_Matrix done, _symDates size:" + _symDates.size());
	}
	
	/*public static void Create_X_finnance_Matrix	(List<String>Repeader_sym_for_training_matching, List<Date_modif> Dates_for_training_matching, 
			Map<String, Company> Companies_match, Double [][] X) throws IOException, ParseException
	*/

}
