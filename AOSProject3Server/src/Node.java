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
	
	//Constructor that Initializes the Port and the NodeNumber	
	public Node(int port) throws UnknownHostException
	{
		sethostName();
		System.out.println(hostName);
		Port = port;
		setNodeNumber();
	}
	// Assigns the Node Number based on the Hostname of the Server
	private void setNodeNumber() {
		//	 int node = Character.getNumericValue(hostName.charAt(3));
			 int node = Integer.valueOf(hostName.substring(2,4));
	                 nodeNumber = node-10;
			
	}
	// Retrieves the Hostname and assigns it to the Server hostName
	private void sethostName()  {
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//returns server hostName
	public String getHostName()
	{
	return hostName;
	}
	//returns node Number
	public static synchronized int getNodeNumber()
	{
	return nodeNumber;
	}

}
