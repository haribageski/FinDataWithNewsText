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
	List<String> linesFromFile;
	
	public ReadUniqueSymFromFile(String pathToFile) throws IOException
	{
		linesFromFile = ReadFromFile.readFileLineByLine(pathToFile);
	}	
	
	public List<String> getLinesFromFile()
	{
		return linesFromFile;
	}
	
	
	
}
