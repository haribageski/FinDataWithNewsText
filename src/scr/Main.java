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
import daily_financial_parameters.Basic_daily_fin_data;
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
import yearly_financial_parameters.basicYearlyFinData;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Main 
{
	final static Charset ENCODING = StandardCharsets.UTF_8;
	static StanfordCoreNLP pipeline;
	static int parameters = 16;
	static int _trainingSize = 0;
	static int _crossValidatSize = 0;
	static int _testingSize = 0;
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
	static Map<Sym_Year, basicYearlyFinData> Column_shares = new TreeMap<Sym_Year, basicYearlyFinData>();
	static Map<Sym_Year, basicYearlyFinData> Column_book_val = new TreeMap<Sym_Year, basicYearlyFinData>();
	static Map<Sym_Year, basicYearlyFinData> Column_ROE = new TreeMap<Sym_Year, basicYearlyFinData>();
	static Map<Sym_Year, basicYearlyFinData> Column_accrual = new TreeMap<Sym_Year, basicYearlyFinData>();
	static Map<Sym_Year, basicYearlyFinData> tempMapY = new TreeMap<Sym_Year, basicYearlyFinData>();

	static Map<Sym_Date, Basic_daily_fin_data> Column_dividends = new TreeMap<Sym_Date, Basic_daily_fin_data>();
	static Map<Sym_Date, Basic_daily_fin_data> Column_qoutes = new TreeMap<Sym_Date, Basic_daily_fin_data>();
	static Map<Sym_Date, Basic_daily_fin_data> Column_SUEs = new TreeMap<Sym_Date, Basic_daily_fin_data>();
	static Map<Sym_Date, Basic_daily_fin_data> tempMap = new TreeMap<Sym_Date, Basic_daily_fin_data>();

	static Map<String, Company> companiesMap = new TreeMap<String, Company>();
	static Map<String, Company> trainingCompaniesMap = new TreeMap<String, Company>();
	static Map<String, Company> crossValidationCompaniesMap = new TreeMap<String, Company>();
	static Map<String, Company> testingCompaniesMap = new TreeMap<String, Company>();

	static NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data> normalizedColumnQuotes;
	
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

		initiateStanfordNLP();	//TODO don't forget this to uncomment
		
		setColumnMapsOfParameters(Symbols);
					
		filterOutliers();
				
		normalizeParameters();
		
		regenerateCompanies();	
		
		filterCompaniesAndSetOtherParameters();
		
		/*for(Sym_Date symDate : _symDates)
			System.out.println(symDate.toString());*/
		
		prepareForMachineLearning();
		
		divideCompaniesMapTrainCrossValTest();
		
		// Prepare neural network representation
		System.out.println("Neural network starts");
		System.out.println("trainingCompaniesMap size:" + trainingCompaniesMap.size());
		
		neuralNet = new RepresentationNetwork( _symDatesForTrainingML, trainingCompaniesMap, _trainingSize, parameters);
		
		// Initiate neural network
		neuralNet.Learn();		
		System.out.println("Neural network done");	
		
		
		
		Double [][] XsCrossValidat = createCrossValidationMatrix();
		List <Sym_Date> symDatesCrossValidatList = new ArrayList<Sym_Date>(_symDatesForCrossValidationML);
		for(int i = 0; i < XsCrossValidat.length; i++)
		{
			Double predicterVal;
			predicterVal = neuralNet.makePrediction( XsCrossValidat[i]);
			Sym_Date symDate = symDatesCrossValidatList.get(i);
			System.out.println("predicterVal normalized:" + predicterVal.toString());
			
			System.out.println("original value:" + normalizedColumnQuotes.getNormalizedData(symDate).get_val().toString());
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
			for(Basic_daily_fin_data daily : company.get_Company_Dividend().get_All_company_dividends())
				writer.println(daily.get_val());
			writer.println("company sue:");
			for(Basic_daily_fin_data daily : company.get_Company_SUE().get_All_company_SUE())
				writer.println(daily.get_val());
			writer.println("company Quotes:");
			for(Basic_daily_fin_data daily : company.get_Company_SUE().get_All_company_SUE())
				writer.println(daily.get_val());
			writer.println("company BM_Ratios:");
			for(Double val : company.get_Fin_fundamentals().getAllBM_Ratios().values())
				writer.println(val);
			writer.println("company Accrual:");
			for(basicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyAccrual().values())
				writer.println(val.getVal());
			writer.println("company Bookval:");
			for(basicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyBookVal().values())
				writer.println(val.getVal());
			writer.println("company MarketVals:");
			for(Double val : company.get_Fin_fundamentals().getAllCompanyMarketVals().values())
				writer.println(val);
			writer.println("company ROE:");
			for(basicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyROE().values())
				writer.println(val.getVal());
			writer.println("company Shares:");
			for(basicYearlyFinData val : company.get_Fin_fundamentals().getAllCompanyShares().values())
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
		for (int f = 0; f < Symbols.size() && f < 30; f++)  
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
			for (Basic_daily_fin_data D : company.get_Company_Dividend().get_All_company_dividends())
				Column_dividends.put(new Sym_Date(Sym, D.get_date()), D);
			for (Basic_daily_fin_data D : company.get_Company_Qoutes().get_All_company_quotes())
				Column_qoutes.put(new Sym_Date(Sym, D.get_date()), D);
			for (Basic_daily_fin_data D : company.get_Company_SUE().get_All_company_SUE())
				Column_SUEs.put(new Sym_Date(Sym, D.get_date()), D);

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
		
		
		outliersDates = (new  ToBeFilteredFromOutliers<Sym_Date, Basic_daily_fin_data>(Column_dividends)).filterFromOutliers();
		outliersDates.addAll((new  ToBeFilteredFromOutliers<Sym_Date, Basic_daily_fin_data>(Column_qoutes)).filterFromOutliers());
		outliersDates.addAll((new  ToBeFilteredFromOutliers<Sym_Date, Basic_daily_fin_data>(Column_SUEs)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, basicYearlyFinData>(Column_shares)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, basicYearlyFinData>(Column_book_val)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, basicYearlyFinData>(Column_accrual)).filterFromOutliers());
		outliersYears.addAll((new  ToBeFilteredFromOutliers<Sym_Year, basicYearlyFinData>(Column_ROE)).filterFromOutliers());
		
		tempMap = new TreeMap<Sym_Date, Basic_daily_fin_data>();
		System.out.println("Before filter_treeset() Dividend size: " + Column_dividends.size());
		for(Sym_Date sD : Column_dividends.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_dividends.get(sD));
		Column_dividends = tempMap;
		System.out.println("After filter_treeset() Dividend size: " + Column_dividends.size());

		System.out.println("Before filter_treeset() quotes size: " + Column_qoutes.size());
		tempMap = new TreeMap<Sym_Date, Basic_daily_fin_data>();
		for(Sym_Date sD : Column_qoutes.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_qoutes.get(sD));
		Column_qoutes = tempMap;
		System.out.println("After filter_treeset quotes() size " + Column_qoutes.size());

		System.out.println("Before filter_treeset() Column_SUEs size: " + Column_SUEs.size()); 
		tempMap = new TreeMap<Sym_Date, Basic_daily_fin_data>();
		for(Sym_Date sD : Column_SUEs.keySet())
			if(!outliersDates.contains(sD) && !outliersYears.contains(sD.get_Date_modif().get_year_in_date()))
				tempMap.put(sD, Column_SUEs.get(sD));
		Column_SUEs = tempMap;
		System.out.println("After filter_treeset Column_SUEs size: " + Column_SUEs.size());
		
		System.out.println("Before filter_treeset() Column_shares size: " + Column_shares.size()); 
		tempMapY = new TreeMap<Sym_Year, basicYearlyFinData>();
		for(Sym_Year sD : Column_shares.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_shares.get(sD));
		Column_shares = tempMapY;
		System.out.println("After filter_treeset Column_shares size: " + Column_shares.size());
		
		System.out.println("Before filter_treeset() Column_book_val size: " + Column_book_val.size());
		tempMapY = new TreeMap<Sym_Year, basicYearlyFinData>();
		for(Sym_Year sD : Column_book_val.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_book_val.get(sD));
		Column_book_val = tempMapY;
		System.out.println("After filter_treeset Column_book_val size: " + Column_book_val.size());
		
		System.out.println("Before filter_treeset() Column_accrual size: " + Column_accrual.size()); 
		tempMapY = new TreeMap<Sym_Year, basicYearlyFinData>();
		for(Sym_Year sD : Column_accrual.keySet())
			if(!outliersYears.contains(sD))
				tempMapY.put(sD, Column_accrual.get(sD));
		Column_accrual = tempMapY;
		System.out.println("After filter_treeset Column_accrual size: " + Column_accrual.size());

		System.out.println("Before filter_treeset() Column_ROE size: " + Column_ROE.size()); 
		tempMapY = new TreeMap<Sym_Year, basicYearlyFinData>();
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
		NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data> normalizedColumnDividends = 
				new NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data>(Column_dividends);
		Column_dividends = normalizedColumnDividends.getValuesOfParameterNormalized();
	
		normalizedColumnQuotes = 
				new NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data>(Column_qoutes);
		Column_qoutes = normalizedColumnQuotes.getValuesOfParameterNormalized();
		
		
		NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data> normalizedColumnSUEs = 
				new NormalizeParameterInAllEntries<Sym_Date, Basic_daily_fin_data>(Column_SUEs);
		Column_SUEs = normalizedColumnSUEs.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData> normalizedColumnShares = 
				new NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData>(Column_shares);
		Column_shares = normalizedColumnShares.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData> normalizedColumnBookVals = 
				new NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData>(Column_book_val);
		Column_book_val = normalizedColumnBookVals.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData> normalizedColumnAccruals = 
				new NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData>(Column_accrual);
		Column_accrual = normalizedColumnAccruals.getValuesOfParameterNormalized();
	
		NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData> normalizedColumnROEs = 
				new NormalizeParameterInAllEntries<Sym_Year, basicYearlyFinData>(Column_ROE);
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

		for (Sym_Date S_D : Column_dividends.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_dividend(Column_dividends.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); 	// the pipeline here is not used at all
				company.add_dividend(Column_dividends.get(S_D));
				companiesMap.put(Sym, company);
			}		
		}

		for (Sym_Date S_D : Column_qoutes.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_quote(Column_qoutes.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_quote(Column_qoutes.get(S_D));
				companiesMap.put(Sym, company);
			}			
		}

		for (Sym_Date S_D : Column_SUEs.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_SUE(Column_SUEs.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_SUE(Column_SUEs.get(S_D));
				companiesMap.put(Sym, company);
			}			
		}

		for (Sym_Year S_D : Column_accrual.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_accrual(S_D, Column_accrual.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_accrual(S_D, Column_accrual.get(S_D));
				companiesMap.put(Sym, company);
			}			
		}

		for (Sym_Year S_D : Column_ROE.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_ROE(S_D, Column_ROE.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_ROE(S_D, Column_ROE.get(S_D));
				companiesMap.put(Sym, company);
			}
		}

		for (Sym_Year S_D : Column_book_val.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_book_val(S_D, Column_book_val.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_book_val(S_D, Column_book_val.get(S_D));
				companiesMap.put(Sym, company);
			}			
		}

		for (Sym_Year S_D : Column_shares.keySet()) {
			String Sym = S_D.get_sym();

			if (companiesMap.containsKey(Sym))
			{
				company = companiesMap.get(Sym);
				company.add_share(S_D, Column_shares.get(S_D));
				companiesMap.replace(Sym, company);
			}
			else
			{
				company = new Company(Sym, pipeline); // the pipeline here is not used at all
				company.add_share(S_D, Column_shares.get(S_D));
				companiesMap.put(Sym, company);
			}			
		}
	}
	
	
	/** Companies recreated. Filter companies with some parameter outlier that has been erased in the previous step and now is null.
	 * Set the other parameters and read or evaluate (and generate) senti files
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


