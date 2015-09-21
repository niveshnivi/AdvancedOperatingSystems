/*A class used to create a Node Object which initializes accordingly and sets the
 *Node Number and Host Name on Starting the Node.
 *The Node Object is Created in Controller.java in main() function and the Object
 *lives throughout the execution of the Program
 */

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
			int node = Integer.valueOf(hostName.substring(2,4));
			nodeNumber = node - 20;
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
