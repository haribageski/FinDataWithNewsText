
package news;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import company.Company;
import other_structures.DateModif;
import reading_data_from_file.ReadUniqueSymFromFile;
import edu.stanford.nlp.util.StringUtils;

/**
 * 
 * @author Hari
 *
 * Class with static members. Used for reading files with stored senti info.
 */
public class AllCompaniesSentimentOfNews 
{
	static int  indexToAdd = 0;
	static List<String> symbols = new ArrayList<String>();
	static List<DateModif>  dates = new ArrayList<DateModif>();
	static String folderPath = "D:\\my documents\\Senior_Project_datasets\\News_senti\\";  
	static List<Double>  _posTitles = new ArrayList<Double>();
	static List<Double> _neutTitles = new ArrayList<Double>();
	static List<Double> _negTitle = new ArrayList<Double>();
	static List<Double> _posDescr = new ArrayList<Double>();
	static List<Double> _neutDescr = new ArrayList<Double>();
	static List<Double> _negDescr = new ArrayList<Double>();
	
	Double[][] _X_Senti;
	static Set<String> _validSymbols;
	
	public AllCompaniesSentimentOfNews(Set<String>Keys)	
	{
		_validSymbols = Keys;
	}
	
	public static void setSymbols(Set<String>Keys)	
	{
		_validSymbols = Keys;
	}
	
	public static List<String> get_symbols()
	{
		return symbols;
	}
	
	public static List<DateModif> get_dates()
	{
		return dates;
	}
	
	public static int readAllSentiFiles()
	{
		List<String> lines = new ArrayList<String>();
		String sym = "";
		
		File dir = new File(folderPath);
		File[] directoryListing = dir.listFiles();
	  	if (directoryListing != null) 
	  	{
		    for (File child : directoryListing) 
		    {
			   	String fileName =  child.getName().toString();
			   	//System.out.println("File name: " + fileName.substring(0, fileName.length()-4));
			   	if(!_validSymbols.contains(fileName.substring(0, fileName.length()-4)))	//resolves inconsistency in matching financial and sentiment files
			   	{
			   		System.out.println("file skipped");
			   		continue;
			   	}
			    	
			   	ReadUniqueSymFromFile Rear_line = null;
				try {
					Rear_line = new ReadUniqueSymFromFile (child.getAbsolutePath());
				} catch (IOException e) {
					System.out.println("ReadSentimentFromFiles: Error in reading a file:" + e);
				}
			   	lines = Rear_line.getUniqueSymbols();	
			    System.out.println("Lines" + lines.toString() + ",index increased");
			    
			    	
			    for(int i =0; i<lines.size(); i++)
				{
					int j=0;
					String[] fields = new String[8];
					
						
					if(lines.get(i).split("\t").length==1)	//empty line
						continue;
					
					fields= lines.get(i).split("\t");		//parses a line in columns by tab delimiter
					indexToAdd++;
				
					//check if some field is NaN
					if(fields[j].contains("NaN") || fields[j+1].contains("NaN") ||fields[j+2].contains("NaN") || 
							fields[j+3].contains("NaN") || fields[j+4].contains("NaN")|| fields[j+5].contains("NaN"))
						continue;
					
							
					//only the first line contains additional first field representing the symbol
					if(i==0)
					{
						sym = fields[0];
						symbols.add(sym);
						j = 1;
					}
					
					try {
						DateModif d = new DateModif(fields[j]);
						dates.add(d);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//store the sentiment
					_posTitles.add( Double.parseDouble(fields[j+1]));
					_negTitle.add( Double.parseDouble(fields[j+2]));
					_neutTitles.add( Double.parseDouble(fields[j+3]));
					_posDescr.add( Double.parseDouble(fields[j+4]));
					_negDescr.add( Double.parseDouble(fields[j+5]));
					_neutDescr.add( Double.parseDouble(fields[j+6]));
				}
		    }
	  	}
	  	System.out.println("readAllSentiFiles() : All files dates size:" + dates.size());
	  	indexToAdd--;		//the last increment is redundant
		return indexToAdd;	
	}
	
	public static Double [][] makeX_FromSentimentData()
	{
		Double[][] X_Senti = new Double[6][indexToAdd];
		X_Senti [0] = _posTitles.toArray	 (new Double[_posTitles.size()]);
		X_Senti [1] = _negTitle.toArray	 (new Double[_neutTitles.size()]);
		X_Senti [2] = _neutTitles.toArray (new Double[_negTitle.size()]);
		X_Senti [3] = _posDescr.toArray	 (new Double[_posDescr.size()]);
		X_Senti [4] = _negDescr.toArray (new Double[_neutDescr.size()]);
		X_Senti [5] = _neutDescr.toArray (new Double[_negDescr.size()]);
		
		return X_Senti;
	}

}
