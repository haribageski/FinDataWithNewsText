package reading_data_from_file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadUniqueSymFromFile 
{
	final static Charset ENCODING = StandardCharsets.UTF_8;
	List<String> uniqueSymbols;
	
	public ReadUniqueSymFromFile(String unique_sym_path) throws IOException
	{
		uniqueSymbols = ReadFromFile.readFileLineByLine(unique_sym_path);
	}	
	
	public List<String> getUniqueSymbols()
	{
		return uniqueSymbols;
	}
	
	
	
}
