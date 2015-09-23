package writing_data_to_file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WriteToFile {
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public static void writeSmallTextFile(List<String> lines, String file_name) throws IOException
	{
		Path path = Paths.get(file_name);
		Files.write(path, lines, ENCODING);
	}

}
