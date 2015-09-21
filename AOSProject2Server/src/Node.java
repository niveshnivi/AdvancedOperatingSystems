import java.net.InetAddress;
import java.net.UnknownHostException;


public class Node extends Thread{
	String hostName;
	int Port;
	static int nodeNumber;
	
	
	public Node(int port) throws UnknownHostException
	{
		sethostName();
		System.out.println(hostName);
		Port = port;
		setNodeNumber();
	}

private void setNodeNumber() {

		
			int node = Character.getNumericValue(hostName.charAt(3));
			nodeNumber = node;
		
	
	}

private void sethostName()  {
		
		
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
public String getHostName()
{
	return hostName;
	
}

public static synchronized int getNodeNumber()
{
	return nodeNumber;
	
}

}
