import java.io.*;


public class FileIO {
	
	static File file;
	
	
	
	public FileIO() throws FileNotFoundException, UnsupportedEncodingException
	{
		file = new File("logs.txt");
		
	}
	public static void write(String logMessage) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
	    out.append(logMessage +  "\n");
	    System.out.print(logMessage);
	    out.close();
		
		
	}
}
