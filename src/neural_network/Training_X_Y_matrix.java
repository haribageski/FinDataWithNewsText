package neural_network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import other_structures.DateModif;
import other_structures.Sym_Date;
import other_structures.Sym_Year;

import company.Company;

public class Training_X_Y_matrix {
	
	public Training_X_Y_matrix(){}
	
	@SuppressWarnings("resource")
	public static void Create_X_Y_finnance_Matrix(List<String>symbolsForTraining, List<DateModif> datesForTraining, 
			Map<String, Company> mapOfCompanies, Double [][] X, Double[] Y , int [] trainingSize) throws FileNotFoundException, UnsupportedEncodingException
	{
		int skipped = 0;
		PrintWriter writer;
		writer = new PrintWriter("sym_date.txt", "UTF-8");
		
		for(int i=0; i<symbolsForTraining.size(); i++)
		{
			String Sym = symbolsForTraining.get(i);
			Sym_Date S_D = new Sym_Date(Sym, datesForTraining.get(i));
			Integer year = S_D.get_Date_modif().get_year_in_date();
			//System.out.println("news in year:" + year); 
			Sym_Year S_Y_prev = new Sym_Year(Sym, year-1);
			
			//System.out.println("i =" + i + ",sym:" + Sym + ",repeader_sym_for_training_matching :" + Repeader_sym_for_training_matching.size() + ", S_Y_prev:" + S_Y_prev.get_year());
			/*
			Companies_match.get(Sym);
			Companies_match.get(Sym).get_Fin_fundamentals();
			*/
			/*
			System.out.println(Companies_match.get(Sym).get_Company_SUE().Get_avg_per_y_SUE().get(year-1).isNaN());
			System.out.println(Companies_match.get(Sym).get_Company_Dividend().get_avg_dividends().get(year-1).isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_book_val().get(S_Y_prev).get_val().isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_shares().get(S_Y_prev).get_val().isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_ROE().get(S_Y_prev).get_val().isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_accrual().get(S_Y_prev).get_val().isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_market_vals().get(S_Y_prev).isNaN());
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_BM_ratios().get(S_Y_prev).isNaN() );
			System.out.println(Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_year_sizes().get(S_Y_prev).isNaN() );
			try {
				System.out.println(Companies_match.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif().get_prev_day_as_datemodif()).isNaN() );
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(Companies_match.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif()).isNaN() );
			*/
			
			try {
				//make sure the previous year is available in all parameters to learn the quote for the current year
				if(!mapOfCompanies.get(Sym).get_Company_SUE().Get_avg_per_y_SUE().containsKey(year-1) ||
						!mapOfCompanies.get(Sym).get_Company_Dividend().get_avg_dividends().containsKey(year-1) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyBookVal().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyShares().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyROE().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyAccrual().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyMarketVals().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllBM_Ratios().containsKey(S_Y_prev)  || 
						!mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyYearSizes().containsKey(S_Y_prev) ||
						!mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().containsKey(S_D.get_Date_modif().get_prev_day_as_datemodif()) ||
						(!mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().containsKey(S_D.get_Date_modif() ) && 
								Y != null) ||
						
						//Companies_match.get(Sym).get_Company_SUE().Get_avg_per_y_SUE().get(year-1).isNaN() ||
						mapOfCompanies.get(Sym).get_Company_Dividend().get_avg_dividends().get(year-1).isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyBookVal().get(S_Y_prev).getVal().isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyShares().get(S_Y_prev).getVal().isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyROE().get(S_Y_prev).getVal().isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyAccrual().get(S_Y_prev).getVal().isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyMarketVals().get(S_Y_prev).isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllBM_Ratios().get(S_Y_prev).isNaN() ||
						mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyYearSizes().get(S_Y_prev).isNaN() ||
						mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif().get_prev_day_as_datemodif()).isNaN() ||
						mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif()).isNaN() 
						
					){
					System.out.println(Sym + " skipped with index = " + i);
					skipped++;
					continue;
				}
				else
				{
					//System.out.println("containsKey:" + Companies_match.get(Sym).get_Fin_fundamentals().get_all_company_book_val().containsKey(S_Y_prev));
					X[0][i-skipped] = 1.0;// Companies_match.get(Sym).get_Company_SUE().Get_avg_per_y_SUE().get(year-1);
					X[1][i-skipped] = mapOfCompanies.get(Sym).get_Company_Dividend().get_avg_dividends().get(year-1);
					X[2][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyBookVal().get(S_Y_prev).getVal();
					X[3][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyShares().get(S_Y_prev).getVal();
					X[4][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyROE().get(S_Y_prev).getVal();
					X[5][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyAccrual().get(S_Y_prev).getVal();
					X[6][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyMarketVals().get(S_Y_prev);
					X[7][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllBM_Ratios().get(S_Y_prev);
					X[8][i-skipped] = mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif().get_prev_day_as_datemodif());
					X[9][i-skipped] = mapOfCompanies.get(Sym).get_Fin_fundamentals().getAllCompanyYearSizes().get(S_Y_prev);
					
					if(Y!=null)
						Y[i-skipped] = mapOfCompanies.get(Sym).get_Company_Qoutes().get_quotes_map().get(S_D.get_Date_modif());
					//System.out.println(Companies_match.get(Sym) + " added for training");
					
					writer.println("sym :" + S_D.get_sym() + " with date" + S_D.get_Date_modif().dateModifToString());	
					
				}
			} catch (ParseException e) {
				System.out.println("Error in accessing some parameter inside Training_X_Y.Create_X_Y_Training_Matrix:" + e);
			}
		} 
		trainingSize[0] = symbolsForTraining.size() - skipped;
		
		//erase empty columns of X
		Double[][]XCorrected = new Double[X.length][trainingSize[0]];
		for(int i = 0; i<X.length; i++)
			for(int j = 0; j< trainingSize[0]; j++)
				XCorrected[i][j] = X[i][j];
		X = XCorrected;
		System.out.println("X training matrix size after correction:" + X[0].length);
	}
	
	/*public static void Create_X_finnance_Matrix	(List<String>Repeader_sym_for_training_matching, List<Date_modif> Dates_for_training_matching, 
			Map<String, Company> Companies_match, Double [][] X) throws IOException, ParseException
	*/

}
