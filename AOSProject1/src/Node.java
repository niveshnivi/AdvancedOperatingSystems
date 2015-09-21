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
		if(node == 0)
		{
			nodeNumber = 9;
		}
		else{
			nodeNumber = node - 1;
		}
		nodeNumber = 0;
	}

private void sethostName()  {
		// TODO Auto-generated method stub
		
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return hostName;
	}
public String getHostName()
{
	return hostName;
	
}

public static int getNodeNumber()
{
	return nodeNumber;
	
}

}
