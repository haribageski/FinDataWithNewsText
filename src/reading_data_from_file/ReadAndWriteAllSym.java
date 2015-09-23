package reading_data_from_file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import writing_data_to_file.WriteToFile;

/*
 * Creates one HashSet of Strings for the unique Symbols that appear in all parameters, 
 * that is we find the intersection of the symbols 
 */
public class ReadAndWriteAllSym 
{
	final static Charset ENCODING = StandardCharsets.UTF_8;
	private HashSet<String> _codesOfCompanies = new HashSet<String>();
	
	public HashSet<String> getCodesOfCompanies()
	{
		return  _codesOfCompanies;
	}
	
	public ReadAndWriteAllSym(String unique_sym_path) throws IOException 
	{
		//first process the R file
		/*Read_tab_delim_unique_sym  Unique_sym = 
			new Read_tab_delim_unique_sym("D:\\my documents\\Senior_Project_datasets\\online_trading_option_unique_sym.txt");
		Codes_of_companies.addAll(get_names(Unique_sym.getUnique_symbols()));
		*/
		
		ReadUniqueSymFromFile Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\R\\Constituents_fin_unique_sym.txt");
		_codesOfCompanies.addAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
		
		Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\earning_surprise_unique_sym.txt");
		_codesOfCompanies.retainAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
		
		Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\Fin_param_unique_sym.txt");
		_codesOfCompanies.retainAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
		
		Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\dividends_unique_sym.txt");
		_codesOfCompanies.retainAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
		
		Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\google_news_unique_sym.txt");
		_codesOfCompanies.retainAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
				
		Unique_sym = new ReadUniqueSymFromFile(
				"D:\\my documents\\Senior_Project_datasets\\quotes_unique_sym.txt");
		_codesOfCompanies.retainAll(ReadFromFile.readTabSeparatedLines(Unique_sym.getUniqueSymbols()));
		
		
		
		List<String> unique_list_of_symbols = new ArrayList<String>(_codesOfCompanies);
		WriteToFile.writeSmallTextFile(unique_list_of_symbols, 
				"D:\\my documents\\Senior_Project_datasets\\unique_company_symbols.txt");
		
		System.out.println("Final number of companies existing in every set is:" + _codesOfCompanies.size());
	}
	
	
	
	
}

