/*
 *The following class Receiver.java is the class used by client in Receiving messages from Servers 1 to 7
 *A server socket constanly listens on port 1234 and processes the messages accordingly and updates the Quorum
 *The Quorum is used to recursively decide if the necessary grants have been received and the Client enters Critical Section
 *The code for entering Critical Section is written in Controller.java.The requests for entering Critical Section are sent
 *in RequestSender.java Class.
 */



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Receiver {
	Node node;
	int port;
	ServerSocket receiver;
	boolean listen = true;
	static int notifyCount = 0;
	static int completeCount = 0;
	static int totalReceivedMessage = 0;
	static boolean[] Quorum = new boolean[8];
	static int ackCount = 0;
	static boolean abortReceived = false;

	// Constructor to initialize the Receiver using Node number and Port number
	public Receiver(Node node,int port) throws IOException
	{
		synchronized(this)
		{
		System.out.println("Receiver Thread " + Thread.currentThread());
		this.node = node;
		this.port = port;
		receiver = new ServerSocket(port);
		Listen();
		}
	}

	// Function to constanly listen on Port and do the Appropriate Processing using Process()
	private void Listen() throws IOException {
		while(true)
		{
//		System.out.println("Listening on port " + receiver.getLocalPort() + "...");
		Socket server = receiver.accept();
		DataInputStream in = new DataInputStream(server.getInputStream());
	        Process(in.readUTF());
        	server.close();
		}
	}

	// Function used to process the Messgae received from Server
	private void Process(String Message) throws IOException {
		IncTotalReceivedMessage();
// 		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		int ObjectNumber;
		Long time;
		if(type.equals("gra"))
		{
		nodeNumber = Character.getNumericValue(Message.charAt(18));
		ObjectNumber = Character.getNumericValue(Message.charAt(31));
		if(ObjectNumber == RequestSender.getObjectRequested())
		{
       	if(Controller.getNodeState().equals("waiting"))
		 {
	  	 RequestSender.IncExchanged();
	  	 }
       	 System.out.println("Received a Grant from Server " + nodeNumber + " for Object " + ObjectNumber);
	  	 updateQuorum(nodeNumber);
		}
		}
		else if(type.equals("sta"))
		{
//			FileIO.write("Setting node to ready");
			Controller.setNodeState("ready");
		}
		else if(type.equals("not"))
		{
			System.out.println("incrementing notifycount");
			IncrementNotify();
		}
		else if(type.equals("fin"))
		{
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("***************** Statisitics of Client "+ Node.getNodeNumber() + " *************************************");
			System.out.println("Total Number of Messages Received by Client " + node.getNodeNumber() + " is " + getTotalReceivedMessage() );
			System.out.println("Total Number of Messages Sent by Client " + node.getNodeNumber() + " is " + RequestSender.getTotalMessageSent());
			System.out.println("*********************************************************************************************************");
			System.exit(0);
		}
		else if(type.equals("ack"))
		{
			IncackCount();
		}
		else if(type.equals("abo"))
		{
			abortReceived = true;
		}
	}
    private synchronized void IncackCount() {
		// TODO Auto-generated method stub
		ackCount++;
	}

	// Function to update the Votes in Quorum from Servers that sent a Grant    
    private synchronized void updateQuorum(int nodeNumber) {
		Quorum[nodeNumber] = true;
	}
    //returns the Quorum Array
    public static synchronized boolean[] getQuorum()
    {
    	return Quorum;
    }
    // Increments the Complete Notification Count 
    private void IncrementComplete() {
		completeCount++;
    }
    // Retruns Complete notification Count
    public static synchronized int getCompleteCount()
    {
    	return completeCount;
    }
    //Increments Notify Count
    private void IncrementNotify() {
		notifyCount++;
                System.out.println("Receiver notify Count at node 0 is" + getNotifyCount());
    }
    // returns Notify Count 
     public synchronized static int getNotifyCount()
    {
    	 return notifyCount;
     }
    // Increment Total Received messages	
	private static synchronized void IncTotalReceivedMessage()
	{
		totalReceivedMessage++;
	}
     // Returns total Received Messages
	public static synchronized int getTotalReceivedMessage()
	{
		return totalReceivedMessage;
	}
     // Clears the quorum after coming out of the critical Section
	public static synchronized void clearQuorum() {
		for(int i = 0;i < Quorum.length;i++)
		{
			Quorum[i] = false;
		}
	}

	public static synchronized int getAckCount() {
		// TODO Auto-generated method stub
		return ackCount;
	}

	public static synchronized boolean abortReceived() {
		// TODO Auto-generated method stub
		return abortReceived;
	}
		
	}


