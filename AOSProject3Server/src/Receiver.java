/*The receiver class contains the Thread on which the Socket Server listens for the Client's request and release messages,also 
 *contains a Sender socket which sends the Grants and Complete notifications to CLients.
 *The servers donot Communicate with Servers.It has a Queue that maintains the requests from the Clients and sends grant 
 *after it received a Release from Client.
 */

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Receiver {
	Node node;
	static int port;
	static	Socket sender;
	ServerSocket receiver;
	boolean listen = true;
	static int completeCount = 0;
	static int totalReceivedMessage = 0;
	static int totalSent = 0;
	static int clientNumber = 0;
	//Queue<Integer> requestQueue = new LinkedList<Integer>();
	Hashtable<Integer,Queue<Integer>> ObjectQueue = new Hashtable <Integer,Queue<Integer>>();
	
	//Constructor  to Initialize the Server Socket to Listen usinf Listen()
	public Receiver(Node node,int port) throws IOException, InterruptedException
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

	//A function which listens to messages on Port 1234
	private void Listen() throws IOException, InterruptedException {
		
		while(true)
		{
		System.out.println("Listening on port " + receiver.getLocalPort() + "...");
		Socket server = receiver.accept();
		DataInputStream in = new DataInputStream(server.getInputStream());
                Process(in.readUTF());
                server.close();
		}
	}
	
	//A function which parses the message received over the socket and processes accordingly 
	private void Process(String Message) throws IOException, InterruptedException {
		//requestTable.put(arg0, arg1)
		IncTotalReceivedMessage();
 		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		int ObjectNumber;
		//Long time;
		if(type.equals("rel"))
		{
		nodeNumber = Character.getNumericValue(Message.charAt(13));
		ObjectNumber = Character.getNumericValue(Message.charAt(26));
		if(Controller.getObjectState(ObjectNumber).equals("locked"))
		{
			if(ObjectQueue.containsKey(ObjectNumber))
			{
			  if(!ObjectQueue.get(ObjectNumber).isEmpty())
			  {
				int node = ObjectQueue.get(ObjectNumber).poll();
				sendGrant(node,ObjectNumber);
				Controller.setObjectState(ObjectNumber,"locked");
				//setClientLocked(node);
			   }
			 else
			  {
				Controller.setObjectState(ObjectNumber,"unlocked");
			  }
			}
			else
			  {
				Controller.setObjectState(ObjectNumber,"unlocked");
			  }
		}
		else
		{
			 removefromQueue(ObjectNumber,nodeNumber);
		}
		}
		else if(type.equals("req"))
		{
			nodeNumber = Character.getNumericValue(Message.charAt(18));
			ObjectNumber = Character.getNumericValue(Message.charAt(31));
			if(Controller.getObjectState(ObjectNumber) == "unlocked")
			{
				System.out.println("Sending a Grant to the node " + nodeNumber);
				sendGrant(nodeNumber,ObjectNumber);
			    Controller.setObjectState(ObjectNumber,"locked");
				//setClientLocked(nodeNumber);
			}
			else
			{
				updateQueue(ObjectNumber,nodeNumber);
			}
		}
		else if(type.equals("Com"))
		{
			if(node.getNodeNumber() == 1)
			{
			IncrementComplete();
			}
			
		}
		else if(type.equals("fin"))
		{
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("************************ Final Statistics of Number of Messages *****************************");
			System.out.println("Total number of messages received by Server " + Node.getNodeNumber() + " is " + getTotalReceivedMessage());
			System.out.println("Total number of messages sent by Server " + Node.getNodeNumber() + " is " + getTotalMessageSent());
			System.out.println("**********************************************************************************************");
			Thread.sleep(5000);
			System.exit(0);
		}
		else if(type.equals("cre"))
		{
			nodeNumber = Character.getNumericValue(Message.charAt(23));
			ObjectNumber = Character.getNumericValue(Message.charAt(16));
			if(checkFile(ObjectNumber))
			{
				sendAck(nodeNumber,ObjectNumber);
			}
			else
			{
				sendAbort(nodeNumber,ObjectNumber);
			}
		}
		else if(type.equals("cri"))
		{
			 ObjectNumber = Character.getNumericValue(Message.charAt(28));
			 write(Message,ObjectNumber);
		}
	}
        
 	private void write(String message, int objectNumber) throws IOException {
 		String fileName = "Server"+Node.getNodeNumber()+"/Object"+objectNumber+".txt";
 		File file = new File(fileName);
 		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		out.append(message +  "\n");
	//	System.out.print(message);
		out.close();
	}

	private void sendAbort(int nodeNumber, int objectNumber) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
 		 int nodeNum = nodeNumber + 20;
		 String hostName = "dc"+(nodeNum)+".utdallas.edu";
		 sender = new Socket(hostName,port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("abort from Server "+ Node.getNodeNumber() + " for Object "+ objectNumber +" at " + System.currentTimeMillis());
		 		
	}

	private void sendAck(int nodeNumber, int objectNumber) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		int nodeNum = nodeNumber + 20;
		 String hostName = "dc"+(nodeNum)+".utdallas.edu";
		 sender = new Socket(hostName,port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("ack from Server "+ Node.getNodeNumber() + " for Object "+ objectNumber +" at " + System.currentTimeMillis());
		
	}

	private boolean checkFile(int objectNumber) {
		// TODO Auto-generated method stub
		String fileName = "Server"+Node.getNodeNumber()+"/Object"+objectNumber+".txt";
		File file = new File(fileName);
		if(file.exists())
		{
		return true;
		}
		else
		{
		return false;
		}
	}

	// Remove a client's request from Queue	
	private synchronized void removefromQueue(int ObjectNumber, int nodeNumber) {
		if(!ObjectQueue.get(ObjectNumber).isEmpty())
		{
		ObjectQueue.get(ObjectNumber).remove(nodeNumber);
		}
	}
	// Returns the client that locked this Server
	private synchronized int getClientLocked() {
		
		return clientNumber;
	}
	// Notes which client has locked the Server
/* 	private synchronized void setClientLocked(int nodeNumber) {
		
		clientNumber = nodeNumber;
		System.out.println("This server is currently locked by Client no " + clientNumber );
	} */
	// Adds a request to Queue
	private synchronized void updateQueue(int ObjectNumber, int nodeNumber2) {
		//System.out.println("The Server is locked to adding the request from " + nodeNumber + " to Queue");
		if(ObjectQueue.containsKey(ObjectNumber))
		{
			ObjectQueue.get(ObjectNumber).add(nodeNumber2);
		}
		else
		{
			Queue<Integer> requestQueue = new LinkedList<Integer>();
			requestQueue.add(nodeNumber2);
			ObjectQueue.put(ObjectNumber, requestQueue);
		}
		//System.out.println("Current Queue is " + requestQueue);
	}
	// Sends grant to the Client that Requested	
	private void sendGrant(int nodeNumber, int objectNumber) throws UnknownHostException, IOException {
		 int nodeNum = nodeNumber + 20;
		 String hostName = "dc"+(nodeNum)+".utdallas.edu";
		 sender = new Socket(hostName,port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("grant from Server "+ Node.getNodeNumber() + " for Object "+ objectNumber +" at " + System.currentTimeMillis());
		 IncTotalSent();
	}
	
   	// Counting the number of Complete notifications Received 
	private void IncrementComplete() {
		completeCount++;
	}
    
        public static synchronized int getCompleteCount()
        {
    	return completeCount;
        }

	private static synchronized void IncTotalReceivedMessage()
	{
		totalReceivedMessage++;
	}

	private static synchronized void IncTotalSent()
	{
		totalSent++;
	}

	public static synchronized int getTotalReceivedMessage()
	{
		return totalReceivedMessage;
	}
	
	public static synchronized int getTotalMessageSent()
	{
		return totalSent;
	}
	// Sends finish to Complete the Computation and exit from all processes to all clients and servers
	public static void sendFinish() throws UnknownHostException, IOException {
	String hostName;
        for(int i=11;i<=17;i++)
        {
          hostName = "dc"+(i)+".utdallas.edu";
          sender = new Socket(hostName,port);
          OutputStream outToServer = sender.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
          out.writeUTF("finish from Server "+ Node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
	  IncTotalSent();
        }
      
        for(int i=21;i<=25;i++)
        {
          hostName = "dc"+(i)+".utdallas.edu";
          sender = new Socket(hostName,port);
          OutputStream outToServer = sender.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
          out.writeUTF("finish from Server "+ Node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
	  IncTotalSent();
        }

	  }	
	}


