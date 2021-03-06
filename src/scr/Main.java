package scr;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Properties;
import java.util.TreeSet;

import company.Company;
import daily_financial_parameters.BasicDailyFinData;
import neural_network.CrossValidationMatrix;
import neural_network.Predict;
import neural_network.RepresentationNetwork;
import neural_network.Training_X_Y_matrix;
import news.AllCompaniesSentimentOfNews;
import other_structures.DateModif;
import other_structures.Sym_Date;
import other_structures.Sym_Year;
import preprocessing.NormalizeParameterInAllEntries;
import preprocessing.ToBeFilteredFromOutliers;
import reading_data_from_file.ReadAndWriteAllSym;
import reading_data_from_file.ReadUniqueSymFromFile;
import yearly_financial_parameters.BasicYearlyFinData;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Main 
{
	final static Charset ENCODING = StandardCharsets.UTF_8;
	static StanfordCoreNLP pipeline;
	static int parameters = 16;
	static int _trainingSize = 0;
	static int _crossValidatSize = 0;
	static int _testingSize = 0;
	static int _K = 2;
	//static List<String> _symbols;
	static Set<Sym_Date> _symDates = new HashSet<Sym_Date>();
	static List<String> _symbolsInSymDates = new ArrayList<String>();;
	static Set<Sym_Date> _symDatesForTrainingML = new HashSet<Sym_Date>();;
	
	static Set<Sym_Date> _symDatesForCrossValidationML = new HashSet<Sym_Date>();
	static List<Sym_Date> _symDatesForTestingML;
	//static List<DateModif> _dates;
	//static List<String> _symbolsForTrainingML;
	//static List<DateModif> _datesForTrainingML;

	// non sorted , needed for normalization and to create X matrix
	static Map<Sym_Year, BasicYearlyFinData> Column_shares = new TreeMap<Sym_Year, BasicYearlyFinData>();
	static Map<Sym_Year, BasicYearlyFinData> Column_book_val = new TreeMap<Sym_Year, BasicYearlyFinData>();
	static Map<Sym_Year, BasicYearlyFinData> Column_ROE = new TreeMap<Sym_Year, BasicYearlyFinData>();
	static Map<Sym_Year, BasicYearlyFinData> Column_accrual = new TreeMap<Sym_Year, BasicYearlyFinData>();
	static Map<Sym_Year, BasicYearlyFinData> tempMapY = new TreeMap<Sym_Year, BasicYearlyFinData>();

	static Map<Sym_Date, BasicDailyFinData> Column_dividends = new TreeMap<Sym_Date, BasicDailyFinData>();
	static Map<Sym_Date, BasicDailyFinData> Column_qoutes = new TreeMap<Sym_Date, BasicDailyFinData>();
	static Map<Sym_Date, BasicDailyFinData> Column_SUEs = new TreeMap<Sym_Date, BasicDailyFinData>();
	static Map<Sym_Date, BasicDailyFinData> tempMap = new TreeMap<Sym_Date, BasicDailyFinData>();

	static Map<String, Company> companiesMap = new TreeMap<String, Company>();
	static Map<String, Company> trainingCompaniesMap = new TreeMap<String, Company>();
	static Map<String, Company> crossValidationCompaniesMap = new TreeMap<String, Company>();
	static Map<String, Company> testingCompaniesMap = new TreeMap<String, Company>();

	static NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData> normalizedColumnQuotes;
	
	static RepresentationNetwork neuralNet;

	public Main() {	}

	public Map<String, Company> returnCompaniesMap() 
	{
		return companiesMap;
	}

	static void initiateStanfordNLP() 
	{
		Properties props = new Properties();
		props.put("annotators", 
				"tokenize, ssplit, pos, lemma, parse, sentiment, ner, dcoref");
		props.put("dcoref.score", true);
		pipeline = new StanfordCoreNLP(props);
	}

	public StanfordCoreNLP getPipe() 
	{
		initiateStanfordNLP();
		return pipeline;
	}

	public static void main(String... aArgs) throws IOException, ParseException 
	{
		String uniqueSymPath = "D:\\my documents\\Senior_Project_datasets\\unique_company_symbols.txt";
		ReadAndWriteAllSym storeConsistentSymbols = new ReadAndWriteAllSym(
				uniqueSymPath);		//the constructor also creates file with consistent symbols
		
		System.out.println("Start");
		/* 
		 * Main.initiate_Stanford_NLP(); //DO NOT FORGET THIS, required when
		 * generating sentiment files!!!!!!!!!
		 */

		List<String> Symbols = new ReadUniqueSymFromFile(uniqueSymPath).getLinesFromFile();

		//initiateStanfordNLP();	//TODO don't forget this to uncomment
		
		setColumnMapsOfParameters(Symbols);
					
		filterOutliers();
				
		normalizeParameters();		//TODO check for division with zero or any operation producing NaN
		
		regenerateCompanies();	
		
		filterCompaniesAndSetOtherParameters();
		
		prepareForMachineLearning();
		
		divideCompaniesMapTrainCrossValTest();
		
		// Prepare neural network representation
		System.out.println("Neural network starts");
		System.out.println("trainingCompaniesMap size:" + trainingCompaniesMap.size());
		
		Double [][] XsCrossValidat = createCrossValidationMatrix();
		List <Sym_Date> symDatesCrossValidatList = new ArrayList<Sym_Date>(_symDatesForCrossValidationML);
		
		Double [][] YCrossValidat = new Double [XsCrossValidat.length][_K];
		for(int i = 0; i < XsCrossValidat.length; i++)
		{
			Sym_Date symDate = symDatesCrossValidatList.get(i);
			System.out.println("symDate:" + symDate.get_Date_modif().toString());
			if(Column_qoutes.get(symDate) == null)
				System.out.println("Cannot find data in Column_qoutes with sym indexed " + i);
			Sym_Date sD = new Sym_Date(symDate.get_sym(), symDate.get_Date_modif().get_prev_day_as_datemodif());
			if(!Column_qoutes.containsKey(sD) || !Column_qoutes.containsKey(symDate))
			{
				YCrossValidat[i][0] = YCrossValidat[i][1] = 0.0;
			}
			else
			{
				if( (Column_qoutes.get(symDate).getVal() - Column_qoutes.get(sD).getVal()) < 0)
				{
					YCrossValidat[i][0] = 1.0;
					YCrossValidat[i][1] = 0.0;
				}
				else
				{
					YCrossValidat[i][0] = 0.0;
					YCrossValidat[i][1] = 1.0;
				}
				//		 			.compareTo(	Column_qoutes.get());
				/*System.out.println("index:" + i + 
						", predicterVal:" + predictedVal[i][0].toString() + " " + predictedVal[i][1].toString() +
						",original value:" + YCrossValidat[i][0].toString() + " " + YCrossValidat[i][1].toString());*/
			}
		}
		
		neuralNet = new RepresentationNetwork( _symDatesForTrainingML, trainingCompaniesMap, _trainingSize, parameters, 
												XsCrossValidat, YCrossValidat);
		
		// Initiate neural network
		neuralNet.Learn();	
		
		
		System.out.println("Neural network learning done");		
		
		
		Double[][] predictedVal = neuralNet.makePrediction();
		for(int i = 0; i < predictedVal.length; i++)
		{
			System.out.println("Predicted value:" + predictedVal[i][0] + " " + predictedVal[i][1] +
					", and original value:" + YCrossValidat[i][0] + " " + YCrossValidat[i][1]);
		}
		
		
	}
	
	
	static Double[][] createCrossValidationMatrix() throws FileNotFoundException, UnsupportedEncodingException
	{
		
		Double [][] XsCrossValidatFinancial = new Double[_crossValidatSize][parameters - 6];	//used to store financial parameters (without senti parameters)
		Double []YCrossValidat = new Double[_crossValidatSize];
		_symDatesForCrossValidationML = CrossValidationMatrix.CreateX_Y_CrossValidationFinnanceMatrix(
				new HashSet<Sym_Date>(_symDatesForCrossValidationML), crossValidationCompaniesMap, 
				XsCrossValidatFinancial, YCrossValidat, _crossValidatSize);
		_crossValidatSize = _symDatesForCrossValidationML.size();
		
		Double[][]XCorrected = new Double[_crossValidatSize][parameters - 6];
		for(int j = 0; j < _crossValidatSize; j++)
			for(int i = 0; i < parameters - 6; i++)
				XCorrected[j][i] = XsCrossValidatFinancial[j][i];
		XsCrossValidatFinancial = XCorrected;
		
		Double[][] X_senti = new Double[_crossValidatSize][6];
		X_senti = AllCompaniesSentimentOfNews.makeX_FromSentimentCrossValidat(CrossValidationMatrix.getSymDates());
		System.out.println("RepresentationNetwork.X_senti size:" + X_senti.length + " x " + X_senti[0].length);
		
		Double [][]XsCrossValidatOverall = new Double[_crossValidatSize][parameters];
		XsCrossValidatOverall = RepresentationNetwork.appendToTheRight(XsCrossValidatFinancial , X_senti);
		
		return XsCrossValidatOverall;
	}
	
	
	
	
	
	

	public int get_num_of_parameters() {
		return parameters;
	}

	public RepresentationNetwork get_neural_net() {
		return neuralNet;
	}
	
	public static void printCompanyMatchMap() throws FileNotFoundException, UnsupportedEncodingException 
	{
		@SuppressWarnings("resource")
		PrintWriter writer = new PrintWriter("companiesMap.txt", "UTF-8");	
		for(Company company : companiesMap.values())
		{
			writer.println("company dividend:");
			for(BasicDailyFinData daily : company.get_Company_Dividend().get_All_company_dividends())
				writer.println(daily.getVal());
			writer.println("company sue:");
			for(BasicDailyFinData daily : company.get_Company_SUE().get_All_company_SUE())
				writer.println(daily.getVal());
			writer.println("company Quotes:");
			for(BasicDailyFinData daily : company.get_Company_SUE().get_All_company_SUE())
				writer.println(daily.getVal());
			writer.println("company BM_Ratios:");
			for(Double val : company.get_Fin_fundamentals().getAllBM_Ratios().values())
				writer.println(val);
			writer.println("company Accrual:");
			for(BasicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyAccrual().values())
				writer.println(val.getVal());
			writer.println("company Bookval:");
			for(BasicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyBookVal().values())
				writer.println(val.getVal());
			writer.println("company MarketVals:");
			for(Double val : company.get_Fin_fundamentals().getAllCompanyMarketVals().values())
				writer.println(val);
			writer.println("company ROE:");
			for(BasicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyROE().values())
				writer.println(val.getVal());
			writer.println("company Shares:");
			for(BasicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyShares().values())
				writer.println(val.getVal());
			writer.println("company size:");
			for(Double val : company.get_Fin_fundamentals().getAllCompanyYearSizes().values())
				writer.println(val);
		}
			
	}
	
	
	/**
	 * First read all financial parameter for each company.
	 * Then find consistent years and filter inconsistent data in Company maps
	 */
	static void setColumnMapsOfParameters(List<String>Symbols)
	{
		for (int f = 0; f < Symbols.size() && f < 150; f++)  
		{
			String Sym = Symbols.get(f);
			
			Company company = null;
			try {
				company = new Company(Sym, pipeline);
			} catch (IOException e) {
				System.out.println("Error in Main new Company():" + e);
				e.printStackTrace();
			} catch (ParseException e) {
				System.out.println("Error in Main new Company():" + e);
			}
			try {
				company.readFinancialParameters();
			} catch (IOException e) {
				System.out.println("Error in Main.company.readFinancialParameters():" + e);
			} catch (ParseException e) {
				System.out.println("Error in Main.company.readFinancialParameters():" + e);
			}
			company.findConsistentYearsFromFinancialData();
			company.filterInconsistentFinDataInMapsOfParameters();
			
			
			//IMPORTANT !!! after filtering outliers, when companies are regenerated call:
			/*
			 * company.readOrEvalSentiOfNews();
			 * company.makeConsistentNewsWithFinancialData();
			 * company.setFinancialParameters();  
			 */

			System.out.println("Company:" + Sym);

			
			/*
			 * Add the parameters to 'global' maps that is one map for all companies
			 */
			for (BasicDailyFinData D : company.get_Company_Dividend().get_All_company_dividends())
				Column_dividends.put(new Sym_Date(Sym, D.getDate()), D);
			for (BasicDailyFinData D : company.get_Company_Qoutes().get_All_company_quotes())
				Column_qoutes.put(new Sym_Date(Sym, D.getDate()), D);
			for (BasicDailyFinData D : company.get_Company_SUE().get_All_company_SUE())
				Column_SUEs.put(new Sym_Date(Sym, D.getDate()), D);

			Column_book_val.putAll(company.get_Fin_fundamentals().getAllCompanyBookVal());
			Column_shares.putAll(company.get_Fin_fundamentals().getAllCompanyShares());
			Column_ROE.putAll(company.get_Fin_fundamentals().getAllCompanyROE());
			Column_accrual.putAll(company.get_Fin_fundamentals().getAllCompanyAccrual());
		}
	}
	
	
	
	/** Filtering outliers using global maps to (first sorting them).
	 * When deleting outlier in one param. we also delete the entries with same key in the other params.
	 */
	static void filterOutliers()
	{
		HashSet<Sym_Date> outliersDates = new HashSet<Sym_Date>();
		HashSet<Sym_Year> outliersYears = new HashSet<Sym_Year>();
		
		
		outliersDates = (new  ToBeFilteredFromOutliers<Sym_Date, BasicDailyFinData>(Column_dividends)).filterFromOutliers();
		outliersDates.addAll((new  ToBeFilteredFromOutliers<Sym_Date, BasicDailyFinData>(Column_qoutes)).filterFromOutliers());
		outliersDates.addAll((new  ToBeFilteredFromOutliers<Sym_Date, BasicDailyFinData>(Column_SUEs)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, BasicYearlyFinData>(Column_shares)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, BasicYearlyFinData>(Column_book_val)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, BasicYearlyFinData>(Column_accrual)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, BasicYearlyFinData>(Column_ROE)).filterFromOutliers());
		
		tempMap = new TreeMap<Sym_Date, BasicDailyFinData>();
		System.out.println("Before filter_treeset() Dividend size: " + Column_dividends.size());
		for(Sym_Date sD : Column_dividends.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_dividends.get(sD));
		Column_dividends = tempMap;
		System.out.println("After filter_treeset() Dividend size: " + Column_dividends.size());

		System.out.println("Before filter_treeset() quotes size: " + Column_qoutes.size());
		tempMap = new TreeMap<Sym_Date, BasicDailyFinData>();
		for(Sym_Date sD : Column_qoutes.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_qoutes.get(sD));
		Column_qoutes = tempMap;
		System.out.println("After filter_treeset quotes() size " + Column_qoutes.size());

		System.out.println("Before filter_treeset() Column_SUEs size: " + Column_SUEs.size()); 
		tempMap = new TreeMap<Sym_Date, BasicDailyFinData>();
		for(Sym_Date sD : Column_SUEs.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_SUEs.get(sD));
		Column_SUEs = tempMap;
		System.out.println("After filter_treeset Column_SUEs size: " + Column_SUEs.size());
		
		System.out.println("Before filter_treeset() Column_shares size: " + Column_shares.size()); 
		tempMapY = new TreeMap<Sym_Year, BasicYearlyFinData>();
		for(Sym_Year sD : Column_shares.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_shares.get(sD));
		Column_shares = tempMapY;
		System.out.println("After filter_treeset Column_shares size: " + Column_shares.size());
		
		System.out.println("Before filter_treeset() Column_book_val size: " + Column_book_val.size());
		tempMapY = new TreeMap<Sym_Year, BasicYearlyFinData>();
		for(Sym_Year sD : Column_book_val.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_book_val.get(sD));
		Column_book_val = tempMapY;
		System.out.println("After filter_treeset Column_book_val size: " + Column_book_val.size());
		
		System.out.println("Before filter_treeset() Column_accrual size: " + Column_accrual.size()); 
		tempMapY = new TreeMap<Sym_Year, BasicYearlyFinData>();
		for(Sym_Year sD : Column_accrual.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_accrual.get(sD));
		Column_accrual = tempMapY;
		System.out.println("After filter_treeset Column_accrual size: " + Column_accrual.size());

		System.out.println("Before filter_treeset() Column_ROE size: " + Column_ROE.size()); 
		tempMapY = new TreeMap<Sym_Year, BasicYearlyFinData>();
		for(Sym_Year sD : Column_ROE.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_ROE.get(sD));
		Column_ROE = tempMapY;
		System.out.println("After filter_treeset Column_ROE size: " + Column_ROE.size());
		/*
		 *  Filtering done
		 */
	}

	
	
	/** 
	 * Normalization...
	 * We must create separate NormalizeParameterInAllEntries class for each parameter for later to be able to 
	 * denormalize.
	*/
	static void normalizeParameters()
	{	
		NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData> normalizedColumnDividends = 
				new NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData>(Column_dividends);
		Column_dividends = normalizedColumnDividends.getValuesOfParameterNormalized();
	
		normalizedColumnQuotes = 
				new NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData>(Column_qoutes);
		Column_qoutes = normalizedColumnQuotes.getValuesOfParameterNormalized();
		
		NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData> normalizedColumnSUEs = 
				new NormalizeParameterInAllEntries<Sym_Date, BasicDailyFinData>(Column_SUEs);
		Column_SUEs = normalizedColumnSUEs.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData> normalizedColumnShares = 
				new NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData>(Column_shares);
		Column_shares = normalizedColumnShares.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData> normalizedColumnBookVals = 
				new NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData>(Column_book_val);
		Column_book_val = normalizedColumnBookVals.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData> normalizedColumnAccruals = 
				new NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData>(Column_accrual);
		Column_accrual = normalizedColumnAccruals.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData> normalizedColumnROEs = 
				new NormalizeParameterInAllEntries<Sym_Year, BasicYearlyFinData>(Column_ROE);
		Column_ROE = normalizedColumnROEs.getValuesOfParameterNormalized();
		
		System.out.println("Normalization done");
		// Normalization done!!!
	}
	
	
	/**
	 * Regenerating each Company after normalization and filtering is complete and storing in the 
	 * global map Companies_match.
	 * @throws IOException
	 * @throws ParseException
	 */
	static void regenerateCompanies() throws IOException, ParseException
	{
		System.out.println("Regenerating companies");

		Company company;

		for (Sym_Date symDate : Column_dividends.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_dividend(Column_dividends.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); 	// the pipeline here is not used at all
				company.add_dividend(Column_dividends.get(symDate));
				companiesMap.put(Sym, company);
			}		
			if(Column_dividends.get(symDate).getVal().isNaN())
				System.out.println("Column_dividends NaN");
		}

		for (Sym_Date symDate : Column_qoutes.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				if(!Column_qoutes.get(symDate).getVal().isInfinite())
				{
					company = companiesMap.get(Sym);
					company.add_quote(Column_qoutes.get(symDate));
					companiesMap.replace(Sym, company);
				}
			}
			else
			{
				if(!Column_qoutes.get(symDate).getVal().isInfinite())
				{
					company = new Company(Sym, pipeline); // the pipeline here is not used at all
					company.add_quote(Column_qoutes.get(symDate));
					companiesMap.put(Sym, company);
				}
			}			
			if(Column_qoutes.get(symDate).getVal().isNaN() || Column_qoutes.get(symDate).getVal().isInfinite())
				System.out.println("Column_qoutes val:" + Column_qoutes.get(symDate).getVal());
		}

		for (Sym_Date symDate : Column_SUEs.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_SUE(Column_SUEs.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_SUE(Column_SUEs.get(symDate));
				companiesMap.put(Sym, company);
			}			
			if(Column_SUEs.get(symDate).getVal().isNaN())
				System.out.println("Column_SUEs NaN");
		}

		for (Sym_Year symDate : Column_accrual.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_accrual(symDate, Column_accrual.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_accrual(symDate, Column_accrual.get(symDate));
				companiesMap.put(Sym, company);
			}	
			if(Column_accrual.get(symDate).getVal().isNaN())
				System.out.println("Column_accrual NaN");
		}

		for (Sym_Year symDate : Column_ROE.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_ROE(symDate, Column_ROE.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_ROE(symDate, Column_ROE.get(symDate));
				companiesMap.put(Sym, company);
			}
			if(Column_ROE.get(symDate).getVal().isNaN())
				System.out.println("Column_ROE NaN");
		}

		for (Sym_Year symDate : Column_book_val.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_book_val(symDate, Column_book_val.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_book_val(symDate, Column_book_val.get(symDate));
				companiesMap.put(Sym, company);
			}			
			if(Column_book_val.get(symDate).getVal().isNaN())
				System.out.println("Column_book_val NaN");
		}

		for (Sym_Year symDate : Column_shares.keySet()) {
			String Sym = symDate.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_share(symDate, Column_shares.get(symDate));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_share(symDate, Column_shares.get(symDate));
				companiesMap.put(Sym, company);
			}			
			if(Column_shares.get(symDate).getVal().isNaN())
				System.out.println("Column_shares NaN");
		}
	}
	
	
	/** Companies recreated. Filter companies with some parameter outlier that has been erased in the previous step and now is null.
	 * Set the other parameters and read or evaluate (and generate) senti files,
	 * @throws ParseException 
	 * @throws IOException 
	 */
	static void filterCompaniesAndSetOtherParameters() throws IOException, ParseException
	{
		Company company;
		
		System.out.println("Set the other parameters");
		for (String Sym : companiesMap.keySet())
		{
			System.out.println("Sym:" + Sym);
			company = companiesMap.get(Sym);
			company.readOrEvalSentiOfNews();	//this will also store senti to file
			company.findConsistentYearsFromFinancialData();
			company.findConsistentDatesFromFinancialData();
			company.makeConsistentNewsWithFinancialData();
			
			//makeConsistentNewsWithFinancialData()...
			List<DateModif> consistentDates = new ArrayList<DateModif>(company.getNewsAvgSentiPerDateDescript().keySet());
			for(DateModif dateModif : consistentDates)
			{
				Sym_Date symDate = new Sym_Date(Sym, dateModif);
				_symDates.add(symDate);
				_symbolsInSymDates.add(Sym);
			}
			
			company.setFinancialParameters(); 		// it finds other derived financial parameters
	
			companiesMap.replace(Sym, company);
		}
		
		System.out.println("after Main.filterCompaniesAndSetOtherParameters() _symDates size:" + _symDates.size());
	}
	
	
	static void prepareForMachineLearning()
	{
		System.out.println("Read_sentiment._symDates size:"	+ _symDates.size());
		// reading from the above generated files
		AllCompaniesSentimentOfNews allCompaniesSentimentOfNews = 
				new AllCompaniesSentimentOfNews(_symDates, _symbolsInSymDates);
		
		//allCompaniesSentimentOfNews.readAllSentiFiles()...
		System.out.println("Size after reading senti files: " + allCompaniesSentimentOfNews.readAllSentiFiles());
		_symDates = new HashSet<Sym_Date>(allCompaniesSentimentOfNews.getSymDates());
		System.out.println("Size of _symDates after setting it from reading senti files: " + 
				_symDates.size());
		
		//_symDates = allCompaniesSentimentOfNews.getSymDates();
		/*_symbols = new ArrayList<String>(
				AllCompaniesSentimentOfNews.getSymbDates());
		_dates = new ArrayList<DateModif>(
				AllCompaniesSentimentOfNews.get_dates());*/

		
		/*System.out.println("Repeader_sym_for_training_matching size:"
				+ _symbolsForTrainingML.size());
		System.out.println("Dates_for_training_matching:"
				+ _datesForTrainingML.size());*/
		
		
		Map<String,Company> tempCompaniesMap = new TreeMap<String, Company>();
		
		for(String key : companiesMap.keySet())
		{
			boolean t = false;
			for(Sym_Date symDate : _symDates)
				if(symDate.get_sym().equals(key))
				{
					t = true;
					break;
				}
			if(t == true)
				tempCompaniesMap.put(key, companiesMap.get(key));
		}
		companiesMap = tempCompaniesMap;
	}
	
	
	
	
	
	public static void divideCompaniesMapTrainCrossValTest()
	{
		List<String> keys = new ArrayList<String>(companiesMap.keySet());
		_symDatesForTestingML  = new ArrayList<Sym_Date>();
		int i = 0;
		for(String key : keys)
		{
			if(i < companiesMap.size()*0.6)
			{
				Company tempCompany = companiesMap.get(key);
				trainingCompaniesMap.put(key, tempCompany);
				for(Sym_Date symDate : _symDates)
					if(symDate.get_sym().equals(key))
						_symDatesForTrainingML.add(symDate);
			}
			else
			if(i >= companiesMap.size()*0.6 && i < companiesMap.size()*0.8)
			{
				Company tempCompany = companiesMap.get(key);
				crossValidationCompaniesMap.put(key, tempCompany);
				for(Sym_Date symDate : _symDates)
					if(symDate.get_sym().equals(key))
						_symDatesForCrossValidationML.add(symDate);
			}
			else
			{
				Company tempCompany = companiesMap.get(key);
				testingCompaniesMap.put(key, tempCompany);
				for(Sym_Date symDate : _symDates)
					if(symDate.get_sym().equals(key))
						_symDatesForTestingML.add(symDate);
			}
			i++;
		}
		
		_trainingSize = _symDatesForTrainingML.size();
		_crossValidatSize = _symDatesForCrossValidationML.size();
		_testingSize = _symDatesForTestingML.size();
		
		System.out.println("training_size:" + _trainingSize + " ,parameters:"
				+ parameters + ", Companies_match" + companiesMap.size());
	}
	
	
}


