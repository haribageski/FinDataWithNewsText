package reading_data_from_file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadFromFile {
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public static List<String> readFileLineByLine(String file_name) throws IOException
	{
		Path path = Paths.get(file_name);
		return Files.readAllLines(path, ENCODING);
	}
	
	static List<String> readTabSeparatedLines(List<String> fileLines)
	{
		Scanner s;
		List<String> names = new ArrayList<String>(); //we don't want fixed size
		for(int i=0;i<fileLines.size();i++)
		{
			s = new Scanner(fileLines.get(i)).useDelimiter("\\t");
			if(s.hasNext() && !fileLines.get(i).isEmpty())
				names.add(s.next());
		}
		return names;
	}
	
	public static String readFileToString(String fineName)
	  {
		  String text="";

		  // This will reference one line at a time
		  String line = null;
		
		  try {
		      // FileReader reads text files in the default encoding.
		      FileReader fileReader = new FileReader(fineName);
		
		      // Always wrap FileReader in BufferedReader.
		      BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		      while((line = bufferedReader.readLine()) != null) {
		      	text+=line;
		      	text+=" ,";
		      }    
		
		      // Always close files.
		      bufferedReader.close();
		  }
		  catch(FileNotFoundException ex) {
		      System.out.println(
		          "Unable to open file '" + 
		        		  fineName + "'");                
		  }
		  catch(IOException ex) {
		      System.out.println(
		          "Error reading file '" 
		          + fineName + "'");                   
		      // Or we could just do this: 
		      // ex.printStackTrace();
		  }
		  return text;
	  }
}
