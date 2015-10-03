package reading_data_from_file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadOneColumnFromFile {
	
	List<String> oneColumn = new ArrayList<String>();
	
	public ReadOneColumnFromFile()
	{
		
	}
	
	public ReadOneColumnFromFile(String file_path, int num_column_to_read) throws IOException
	{
		readOneColumnFromTabSeparColumns(file_path,num_column_to_read);	//main operation
	}
	
	public List<String> getTheColumn()	//getter method
	{
		return oneColumn;
	}
	
	public List<String> readOneColumnFromTabSeparColumns(String filePath, int indexOfTheColumn) throws IOException 		//stores the column in text_column
	{
		oneColumn.clear();
		BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(new java.io.File(filePath))));
		String line;
		
		while((line = read.readLine()) != null)
		{
			String[] columns = line.split("\\t");
			boolean skeepLine = false;
			for(int i = 0; i < columns.length; i++)
				if(columns[i].equals("null") || columns[i].equals("NaN") || columns[i].equals("") )
					skeepLine = true;
			if(!skeepLine)
				oneColumn.add(columns[indexOfTheColumn]);
		}
		read.close();
		return oneColumn;
	}
	
}
