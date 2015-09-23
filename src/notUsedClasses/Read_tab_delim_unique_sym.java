package notUsedClasses;
/*package reading_data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Read_tab_delim_unique_sym {
	
	List<String> Unique_symbols;
	
	public Read_tab_delim_unique_sym(String unique_sym_path) throws IOException
	{
		Unique_symbols = readTabDelimSymbols(unique_sym_path);
	}	
	
	public List<String> getUnique_symbols()
	{
		return Unique_symbols;
	}
*/	

	
	/*
	 * Reading from file line by line using BufferedReader
	 * 
	 * input Param: String file_path
	 * output Param: List<String> Symbols
	 */
/*
	List<String> readTabDelimSymbols(String file_path) throws IOException	//maybe useful for deleting unnecessary files
	{
		
		List<String> Symbols = new ArrayList<String>();;
		  
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(new java.io.File(file_path))));
				
		String line;
			
		while((line = read.readLine()) != null)
		{
			if(line.toString()!="")
				Symbols.add(line.toString());
		}
		read.close();
		return Symbols;
	}
}
*/