
package news;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import company.Company;
import other_structures.DateModif;
import other_structures.Sym_Date;
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
	static int  indexesToAdd = 0;
	static String folderPath = "D:\\my documents\\Senior_Project_datasets\\News_senti\\";  
	static List<Double>  _posTitles = new ArrayList<Double>();
	static List<Double> _neutTitles = new ArrayList<Double>();
	static List<Double> _negTitle = new ArrayList<Double>();
	static List<Double> _posDescr = new ArrayList<Double>();
	static List<Double> _neutDescr = new ArrayList<Double>();
	static List<Double> _negDescr = new ArrayList<Double>();
	static Set<Sym_Date> _sentiFilesSymDatesOrdered = new HashSet<Sym_Date>();
	
	Double[][] _X_Senti;
	static Set<Sym_Date> _validSymDates = new HashSet<Sym_Date>();
	static List<String> _validSymbols = new ArrayList<String>();
	
	public AllCompaniesSentimentOfNews(Set<Sym_Date> symDates ,List<String> Syms)	
	{
		_validSymDates = symDates;
		_validSymbols = Syms;
	}
	
	/**
	 * Sets _validSymbols which are used for reading the Sentiment files
	 * @param Keys
	 */
	public void setSymbols(Set<Sym_Date>Keys)	
	{
		_validSymDates = Keys;
	}
	
	public List<Sym_Date> getSymDates()
	{
		System.out.println("AllCompaniesSentimentOfNews.getSymDates() sentiFilesSymDatesOrdered size:" + 
				_sentiFilesSymDatesOrdered.size());
		return new ArrayList<Sym_Date>(_sentiFilesSymDatesOrdered);
	}
	
	/*public static List<DateModif> get_dates()
	{
		return dates;
	}*/
	
	/**
	 * Reads from the generated sentiment files. Considers only lines with consistent date with the 
	 * financial parameters and with relevant,nonempty fields.
	 *  
	 * @return added lines : int
	 */
	public int readAllSentiFiles()
	{
		List<String> lines = new ArrayList<String>();
		String sym = "";
		Sym_Date symDate = new Sym_Date(null, null);
		
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
			   	lines = Rear_line.getLinesFromFile();	
			    System.out.println("Lines" + lines.toString() + ",index increased");
			    
			    	
			    for(int i = 0; i<lines.size(); i++)
				{
			    	//System.out.println("Line:" + lines.get(i).toString());
					    
					int j=0;
					String[] fields = new String[8];
					
						
					if(lines.get(i).split("\t").length < 6)	//empty line
					{
						System.out.println("readAllSentiFiles(): empty line continue");
						continue;
					}
					
					fields = lines.get(i).split("\t");		//parses a line in columns by tab delimiter
					indexesToAdd++;
				
					//check if some field is NaN
					if(fields[j].contains("NaN") || fields[j+1].contains("NaN") ||fields[j+2].contains("NaN") || 
							fields[j+3].contains("NaN") || fields[j+4].contains("NaN")|| fields[j+5].contains("NaN"))
					{
						System.out.println("readAllSentiFiles(): NaN continue");
						continue;
					}
					
					//only the first line contains additional first field representing the symbol
					if(i==0)
					{
						sym = fields[0];
						//symbols.add(sym);
						j = 1;
					}
					
					try {
						DateModif d = new DateModif(fields[j]);
						symDate = new Sym_Date(sym, d);
						//System.out.println("DateModif for current line:" + sym + " " + d.toString());
						/*if(!_validSymDates.contains(symDate))
						{
							System.out.println("_validSymDates doesn't contains " + symDate.toString());
							continue;
						}*/
						/*else
							symDates.add(symDate);*/
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					//store the sentiment
					_posTitles.add( Double.parseDouble(fields[j+1]));
					_negTitle.add( Double.parseDouble(fields[j+2]));
					_neutTitles.add( Double.parseDouble(fields[j+3]));
					_posDescr.add( Double.parseDouble(fields[j+4]));
					_negDescr.add( Double.parseDouble(fields[j+5]));
					_neutDescr.add( Double.parseDouble(fields[j+6]));
					_sentiFilesSymDatesOrdered.add(symDate);
					//System.out.println("adding to _sentiFilesSymDatesOrdered");
				}
		    }
	  	}
	  	indexesToAdd--;		//the last increment is redundant
		
	  	return _neutDescr.size();	
	}
	
	public static Double [][] makeX_FromSentimentData( Set<Sym_Date>lastValidSymDatesFromTrainingMatrix)
	{
		_validSymDates = lastValidSymDatesFromTrainingMatrix;
		Double[][] X_Senti = new Double[6][_validSymDates.size()];
		int j = 0;
		List <Sym_Date> _sentiFilesSymDatesOrderedList = new ArrayList<Sym_Date>(_sentiFilesSymDatesOrdered);
		for(Sym_Date symDate : _validSymDates)
		{
			for(int i = 0; i < _sentiFilesSymDatesOrdered.size(); i++)
			{
				if(_sentiFilesSymDatesOrderedList.get(i).equals(symDate))
				{
					X_Senti [0][j] = _posTitles.get(i);
					X_Senti [1][j] = _negTitle.get(i);
					X_Senti [2][j] = _neutTitles.get(i);
					X_Senti [3][j] = _posDescr.get(i);
					X_Senti [4][j] = _negDescr.get(i);
					X_Senti [5][j] = _neutDescr.get(i);
				}
			}
			j++;
		}
		return X_Senti;
	}

}
