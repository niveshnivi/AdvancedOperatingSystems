import java.io.*;


public class FileIO {
	
	public static void write(String logMessage, int i) throws IOException {
		int ObjectNumber = i;
		if(i <= 5)
		{
		for(int j=i;j<=i+3;i++)
		{
		File file = new File("Server"+j+"/Object"+ObjectNumber+".txt");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
	    out.append(logMessage +  "\n");
	    System.out.print(logMessage);
	    out.close();
		}
		}
		else if(i == 6)
		{
		for(int j=i;j<=i+2;j++)
		{
		File file = new File("Server"+j+"/Object"+ObjectNumber+".txt");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		out.append(logMessage +  "\n");
		System.out.print(logMessage);
		out.close();	
		}
		File file = new File("Server1/Object"+ObjectNumber+".txt");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		out.append(logMessage +  "\n");
		System.out.print(logMessage);
		out.close();	
		}
		else if(i == 7)
		{
			for(int j=1;j<=2;j++)
			{
			File file = new File("Server"+j+"/Object"+ObjectNumber+".txt");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.append(logMessage +  "\n");
			System.out.print(logMessage);
			out.close();	
			}
			File file = new File("Server7/Object"+ObjectNumber+".txt");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.append(logMessage +  "\n");
			System.out.print(logMessage);
			out.close();	
		}
	}
}
