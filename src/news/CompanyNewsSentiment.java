package news;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import company.Company;

import other_structures.DateModif;
import reading_data_from_file.ReadOneColumnFromFile;
import reading_data_from_file.ReadFromFile;
import reading_data_from_file.ReadUniqueSymFromFile;
import writing_data_to_file.WriteToFile;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


public class CompanyNewsSentiment 
{
	String _sym;
	Map<DateModif, Double[]> _avgSentiPerDateTitle =  new HashMap<DateModif, Double[]>();	
	Map<DateModif, Double[]> _avgSentiPerDateDescript =  new HashMap<DateModif, Double[]>();	
	Double[] _sentimentForTitles;
	Double[] _sentimentForDescriptions;
	String _filePathToTextNews;
	String _filePathToSentimentOfNews;
	String _outputPath;
	static StanfordCoreNLP _pipeline = null; 
	
	
	public CompanyNewsSentiment(String symbol, StanfordCoreNLP pipeln)
	{
		_sym = symbol;
		_pipeline = pipeln;
		_filePathToTextNews = "D:\\my documents\\Senior_Project_datasets\\News\\google_news_" + symbol + ".txt";
		_filePathToSentimentOfNews = "D:\\my documents\\Senior_Project_datasets\\News_Senti\\" + symbol + ".txt";
		_outputPath = _filePathToSentimentOfNews;
	}
	
	public CompanyNewsSentiment(String symbol, StanfordCoreNLP pipeln,
			Map<DateModif, Double[]> avgSentiPerDateTitle,	Map<DateModif, Double[]> avgSentiPerDateDescript)
	{
		_sym = symbol;
		_pipeline = pipeln;
		_avgSentiPerDateTitle = avgSentiPerDateTitle;
		_avgSentiPerDateDescript = avgSentiPerDateDescript;
		//System.out.println("News for :" + _sym);
	}
	
	public Map<DateModif, Double[]> getAvgSentiPerDateTitle()
	{
		return _avgSentiPerDateTitle;
	}
	
	public Map<DateModif, Double[]> getAvgSentiPerDateDescript()
	{
		return _avgSentiPerDateDescript;
	}
	
	void initiate_Stanford_NLP(Properties props)
	{
		_pipeline = new StanfordCoreNLP(props);
	}

	
	/**
	 * If a file with sentiment info exists for the corresponding symbol, the parameters are set from the file and 
	 * it returns true. If there is no such file or it is empty or some fields are NaN it returns false.
	 * @return boolean
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("resource")
	public boolean readSentiFile() throws IOException, ParseException
	{
		List<String> lines = new ArrayList<String>();
		
		File fileReader = new File(_filePathToSentimentOfNews);
		
		if(fileReader.exists() && !fileReader.isDirectory())
		{	
			System.out.println("CompanyNewsSentiment.readSentiFile() for symbol:" + _sym);
			
			lines = ReadFromFile.readFileLineByLine(_filePathToSentimentOfNews);
			//System.out.println("lines.size():" + lines.size());
			for(int i = 0; i < lines.size(); i++)
			{
				//System.out.println("line:" + lines.get(i));
				int j=0;
				String[] fields = new String[8];
					
				if(lines.get(i).equals(""))	 //empty line
				{
					if(i != lines.size()-1)	
						return false;
					else		//the generated files end with an empty line
						continue;
				}
					
				
				fields= lines.get(i).split("\t");		//parses a line in columns by tab delimiter
			
				if(fields.length < 6)
					continue;
				
				//check if some field is NaN
				if(fields[j].contains("NaN") || 
						fields[j+1].contains("NaN") ||
						fields[j+2].contains("NaN") || 
						fields[j+3].contains("NaN") || 
						fields[j+4].contains("NaN") || 
						fields[j+5].contains("NaN"))
					continue;
				
						
				//only the first line contains additional first field representing the symbol
				if(i == 0)
					j = 1;
				
				DateModif date = null;
				try {
					date = new DateModif(fields[j]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				_sentimentForTitles = new Double[3];
				_sentimentForTitles[0] = Double.parseDouble(fields[j+1]);
				_sentimentForTitles[1] = Double.parseDouble(fields[j+2]);
				_sentimentForTitles[2] = Double.parseDouble(fields[j+3]);
				
				_sentimentForDescriptions = new Double[3];
				_sentimentForDescriptions[0] = Double.parseDouble(fields[j+4]);
				_sentimentForDescriptions[1] = Double.parseDouble(fields[j+5]);
				_sentimentForDescriptions[2] = Double.parseDouble(fields[j+6]);
				
				_avgSentiPerDateTitle.put(date, _sentimentForTitles);
				_avgSentiPerDateDescript.put(date, _sentimentForDescriptions);
			}
			return true;
		}
		return false;			
	}
	
	
	/**
	 * Finds number of positive, negative, and neutral sentences for the input text
	 * 
	 * @param text
	 * @return Double[3] corresponding to the number of pos,neg,neut sentences
	 */
	public Double[] evaluateSentiOfText(String text)	
	{
		System.out.println("evaluateSentiOfText() - " + text);
		double pos_sentences = 0.0, neg_sentences = 0.0, neut_sentences =  0.0;
		Double[] sentiForTheThreeKinds = new Double[3];
		Annotation annotation = new Annotation(text);	//Annotation is a Map 
		_pipeline.annotate(annotation);
		    
		// An Annotation is a Map and you can get and use the various analyses individually.
		// For instance, this gets the parse tree of the first sentence in the text.
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		
		if (sentences != null && sentences.size() > 0) 
		{
			
			for(CoreMap sentence: sentences) 
			{
				String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
				System.out.println("Sentiment for :" + sentence.toString() + "is: " + sentiment);
				switch (sentiment)
				{
					case "Positive":
						pos_sentences++;
						System.out.println("Positive");
						System.out.println("Positive sentences after increment: " + pos_sentences);
						break;
					case "Negative":
						neg_sentences++;
						System.out.println("Negative");
						System.out.println("Negative sentences after increment: " + neg_sentences);
						break;
					case "Neutral":
						neut_sentences++;
						System.out.println("Neutral");
						System.out.println("Neutral sentences after increment: " + neut_sentences);
						break;
				}
			}
		}
		sentiForTheThreeKinds[0] = pos_sentences;
		sentiForTheThreeKinds[1] = neg_sentences;
		sentiForTheThreeKinds[2] = neut_sentences;
		
		return sentiForTheThreeKinds;
	}
	
		
	/**
	 * Evaluate sentiments of relevant news by calling the above function and sets the value 
	 * of avgSentiPerDate and  total_news
	 * 
	 * @param All_company_SUE_years
	 * @param Earliest_SUE_date
	 * @param All_company_fin_fundam_years
	 * @param All_company_quotes_years
	 * @param All_company_quotes_dates
	 * @param All_company_divi_years
	 * @throws IOException
	 * @throws ParseException
	 */
	public void evalSentiForAllNewsOfACompany( HashSet<Integer> All_company_SUE_years, 
			HashSet<Integer> All_company_fin_fundam_years, HashSet<Integer> All_company_quotes_years, Map<DateModif , Double> All_company_quotes_dates,
			Set<Integer> All_company_divi_years) throws IOException	, ParseException
	{
		System.out.println("evalSentiForAllNewsOfACompany...");
		/*System.out.println("All_company_SUE_years size:" + All_company_SUE_years.size());
		System.out.println("All_company_fin_fundam_years size:" + All_company_fin_fundam_years.size());
		System.out.println("All_company_quotes_years size:" + All_company_quotes_years.size());
		System.out.println("All_company_quotes_dates size:" + All_company_quotes_dates.size());
		System.out.println("All_company_divi_years size:" + All_company_divi_years.size());*/
		Double pos_titl = 0.0, neg_titl = 0.0, neut_titl = 0.0;
		Double pos_descr = 0.0, neg_descr = 0.0, neut_descr = 0.0;
		boolean newsIsConsistentInDateWithFinParams = true;		//we need this for checking if we should add the last news as one with unique date
		
		ReadOneColumnFromFile Read_date = new ReadOneColumnFromFile(_filePathToTextNews, 1);
		ReadOneColumnFromFile Read_title = new ReadOneColumnFromFile(_filePathToTextNews, 2);
		ReadOneColumnFromFile Read_description = new ReadOneColumnFromFile(_filePathToTextNews, 3);
		
		
		DateModif date = new DateModif(), prev_date = new DateModif();
		//Integer year = 0;
		int numOfTitleSentences = 0, numOfDescriptSentences = 0;
		String Title = "";
		String Description = "";
		_sentimentForTitles = new Double[3];
		_sentimentForDescriptions = new Double[3];
		
		//initiate_Stanford_NLP();		I shifted it in upper level class to call it fewer times
		
		for(int i = 0; i < Read_date.getTheColumn().size(); i++)
		{
			newsIsConsistentInDateWithFinParams = false;
			date = new DateModif(Read_date.getTheColumn().get(i) );
			
			//TODO check if date is relevant
			
			int year = date.get_year_in_date();

			Title = Read_title.getTheColumn().get(i) ;
			Description = Read_description.getTheColumn().get(i) ;
			SingleGoogleNews News = new SingleGoogleNews(_sym, date, year, Title, Description, "");
			
			//filter some news, find sentiment only for relevant news judging from the date of the news
			if(!News.isRelevant(All_company_SUE_years, All_company_fin_fundam_years, 
				All_company_quotes_years,All_company_quotes_dates, All_company_divi_years))
			{
				continue;
			}
			else
				newsIsConsistentInDateWithFinParams = true;

			if(!date.equals(prev_date)) 		//different dates, want to record news sentiment for the previous date
			{
				if(i!=0)	//not the first date first entry
				{
					_sentimentForTitles[0] = pos_titl ;
					_sentimentForTitles[1] = neg_titl ;
					_sentimentForTitles[2] = neut_titl ;
					if(numOfTitleSentences != 0)
					{
						for(int j = 0; j < 3; j++)
							_sentimentForTitles[j] /= (double)numOfTitleSentences;
					}
					
					_sentimentForDescriptions[0] = pos_descr;
					_sentimentForDescriptions[1] = neg_descr;
					_sentimentForDescriptions[2] = neut_descr; 
					if(numOfDescriptSentences != 0)
					{
						for(int j = 0; j < 3; j++)
							_sentimentForDescriptions[j] /= (double)numOfDescriptSentences;
					}
					
					_avgSentiPerDateTitle.put(date, _sentimentForTitles);
					_avgSentiPerDateDescript.put(date, _sentimentForDescriptions);
					
					_sentimentForTitles = new Double[3];
					_sentimentForDescriptions = new Double[3];
					
					pos_titl = 0.0;
					neut_titl = 0.0;
					neg_titl = 0.0;
					pos_descr = 0.0;
					neut_descr = 0.0;
					neg_descr = 0.0;
					numOfDescriptSentences = numOfTitleSentences = 0;
				}
				
				prev_date=date;
			}
		
			_sentimentForTitles = evaluateSentiOfText(Title);
			_sentimentForDescriptions = evaluateSentiOfText(Description);
			pos_titl += _sentimentForTitles[0];
			neg_titl += _sentimentForTitles[1];
			neut_titl += _sentimentForTitles[2];
			pos_descr += _sentimentForDescriptions[0];
			neg_descr += _sentimentForDescriptions[1];
			neut_descr += _sentimentForDescriptions[2];
			
			for(int j = 0; j < 3; j++)
			{
				numOfTitleSentences += _sentimentForTitles[j];
				numOfDescriptSentences += _sentimentForDescriptions[j];
			}
		}
		
		//we need to add the info about the last year if relevant, so we do it here...
		if(newsIsConsistentInDateWithFinParams == true)
		{
			_sentimentForTitles[0] = pos_titl ;
			_sentimentForTitles[1] = neg_titl ;
			_sentimentForTitles[2] = neut_titl ;
			if(numOfTitleSentences != 0)
			{
				for(int j = 0; j < 3; j++)
					_sentimentForTitles[j] /= (double)numOfTitleSentences;
			}
			
			_sentimentForDescriptions[0] = pos_descr;
			_sentimentForDescriptions[1] = neg_descr;
			_sentimentForDescriptions[2] = neut_descr; 
			if(numOfDescriptSentences != 0)
			{
				for(int j = 0; j < 3; j++)
					_sentimentForDescriptions[j] /= (double)numOfDescriptSentences;
			}
			
			_avgSentiPerDateTitle.put(date, _sentimentForTitles);
			_avgSentiPerDateDescript.put(date, _sentimentForDescriptions);
		}
		System.out.println("CompanyNewsSentiment.evalSentiForAllNewsOfACompany : _avgSentiPerDateTitle:" + _avgSentiPerDateTitle.values().toString());
	
		//Store senti to file
		getAndWriteToFileSentimentOneCompany();
	}		
	
	
	
	public void getAndWriteToFileSentimentOneCompany()
	{		   
		List <String> outputLines = new ArrayList<String>();
		if(_avgSentiPerDateDescript.size()==0 || _avgSentiPerDateTitle.size()==0)	//don't want to generate empty files
		{
			System.out.println("/don't want to generate empty files");
			return;
		}
		 
		System.out.println("Company news for " + _sym + " in number are : " + _avgSentiPerDateTitle.size());
		
		String output = _sym + "\t";
		for (Entry<DateModif, Double[]> entry : _avgSentiPerDateTitle.entrySet())
		{
			Double[] sentimentOnADate = new Double[3];
			sentimentOnADate = entry.getValue();
			
			if(sentimentOnADate[0].toString().equals("NaN") || sentimentOnADate[1].toString().equals("NaN") || sentimentOnADate[2].toString().equals("NaN"))
				continue;
			
			output += entry.getKey().dateModifToString() + "\t";
			
			for(int i=0;i<3;i++)
				output += (sentimentOnADate[i].toString() + "\t");
		     	
			sentimentOnADate = new Double[3];
			sentimentOnADate = _avgSentiPerDateDescript.get(entry.getKey());
			for(int i=0;i<2;i++)
				output += (sentimentOnADate[i].toString() + "\t");
			output += (sentimentOnADate[2].toString() + "\n");
		 }
		 
		 outputLines.add(output);
		 System.out.println("Will output: " + outputLines);
		 try 
		 {
			 WriteToFile.writeSmallTextFile(outputLines, _outputPath);
			 log(outputLines);
		 } catch (IOException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	}
	
	
	private static void log(Object aMsg)
	{
		System.out.println(String.valueOf(aMsg));
	}
}
