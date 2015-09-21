import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Receiver {
	Node node;
	static int port;
	static	Socket sender;
	ServerSocket receiver;
	boolean listen = true;
	//static int notifyCount = 0;
	static int completeCount = 0;
	
	static int totalReceivedMessage = 0;
	static int clientNumber = 0;
	Queue<Integer> requestQueue = new LinkedList<Integer>();
	//PriorityQueue<Integer> requestQueue = new PriorityQueue<Integer>();  	
	
	public Receiver(Node node,int port) throws IOException,InterruptedException
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
	
	private void Process(String Message) throws IOException, InterruptedException {
		//requestTable.put(arg0, arg1)
		IncTotalReceivedMessage();
 		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		//Long time;
		if(type.equals("rel"))
		{
		nodeNumber = Character.getNumericValue(Message.charAt(13));
		if(nodeNumber == getClientLocked() && Controller.getNodeState().equals("locked"))
		{
			if(!requestQueue.isEmpty())
			{
				sendGrant(requestQueue.poll());
				Controller.setNodeState("locked");
				setClientLocked(nodeNumber);
			}
			else
			{
				Controller.setNodeState("unlocked");
			}
		}
		else
		{
			 removefromQueue(nodeNumber);
		}
		}
		else if(type.equals("req"))
		{
			nodeNumber = Character.getNumericValue(Message.charAt(18));
			if(Controller.getNodeState() == "unlocked")
			{
				System.out.println("Sending a Grant to the node " + nodeNumber);
				sendGrant(nodeNumber);
				Controller.setNodeState("locked");
				setClientLocked(nodeNumber);
			}
			else
			{
				updateQueue(nodeNumber);
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
			System.out.println("Total number of messages received by Server " + Node.getNodeNumber() + "is " + getTotalReceivedMessage());
			Thread.sleep(5000);
			System.exit(0);
		}
	}
        
	
	private synchronized void removefromQueue(int nodeNumber) {
		// TODO Auto-generated method stub
		requestQueue.remove(nodeNumber);
	}
	private synchronized int getClientLocked() {
		// TODO Auto-generated method stub
		return clientNumber;
	}
	private synchronized void setClientLocked(int nodeNumber) {
		// TODO Auto-generated method stub
		clientNumber = nodeNumber;
	}
	private synchronized void updateQueue(int nodeNumber) {
		requestQueue.add(nodeNumber);
	}
	
	
	
	private void sendGrant(int nodeNumber) throws UnknownHostException, IOException {
		 int nodeNum = nodeNumber + 10;
		 String hostName = "dc"+(nodeNum)+"utdallas.edu";
		 sender = new Socket("localhost",port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("grant from Server "+ Node.getNodeNumber() + " at " + System.currentTimeMillis());
	}
	
    
	private void IncrementComplete() {
		// TODO Auto-generated method stub
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

	public static synchronized int getTotalReceivedMessage()
	{
		return totalReceivedMessage;
	}
	public static void sendFinish() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		String hostName;
        for(int i=1;i<=7;i++)
        {
          
           hostName = "dc0"+(i)+".utdallas.edu";
          sender = new Socket(hostName,port);
          OutputStream outToServer = sender.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
//          System.out.println("Sending Finish to Nodenumber " + i + " hostName " + hostName + " from " + node.getNodeNumber());
          out.writeUTF("finish from Server "+ Node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
        }
        for(int i=10;i<=14;i++)
        {
          
           hostName = "dc"+(i)+".utdallas.edu";
          sender = new Socket(hostName,port);
          OutputStream outToServer = sender.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
//          System.out.println("Sending Finish to Nodenumber " + i + " hostName " + hostName + " from " + node.getNodeNumber());
          out.writeUTF("finish from Server "+ Node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
        }

	  }	
	}


