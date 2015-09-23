package notUsedClasses;
/*package reading_data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Process_R_Constituent_fin 		//may not needed
{
	String file_path;
	final Charset ENCODING = StandardCharsets.UTF_8;
	
	Process_R_Constituent_fin(String input_file, String file_unique_sym) throws IOException
	{
		set_input_path(input_file);
		process_the_R_file();
		generate_file_unique_sym(file_unique_sym);
	}
	
	void set_input_path(String input_file)
	{
		file_path=input_file;
	}
	
	void process_the_R_file() throws IOException
	{
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(new java.io.File(file_path))));
		String line;
		List<String> lines = new ArrayList<String>();
		Path path = Paths.get(file_path);
		
		while((line = read.readLine()) != null)
		{
			for(int i=0; i<line.length(); i++)	//get rid of new line characters
			{
				if(line.subSequence(i, 1).equals("\n"));
					line = line.substring(0,i) + " " + line.substring(i+1);
			}
			lines.add(line);
		}
		Files.write(path, lines, ENCODING);
		read.close();
	}
	
	void generate_file_unique_sym(String file_unique_sym) throws IOException
	{
		List<String> Symbols = new ArrayList<String>();
		File R_Constituent_fin =  new java.io.File(file_path);
		BufferedReader read = 
				new BufferedReader(new InputStreamReader(new FileInputStream(R_Constituent_fin)));
		
		String line;
		
		while((line = read.readLine()) != null)
		{
			String[] columns = line.split("\\t");
			Symbols.add(columns[0]);
		}
		Path path = Paths.get(file_unique_sym);
		Files.write(path, Symbols, ENCODING);
		read.close();
	}
}
*/